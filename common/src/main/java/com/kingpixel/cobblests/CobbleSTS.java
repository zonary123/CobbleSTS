package com.kingpixel.cobblests;

import ca.landonjw.gooeylibs2.api.tasks.Task;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.kingpixel.cobblests.Config.Config;
import com.kingpixel.cobblests.Config.Lang;
import com.kingpixel.cobblests.command.CommandTree;
import com.kingpixel.cobblests.database.DataBaseFactory;
import com.kingpixel.cobblests.model.UserInfo;
import com.kingpixel.cobblests.utils.STSUtil;
import com.kingpixel.cobbleutils.Model.PokemonFormula;
import com.kingpixel.cobbleutils.util.PlayerUtils;
import com.kingpixel.cobbleutils.util.TypeMessage;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import kotlin.Unit;
import net.minecraft.server.MinecraftServer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Carlos Varas Alonso - 28/04/2024 23:50
 */
public class CobbleSTS {
  public static final String MOD_ID = "cobblests";
  public static final String MOD_NAME = "CobbleSTS";
  public static final String PATH = "/config/cobblests/";
  public static final String PATH_DATA = PATH + "data/";
  public static Lang language = new Lang();
  public static MinecraftServer server;
  public static Config config = new Config();
  private static Task broadcastTask;
  public static ExecutorService EXECUTOR_STS = Executors.newFixedThreadPool(4, new ThreadFactoryBuilder()
    .setDaemon(true)
    .setNameFormat("CobbleSTS-Executor-%d")
    .build());

  public static void init() {
    events();
  }

  public static void load() {
    PokemonFormula.removeFormula(MOD_ID);
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
      if (CobbleSTS.config.isReleasePokemon())
        STSUtil.Sell(evt.getPokemon(), evt.getPlayer(), STSUtil.STSAction.RELEASE);
      return Unit.INSTANCE;
    });
  }


  private static void files() {
    language.init();
    config.init();
  }

  private static void tasks() {
    if (!config.isNotifyReady() || config.getAlertCooldown() <= 0) return;

    if (broadcastTask != null) broadcastTask.setExpired();
    long cooldown = (20L * 60L) * config.getAlertCooldown();
    broadcastTask = Task.builder()
      .execute(() -> {
        CompletableFuture.runAsync(() -> {
            var players = server.getPlayerManager().getPlayerList();
            for (var player : players) {
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
          }, CobbleSTS.EXECUTOR_STS)
          .orTimeout(30, TimeUnit.SECONDS)
          .exceptionally(e -> {
            e.printStackTrace();
            return null;
          });
      })
      .interval(cooldown)
      .infinite()
      .build();
  }
}
