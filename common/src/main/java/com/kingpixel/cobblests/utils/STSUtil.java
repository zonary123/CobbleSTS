package com.kingpixel.cobblests.utils;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblests.CobbleSTS;
import com.kingpixel.cobbleutils.util.EconomyUtil;
import com.kingpixel.cobbleutils.util.PlayerUtils;
import com.kingpixel.cobbleutils.util.PokemonUtils;
import net.minecraft.server.network.ServerPlayerEntity;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.math.BigDecimal;
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

  public static BigDecimal getPrice(Pokemon pokemon) {
    return Sell(pokemon, false, null, false);
  }

  public static BigDecimal Sell(Pokemon pokemon, boolean execute, ServerPlayerEntity player, boolean release) {
    boolean isBan = CobbleSTS.config.getBlacklisted().contains(pokemon.showdownId())
      || (pokemon.getShiny() && !CobbleSTS.config.isAllowshiny())
      || ((pokemon.isLegendary() || CobbleSTS.config.getIslegends().contains(pokemon.showdownId())) && !CobbleSTS.config.isAllowlegendary());
    if (isBan) return BigDecimal.ZERO;

    double base = CobbleSTS.config.getPokemon().getOrDefault(pokemon.showdownId(), CobbleSTS.config.getBase()).doubleValue();
    double shiny = pokemon.getShiny() ? CobbleSTS.config.getShiny().doubleValue() : 0;
    double label = 0;
    for (String s : pokemon.getForm().getLabels()) {
      label += CobbleSTS.config.getLabel().getOrDefault(s, BigDecimal.ZERO).doubleValue();
    }
    double happiness = pokemon.getFriendship();
    double gender = CobbleSTS.config.getGender()
      .getOrDefault(pokemon.getGender().getShowdownName(), CobbleSTS.config.getDefaultability()).doubleValue();
    double form = CobbleSTS.config.getForm()
      .getOrDefault(pokemon.getForm().getName(), CobbleSTS.config.getDefaultform()).doubleValue();
    double nature = CobbleSTS.config.getNature()
      .getOrDefault(pokemon.getNature().getName().getNamespace(), CobbleSTS.config.getDefaultnature()).doubleValue();
    double ability = PokemonUtils.isAH(pokemon) ? CobbleSTS.config.getAh().doubleValue() : CobbleSTS.config.getAbility()
      .getOrDefault(pokemon.getAbility().getName(), CobbleSTS.config.getDefaultability()).doubleValue();
    double ball = CobbleSTS.config.getBall()
      .getOrDefault(pokemon.getCaughtBall().getName().getPath(), CobbleSTS.config.getDefaultball()).doubleValue();
    double catchRate = pokemon.getForm().getCatchRate();
    double level = pokemon.getLevel();
    double totalIVs = PokemonUtils.getIvsTotal(pokemon.getIvs());
    double totalEVs = PokemonUtils.getEvsTotal(pokemon.getEvs());
    double averageIVs = PokemonUtils.getIvsAverage(pokemon.getIvs());
    double averageEVs = PokemonUtils.getEvsAverage(pokemon.getEvs());
    double shinyMultiplier = pokemon.getShiny() ? CobbleSTS.config.getShinyMultiplier() : 1.0;
    double legendaryMultiplier = pokemon.isLegendary() ? CobbleSTS.config.getLegendaryMultiplier() : 1.0;
    double mythicalMultiplier = pokemon.isMythical() ? CobbleSTS.config.getMythicalMultiplier() : 1.0;
    double ultraBeastMultiplier = pokemon.isUltraBeast() ? CobbleSTS.config.getUltraBeastMultiplier() : 1.0;


    String formula = CobbleSTS.config.getPriceFormula();
    formula = formula.replace("base", String.valueOf(base))
      .replace("priceShiny", String.valueOf(shiny))
      .replace("happiness", String.valueOf(happiness))
      .replace("gender", String.valueOf(gender))
      .replace("form", String.valueOf(form))
      .replace("nature", String.valueOf(nature))
      .replace("ability", String.valueOf(ability))
      .replace("ball", String.valueOf(ball))
      .replace("catchRate", String.valueOf(catchRate))
      .replace("level", String.valueOf(level))
      .replace("totalIvs", String.valueOf(totalIVs))
      .replace("totalEvs", String.valueOf(totalEVs))
      .replace("shinyMultiplier", String.valueOf(shinyMultiplier))
      .replace("legendaryMultiplier", String.valueOf(legendaryMultiplier))
      .replace("mythicalMultiplier", String.valueOf(mythicalMultiplier))
      .replace("ultraBeastMultiplier", String.valueOf(ultraBeastMultiplier))
      .replace("averageIvs", String.valueOf(averageIVs))
      .replace("averageEvs", String.valueOf(averageEVs))
      .replace("label", String.valueOf(label));

    Expression expression = new ExpressionBuilder(formula)
      .build();
    BigDecimal price = BigDecimal.valueOf(expression.evaluate()).setScale(2, BigDecimal.ROUND_HALF_UP);


    if (price.compareTo(CobbleSTS.config.getLimitPrice()) > 0) price = CobbleSTS.config.getLimitPrice();

    if (price.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;

    if (execute) {
      PlayerPartyStore partyStorageSlot = Cobblemon.INSTANCE.getStorage().getParty(player);

      CobbleSTS.manager.addPlayerWithDate(player, PlayerUtils.getCooldown(CobbleSTS.config.getCooldowns(),
        CobbleSTS.config.getCooldown(), player
      ));

      if (release) {
        BigDecimal lostAmount = price.multiply(CobbleSTS.config.getLostPriceForRelease());
        price = price.subtract(lostAmount);
      }

      EconomyUtil.addMoney(player, CobbleSTS.config.getCurrency(), price);
      if (!partyStorageSlot.remove(pokemon)) {
        Cobblemon.INSTANCE.getStorage().getPC(player).remove(pokemon);
      }
      PlayerUtils.sendMessage(player, CobbleSTS.language.getSell()
        .replace("%player%", player.getGameProfile().getName())
        .replace("%pokemon%", pokemon.getSpecies().getName())
        .replace("%price%", price.toString()), CobbleSTS.language.getPrefix());
    } else {
      return price;
    }

    return price;
  }

}
