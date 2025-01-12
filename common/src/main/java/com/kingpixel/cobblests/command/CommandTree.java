package com.kingpixel.cobblests.command;

import com.kingpixel.cobblests.CobbleSTS;
import com.kingpixel.cobbleutils.api.PermissionApi;
import com.kingpixel.cobbleutils.util.AdventureTranslator;
import com.kingpixel.cobbleutils.util.PlayerUtils;
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
      base.executes(new CommandSTS())
    );

    // /sts other <player>
    dispatcher.register(
      base
        .then(CommandManager.literal("other")
          .requires(source -> PermissionApi.hasPermission(
            source, "cobblests.other", 2
          ))
          .then(
            CommandManager.argument("player", EntityArgumentType.player())
              .executes(new CommandSTSOther())
          ))
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
              CobbleSTS.language.getPrefix());
          } else {
            context.getSource().sendMessage(AdventureTranslator.toNative(
              message));
          }
          return 1;
        })));

    // /sts pc
    dispatcher.register(base.then(
        CommandManager.literal("pc")
          .requires(source -> PermissionApi.hasPermission(
            source, "cobblests.user", 2
          ))
          .executes(new CommandSTSPC())
      )
    );
  }
}
