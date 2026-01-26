package com.kingpixel.ultrasts;

import ca.landonjw.gooeylibs2.api.tasks.Task;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.kingpixel.ultrasts.Config.Config;
import com.kingpixel.ultrasts.Config.Lang;
import com.kingpixel.ultrasts.command.CommandTree;
import com.kingpixel.ultrasts.database.DataBaseFactory;
import com.kingpixel.ultrasts.model.UserInfo;
import com.kingpixel.ultrasts.utils.STSUtil;
import com.kingpixel.cobbleutils.util.PlayerUtils;
import com.kingpixel.cobbleutils.util.TypeMessage;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import kotlin.Unit;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;

import java.util.Collections;
import java.util.concurrent.*;

/**
 * @author Carlos Varas Alonso - 28/04/2024 23:50
 */
public class UltraSTS implements ModInitializer {
  public static final String MOD_ID = "ultrasts";
  public static final String MOD_NAME = "UltraSTS";
  public static final String PATH = "/config/ultrasts/";
  public static final String PATH_DATA = PATH + "data/";
  public static Lang language = new Lang();
  public static MinecraftServer server;
  public static Config config = new Config();
  public static final ExecutorService EXECUTOR_STS = Executors.newFixedThreadPool(1, new ThreadFactoryBuilder()
    .setDaemon(true)
    .setNameFormat("UltraSTS-Executor-%d")
    .build());
  private static final ScheduledExecutorService SCHEDULED_EXECUTOR_STS = Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder()
    .setDaemon(true)
    .setNameFormat("UltraSTS-Scheduled-Executor-%d")
    .build());

  public static void init() {
    events();
  }

  public static void load() {
    files();
    tasks();
    DataBaseFactory.init(config.getDatabase());
  }

  private static void events() {
    files();

    CommandRegistrationEvent.EVENT.register((dispatcher, registry, selection) -> {
      CommandTree.register(dispatcher);
    });

    LifecycleEvent.SERVER_STARTED.register(server -> load());

    LifecycleEvent.SERVER_STOPPING.register(server -> {
      DataBaseFactory.INSTANCE.disconnect();
    });

    PlayerEvent.PLAYER_JOIN.register(player -> {
      CompletableFuture.runAsync(() -> {
          var userinfo = DataBaseFactory.INSTANCE.getUserInfo(player);
          if (userinfo != null) {
            if (userinfo.check(player)) {
              DataBaseFactory.INSTANCE.updateUserInfo(userinfo);
            }
          }
        }, EXECUTOR_STS)
        .exceptionally(e -> {
          e.printStackTrace();
          return null;
        });
    });

    PlayerEvent.PLAYER_QUIT.register(player -> {
      DataBaseFactory.INSTANCE.removeIfNecessary(player);
    });

    LifecycleEvent.SERVER_LEVEL_LOAD.register(level -> server = level.getServer());

    CobblemonEvents.POKEMON_RELEASED_EVENT_POST.subscribe(Priority.NORMAL, evt -> {
      if (UltraSTS.config.isReleasePokemon())
        STSUtil.sell(evt.getPokemon(), evt.getPlayer(), STSUtil.STSAction.RELEASE);
      return Unit.INSTANCE;
    });
  }


  private static void files() {
    language.init();
    config.init();
  }

  private static void tasks() {
    if (!config.isNotifyReady() || config.getAlertCooldown() <= 0) return;
    long cooldown = (20L * 60L) * config.getAlertCooldown();
    SCHEDULED_EXECUTOR_STS.scheduleWithFixedDelay(() -> {
      if (server == null) return;
      var players = Collections.synchronizedCollection(server.getPlayerManager().getPlayerList());
      for (var player : players) {
        if (player == null) continue;
        UserInfo userInfo = DataBaseFactory.INSTANCE.getUserInfo(player);
        if (!userInfo.hasCooldown()) {
          PlayerUtils.sendMessage(
            player,
            language.getReadytosell(),
            language.getPrefix(),
            TypeMessage.CHAT
          );
        }
      }
    }, cooldown, cooldown, TimeUnit.SECONDS);
  }

  @Override
  public void onInitialize() {
    init();
  }
}
