package com.kingpixel.cobblests.utils;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.EVs;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.util.LocalizationUtilsKt;
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
        LocalizationUtilsKt.lang(pokemon.getNature().getDisplayName().replace("cobblemon.", "")).getString())));
    lore.add(CobbleSTS.language.getDescAbility().replace("%ability%",
      CobbleSTS.language.getAbility().getOrDefault(pokemon.getAbility().getDisplayName().split("\\.")[2],
        LocalizationUtilsKt.lang(pokemon.getAbility().getDisplayName().replace("cobblemon.", "")).getString())));
    lore.add(CobbleSTS.language.getDescBall().replace("%ball%",
      CobbleSTS.language.getBall().getOrDefault(pokemon.getCaughtBall().getName().getPath(),
        Utils.parseItemId(pokemon.getCaughtBall().getName().toLanguageKey().replace(".", ":")).getDisplayName().getString().replace("[", "").replace("]", ""))));
    lore.add(CobbleSTS.language.getDescprice().replace("%price%", String.valueOf(Sell(pokemon, false, null))));
    return lore;
  }

  public static int Sell(Pokemon pokemon, boolean execute, Player player) {
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

    // STSNature
    price += CobbleSTS.config.getNature().getOrDefault(pokemon.getNature().getDisplayName().split("\\.")[2],
      CobbleSTS.config.getDefaultnature());

    // Ability
    price += CobbleSTS.config.getAbility().getOrDefault(pokemon.getAbility().getDisplayName().split("\\.")[2],
      CobbleSTS.config.getDefaultability());

    // Ball
    price += CobbleSTS.config.getBall().getOrDefault(pokemon.getCaughtBall().getName().getPath(),
      CobbleSTS.config.getDefaultball());

    if (execute) {
      try {
        command(player, pokemon, price);
      } catch (NoPokemonStoreException e) {
        e.printStackTrace();
      }
    } else {
      return price;
    }


    return price;
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

  private static void command(Player player, Pokemon pokemon, int price) throws NoPokemonStoreException {
    CommandSourceStack serverSource = CobbleSTS.server.createCommandSourceStack();
    CommandDispatcher<CommandSourceStack> disparador = CobbleSTS.server.getCommands().getDispatcher();
    PlayerPartyStore partyStorageSlot = Cobblemon.INSTANCE.getStorage().getParty(player.getUUID());
    CobbleSTS.manager.addPlayerWithDate(player, CobbleSTS.config.getCooldown());
    try {
      String r = CobbleSTS.config.getEcocommand().replace("%player%", player.getGameProfile().getName()).replace(
        "%amount%", String.valueOf(price)).replace("%price%", String.valueOf(price));
      ParseResults<CommandSourceStack> parseResults = disparador.parse(r, serverSource);
      disparador.execute(parseResults);
      if (!partyStorageSlot.remove(pokemon)) {
        Cobblemon.INSTANCE.getStorage().getPC(player.getUUID()).remove(pokemon);
      }
      player.sendSystemMessage(AdventureTranslator.toNative(CobbleSTS.language.getSell().replace("%player%",
        player.getGameProfile().getName()).replace("%pokemon%", pokemon.getSpecies().getName()).replace("%price%",
        String.valueOf(price))));
    } catch (CommandSyntaxException e) {
      player.sendSystemMessage(AdventureTranslator.toNative("Error to give money"));
      e.printStackTrace();
    }
  }
}
