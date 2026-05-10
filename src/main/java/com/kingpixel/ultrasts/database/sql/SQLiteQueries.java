package com.kingpixel.ultrasts.database.sql;

public class SQLiteQueries implements SQLQueries {

  @Override
  public String createTable() {
    return """
          CREATE TABLE IF NOT EXISTS users (
              uuid TEXT PRIMARY KEY,
              username TEXT NOT NULL,
              options TEXT NOT NULL,
              money_gained TEXT NOT NULL,
              cooldowns TEXT NOT NULL
          );
      """;
  }

  @Override
  public String createIndex() {
    return """
          CREATE INDEX IF NOT EXISTS idx_username
          ON users(username);
      """;
  }

  @Override
  public String upsertUser() {
    return """
          INSERT INTO users (uuid, username, options, money_gained, cooldowns)
          VALUES (?, ?, ?, ?, ?)
          ON CONFLICT(uuid) DO UPDATE SET
              username = excluded.username,
              options = excluded.options,
              money_gained = excluded.money_gained,
              cooldowns = excluded.cooldowns
      """;
  }

  @Override
  public String findUserByUUID() {
    return "SELECT * FROM users WHERE uuid = ?";
  }

  @Override
  public String findTopUsers() {
    return """
          SELECT *
          FROM users
          WHERE json_extract(money_gained, ?) IS NOT NULL
          ORDER BY CAST(json_extract(money_gained, ?) AS REAL) DESC
          LIMIT ? OFFSET ?
      """;
  }
}