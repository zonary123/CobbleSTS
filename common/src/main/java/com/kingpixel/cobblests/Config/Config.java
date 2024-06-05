package com.kingpixel.cobblests.Config;

import com.google.gson.Gson;
import com.kingpixel.cobblests.CobbleSTS;
import com.kingpixel.cobblests.utils.Utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


/**
 * @author Carlos Varas Alonso - 29/04/2024 0:14
 */
public class Config {
  private String lang;
  private String ecocommand;
  private boolean allowshiny;
  private boolean allowlegendary;
  private boolean havecooldown;
  private int cooldown;
  private int base;
  private int level;
  private int shiny;
  private int legendary;
  private int ivs;
  private int evs;
  private int happiness;
  private int defaultgender;
  private int defaultform;
  private int defaultnature;
  private int defaultability;
  private int defaultball;
  private List<String> islegends;
  private Map<String, Integer> legends;
  private Map<String, Integer> gender;
  private Map<String, Integer> form;
  private Map<String, Integer> nature;
  private Map<String, Integer> ability;
  private Map<String, Integer> ball;
  private Map<String, Integer> pokemon;

  public Config() {
    lang = "en";
    ecocommand = "eco give %player% %price%";
    havecooldown = true;
    cooldown = 30;
    allowshiny = true;
    allowlegendary = true;
    base = 500;
    level = 250;
    shiny = 1000;
    legendary = 5000;
    ivs = 100;
    evs = 100;
    happiness = 100;
    defaultgender = 0;
    defaultform = 0;
    defaultnature = 0;
    defaultability = 0;
    defaultball = 0;
    islegends = List.of("Magikarp");
    legends = Map.of("Articuno", 10000);
    gender = Map.of("M", 0, "F", 0, "N", 0);
    form = Map.of("Galar", 0);
    nature = Map.of("Hardy", 0);
    ability = Map.of("None", 0);
    ball = Map.of("poke_ball", 0);
    pokemon = Map.of("Magikarp", 100);
  }

  public List<String> getIslegends() {
    return islegends;
  }

  public String getEcocommand() {
    return ecocommand;
  }

  public String getLang() {
    return lang;
  }

  public int getBase() {
    return base;
  }

  public int getLevel() {
    return level;
  }

  public int getShiny() {
    return shiny;
  }

  public int getLegendary() {
    return legendary;
  }

  public int getIvs() {
    return ivs;
  }

  public int getEvs() {
    return evs;
  }

  public int getHappiness() {
    return happiness;
  }

  public int getDefaultgender() {
    return defaultgender;
  }

  public int getDefaultform() {
    return defaultform;
  }

  public int getDefaultnature() {
    return defaultnature;
  }

  public int getDefaultability() {
    return defaultability;
  }

  public int getDefaultball() {
    return defaultball;
  }

  public Map<String, Integer> getGender() {
    return gender;
  }

  public Map<String, Integer> getForm() {
    return form;
  }

  public Map<String, Integer> getNature() {
    return nature;
  }

  public Map<String, Integer> getAbility() {
    return ability;
  }

  public Map<String, Integer> getBall() {
    return ball;
  }

  public Map<String, Integer> getPokemon() {
    return pokemon;
  }

  public boolean isHavecooldown() {
    return havecooldown;
  }

  public int getCooldown() {
    return cooldown;
  }

  public boolean isAllowshiny() {
    return allowshiny;
  }

  public boolean isAllowlegendary() {
    return allowlegendary;
  }

  public Map<String, Integer> getLegends() {
    return legends;
  }

  public void init() {
    CompletableFuture<Boolean> futureRead = Utils.readFileAsync(CobbleSTS.path, "config.json",
      el -> {
        Gson gson = Utils.newGson();
        Config config = gson.fromJson(el, Config.class);
        islegends = config.getIslegends();
        lang = config.getLang();
        havecooldown = config.isHavecooldown();
        cooldown = config.getCooldown();
        ecocommand = config.getEcocommand();
        allowshiny = config.isAllowshiny();
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
