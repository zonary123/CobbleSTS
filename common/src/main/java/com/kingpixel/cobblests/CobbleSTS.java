package com.kingpixel.cobblests;

import ca.landonjw.gooeylibs2.api.tasks.Task;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.kingpixel.cobblests.Config.Config;
import com.kingpixel.cobblests.Config.Lang;
import com.kingpixel.cobblests.command.CommandTree;
import com.kingpixel.cobblests.manager.STSManager;
import com.kingpixel.cobblests.utils.STSUtil;
import com.kingpixel.cobbleutils.util.PlayerUtils;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import kotlin.Unit;
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
  public static STSManager manager = new STSManager();
  private static Task broadcastTask;

  public static void init() {
    LOGGER.info("Initializing " + MOD_NAME);
    events();
  }

  public static void load() {
    files();
    manager.init();
    tasks();
  }

  private static void events() {
    files();
    CommandRegistrationEvent.EVENT.register((dispatcher, registry, selection) -> CommandTree.register(dispatcher));
    LifecycleEvent.SERVER_STARTED.register(server -> load());
    PlayerEvent.PLAYER_JOIN.register(player -> manager.addPlayer(player));
    LifecycleEvent.SERVER_LEVEL_LOAD.register(level -> server = level.getServer());
    LifecycleEvent.SERVER_STOPPING.register((server) -> {
      LOGGER.info("Stopping " + MOD_NAME);
    });

    CobblemonEvents.POKEMON_RELEASED_EVENT_POST.subscribe(Priority.NORMAL, evt -> {
      if (CobbleSTS.config.isReleasePokemon()) {
        STSUtil.Sell(evt.getPokemon(), true, evt.getPlayer(), true);
      }
      return Unit.INSTANCE;
    });
  }


  private static void files() {
    language.init();
    config.init();
  }

  private static void tasks() {
    if (!config.isNotifyReady()) return;

    if (broadcastTask != null) broadcastTask.setExpired();

    broadcastTask = Task.builder()
      .execute(() -> {
        if (server != null) {
          server.getPlayerManager().getPlayerList().forEach(player -> {
            if (manager.hasCooldownEnded(player) && !manager.getUserInfo().get(player.getUuid()).isMessagesend()) {
              manager.getUserInfo().get(player.getUuid()).setMessagesend(true);
              PlayerUtils.sendMessage(player,
                language.getReadytosell(),
                CobbleSTS.language.getPrefix());
            }
          });
        }
      })
      .interval(20L * 60L)
      .build();
  }
}
