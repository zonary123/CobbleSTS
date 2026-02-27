package com.kingpixel.ultrasts.database;

import com.google.gson.reflect.TypeToken;
import com.kingpixel.cobbleutils.util.UtilsFile;
import com.kingpixel.ultrasts.UltraSTS;
import com.kingpixel.ultrasts.models.STS;
import com.kingpixel.ultrasts.models.User;
import com.kingpixel.ultrasts.models.UserOptions;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class SQLDatabaseClient extends DatabaseClient {

  private HikariDataSource dataSource;

  private static final Type MONEY_TYPE = new TypeToken<Map<String, BigDecimal>>() {
  }.getType();
  private static final Type COOLDOWN_TYPE = new TypeToken<Map<String, Long>>() {
  }.getType();

  @Override
  public void connect() {
    var config = UltraSTS.config.getDatabase();

    HikariConfig hikari = new HikariConfig();
    hikari.setJdbcUrl(config.getUrl());
    hikari.setUsername(config.getUser());
    hikari.setPassword(config.getPassword());
    hikari.setPoolName("UltraSTS-Pool");
    hikari.setMaximumPoolSize(10);

    dataSource = new HikariDataSource(hikari);
  }

  @Override
  public void disconnect() {
    saveAll().join();
    if (dataSource != null) dataSource.close();
  }

  @Override
  public CompletableFuture<@Nullable User> findUser(@NotNull UUID uuid) {
    return UltraSTS.ASYNC.supply(() -> {
      User cached = getUser(uuid);
      if (cached != null) return cached;

      try (Connection conn = dataSource.getConnection();
           PreparedStatement ps = conn.prepareStatement(
             "SELECT * FROM users WHERE uuid = ?")) {

        ps.setString(1, uuid.toString());

        try (ResultSet rs = ps.executeQuery()) {
          if (!rs.next()) return null;

          return parseUser(rs);
        }

      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    });
  }

  @Override
  public CompletableFuture<Void> saveUser(@NotNull User user) {
    return UltraSTS.ASYNC.runAsync(() -> {

      String sql = """
        INSERT INTO users (uuid, username, options, money_gained, cooldowns)
        VALUES (?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            username = VALUES(username),
            options = VALUES(options),
            money_gained = VALUES(money_gained),
            cooldowns = VALUES(cooldowns)
        """;

      try (Connection conn = dataSource.getConnection();
           PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, user.getUuid().toString());
        ps.setString(2, user.getUsername());
        ps.setString(3, UtilsFile.getGson().toJson(user.getOptions()));
        ps.setString(4, UtilsFile.getGson().toJson(user.getMoneyGained()));
        ps.setString(5, UtilsFile.getGson().toJson(user.getCooldowns()));

        ps.executeUpdate();

      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  @Override
  public CompletableFuture<List<User>> findTopUsers(int limit, int page, STS sts) {
    return UltraSTS.ASYNC.supply(() -> {

      List<User> list = new ArrayList<>();

      String sql = """
        SELECT * FROM users
        ORDER BY CAST(JSON_EXTRACT(money_gained, ?) AS DECIMAL(30,10)) DESC
        LIMIT ? OFFSET ?
        """;

      try (Connection conn = dataSource.getConnection();
           PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, "$.\"" + sts.getId() + "\"");
        ps.setInt(2, limit);
        ps.setInt(3, (page - 1) * limit);

        try (ResultSet rs = ps.executeQuery()) {
          while (rs.next()) {
            list.add(parseUser(rs));
          }
        }

      } catch (Exception e) {
        e.printStackTrace();
      }

      return list;
    });
  }

  private User parseUser(ResultSet rs) throws Exception {

    UUID uuid = UUID.fromString(rs.getString("uuid"));
    String username = rs.getString("username");

    UserOptions options = UtilsFile.getGson().fromJson(
      rs.getString("options"),
      UserOptions.class
    );

    Map<String, BigDecimal> money = UtilsFile.getGson().fromJson(
      rs.getString("money_gained"),
      MONEY_TYPE
    );

    Map<String, Long> cooldowns = UtilsFile.getGson().fromJson(
      rs.getString("cooldowns"),
      COOLDOWN_TYPE
    );

    return User.builder()
      .uuid(uuid)
      .username(username)
      .options(options != null ? options : new UserOptions())
      .moneyGained(money != null ? new ConcurrentHashMap<>(money) : new ConcurrentHashMap<>())
      .cooldowns(cooldowns != null ? new ConcurrentHashMap<>(cooldowns) : new ConcurrentHashMap<>())
      .build();
  }
}