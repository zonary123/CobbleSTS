package com.kingpixel.ultrasts;

import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.util.async.AsyncContext;
import com.kingpixel.cobbleutils.util.async.UtilsAsync;
import com.kingpixel.ultrasts.commands.Commands;
import com.kingpixel.ultrasts.configs.Config;
import com.kingpixel.ultrasts.configs.Lang;
import com.kingpixel.ultrasts.configs.STSConf;
import com.kingpixel.ultrasts.database.DatabaseClient;
import com.kingpixel.ultrasts.database.DatabaseFactory;
import com.kingpixel.ultrasts.models.User;
import com.kingpixel.ultrasts.tasks.Tasks;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import java.nio.file.Path;

/**
 * @author Carlos Varas Alonso - 28/04/2024 23:50
 */
public class UltraSTS implements ModInitializer {
  public static final String MOD_ID = "ultrasts";
  public static final String MOD_NAME = "UltraSTS";
  private static final Path PATH = CobbleUtils.getPath().resolve(MOD_ID);
  public static DatabaseClient database;

  public static Config config;
  public static Lang lang;


  public static final AsyncContext ASYNC = UtilsAsync.createContext(MOD_ID, MOD_NAME, 1, 1);

  @Override
  public void onInitialize() {
    CobbleUtils.LOGGER.info(MOD_ID, "Initializing " + MOD_NAME);
    events();
    reload();
  }

  public static void reload() {
    files();
    database = DatabaseFactory.init();
    database.connect();
  }

  public static void files() {
    Config.init();
    Lang.init();
    STSConf.init();
  }

  public static void events() {
    PlayerEvent.PLAYER_JOIN.register(player -> database.findUser(player)
      .whenComplete((user, throwable) -> {
        if (throwable != null) {
          throwable.printStackTrace();
          return;
        }
        if (user == null) user = new User(player);
        user.fix(player);
        user.save();
        DatabaseClient.USERS.put(player.getUuid(), user);
      })
    );

    PlayerEvent.PLAYER_QUIT.register(player -> {
      User user = database.getUser(player);
      if (user != null) user.save();
    });

    LifecycleEvent.SERVER_STARTED.register(evt -> Tasks.register());

    LifecycleEvent.SERVER_STOPPING.register(server -> database.disconnect());

    CommandRegistrationCallback.EVENT.register((dispatcher, access, env) -> {
      Commands.register(dispatcher);
    });


  }

  public static Path getPath() {
    return PATH;
  }

}
