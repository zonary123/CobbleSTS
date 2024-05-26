package com.kingpixel.fabric.cobblests;

import com.kingpixel.cobblests.CobbleSTS;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;


public class FabricModExample implements ModInitializer {

  @Override
  public void onInitialize() {
    CobbleSTS.init();
    ServerLifecycleEvents.SERVER_STARTED.register(t -> CobbleSTS.load());
    ServerWorldEvents.LOAD.register((t, e) -> CobbleSTS.server = t);
  }
}