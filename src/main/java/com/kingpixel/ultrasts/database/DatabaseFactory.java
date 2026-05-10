package com.kingpixel.ultrasts.database;

import com.kingpixel.ultrasts.UltraSTS;

public class DatabaseFactory {
  public static DatabaseClient init() {
    if (UltraSTS.database != null) UltraSTS.database.disconnect();
    return switch (UltraSTS.config.getDatabase().getType()) {
      case JSON -> new JsonDatabaseClient();
      case MONGODB -> new MongoDatabaseClient();
      case SQLITE, MARIADB, MYSQL, H2 -> new SQLDatabaseClient();
      default -> throw new IllegalStateException("Unexpected value: " + UltraSTS.config.getDatabase().getType());
    };
  }
}
