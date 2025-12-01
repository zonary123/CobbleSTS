package com.kingpixel.cobblests.database;

import com.kingpixel.cobblests.CobbleSTS;
import com.kingpixel.cobblests.model.UserInfo;
import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.Model.DataBaseConfig;
import net.minecraft.server.network.ServerPlayerEntity;

import java.sql.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Class to handle the SQLite database.
 * Author: Carlos Varas Alonso - 22/02/2025 4:04
 */
public class DataBaseSQLite extends DataBaseClient {

  private Connection connection;

  public DataBaseSQLite(DataBaseConfig config) {
    super();
    try {
      this.connection = DriverManager.getConnection(config.getUrl());
    } catch (SQLException e) {
      CobbleUtils.LOGGER.error(CobbleSTS.MOD_ID, "Error connecting to the SQLite database");
    }
  }

  @Override
  public void connect() {
    CobbleUtils.LOGGER.info(CobbleSTS.MOD_ID, "Connecting to the SQLite database...");
    try (Statement statement = connection.createStatement()) {
      String createTableQuery = """
        CREATE TABLE IF NOT EXISTS users (
            uuid TEXT PRIMARY KEY,
            playername TEXT NOT NULL,
            cooldown INTEGER NOT NULL
        );
        """;
      statement.execute(createTableQuery);
    } catch (SQLException e) {
      CobbleUtils.LOGGER.error(CobbleSTS.MOD_ID, "Error creating SQLite table");
    }
  }

  @Override
  public void disconnect() {
    try {
      if (connection != null && !connection.isClosed()) {
        connection.close();
        CobbleUtils.LOGGER.info(CobbleSTS.MOD_ID, "Disconnected from the SQLite database.");
      }
    } catch (SQLException e) {
      CobbleUtils.LOGGER.error(CobbleSTS.MOD_ID, "Error disconnecting from the SQLite database");
    }
  }

  @Override
  public UserInfo getUserInfo(ServerPlayerEntity player) {
    UserInfo userInfo = DataBaseFactory.users.get(player.getUuid());
    if (userInfo != null) return userInfo;

    String query = "SELECT * FROM users WHERE uuid = ?";
    try (PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, player.getUuidAsString());
      ResultSet resultSet = statement.executeQuery();

      if (resultSet.next()) {
        userInfo = new UserInfo(
          resultSet.getString("uuid"),
          resultSet.getString("playerName"),
          resultSet.getLong("cooldown")
        );
      } else {
        userInfo = new UserInfo(player);
        updateUserInfo(userInfo);
      }
    } catch (SQLException e) {
      CobbleUtils.LOGGER.error(CobbleSTS.MOD_ID, "Error retrieving user information from SQLite");
    }

    DataBaseFactory.users.put(player.getUuid(), userInfo);
    return userInfo;
  }

  @Override
  public void updateUserInfo(UserInfo userInfo) {
    CompletableFuture.runAsync(() -> {
        String query = "INSERT INTO users (uuid, playerName, cooldown) VALUES (?, ?, ?) " +
          "ON CONFLICT(uuid) DO UPDATE SET playerName = excluded.playerName, cooldown = excluded.cooldown";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
          statement.setString(1, userInfo.getUuid());
          statement.setString(2, userInfo.getPlayerName());
          statement.setLong(3, userInfo.getCooldown());
          statement.executeUpdate();
        } catch (SQLException e) {
          CobbleUtils.LOGGER.error(CobbleSTS.MOD_ID, "Error updating user information in SQLite");
        }
      }, CobbleSTS.EXECUTOR_STS)
      .orTimeout(5, TimeUnit.SECONDS)
      .exceptionally(ex -> {
        CobbleUtils.LOGGER.error(CobbleSTS.MOD_ID,
          "Async error while updating user information in SQLite: " + userInfo.getUuid());
        return null;
      });
  }
}