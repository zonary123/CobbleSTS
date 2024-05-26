package com.kingpixel.forge.cobblests;

import com.kingpixel.cobblests.CobbleSTS;
import com.kingpixel.cobblests.command.CommandTree;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(CobbleSTS.MOD_ID)
public class ForgeModExample {

  public ForgeModExample() {
    CobbleSTS.init();
    MinecraftForge.EVENT_BUS.register(this);
  }

  @SubscribeEvent
  public void registerCommands(RegisterCommandsEvent event) {
    CommandTree.register(event.getDispatcher());
  }

  @SubscribeEvent
  public void serverStartedEvent(ServerStartedEvent event) {
    CobbleSTS.load();
  }

  @SubscribeEvent
  public void worldLoadEvent(LevelEvent.Load event) {
    CobbleSTS.server = event.getLevel().getServer();
  }
}
