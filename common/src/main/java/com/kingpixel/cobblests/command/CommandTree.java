package com.kingpixel.cobblests.command;

import ca.landonjw.gooeylibs2.api.UIManager;
import com.cobblemon.mod.common.command.argument.PartySlotArgumentType;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblests.CobbleSTS;
import com.kingpixel.cobblests.database.DataBaseFactory;
import com.kingpixel.cobblests.utils.STSUtil;
import com.kingpixel.cobbleutils.api.EconomyApi;
import com.kingpixel.cobbleutils.api.PermissionApi;
import com.kingpixel.cobbleutils.util.AdventureTranslator;
import com.kingpixel.cobbleutils.util.PlayerUtils;
import com.kingpixel.cobbleutils.util.TypeMessage;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * @author Carlos Varas Alonso - 25/05/2024 19:35
 */
public class CommandTree {
  private static final String literal = "sts";

  public static void register(
    CommandDispatcher<ServerCommandSource> dispatcher
  ) {
    LiteralArgumentBuilder<ServerCommandSource> base = CommandManager.literal(literal)
      .requires(source -> PermissionApi.hasPermission(
        source, "cobblests.user", 2
      ));
    // /sts
    dispatcher.register(
      base.executes(context -> {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;
        open(player);
        return 1;
      })
    );

    // /sts other <player>
    dispatcher.register(
      base
        .then(CommandManager.literal("other")
          .requires(source -> PermissionApi.hasPermission(
            source, "cobblests.other", 2
          ))
          .then(
            CommandManager.argument("player", EntityArgumentType.players())
              .executes(context -> {
                var players = EntityArgumentType.getPlayers(context, "player");
                for (ServerPlayerEntity player : players) {
                  open(player);
                }
                return 1;
              })
          ))
    );

    dispatcher.register(
      base
        .then(CommandManager.literal("reset")
          .requires(source -> PermissionApi.hasPermission(
            source, "cobblests.reset", 2
          ))
          .executes(context -> {
            if (context.getSource().isExecutedByPlayer()) {
              var player = context.getSource().getPlayer();
              var userinfo = DataBaseFactory.INSTANCE.getUserInfo(player);
              userinfo.setCooldown(0);
              DataBaseFactory.INSTANCE.updateUserInfo(userinfo);
            }
            return 0;
          })
          .then(
            CommandManager.argument("player", EntityArgumentType.players())
              .executes(context -> {
                var players = EntityArgumentType.getPlayers(context, "player");
                for (ServerPlayerEntity player : players) {
                  var userinfo = DataBaseFactory.INSTANCE.getUserInfo(player);
                  userinfo.setCooldown(0);
                  DataBaseFactory.INSTANCE.updateUserInfo(userinfo);
                }
                return 1;
              })
          )
        )
    );

    // /sts reload
    dispatcher.register(base
      .then(CommandManager.literal("reload")
        .requires(source -> PermissionApi.hasPermission(source,
          "cobblests.reload", 2
        ))
        .executes(context -> {
          CobbleSTS.load();
          ServerPlayerEntity player = context.getSource().getPlayer();
          String message = CobbleSTS.language.getReload().replace("%prefix%", CobbleSTS.language.getPrefix());
          if (player != null) {
            PlayerUtils.sendMessage(
              player,
              message,
              CobbleSTS.language.getPrefix(),
              TypeMessage.CHAT);
          } else {
            context.getSource().sendMessage(AdventureTranslator.toNative(
              message));
          }
          return 1;
        })
      )
    );

    // /sts sell <slot>
    dispatcher.register(
      base
        .then(CommandManager.literal("sell")
          .requires(source -> PermissionApi.hasPermission(
            source, "cobblests.sell", 2
          ))
          .then(
            CommandManager.argument("slot", PartySlotArgumentType.Companion.partySlot())
              .executes(context -> {
                if (!context.getSource().isExecutedByPlayer()) return 0;
                ServerPlayerEntity player = context.getSource().getPlayer();
                if (player == null) return 0;
                if (PlayerUtils.isBattle(player)) return 0;
                var pokemon = PartySlotArgumentType.Companion.getPokemon(context, "slot");
                STSUtil.sell(pokemon, player, STSUtil.STSAction.SELL);
                return 1;
              })
          )
        )
    );

  }

  private static void open(ServerPlayerEntity player) {
    if (player == null) return;
    if (PlayerUtils.isBattle(player)) return;
    CobbleSTS.language.getPartyPcMenu().openParty(
      player,
      template -> {

      },
      pokemonAction -> {
        Pokemon pokemon = pokemonAction.getPokemon();
        ServerPlayerEntity player1 = pokemonAction.getAction().getPlayer();
        STSUtil.sell(pokemon, player1, STSUtil.STSAction.SELL);
        UIManager.closeUI(player1);
      },
      close -> UIManager.closeUI(close.getPlayer()),
      CobbleSTS.config.getBlacklist(),
      CobbleSTS.language.getPokemonLore(),
      (pokemon, lore) -> {
        String value = EconomyApi.formatMoney(STSUtil.getPrice(pokemon), CobbleSTS.config.getEconomyUse());
        lore.replaceAll(s -> s.replace("%price%", value));
      },
      CobbleSTS.language.getConfirmMenu()
    );
  }
}
