package com.kingpixel.cobblests.utils;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblests.CobbleSTS;
import com.kingpixel.cobblests.database.DataBaseFactory;
import com.kingpixel.cobbleutils.api.EconomyApi;
import com.kingpixel.cobbleutils.util.PlayerUtils;
import com.kingpixel.cobbleutils.util.PokemonUtils;
import com.kingpixel.cobbleutils.util.TypeMessage;
import net.minecraft.server.network.ServerPlayerEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Carlos Varas Alonso - 12/04/2025 19:18
 */
public class STSUtil {
  public static void Sell(Pokemon pokemon, ServerPlayerEntity player, STSAction stsAction) {
    var userinfo = DataBaseFactory.INSTANCE.getUserInfo(player);
    BigDecimal price = getPrice(pokemon);
    if (stsAction == STSAction.RELEASE) {
      BigDecimal lostPriceForRelease = CobbleSTS.config.getLostPriceForRelease();

      if (lostPriceForRelease.compareTo(BigDecimal.ZERO) > 0) {
        if (lostPriceForRelease.compareTo(BigDecimal.ZERO) < 0 || lostPriceForRelease.compareTo(BigDecimal.ONE) > 0) {
          throw new IllegalArgumentException("The lost price for release must be between 0 and 1 (0% and 100%)");
        }

        BigDecimal discount = price.multiply(lostPriceForRelease);
        price = price.subtract(discount);
      }
    } else {
      if (userinfo.hasCooldown()) {
        PlayerUtils.sendMessage(
          player,
          CobbleSTS.language.getCooldownMessage()
            .replace("%time%", PlayerUtils.getCooldown(new Date(userinfo.getCooldown()))),
          CobbleSTS.language.getPrefix(),
          TypeMessage.CHAT
        );
        return;
      }
    }

    if (price.compareTo(BigDecimal.ZERO) <= 0) {
      if (STSAction.SELL == stsAction) {
        PlayerUtils.sendMessage(
          player,
          CobbleSTS.language.getMessagePriceIsZero(),
          CobbleSTS.language.getPrefix(),
          TypeMessage.CHAT
        );
      }
      return;
    }

    if (!Cobblemon.INSTANCE.getStorage().getParty(player).remove(pokemon)) {
      Cobblemon.INSTANCE.getStorage().getPC(player).remove(pokemon);
    }
    PlayerUtils.sendMessage(
      player,
      PokemonUtils.replace(CobbleSTS.language.getMessageSell(), pokemon)
        .replace("%price%", EconomyApi.formatMoney(price, CobbleSTS.config.getEconomyUse())),
      CobbleSTS.language.getPrefix(),
      TypeMessage.CHAT
    );
    EconomyApi.addMoney(player.getUuid(), price, CobbleSTS.config.getEconomyUse());
    if (stsAction == STSAction.SELL) {
      userinfo.setCooldown(player);
      DataBaseFactory.INSTANCE.updateUserInfo(userinfo);
    }
  }

  public static BigDecimal getPrice(Pokemon pokemon) {
    var price = BigDecimal.valueOf(CobbleSTS.config.getFormula().getPokemonExpression(pokemon, CobbleSTS.MOD_ID).evaluate());
    if (price.compareTo(CobbleSTS.config.getLimitPrice()) > 0) price = CobbleSTS.config.getLimitPrice();
    return price;
  }

  public enum STSAction {
    SELL,
    RELEASE
  }
}
