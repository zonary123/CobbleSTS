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
import java.util.*;
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

    // Crear la tabla automáticamente
    String createTableSQL = """
          CREATE TABLE IF NOT EXISTS users (
              uuid CHAR(36) NOT NULL PRIMARY KEY,
              username VARCHAR(32) NOT NULL,
              options TEXT NOT NULL,
              money_gained TEXT NOT NULL,
              cooldowns TEXT NOT NULL,
              INDEX idx_username (username)
          ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
      """;

    try (Connection conn = dataSource.getConnection();
         PreparedStatement ps = conn.prepareStatement(createTableSQL)) {
      ps.execute();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void disconnect() {
    try {
      saveAll().join(); // Espera que todo se guarde antes de cerrar
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (dataSource != null) dataSource.close();
  }

  @Override
  public CompletableFuture<@Nullable User> findUser(@NotNull UUID uuid) {
    return UltraSTS.ASYNC.supply(() -> {
      User cached = getUser(uuid);
      if (cached != null) return cached;

      try (Connection conn = dataSource.getConnection();
           PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE uuid = ?")) {

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
        user.setDirty(false); // marcar como guardado

      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  @Override
  public CompletableFuture<List<User>> findTopUsers(int limit, int page, STS sts) {
    // Compatible con cualquier versión: leemos todos los usuarios y ordenamos en Java
    return UltraSTS.ASYNC.supply(() -> {
      List<User> list = new ArrayList<>();

      try (Connection conn = dataSource.getConnection();
           PreparedStatement ps = conn.prepareStatement("SELECT * FROM users")) {

        try (ResultSet rs = ps.executeQuery()) {
          while (rs.next()) {
            list.add(parseUser(rs));
          }
        }

      } catch (Exception e) {
        e.printStackTrace();
      }

      // Ordenar en Java según money_gained
      list.removeIf(user -> !user.getMoneyGained().containsKey(sts.getId()));
      list.sort((u1, u2) -> u2.getMoneyGained()
        .getOrDefault(sts.getId(), BigDecimal.ZERO)
        .compareTo(u1.getMoneyGained().getOrDefault(sts.getId(), BigDecimal.ZERO)));

      int safeLimit = Math.max(1, limit);
      int safePage = Math.max(1, page);
      int fromIndex = (safePage - 1) * safeLimit;
      int toIndex = Math.min(fromIndex + safeLimit, list.size());

      if (fromIndex >= list.size()) return Collections.emptyList();
      return list.subList(fromIndex, toIndex);
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

  @Override
  public CompletableFuture<Void> saveAll() {
    var users = getSavableUsers();
    if (users.isEmpty()) return CompletableFuture.completedFuture(null);

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

        for (User user : users) {
          ps.setString(1, user.getUuid().toString());
          ps.setString(2, user.getUsername());
          ps.setString(3, UtilsFile.getGson().toJson(user.getOptions()));
          ps.setString(4, UtilsFile.getGson().toJson(user.getMoneyGained()));
          ps.setString(5, UtilsFile.getGson().toJson(user.getCooldowns()));
          ps.addBatch();

          user.setDirty(false); // marcar como guardado
        }

        ps.executeBatch();

      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }
}