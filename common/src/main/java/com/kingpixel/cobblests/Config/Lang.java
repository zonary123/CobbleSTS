package com.kingpixel.cobblests.Config;

import com.google.gson.Gson;
import com.kingpixel.cobblests.CobbleSTS;
import com.kingpixel.cobblests.Model.ItemModel;
import com.kingpixel.cobblests.utils.Utils;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author Carlos Varas Alonso - 28/04/2024 23:58
 */
@Getter
public class Lang {
  private String prefix;
  private String reload;
  private String title;
  private String titleconfirm;
  private String titlePc;
  private String titleInfo;
  private String titleGender;
  private String titleForm;
  private String titleNature;
  private String titleAbility;
  private String titleBall;
  private String titlePokemon;
  private String titleLegends;
  private String sell;
  private String fill;
  private String yes;
  private String no;
  private String colorhexnamepoke;
  private String colorhexItem;
  private String colorPrice;
  private String colorSeparator;
  private String separator;
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
  private String messagecooldown;
  private String readytosell;
  private ItemModel info;
  private ItemModel nopokemon;
  private ItemModel confirm;
  private ItemModel cancel;
  private ItemModel itemNotAllowShiny;
  private ItemModel itemNotAllowLegendary;
  private ItemModel itempreviouspage;
  private ItemModel itemclose;
  private ItemModel itemnextpage;
  private ItemModel Pc;
  // Items info
  private ItemModel ItemGender;
  private ItemModel ItemForm;
  private ItemModel ItemNature;
  private ItemModel ItemAbility;
  private ItemModel ItemBall;
  private ItemModel ItemPokemon;
  private ItemModel ItemLegends;
  private Map<String, String> gender;
  private Map<String, String> form;
  private Map<String, String> nature;
  private Map<String, String> ability;
  private Map<String, String> ball;

  public Lang() {
    prefix = "<gradient:#27b3cf:#88d4e3>CobbleSTS <#EA814F>»";
    reload = "%prefix% <gradient:#27b3cf:#88d4e3>The plugin has been reloaded!";
    title = "<gradient:#27b3cf:#88d4e3>CobbleSTS";
    titleconfirm = "<gradient:#3ec758:#a2f2b2>Confirm";
    titlePc = "§bPC";
    titleInfo = "§bInfo";
    titleGender = "§bGender";
    titleForm = "§bForm";
    titleNature = "§bNature";
    titleAbility = "§bAbility";
    titleBall = "§bBall";
    titlePokemon = "§bPokemon";
    separator = " &6» ";
    titleLegends = "§bLegends";
    sell = "<gradient:#27b3cf:#88d4e3>Sell %pokemon% for %price%$";
    fill = "minecraft:gray_stained_glass_pane";
    colorhexnamepoke = "<gradient:#27b3cf:#88d4e3>";
    colorhexItem = "<gradient:#27b3cf:#88d4e3>";
    colorPrice = "<gradient:#27b3cf:#88d4e3>";
    colorSeparator = "<gradient:#27b3cf:#88d4e3>";
    descLevel = "&7Level: &e%level%";
    descShiny = "&7Shiny: &e%shiny%";
    descLegendary = "&7Legendary: &e%legendary%";
    descIvs = "&7IVs: &e%ivs%";
    descEvs = "&7EVs: &e%evs%";
    descHappiness = "&7Happiness: &e%happiness%";
    descGender = "&7Gender: &e%gender%";
    descForm = "&7Form: &e%form%";
    descNature = "&7Nature: &e%nature%";
    descAbility = "&7Ability: &e%ability%";
    descBall = "&7Ball: &e%ball%";
    descprice = "&7Price: &e%price%";
    yes = "Yes";
    no = "No";
    messagecooldown = "&7You must wait &e%day%&6d &e%hour%&6h &e%minut%&6m &e%seconds%&6s &7to sell another pokemon.";
    readytosell = "&aReady to sell in STS";
    itemNotAllowShiny = new ItemModel("cobblemon:luxury_ball", "<gradient:#db2e2e:#e68c8c>Shiny not allowed", List.of(""));
    itemNotAllowLegendary = new ItemModel("cobblemon:master_ball", "<gradient:#db2e2e:#e68c8c>Legendary not allowed", List.of(
      ""));
    info = new ItemModel("minecraft:book", "<gradient:#27b3cf:#88d4e3>Info Price", List.of(""));
    nopokemon = new ItemModel("cobblemon:poke_ball", "<gradient:#db2e2e:#e68c8c>Empty slot", List.of(""));
    confirm = new ItemModel("minecraft:lime_stained_glass_pane", "<gradient:#3ec758:#a2f2b2>Confirm", List.of(""));
    cancel = new ItemModel("minecraft:red_stained_glass_pane", "<gradient:#db2e2e:#e68c8c>Cancel", List.of(""));
    gender = Map.of("N", "&7None", "M", "&bMale", "F", "&dFemale");
    form = Map.of("Galar", "Galar");
    nature = Map.of("Hardy", "Hardy");
    ability = Map.of("None", "None");
    ball = Map.of("poke_ball", "&c&lPoke &f&lBall", "master_ball", "§5Master Ball");
    Pc = new ItemModel("cobblemon:pc", "&bPc", List.of(""));
    itempreviouspage = new ItemModel("minecraft:arrow", "&7Previous Page", List.of("&7Click to go to the previous " +
      "page"));
    itemnextpage = new ItemModel("minecraft:arrow", "&7Next Page", List.of("&7Click to go to the next page"));
    itemclose = new ItemModel("minecraft:barrier", "&cClose", List.of("&7Click to close the menu"));
    // Items info
    ItemGender = new ItemModel(10, "minecraft:light_blue_wool", "&bGender", List.of("&7Price default: %base%",
      "&7Click to open " +
        "for more " +
        "information"));
    ItemForm = new ItemModel(12, "minecraft:book", "&bForm", List.of("&7Price default: %base%", "&7Click to open for " +
      "more " +
      "information"));
    ItemNature = new ItemModel(14, "cobblemon:everstone", "&bNature", List.of("&7Price default: %base%", "&7Click to " +
      "open " +
      "for more " +
      "information"));
    ItemAbility = new ItemModel(16, "cobblemon:ability_capsule", "&bAbility", List.of("&7Price default: %base%",
      "&7Click to open" +
        " " +
        "for more " +
        "information"));
    ItemBall = new ItemModel(29, "cobblemon:poke_ball", "&bBall", List.of("&7Price default: %base%", "&7Click to open" +
      " for " +
      "more " +
      "information"));
    ItemPokemon = new ItemModel(31, "pokemon:squirtle", "&bPokemon", List.of("&7Not have price default", "&7Click to " +
      "open for more " +
      "information"));
    ItemLegends = new ItemModel(33, "pokemon:squirtle", "&bLegends", List.of("&7Not have price default", "&7Click to " +
      "open for more " +
      "information"));
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
        colorhexnamepoke = lang.getColorhexnamepoke();
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
        titleInfo = lang.getTitleInfo();
        titleGender = lang.getTitleGender();
        titleForm = lang.getTitleForm();
        titleNature = lang.getTitleNature();
        titleAbility = lang.getTitleAbility();
        titleBall = lang.getTitleBall();
        titlePokemon = lang.getTitlePokemon();
        titleLegends = lang.getTitleLegends();
        gender = lang.getGender();
        form = lang.getForm();
        nature = lang.getNature();
        ability = lang.getAbility();
        ball = lang.getBall();
        itemNotAllowShiny = lang.getItemNotAllowShiny();
        itemNotAllowLegendary = lang.getItemNotAllowLegendary();
        readytosell = lang.getReadytosell();
        messagecooldown = lang.getMessagecooldown();
        titlePc = lang.getTitlePc();
        itempreviouspage = lang.getItempreviouspage();
        itemclose = lang.getItemclose();
        itemnextpage = lang.getItemnextpage();
        Pc = lang.getPc();
        ItemGender = lang.getItemGender();
        ItemForm = lang.getItemForm();
        ItemNature = lang.getItemNature();
        ItemAbility = lang.getItemAbility();
        ItemBall = lang.getItemBall();
        ItemPokemon = lang.getItemPokemon();
        ItemLegends = lang.getItemLegends();
        colorhexItem = lang.getColorhexItem();
        colorPrice = lang.getColorPrice();
        colorSeparator = lang.getColorSeparator();

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
