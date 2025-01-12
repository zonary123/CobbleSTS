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

import static com.kingpixel.cobblests.CobbleSTS.LOGGER;

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
    boolean isBan =
      CobbleSTS.config.getBlacklisted().contains(pokemon.showdownId()) || (pokemon.getShiny() && !CobbleSTS.config.isAllowshiny()) || (!CobbleSTS.config.isAllowlegendary() && pokemon.isLegendary());
    if (isBan) return BigDecimal.ZERO;

    BigDecimal base = CobbleSTS.config.getPokemon().getOrDefault(pokemon.showdownId(), CobbleSTS.config.getBase());
    BigDecimal shiny = pokemon.getShiny() ? CobbleSTS.config.getShiny() : BigDecimal.ZERO;
    BigDecimal legendary = pokemon.isLegendary() ? CobbleSTS.config.getLegendary() : BigDecimal.ZERO;
    BigDecimal label = BigDecimal.ZERO;
    for (String s : pokemon.getForm().getLabels()) {
      label = label.add(CobbleSTS.config.getLabel().getOrDefault(s, BigDecimal.ZERO));
    }
    BigDecimal happiness = BigDecimal.valueOf(pokemon.getFriendship());
    BigDecimal gender = CobbleSTS.config.getGender()
      .getOrDefault(pokemon.getGender().getShowdownName(), CobbleSTS.config.getDefaultability());
    BigDecimal form = CobbleSTS.config.getForm()
      .getOrDefault(pokemon.getForm().getName(), CobbleSTS.config.getDefaultform());
    BigDecimal nature = CobbleSTS.config.getNature()
      .getOrDefault(pokemon.getNature().getName().getNamespace(), CobbleSTS.config.getDefaultnature());
    BigDecimal ability = PokemonUtils.isAH(pokemon) ? CobbleSTS.config.getAh() : CobbleSTS.config.getAbility()
      .getOrDefault(pokemon.getAbility().getName(), CobbleSTS.config.getDefaultability());
    BigDecimal ball = CobbleSTS.config.getBall()
      .getOrDefault(pokemon.getCaughtBall().getName().getPath(), CobbleSTS.config.getDefaultball());
    BigDecimal catchRate = BigDecimal.valueOf(pokemon.getForm().getCatchRate());
    BigDecimal level = BigDecimal.valueOf(pokemon.getLevel());
    BigDecimal totalIVs = BigDecimal.valueOf(PokemonUtils.getIvsTotal(pokemon.getIvs()));
    BigDecimal totalEVs = BigDecimal.valueOf(PokemonUtils.getEvsTotal(pokemon.getEvs()));
    BigDecimal averageIVs = BigDecimal.valueOf(PokemonUtils.getIvsAverage(pokemon.getIvs()));
    BigDecimal averageEVs = BigDecimal.valueOf(PokemonUtils.getEvsAverage(pokemon.getEvs()));
    BigDecimal shinyMultiplier = pokemon.getShiny() ? BigDecimal.valueOf(CobbleSTS.config.getShinyMultiplier()) : BigDecimal.ONE;
    BigDecimal legendaryMultiplier = pokemon.isLegendary() ? BigDecimal.valueOf(CobbleSTS.config.getLegendaryMultiplier()) : BigDecimal.ONE;
    BigDecimal mythicalMultiplier = pokemon.isMythical() ? BigDecimal.valueOf(CobbleSTS.config.getMythicalMultiplier()) : BigDecimal.ONE;
    BigDecimal ultraBeastMultiplier = pokemon.isUltraBeast() ? BigDecimal.valueOf(CobbleSTS.config.getUltraBeastMultiplier()) : BigDecimal.ONE;
    BigDecimal rarityMultiplier = CobbleSTS.config.getRarity().getOrDefault(PokemonUtils.getRarityS(pokemon),
      BigDecimal.ONE);
    String formula = CobbleSTS.config.getPriceFormula();
    try {

      Expression expression = new ExpressionBuilder(formula)
        .variable("base")
        .variable("basePrice")
        .variable("priceShiny")
        .variable("priceLegendary")
        .variable("happiness")
        .variable("gender")
        .variable("form")
        .variable("label")
        .variable("nature")
        .variable("ability")
        .variable("averageIvs")
        .variable("averageEvs")
        .variable("ball")
        .variable("catchRate")
        .variable("level")
        .variable("totalIvs")
        .variable("totalEvs")
        .variable("shinyMultiplier")
        .variable("legendaryMultiplier")
        .variable("mythicalMultiplier")
        .variable("ultraBeastMultiplier")
        .variable("rarityMultiplier")
        .build()
        .setVariable("base", base.doubleValue())
        .setVariable("basePrice", base.doubleValue())
        .setVariable("priceShiny", shiny.doubleValue())
        .setVariable("priceLegendary", legendary.doubleValue())
        .setVariable("happiness", happiness.doubleValue())
        .setVariable("gender", gender.doubleValue())
        .setVariable("form", form.doubleValue())
        .setVariable("nature", nature.doubleValue())
        .setVariable("rarityMultiplier", 1)
        .setVariable("ability", ability.doubleValue())
        .setVariable("ball", ball.doubleValue())
        .setVariable("catchRate", catchRate.doubleValue())
        .setVariable("level", level.doubleValue())
        .setVariable("totalIvs", totalIVs.doubleValue())
        .setVariable("totalEvs", totalEVs.doubleValue())
        .setVariable("averageIvs", averageIVs.doubleValue())
        .setVariable("averageEvs", averageEVs.doubleValue())
        .setVariable("label", label.doubleValue())
        .setVariable("shinyMultiplier", shinyMultiplier.doubleValue())
        .setVariable("legendaryMultiplier", legendaryMultiplier.doubleValue())
        .setVariable("mythicalMultiplier", mythicalMultiplier.doubleValue())
        .setVariable("ultraBeastMultiplier", ultraBeastMultiplier.doubleValue());


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
    } catch (Exception e) {
      LOGGER.error("Error evaluating formula: " + formula);
      e.printStackTrace();
      return BigDecimal.ZERO;
    }
  }
}
