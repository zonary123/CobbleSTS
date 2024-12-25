package com.kingpixel.cobblests.Config;

import com.google.gson.Gson;
import com.kingpixel.cobblests.CobbleSTS;
import com.kingpixel.cobbleutils.util.Utils;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


/**
 * @author Carlos Varas Alonso - 29/04/2024 0:14
 */
@Getter
public class Config {
  private boolean debug;
  private boolean useCobbleUtilsItems;
  private boolean releasePokemon;
  private boolean notifyReady;
  private BigDecimal lostPriceForRelease;
  private String lang;
  private String currency;
  private int guiinforows;
  private boolean allowshiny;
  private boolean allowlegendary;
  private boolean havecooldown;
  private boolean hiperinfo;
  private int cooldown;
  private int decimals;
  private Map<String, Integer> cooldowns;
  private String priceFormula;
  private double shinyMultiplier;
  private double legendaryMultiplier;
  private double MythicalMultiplier;
  private double UltraBeastMultiplier;
  private BigDecimal limitPrice;
  private BigDecimal base;
  private BigDecimal shiny;
  private BigDecimal legendary;
  private BigDecimal ah;
  private BigDecimal defaultgender;
  private BigDecimal defaultform;
  private BigDecimal defaultnature;
  private BigDecimal defaultability;
  private BigDecimal defaultball;
  private List<String> islegends;
  private List<String> blacklisted;
  private Map<String, BigDecimal> gender;
  private Map<String, BigDecimal> form;
  private Map<String, BigDecimal> nature;
  private Map<String, BigDecimal> ability;
  private Map<String, BigDecimal> ball;
  private Map<String, BigDecimal> label;
  private Map<String, BigDecimal> pokemon;

  public Config() {
    debug = false;
    useCobbleUtilsItems = true;
    releasePokemon = false;
    notifyReady = true;
    priceFormula = "base + (level * 1) + priceShiny + (totalIvs * 1) + (averageIvs * 1) + (totalEvs " +
      "* 1) + " +
      "(averageEvs * 1)" +
      " + label + " +
      "(happiness * 1) " +
      "+ gender + form + " +
      "nature + " +
      "ability + ball" +
      " + (((catchRate / 100) * level" +
      " + log" +
      "(totalIvs)" +
      "/2 + " +
      "sqrt" +
      "(totalEvs) - 1) * " +
      "shinyMultiplier * " +
      "legendaryMultiplier * mythicalMultiplier * ultraBeastMultiplier)";
    decimals = 0;
    lostPriceForRelease = BigDecimal.valueOf(25);
    lang = "en";
    currency = "dollars";
    guiinforows = 6;
    havecooldown = true;
    allowshiny = true;
    allowlegendary = true;
    hiperinfo = true;
    cooldown = 30;
    cooldowns = Map.of(
      "group.vip", 20,
      "group.vip+", 10,
      "group.vip++", 5
    );
    limitPrice = BigDecimal.valueOf(0);
    base = BigDecimal.valueOf(0);
    shiny = BigDecimal.valueOf(0);
    legendary = BigDecimal.valueOf(0);
    ah = BigDecimal.valueOf(0);
    defaultgender = BigDecimal.valueOf(0);
    defaultform = BigDecimal.valueOf(0);
    defaultnature = BigDecimal.valueOf(0);
    defaultability = BigDecimal.valueOf(0);
    defaultball = BigDecimal.valueOf(0);
    shinyMultiplier = 1.0;
    legendaryMultiplier = 1.0;
    MythicalMultiplier = 1.0;
    UltraBeastMultiplier = 1.0;
    blacklisted = List.of("Magikarp");
    islegends = List.of("Magikarp");
    gender = Map.of("M", BigDecimal.ZERO, "F", BigDecimal.ZERO, "N", BigDecimal.ZERO);
    form = Map.of("Galar", BigDecimal.ZERO);
    nature = Map.of("Hardy", BigDecimal.ZERO);
    ability = Map.of("None", BigDecimal.ZERO);
    ball = Map.of("poke_ball", BigDecimal.ZERO);
    pokemon = Map.of("Magikarp", BigDecimal.valueOf(0));
    label = Map.of("gen1", BigDecimal.ZERO);
  }


  public void init() {
    CompletableFuture<Boolean> futureRead = Utils.readFileAsync(CobbleSTS.path, "config.json",
      el -> {
        Gson gson = Utils.newGson();
        Config config = gson.fromJson(el, Config.class);
        debug = config.isDebug();
        useCobbleUtilsItems = config.isUseCobbleUtilsItems();
        islegends = config.getIslegends();
        lang = config.getLang();
        guiinforows = config.getGuiinforows();
        havecooldown = config.isHavecooldown();
        cooldown = config.getCooldown();
        cooldowns = config.getCooldowns();
        allowshiny = config.isAllowshiny();
        decimals = config.getDecimals();
        blacklisted = config.getBlacklisted();
        allowlegendary = config.isAllowlegendary();
        notifyReady = config.isNotifyReady();
        base = config.getBase();
        priceFormula = config.getPriceFormula();
        shiny = config.getShiny();
        legendary = config.getLegendary();
        defaultgender = config.getDefaultgender();
        defaultform = config.getDefaultform();
        defaultnature = config.getDefaultnature();
        defaultability = config.getDefaultability();
        defaultball = config.getDefaultball();
        gender = config.getGender();
        form = config.getForm();
        nature = config.getNature();
        ability = config.getAbility();
        ball = config.getBall();
        pokemon = config.getPokemon();
        currency = config.getCurrency();
        hiperinfo = config.isHiperinfo();
        releasePokemon = config.isReleasePokemon();
        limitPrice = config.getLimitPrice();
        lostPriceForRelease = config.getLostPriceForRelease();
        shinyMultiplier = config.getShinyMultiplier();
        legendaryMultiplier = config.getLegendaryMultiplier();
        MythicalMultiplier = config.getMythicalMultiplier();
        UltraBeastMultiplier = config.getUltraBeastMultiplier();
        label = config.getLabel();
        ah = config.getAh();

        String data = gson.toJson(this);
        CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(CobbleSTS.path, "config.json",
          data);
        if (!futureWrite.join()) {
          CobbleSTS.LOGGER.fatal("Could not write lang.json file for CobbleHunt.");
        }
      });

    if (!futureRead.join()) {
      CobbleSTS.LOGGER.info("No config.json file found for" + CobbleSTS.MOD_NAME + ". Attempting to generate one.");
      Gson gson = Utils.newGson();
      String data = gson.toJson(this);
      CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(CobbleSTS.path, "config.json",
        data);

      if (!futureWrite.join()) {
        CobbleSTS.LOGGER.fatal("Could not write config.json file for CobbleHunt.");
      }
    }

  }
}
