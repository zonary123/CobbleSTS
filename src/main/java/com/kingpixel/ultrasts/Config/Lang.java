package com.kingpixel.ultrasts.Config;

import com.google.gson.Gson;
import com.kingpixel.ultrasts.UltraSTS;
import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.ui.ConfirmMenu;
import com.kingpixel.cobbleutils.ui.PartyPcMenu;
import com.kingpixel.cobbleutils.util.Utils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Carlos Varas Alonso - 28/04/2024 23:58
 */
@Getter
public class Lang {
  private String prefix;
  private String reload;
  private String readytosell;
  private String cooldownMessage;
  private String messageSell;
  private String messagePriceIsZero;
  private String messageInBattle;
  private List<String> pokemonLore;
  private PartyPcMenu partyPcMenu;
  private ConfirmMenu confirmMenu;

  public Lang() {
    prefix = "<gradient:#27b3cf:#88d4e3>CobbleSTS <#EA814F>»";
    reload = "%prefix% <gradient:#27b3cf:#88d4e3>The plugin has been reloaded!";
    cooldownMessage = "%prefix% <gradient:#27b3cf:#88d4e3>You have to wait %time% before you can use this command again.";
    messageSell = "%prefix% <gradient:#27b3cf:#88d4e3>You have sold the pokemon %pokemon% for %price%!";
    messagePriceIsZero = "%prefix% <gradient:#27b3cf:#88d4e3>The price of the pokemon is 0, you can release it.";
    pokemonLore = new ArrayList<>(CobbleUtils.language.getLorepokemon());
    pokemonLore.add("&7Price: &e%price%");
    readytosell = "&aReady to sell in STS";
    messageInBattle = "%prefix% <gradient:#27b3cf:#88d4e3>You cannot sell a pokemon that is in battle.";
    partyPcMenu = new PartyPcMenu();
    confirmMenu = new ConfirmMenu();
  }

  public void init() {
    CompletableFuture<Boolean> futureRead = Utils.readFileAsync(UltraSTS.PATH + "lang/", UltraSTS.config.getLang() + ".json",
      el -> {
        Gson gson = Utils.newGson();
        UltraSTS.language = gson.fromJson(el, Lang.class);
        String data = gson.toJson(UltraSTS.language);
        CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(UltraSTS.PATH + "lang/", UltraSTS.config.getLang() + ".json",
          data);
        if (!futureWrite.join()) {
          CobbleUtils.LOGGER.info(UltraSTS.MOD_ID, "Could not write lang.json file.");
        }
      });

    if (!futureRead.join()) {
      CobbleUtils.LOGGER.info(UltraSTS.MOD_ID, "Could not read lang.json file.");
      Gson gson = Utils.newGson();
      UltraSTS.language = this;
      String data = gson.toJson(UltraSTS.language);
      CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(UltraSTS.PATH + "lang/", UltraSTS.config.getLang() + ".json",
        data);

      if (!futureWrite.join()) {
        CobbleUtils.LOGGER.info(UltraSTS.MOD_ID, "Could not write lang.json file.");
      }
    }
  }

}
