package com.kingpixel.cobblests.Config;

import com.google.gson.Gson;
import com.kingpixel.cobblests.CobbleSTS;
import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.Model.ItemModel;
import com.kingpixel.cobbleutils.util.Utils;
import lombok.Getter;

import java.util.ArrayList;
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
  private String colorhexnamepoke;
  private String colorhexItem;
  private String colorPrice;
  private String colorSeparator;
  private String separator;
  private String descprice;
  private String messagecooldown;
  private String readytosell;
  private List<String> pokemonLore;
  private ItemModel info;
  private ItemModel nopokemon;
  private ItemModel confirm;
  private ItemModel cancel;
  private ItemModel itemNotAllowShiny;
  private ItemModel itemNotAllowLegendary;
  private ItemModel itemBlacklisted;
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
    pokemonLore = new ArrayList<>(CobbleUtils.language.getLorepokemon());
    pokemonLore.add("Price: %price%");
    itemBlacklisted = new ItemModel("cobblemon:master_ball", "<gradient:#db2e2e:#e68c8c>Blacklisted", List.of(""));
    messagecooldown = "&7You must wait &e%day%&6d &e%hour%&6h &e%minut%&6m &e%seconds%&6s &7to sell another pokemon.";
    readytosell = "&aReady to sell in STS";
    itemNotAllowShiny = new ItemModel("cobblemon:luxury_ball", "<gradient:#db2e2e:#e68c8c>Shiny not allowed", List.of(""));
    itemNotAllowLegendary = new ItemModel("cobblemon:master_ball", "<gradient:#db2e2e:#e68c8c>Legendary not allowed", List.of(
      ""));
    info = new ItemModel("minecraft:book", "<gradient:#27b3cf:#88d4e3>Info Price", List.of(""));
    nopokemon = CobbleUtils.language.getItemNoPokemon();
    confirm = CobbleUtils.language.getItemConfirm();
    cancel = CobbleUtils.language.getItemCancel();
    gender = Map.of("N", "&7None", "M", "&bMale", "F", "&dFemale");
    form = Map.of("Galar", "Galar");
    nature = Map.of("Hardy", "Hardy");
    ability = Map.of("None", "None");
    ball = Map.of("poke_ball", "&c&lPoke &f&lBall", "master_ball", "§5Master Ball");
    Pc = CobbleUtils.language.getItemPc();
    itempreviouspage = CobbleUtils.language.getItemPrevious();
    itemnextpage = CobbleUtils.language.getItemNext();
    itemclose = CobbleUtils.language.getItemClose();
    // Items info
    ItemGender = new ItemModel(10, "minecraft:light_blue_wool", "&bGender", List.of("&7Price default: %base%",
      "&7Click to open " +
        "for more " +
        "information"), 0);
    ItemForm = new ItemModel(12, "minecraft:book", "&bForm", List.of("&7Price default: %base%", "&7Click to open for " +
      "more " +
      "information"), 0);
    ItemNature = new ItemModel(14, "cobblemon:everstone", "&bNature", List.of("&7Price default: %base%", "&7Click to " +
      "open " +
      "for more " +
      "information"), 0);
    ItemAbility = new ItemModel(16, "cobblemon:ability_capsule", "&bAbility", List.of("&7Price default: %base%",
      "&7Click to open" +
        " " +
        "for more " +
        "information"), 0);
    ItemBall = new ItemModel(29, "cobblemon:poke_ball", "&bBall", List.of("&7Price default: %base%", "&7Click to open" +
      " for " +
      "more " +
      "information"), 0);
    ItemPokemon = new ItemModel(31, "pokemon:squirtle", "&bPokemon", List.of("&7Not have price default", "&7Click to " +
      "open for more " +
      "information"), 0);
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
        info = lang.getInfo();

        colorhexnamepoke = lang.getColorhexnamepoke();
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
        pokemonLore = lang.getPokemonLore();
        itemnextpage = lang.getItemnextpage();

        ItemGender = lang.getItemGender();
        ItemForm = lang.getItemForm();
        ItemNature = lang.getItemNature();
        ItemAbility = lang.getItemAbility();
        ItemBall = lang.getItemBall();
        ItemPokemon = lang.getItemPokemon();
        colorhexItem = lang.getColorhexItem();
        colorPrice = lang.getColorPrice();
        colorSeparator = lang.getColorSeparator();
        separator = lang.getSeparator();
        itemBlacklisted = lang.getItemBlacklisted();
        itempreviouspage = lang.getItempreviouspage();
        itemclose = lang.getItemclose();
        itemnextpage = lang.getItemnextpage();
        confirm = lang.getConfirm();
        cancel = lang.getCancel();
        Pc = lang.getPc();


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
