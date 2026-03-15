package com.kingpixel.ultrasts.gui;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.Model.EconomyUse;
import com.kingpixel.cobbleutils.api.EconomyApi;
import com.kingpixel.cobbleutils.util.PlayerUtils;
import com.kingpixel.cobbleutils.util.PokemonUtils;
import com.kingpixel.cobbleutils.util.TypeMessage;
import com.kingpixel.ultrasts.UltraSTS;
import com.kingpixel.ultrasts.models.STS;
import com.kingpixel.ultrasts.models.User;
import lombok.Data;
import net.minecraft.server.network.ServerPlayerEntity;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Data
public class STSMenu {

  public static CompletableFuture<Void> open(ServerPlayerEntity player, STS selectSTS) {

    User user = UltraSTS.database.getUser(player);

    if (user == null) {
      PlayerUtils.sendMessage(player, "&cYou cannot join this STS category!", UltraSTS.lang.getPrefix(), TypeMessage.CHAT);
      UltraSTS.lang.getMenu().open(player);
      return CompletableFuture.completedFuture(null);
    }

    if (user.hasCooldown(selectSTS)) {
      PlayerUtils.sendMessage(
        player,
        UltraSTS.lang.getCooldownMessage()
          .replace("%time%", PlayerUtils.getCooldown(user.getCooldown(selectSTS))),
        UltraSTS.lang.getPrefix(),
        TypeMessage.CHAT
      );
      return CompletableFuture.completedFuture(null);
    }

    return UltraSTS.ASYNC.runAsync(() -> UltraSTS.lang.getPartyPcMenu().openParty(
      player,
      template -> {
      },
      pokemonButtonAction -> user.sellPokemon(selectSTS, pokemonButtonAction.getPokemon(), player)
        .whenComplete((success, throwable) -> {

          if (throwable != null) {
            throwable.printStackTrace();
            PlayerUtils.sendMessage(player, "&cAn error occurred while selling your pokemon!", UltraSTS.lang.getPrefix(), TypeMessage.CHAT);
            return;
          }

          if (!success) {
            PlayerUtils.sendMessage(player, "&cYou cannot sell this pokemon for STS!", UltraSTS.lang.getPrefix(), TypeMessage.CHAT);
            return;
          }

          Pokemon pokemon = pokemonButtonAction.getPokemon();

          var party = Cobblemon.INSTANCE.getStorage().getParty(player);
          var pc = Cobblemon.INSTANCE.getStorage().getPC(player);

          CobbleUtils.server.execute(() -> {

            if (party.remove(pokemon) || pc.remove(pokemon)) {

              BigDecimal price = BigDecimal.valueOf(selectSTS.getFormula().getPokemonValue(pokemon));
              EconomyUse economyUse = selectSTS.getEconomy();

              boolean deposited = EconomyApi.addMoney(player.getUuid(), price, economyUse);

              if (deposited) {
                PlayerUtils.sendMessage(
                  player,
                  PokemonUtils.replace(
                    UltraSTS.lang.getNotificationSelling()
                      .replace("%price%", EconomyApi.formatMoney(price, economyUse)),
                    pokemon
                  ),
                  UltraSTS.lang.getPrefix(),
                  TypeMessage.CHAT
                );
              } else {

                PlayerUtils.sendMessage(player,
                  "&cAn error occurred while depositing the money for your pokemon!",
                  UltraSTS.lang.getPrefix(),
                  TypeMessage.CHAT
                );

                party.add(pokemon);
                user.removeCooldown(selectSTS);
              }

            } else {
              user.removeCooldown(selectSTS);
            }
          });

          UltraSTS.lang.getMenu().open(player);
        }),
      close -> UltraSTS.lang.getMenu().open(player),
      selectSTS.getBlackList(),
      UltraSTS.lang.getPokemonLore(),
      (pokemon, lore) -> {

        EconomyUse economyUse = selectSTS.getEconomy();

        String price = EconomyApi.formatMoney(
          BigDecimal.valueOf(selectSTS.getFormula().getPokemonValue(pokemon)),
          economyUse
        );

        lore.replaceAll(s -> PokemonUtils.replace(
          s.replace("%price%", price),
          pokemon
        ));
      },
      UltraSTS.lang.getConfirmMenu()
    )).exceptionally(e -> {
      e.printStackTrace();
      return null;
    });
  }
}