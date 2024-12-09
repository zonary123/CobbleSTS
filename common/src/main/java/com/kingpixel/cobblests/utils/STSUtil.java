package com.kingpixel.cobblests.utils;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblests.CobbleSTS;
import com.kingpixel.cobbleutils.util.EconomyUtil;
import com.kingpixel.cobbleutils.util.PlayerUtils;
import com.kingpixel.cobbleutils.util.PokemonUtils;
import net.minecraft.server.level.ServerPlayer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Carlos Varas Alonso - 26/05/2024 4:23
 */
public class STSUtil {
  public static List<String> formatPokemonLore(Pokemon pokemon) {
    List<String> lore = new ArrayList<>(PokemonUtils.replace(CobbleSTS.language.getPokemonLore(), pokemon));
    lore.replaceAll(s -> s.replace("%price%", String.valueOf(Sell(pokemon, false, null, false))));
    return lore;
  }

  public static BigDecimal Sell(Pokemon pokemon, boolean execute, ServerPlayer player, boolean release) {
    boolean isBan = CobbleSTS.config.getBlacklisted().contains(pokemon.showdownId())
      || (pokemon.getShiny() && !CobbleSTS.config.isAllowshiny())
      || ((pokemon.isLegendary() || CobbleSTS.config.getIslegends().contains(pokemon.getSpecies().getName())) && !CobbleSTS.config.isAllowlegendary());
    if (isBan) return BigDecimal.ZERO;
    BigDecimal price = CobbleSTS.config.getPokemon()
      .getOrDefault(pokemon.getSpecies().getName(), CobbleSTS.config.getBase());

    // Level
    price = price.add(BigDecimal.valueOf(pokemon.getLevel()).multiply(CobbleSTS.config.getLevel()));

    // Shiny
    if (pokemon.getShiny()) {
      price = price.add(CobbleSTS.config.getShiny());
    }

    // Legendary
    if (pokemon.isLegendary()) {
      price = price.add(CobbleSTS.config.getLegendary());
    }

    // Ivs
    price = price.add(BigDecimal.valueOf(PokemonUtils.getIvsAverage(pokemon.getIvs()))
      .multiply(CobbleSTS.config.getIvs()));

    // Evs
    price = price.add(BigDecimal.valueOf(PokemonUtils.getEvsAverage(pokemon.getEvs()))
      .multiply(CobbleSTS.config.getEvs()));

    // Happiness
    price = price.add(BigDecimal.valueOf(pokemon.getFriendship())
      .multiply(CobbleSTS.config.getHappiness()));

    // Gender
    price = price.add(CobbleSTS.config.getGender()
      .getOrDefault(pokemon.getGender().getShowdownName(), CobbleSTS.config.getDefaultgender()));

    // Form
    price = price.add(CobbleSTS.config.getForm()
      .getOrDefault(pokemon.getForm().getName(), CobbleSTS.config.getDefaultform()));

    // Nature
    price = price.add(CobbleSTS.config.getNature()
      .getOrDefault(pokemon.getNature().getDisplayName().split("\\.")[2], CobbleSTS.config.getDefaultnature()));

    // Ability
    price = price.add(CobbleSTS.config.getAbility()
      .getOrDefault(pokemon.getAbility().getDisplayName().split("\\.")[2], CobbleSTS.config.getDefaultability()));

    // Ball
    price = price.add(CobbleSTS.config.getBall()
      .getOrDefault(pokemon.getCaughtBall().getName().getPath(), CobbleSTS.config.getDefaultball()));

    price = price.setScale(2, RoundingMode.HALF_UP);

    if (price.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;


    if (execute) {
      try {
        PlayerPartyStore partyStorageSlot = Cobblemon.INSTANCE.getStorage().getParty(player.getUUID());

        CobbleSTS.manager.addPlayerWithDate(player, PlayerUtils.getCooldown(CobbleSTS.config.getCooldowns(),
          CobbleSTS.config.getCooldown(), player
        ));

        if (release) {
          BigDecimal lostAmount = price.multiply(CobbleSTS.config.getLostPriceForRelease());
          price = price.subtract(lostAmount);
        }

        EconomyUtil.addMoney(player, CobbleSTS.config.getCurrency(), price);
        if (!partyStorageSlot.remove(pokemon)) {
          try {
            Cobblemon.INSTANCE.getStorage().getPC(player.getUUID()).remove(pokemon);
          } catch (NoPokemonStoreException e) {
            throw new RuntimeException(e);
          }
        }
        PlayerUtils.sendMessage(player, CobbleSTS.language.getSell()
          .replace("%player%", player.getGameProfile().getName())
          .replace("%pokemon%", pokemon.getSpecies().getName())
          .replace("%price%", price.toString()), CobbleSTS.language.getPrefix());
      } catch (NoPokemonStoreException e) {
        throw new RuntimeException(e);
      }
    } else {
      return price;
    }

    return price;
  }

}
