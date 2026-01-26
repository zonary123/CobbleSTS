package com.kingpixel.ultrasts.database;

import com.kingpixel.ultrasts.UltraSTS;
import com.kingpixel.ultrasts.model.UserInfo;
import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.Model.DataBaseConfig;
import net.minecraft.server.network.ServerPlayerEntity;

import java.sql.*;
import java.util.concurrent.CompletableFuture;

/**
 * Class to handle the MySQL database.
 * Author: Carlos Varas Alonso - 22/02/2025 4:04
 */
public class DataBaseMySQL extends DataBaseClient {

  private Connection connection;

  public DataBaseMySQL(DataBaseConfig config) {
    super();
    try {
      this.connection = DriverManager.getConnection(
        config.getUrl(), config.getUser(), config.getPassword()
      );
    } catch (SQLException e) {
      CobbleUtils.LOGGER.error(UltraSTS.MOD_ID, "Error connecting to the MySQL database");
    }
  }

  @Override
  public void connect() {
    CobbleUtils.LOGGER.info(UltraSTS.MOD_ID, "Connecting to the MySQL database...");
    try (Statement statement = connection.createStatement()) {
      String createTableQuery = """
            CREATE TABLE IF NOT EXISTS users (
                uuid VARCHAR(36) PRIMARY KEY,
                playername VARCHAR(255) NOT NULL,
                cooldown LONG NOT NULL
            );
        """;
      statement.execute(createTableQuery);
      CobbleUtils.LOGGER.info(UltraSTS.MOD_ID, "Table 'users' ensured in MySQL database.");
    } catch (SQLException e) {
      CobbleUtils.LOGGER.error(UltraSTS.MOD_ID, "Error creating 'users' table in MySQL database");
    }
  }

  @Override
  public void disconnect() {
    try {
      if (connection != null && !connection.isClosed()) {
        connection.close();
        CobbleUtils.LOGGER.info(UltraSTS.MOD_ID, "Disconnected from the MySQL database.");
      }
    } catch (SQLException e) {
      CobbleUtils.LOGGER.error(UltraSTS.MOD_ID, "Error disconnecting from the MySQL database");
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
      CobbleUtils.LOGGER.error(UltraSTS.MOD_ID, "Error retrieving user information from MySQL");
    }

    DataBaseFactory.users.put(player.getUuid(), userInfo);
    return userInfo;
  }

  @Override
  public void updateUserInfo(UserInfo userInfo) {
    CompletableFuture.runAsync(() -> {
        String query = "INSERT INTO users (uuid, playerName, cooldown) VALUES (?, ?, ?) " +
          "ON DUPLICATE KEY UPDATE playerName = VALUES(playerName), cooldown = VALUES(cooldown)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
          statement.setString(1, userInfo.getUuid());
          statement.setString(2, userInfo.getPlayerName());
          statement.setLong(3, userInfo.getCooldown());
          statement.executeUpdate();
        } catch (SQLException e) {
          CobbleUtils.LOGGER.error(UltraSTS.MOD_ID, "Error updating user information in MySQL");
        }
      }, UltraSTS.EXECUTOR_STS)
      .exceptionally(ex -> {
        CobbleUtils.LOGGER.error(UltraSTS.MOD_ID,
          "Async error while updating user information in MySQL: " + userInfo.getUuid());
        return null;
      });
  }
}