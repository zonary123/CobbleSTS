package com.kingpixel.ultrasts.Config;

import com.google.gson.Gson;
import com.kingpixel.ultrasts.UltraSTS;
import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.Model.DataBaseConfig;
import com.kingpixel.cobbleutils.Model.EconomyUse;
import com.kingpixel.cobbleutils.Model.PokemonBlackList;
import com.kingpixel.cobbleutils.Model.PokemonFormula;
import com.kingpixel.cobbleutils.util.Utils;
import com.kingpixel.cobbleutils.util.economys.ImpactorEconomy;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


/**
 * @author Carlos Varas Alonso - 29/04/2024 0:14
 */
@Getter
public class Config {
  private boolean debug;
  private String lang;
  private DataBaseConfig database;
  private boolean releasePokemon;
  private boolean notifyReady;
  private BigDecimal limitPrice;
  private BigDecimal lostPriceForRelease;
  private EconomyUse economyUse;
  private int alertCooldown;
  private int cooldown;
  private Map<String, Integer> cooldowns;
  private PokemonFormula formula;
  private PokemonBlackList blacklist;

  public Config() {
    debug = false;
    database = new DataBaseConfig(UltraSTS.MOD_ID);
    releasePokemon = false;
    notifyReady = true;
    limitPrice = BigDecimal.valueOf(100000);
    economyUse = new EconomyUse(ImpactorEconomy.IDENTIFY, "");
    lostPriceForRelease = BigDecimal.valueOf(25);
    lang = "en";
    alertCooldown = 5;
    cooldown = 30;
    cooldowns = Map.of(
      "cooldown.vip", 20
    );
    formula = new PokemonFormula();
    blacklist = new PokemonBlackList();
  }


  public void init() {
    CompletableFuture<Boolean> futureRead = Utils.readFileAsync(UltraSTS.PATH, "config.json",
      el -> {
        Gson gson = Utils.newGson();
        UltraSTS.config = gson.fromJson(el, Config.class);
        String data = gson.toJson(UltraSTS.config);
        CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(UltraSTS.PATH, "config.json",
          data);
        if (!futureWrite.join()) {
          CobbleUtils.LOGGER.info(UltraSTS.MOD_ID, "Could not write config.json file for CobbleSTS.");
        }
      });

    if (!futureRead.join()) {
      CobbleUtils.LOGGER.info(UltraSTS.MOD_ID, "No config.json file found for" + UltraSTS.MOD_NAME + ". Attempting to generate one.");
      Gson gson = Utils.newGson();
      UltraSTS.config = this;
      String data = gson.toJson(UltraSTS.config);
      CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(UltraSTS.PATH, "config.json",
        data);

      if (!futureWrite.join()) {
        CobbleUtils.LOGGER.info(UltraSTS.MOD_ID, "Could not write config.json file for CobbleSTS.");
      } else {
        CobbleUtils.LOGGER.info(UltraSTS.MOD_ID, "Config.json file created successfully.");
      }
    }

  }
}
