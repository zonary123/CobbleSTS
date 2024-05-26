package com.kingpixel.cobblests.utils;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.EVs;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblests.CobbleSTS;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Carlos Varas Alonso - 26/05/2024 4:23
 */
public class STSUtil {

  public static List<String> formatPokemonLore(Pokemon pokemon) {
    List<String> lore = new ArrayList<>();
    lore.add(CobbleSTS.language.getDescLevel().replace("%level%", String.valueOf(pokemon.getLevel())));
    lore.add(CobbleSTS.language.getDescShiny().replace("%shiny%", pokemon.getShiny() ?
      CobbleSTS.language.getYes() : CobbleSTS.language.getNo()));
    lore.add(CobbleSTS.language.getDescLegendary().replace("%legendary%", pokemon.isLegendary() ?
      CobbleSTS.language.getYes() : CobbleSTS.language.getNo()));
    lore.add(CobbleSTS.language.getDescIvs().replace("%ivs%", String.valueOf(getAverageIVs(pokemon))));
    lore.add(CobbleSTS.language.getDescEvs().replace("%evs%", String.valueOf(getAverageEvs(pokemon))));
    lore.add(CobbleSTS.language.getDescHappiness().replace("%happiness%", String.valueOf(pokemon.getFriendship())));
    lore.add(CobbleSTS.language.getDescGender().replace("%gender%",
      CobbleSTS.language.getGender().getOrDefault(pokemon.getGender().getShowdownName(), pokemon.getGender().getShowdownName())));
    lore.add(CobbleSTS.language.getDescForm().replace("%form%",
      CobbleSTS.language.getForm().getOrDefault(pokemon.getForm().getName(),
        pokemon.getForm().getName())));
    lore.add(CobbleSTS.language.getDescNature().replace("%nature%",
      CobbleSTS.language.getNature().getOrDefault(pokemon.getNature().getDisplayName().split("\\.")[2],
        pokemon.getNature().getDisplayName().split("\\.")[2])));
    lore.add(CobbleSTS.language.getDescAbility().replace("%ability%",
      CobbleSTS.language.getAbility().getOrDefault(pokemon.getAbility().getDisplayName().split("\\.")[2],
        pokemon.getAbility().getDisplayName().split("\\.")[2])));
    lore.add(CobbleSTS.language.getDescBall().replace("%ball%",
      CobbleSTS.language.getBall().getOrDefault(pokemon.getCaughtBall().getName().getPath(),
        pokemon.getCaughtBall().getName().getPath())));
    lore.add(CobbleSTS.language.getDescprice().replace("%price%", String.valueOf(Sell(pokemon, false, null, 0))));
    return lore;
  }

  public static int Sell(Pokemon pokemon, boolean execute, Player player, int index) {
    int price = CobbleSTS.config.getPokemon().getOrDefault(pokemon.getSpecies().getName(), CobbleSTS.config.getBase());

    // Level
    price += pokemon.getLevel() * CobbleSTS.config.getLevel();

    // Shiny
    price += pokemon.getShiny() ? CobbleSTS.config.getShiny() : 0;

    // Legendary
    price += pokemon.isLegendary() ? CobbleSTS.config.getLegendary() : 0;

    // Ivs
    price += getAverageIVs(pokemon) * CobbleSTS.config.getIvs();

    // Evs
    price += getAverageEvs(pokemon) * CobbleSTS.config.getEvs();

    // Happiness
    price += pokemon.getFriendship() * CobbleSTS.config.getHappiness();

    // Gender
    price += CobbleSTS.config.getGender().getOrDefault(pokemon.getGender().getShowdownName(),
      CobbleSTS.config.getDefaultgender());

    // Form
    price += CobbleSTS.config.getForm().getOrDefault(pokemon.getForm().getName(), CobbleSTS.config.getDefaultform());

    // Nature
    price += CobbleSTS.config.getNature().getOrDefault(pokemon.getNature().getDisplayName().split("\\.")[2],
      CobbleSTS.config.getDefaultnature());

    // Ability
    price += CobbleSTS.config.getAbility().getOrDefault(pokemon.getAbility().getDisplayName().split("\\.")[2],
      CobbleSTS.config.getDefaultability());

    // Ball
    price += CobbleSTS.config.getBall().getOrDefault(pokemon.getCaughtBall().getName().getPath(),
      CobbleSTS.config.getDefaultball());

    if (CobbleSTS.config.isDebug()) {
      showInfo(pokemon, price);
    }
    if (execute) {
      try {
        command(player, pokemon, price, index);
      } catch (NoPokemonStoreException e) {
        e.printStackTrace();
      }
    } else {
      return price;
    }


    return price;
  }

  private static void showInfo(Pokemon pokemon, int price) {
    String info = "Pokemon: " + pokemon.getSpecies().getName() + "\n" +
      "Level: " + pokemon.getLevel() + "\n" +
      "Shiny: " + (pokemon.getShiny() ? "Yes" : "No") + "\n" +
      "Legendary: " + (pokemon.isLegendary() ? "Yes" : "No") + "\n" +
      "IVs: " + getAverageIVs(pokemon) + "\n" +
      "EVs: " + getAverageEvs(pokemon) + "\n" +
      "Happiness: " + pokemon.getFriendship() + "\n" +
      "Gender: " + pokemon.getGender().getShowdownName() + "\n" +
      "Form: " + pokemon.getForm().getName() + "\n" +
      "Nature: " + pokemon.getNature().getDisplayName().split("\\.")[2] + "\n" +
      "Ability: " + pokemon.getAbility().getDisplayName().split("\\.")[2] + "\n" +
      "Ball: " + pokemon.getCaughtBall().getName().getPath() + "\n" +
      "Price: " + price;

    System.out.println(info);
  }

  private static int getAverageIVs(Pokemon pokemon) {
    IVs ivs = pokemon.getIvs();
    AtomicInteger ivsTotal = new AtomicInteger();
    ivs.forEach((ivType) -> ivsTotal.addAndGet(ivType.getValue()));
    return ivsTotal.get() / 6;
  }

  private static int getAverageEvs(Pokemon pokemon) {
    EVs evs = pokemon.getEvs();
    AtomicInteger evsTotal = new AtomicInteger();
    evs.forEach((evType) -> evsTotal.addAndGet(evType.getValue()));
    return evsTotal.get() / 6;
  }

  private static void command(Player player, Pokemon pokemon, int price, int index) throws NoPokemonStoreException {
    CommandSourceStack serverSource = CobbleSTS.server.createCommandSourceStack();
    CommandDispatcher<CommandSourceStack> disparador = CobbleSTS.server.getCommands().getDispatcher();
    PlayerPartyStore partyStorageSlot = Cobblemon.INSTANCE.getStorage().getParty(player.getUUID());

    try {
      String r = CobbleSTS.config.getEcocommand().replace("%player%", player.getGameProfile().getName()).replace(
        "%amount%", String.valueOf(price));
      ParseResults<CommandSourceStack> parseResults = disparador.parse(r, serverSource);
      disparador.execute(parseResults);
      partyStorageSlot.remove(pokemon);
      player.sendSystemMessage(TextUtil.parseHexCodes(CobbleSTS.language.getSell().replace("%player%",
        player.getGameProfile().getName()).replace("%pokemon%", pokemon.getSpecies().getName()).replace("%price%",
        String.valueOf(price))));
    } catch (CommandSyntaxException e) {
      player.sendSystemMessage(TextUtil.parseHexCodes("Error al dar el dinero"));
      e.printStackTrace();
    }
  }
}
