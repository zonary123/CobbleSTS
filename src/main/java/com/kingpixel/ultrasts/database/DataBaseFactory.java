package com.kingpixel.ultrasts.database;

import com.kingpixel.ultrasts.model.UserInfo;
import com.kingpixel.cobbleutils.Model.DataBaseConfig;
import com.kingpixel.cobbleutils.Model.DataBaseType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Carlos Varas Alonso - 22/02/2025 3:52
 */
public class DataBaseFactory {
  public static final Map<UUID, UserInfo> users = new HashMap<>();
  public static DataBaseClient INSTANCE;
  
  public static void init(DataBaseConfig config) {
    if (INSTANCE != null) INSTANCE.disconnect();
    switch (config.getType()) {
      case JSON -> INSTANCE = new DataBaseJSON(config);
      case MYSQL -> INSTANCE = new DataBaseMySQL(config);
      case SQLITE -> INSTANCE = new DataBaseSQLite(config);
      case MONGODB -> INSTANCE = new DataBaseMongoDB(config);
      default ->
        throw new IllegalArgumentException("Invalid database type, Available types: " + Arrays.toString(DataBaseType.values()));
    }
    INSTANCE.connect();
  }
}
