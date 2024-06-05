package com.kingpixel.fabric.cobblests;

import com.kingpixel.cobblests.CobbleSTS;
import net.fabricmc.api.ModInitializer;


public class CobbleSTSFabric implements ModInitializer {

  @Override
  public void onInitialize() {
    CobbleSTS.init();
  }
}