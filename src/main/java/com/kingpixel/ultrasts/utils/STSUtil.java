package com.kingpixel.ultrasts.utils;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.ultrasts.UltraSTS;
import com.kingpixel.ultrasts.database.DataBaseFactory;
import com.kingpixel.cobbleutils.api.EconomyApi;
import com.kingpixel.cobbleutils.util.PlayerUtils;
import com.kingpixel.cobbleutils.util.PokemonUtils;
import com.kingpixel.cobbleutils.util.TypeMessage;
import net.minecraft.server.network.ServerPlayerEntity;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Carlos Varas Alonso - 12/04/2025 19:18
 */
public class STSUtil {
  public static void sell(Pokemon pokemon, ServerPlayerEntity player, STSAction stsAction) {
    if (PlayerUtils.isBattle(player)) return;
    CompletableFuture.runAsync(() -> {
        var userinfo = DataBaseFactory.INSTANCE.getUserInfo(player);
        BigDecimal price = getPrice(pokemon);
        if (stsAction == STSAction.RELEASE) {
          BigDecimal lostPriceForRelease = UltraSTS.config.getLostPriceForRelease();

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
              UltraSTS.language.getCooldownMessage()
                .replace("%time%", PlayerUtils.getCooldown(userinfo.getCooldown())),
              UltraSTS.language.getPrefix(),
              TypeMessage.CHAT
            );
            return;
          }
        }

        if (price.compareTo(BigDecimal.ZERO) <= 0) {
          if (STSAction.SELL == stsAction) {
            PlayerUtils.sendMessage(
              player,
              UltraSTS.language.getMessagePriceIsZero(),
              UltraSTS.language.getPrefix(),
              TypeMessage.CHAT
            );
          }
          return;
        }
        UltraSTS.server.execute(() -> {
          if (!Cobblemon.INSTANCE.getStorage().getParty(player).remove(pokemon))
            Cobblemon.INSTANCE.getStorage().getPC(player).remove(pokemon);
        });
        PlayerUtils.sendMessage(
          player,
          PokemonUtils.replace(UltraSTS.language.getMessageSell(), pokemon)
            .replace("%price%", EconomyApi.formatMoney(price, UltraSTS.config.getEconomyUse())),
          UltraSTS.language.getPrefix(),
          TypeMessage.CHAT
        );
        EconomyApi.addMoney(player.getUuid(), price, UltraSTS.config.getEconomyUse());
        if (stsAction == STSAction.SELL) {
          userinfo.setCooldown(player);
          DataBaseFactory.INSTANCE.updateUserInfo(userinfo);
        }
      }, UltraSTS.EXECUTOR_STS)
      .orTimeout(5, TimeUnit.SECONDS)
      .exceptionally(e -> {
        e.printStackTrace();
        return null;
      });
  }

  public static BigDecimal getPrice(Pokemon pokemon) {
    double basePrice = UltraSTS.config.getFormula().getPokemonValue(pokemon);
    var price = BigDecimal.valueOf(basePrice);
    if (price.compareTo(UltraSTS.config.getLimitPrice()) > 0) price = UltraSTS.config.getLimitPrice();
    return price;
  }

  public enum STSAction {
    SELL,
    RELEASE
  }
}
