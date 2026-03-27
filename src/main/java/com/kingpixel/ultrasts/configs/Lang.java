package com.kingpixel.ultrasts.configs;

import com.kingpixel.cobbleutils.ui.ConfirmMenu;
import com.kingpixel.cobbleutils.ui.PartyPcMenu;
import com.kingpixel.cobbleutils.util.UtilsFile;
import com.kingpixel.ultrasts.UltraSTS;
import com.kingpixel.ultrasts.gui.*;
import lombok.Data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Data
public class Lang {
  private String prefix = "&7[&6UltraSTS&7] &r";
  private String selected = "&7[&aSelected&7]";
  private String cooldownMessage = "%prefix% &cYou are on cooldown for %time%!";
  private String notificationSTSAvailable = "&aYou have some STS available!";
  private String notificationSelling = "%prefix% &6You are sold %pokemon% STS for %price%!";
  private String notificationSellingMulti = "%prefix% &6You sold %amount% Pokémon for %price%!";
  private List<String> pokemonLore = List.of(
    "%lorepokemon%",
    "&7Sell this pokemon for STS!",
    "&7Value: %price% STS"
  );
  // GUI
  private Menu menu = new Menu();
  private STSCategoryMenu stsCategoryMenu = new STSCategoryMenu();
  private ProfileMenu profileMenu = new ProfileMenu();
  private PartyPcMenu partyPcMenu = new PartyPcMenu();
  private ConfirmMenu confirmMenu = new ConfirmMenu();
  private SelectLeaderBoardMenu selectLeaderBoardMenu = new SelectLeaderBoardMenu();
  private LeaderBoardMenu leaderBoardMenu = new LeaderBoardMenu();
  private STSSellMenu stsSellMenu = new STSSellMenu();

  public static void init() {
    String language = UltraSTS.config.getLang();
    Path file = UltraSTS.getPath().resolve("lang").resolve(language + ".json");
    try {
      Lang lang = UtilsFile.read(file, Lang.class);
      if (lang == null) lang = new Lang();
      lang.fix();
      UltraSTS.lang = lang;
      UtilsFile.writeAsync(file, lang);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void fix() {

  }
}
