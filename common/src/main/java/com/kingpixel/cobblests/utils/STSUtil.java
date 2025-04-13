package com.kingpixel.cobblests.utils;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblests.CobbleSTS;
import com.kingpixel.cobblests.database.DataBaseFactory;
import com.kingpixel.cobbleutils.api.EconomyApi;
import com.kingpixel.cobbleutils.util.PlayerUtils;
import com.kingpixel.cobbleutils.util.TypeMessage;
import net.minecraft.server.network.ServerPlayerEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * @author Carlos Varas Alonso - 12/04/2025 19:18
 */
public class STSUtil {
  public static void Sell(Pokemon pokemon, ServerPlayerEntity player, STSAction stsAction) {
    BigDecimal price = getPrice(pokemon);
    var userinfo = DataBaseFactory.INSTANCE.getUserInfo(player);
    if (stsAction == STSAction.RELEASE) {
      // Calcular el 25% del precio original y restarlo
      BigDecimal discount = price.multiply(CobbleSTS.config.getLostPriceForRelease().divide(BigDecimal.valueOf(100),
        RoundingMode.UNNECESSARY));
      price = price.subtract(discount);
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

    if (price.compareTo(BigDecimal.ZERO) < 0) return;

    if (!Cobblemon.INSTANCE.getStorage().getParty(player).remove(pokemon)) {
      Cobblemon.INSTANCE.getStorage().getPC(player).remove(pokemon);
    }

    EconomyApi.addMoney(player.getUuid(), price, CobbleSTS.config.getEconomyUse());
    userinfo.setCooldown(player);
    DataBaseFactory.INSTANCE.updateUserInfo(userinfo);
  }

  public static BigDecimal getPrice(Pokemon pokemon) {
    return BigDecimal.valueOf(CobbleSTS.config.getFormula().getPokemonExpression(pokemon, CobbleSTS.MOD_ID).evaluate());
  }

  public enum STSAction {
    SELL,
    RELEASE
  }
}
