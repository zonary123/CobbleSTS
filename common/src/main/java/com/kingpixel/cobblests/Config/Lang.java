package com.kingpixel.cobblests.Config;

import com.google.gson.Gson;
import com.kingpixel.cobblests.CobbleSTS;
import com.kingpixel.cobblests.Model.ItemModel;
import com.kingpixel.cobblests.utils.Utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author Carlos Varas Alonso - 28/04/2024 23:58
 */

public class Lang {
  private String prefix;
  private String reload;
  private String title;
  private String titleconfirm;
  private String sell;
  private String fill;
  private String yes;
  private String no;
  private String descLevel;
  private String descShiny;
  private String descLegendary;
  private String descIvs;
  private String descEvs;
  private String descHappiness;
  private String descGender;
  private String descForm;
  private String descNature;
  private String descAbility;
  private String descBall;
  private String descprice;
  private ItemModel info;
  private ItemModel nopokemon;
  private ItemModel confirm;
  private ItemModel cancel;
  private Map<String, String> gender;
  private Map<String, String> form;
  private Map<String, String> nature;
  private Map<String, String> ability;
  private Map<String, String> ball;

  public Lang() {
    prefix = "{#E39651}CobbleSpawnNotify {#EA814F}»";
    reload = "{#E39651}The plugin has been reloaded!";
    title = "{#E39651}CobbleSTS";
    titleconfirm = "{#E39651}Confirm";
    sell = "{#E39651}Sell %pokemon% for %price%?";
    fill = "minecraft:gray_stained_glass_pane";
    descLevel = "§7Level: §e%level%";
    descShiny = "§7Shiny: §e%shiny%";
    descLegendary = "§7Legendary: §e%legendary%";
    descIvs = "§7IVs: §e%ivs%";
    descEvs = "§7EVs: §e%evs%";
    descHappiness = "§7Happiness: §e%happiness%";
    descGender = "§7Gender: §e%gender%";
    descForm = "§7Form: §e%form%";
    descNature = "§7Nature: §e%nature%";
    descAbility = "§7Ability: §e%ability%";
    descBall = "§7Ball: §e%ball%";
    descprice = "§7Price: §e%price%";
    yes = "Yes";
    no = "No";
    info = new ItemModel("minecraft:book", "{#E39651}Info", List.of(""));
    nopokemon = new ItemModel("cobblemon:poke_ball", "{#E39651}No Pokemon", List.of(""));
    confirm = new ItemModel("minecraft:lime_stained_grass_pane", "{#E39651}Confirm", List.of(""));
    cancel = new ItemModel("minecraft:red_stained_grass_pane", "{#E39651}Cancel", List.of(""));
    gender = Map.of("N", "§7None", "M", "§6Male", "F", "§dFemale");
    form = Map.of("Galar", "{#E396}Galar");
    nature = Map.of("Hardy", "{#E396}Hardy");
    ability = Map.of("None", "{#E396}None");
    ball = Map.of("poke_ball", "{#E396}Poke Ball");

  }

  public String getYes() {
    return yes;
  }

  public String getNo() {
    return no;
  }

  public String getDescLevel() {
    return descLevel;
  }

  public ItemModel getConfirm() {
    return confirm;
  }

  public ItemModel getCancel() {
    return cancel;
  }

  public String getDescprice() {
    return descprice;
  }

  public String getTitle() {
    return title;
  }

  public String getPrefix() {
    return prefix;
  }

  public String getReload() {
    return reload;
  }

  public String getFill() {
    return fill;
  }

  public String getSell() {
    return sell;
  }

  public ItemModel getInfo() {
    return info;
  }

  public ItemModel getNopokemon() {
    return nopokemon;
  }

  public Map<String, String> getGender() {
    return gender;
  }

  public Map<String, String> getForm() {
    return form;
  }

  public Map<String, String> getNature() {
    return nature;
  }

  public Map<String, String> getAbility() {
    return ability;
  }

  public Map<String, String> getBall() {
    return ball;
  }

  public String getTitleconfirm() {
    return titleconfirm;
  }

  public String getDescShiny() {
    return descShiny;
  }

  public String getDescLegendary() {
    return descLegendary;
  }

  public String getDescIvs() {
    return descIvs;
  }

  public String getDescEvs() {
    return descEvs;
  }

  public String getDescHappiness() {
    return descHappiness;
  }

  public String getDescGender() {
    return descGender;
  }

  public String getDescForm() {
    return descForm;
  }

  public String getDescNature() {
    return descNature;
  }

  public String getDescAbility() {
    return descAbility;
  }

  public String getDescBall() {
    return descBall;
  }

  public void init() {
    CompletableFuture<Boolean> futureRead = Utils.readFileAsync(CobbleSTS.path + "lang/", CobbleSTS.config.getLang() + ".json",
      el -> {
        Gson gson = Utils.newGson();
        Lang lang = gson.fromJson(el, Lang.class);
        prefix = lang.getPrefix();
        reload = lang.getReload();
        title = lang.getTitle();
        sell = lang.getSell();
        fill = lang.getFill();
        yes = lang.getYes();
        no = lang.getNo();
        info = lang.getInfo();
        confirm = lang.getConfirm();
        cancel = lang.getCancel();
        descLevel = lang.getDescLevel();
        descShiny = lang.getDescShiny();
        descLegendary = lang.getDescLegendary();
        descIvs = lang.getDescIvs();
        descEvs = lang.getDescEvs();
        descHappiness = lang.getDescHappiness();
        descGender = lang.getDescGender();
        descForm = lang.getDescForm();
        descNature = lang.getDescNature();
        descAbility = lang.getDescAbility();
        descBall = lang.getDescBall();
        descprice = lang.getDescprice();
        nopokemon = lang.getNopokemon();
        titleconfirm = lang.getTitleconfirm();
        gender = lang.getGender();
        form = lang.getForm();
        nature = lang.getNature();
        ability = lang.getAbility();
        ball = lang.getBall();
        String data = gson.toJson(this);
        CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(CobbleSTS.path + "lang/", CobbleSTS.config.getLang() + ".json",
          data);
        if (!futureWrite.join()) {
          CobbleSTS.LOGGER.fatal("Could not write lang.json file for CobbleHunt.");
        }
      });

    if (!futureRead.join()) {
      CobbleSTS.LOGGER.info("No lang.json file found for" + CobbleSTS.MOD_NAME + ". Attempting to generate one.");
      Gson gson = Utils.newGson();
      String data = gson.toJson(this);
      CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(CobbleSTS.path + "lang/", CobbleSTS.config.getLang() + ".json",
        data);

      if (!futureWrite.join()) {
        CobbleSTS.LOGGER.fatal("Could not write lang.json file for CobbleHunt.");
      }
    }
  }

}
