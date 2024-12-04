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
  private boolean useCobbleUtilsItems;
  private boolean releasePokemon;
  private String lang;
  private String currency;
  private int guiinforows;
  private boolean allowshiny;
  private boolean allowlegendary;
  private boolean havecooldown;
  private boolean hiperinfo;
  private int cooldown;
  private BigDecimal base;
  private BigDecimal level;
  private BigDecimal shiny;
  private BigDecimal legendary;
  private BigDecimal ivs;
  private BigDecimal evs;
  private BigDecimal happiness;
  private BigDecimal defaultgender;
  private BigDecimal defaultform;
  private BigDecimal defaultnature;
  private BigDecimal defaultability;
  private BigDecimal defaultball;
  private List<String> islegends;
  private List<String> blacklisted;
  private Map<String, BigDecimal> legends;
  private Map<String, BigDecimal> gender;
  private Map<String, BigDecimal> form;
  private Map<String, BigDecimal> nature;
  private Map<String, BigDecimal> ability;
  private Map<String, BigDecimal> ball;
  private Map<String, BigDecimal> pokemon;

  public Config() {
    useCobbleUtilsItems = true;
    releasePokemon = false;
    lang = "en";
    currency = "dollars";
    guiinforows = 6;
    havecooldown = true;
    allowshiny = true;
    allowlegendary = true;
    hiperinfo = true;
    cooldown = 30;
    base = BigDecimal.valueOf(500);
    level = BigDecimal.valueOf(250);
    shiny = BigDecimal.valueOf(1000);
    legendary = BigDecimal.valueOf(5000);
    ivs = BigDecimal.valueOf(100);
    evs = BigDecimal.valueOf(100);
    happiness = BigDecimal.valueOf(100);
    defaultgender = BigDecimal.valueOf(0);
    defaultform = BigDecimal.valueOf(0);
    defaultnature = BigDecimal.valueOf(0);
    defaultability = BigDecimal.valueOf(0);
    defaultball = BigDecimal.valueOf(0);
    blacklisted = List.of("Magikarp");
    islegends = List.of("Magikarp");
    legends = Map.of("Articuno", BigDecimal.valueOf(10000));
    gender = Map.of("M", BigDecimal.ZERO, "F", BigDecimal.ZERO, "N", BigDecimal.ZERO);
    form = Map.of("Galar", BigDecimal.ZERO);
    nature = Map.of("Hardy", BigDecimal.ZERO);
    ability = Map.of("None", BigDecimal.ZERO);
    ball = Map.of("poke_ball", BigDecimal.ZERO);
    pokemon = Map.of("Magikarp", BigDecimal.valueOf(100));
  }


  public void init() {
    CompletableFuture<Boolean> futureRead = Utils.readFileAsync(CobbleSTS.path, "config.json",
      el -> {
        Gson gson = Utils.newGson();
        Config config = gson.fromJson(el, Config.class);
        useCobbleUtilsItems = config.isUseCobbleUtilsItems();
        islegends = config.getIslegends();
        lang = config.getLang();
        guiinforows = config.getGuiinforows();
        havecooldown = config.isHavecooldown();
        cooldown = config.getCooldown();
        allowshiny = config.isAllowshiny();
        blacklisted = config.getBlacklisted();
        allowlegendary = config.isAllowlegendary();
        legends = config.getLegends();
        base = config.getBase();
        level = config.getLevel();
        shiny = config.getShiny();
        legendary = config.getLegendary();
        ivs = config.getIvs();
        evs = config.getEvs();
        happiness = config.getHappiness();
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
