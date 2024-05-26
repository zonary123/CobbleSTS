package com.kingpixel.cobblests;

import com.kingpixel.cobblests.Config.Config;
import com.kingpixel.cobblests.Config.Lang;
import com.kingpixel.cobblests.Config.STSConfig;
import com.kingpixel.cobblests.permissions.STSPermission;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Carlos Varas Alonso - 28/04/2024 23:50
 */
public class CobbleSTS {
  public static final String MOD_ID = "cobblests";
  public static final Logger LOGGER = LogManager.getLogger();
  public static final String MOD_NAME = "CobbleSTS";
  public static final String path = "/config/cobblests/";
  public static Lang language = new Lang();
  public static MinecraftServer server;
  public static Config config = new Config();
  public static STSConfig dexpermission = new STSConfig();
  public static STSPermission permissions = new STSPermission();

  public static void init() {
    LOGGER.info("Initializing " + MOD_NAME);

  }

  public static void load() {
    language.init();
    config.init();
  }

}
