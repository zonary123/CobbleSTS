package com.kingpixel.cobblests.command;

import com.kingpixel.cobblests.CobbleSTS;
import com.kingpixel.cobbleutils.api.PermissionApi;
import com.kingpixel.cobbleutils.util.AdventureTranslator;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;

import java.util.Objects;

/**
 * @author Carlos Varas Alonso - 25/05/2024 19:35
 */
public class CommandTree {
  private static final String literal = "sts";

  public static void register(
    CommandDispatcher<CommandSourceStack> dispatcher
  ) {
    LiteralArgumentBuilder<CommandSourceStack> base = Commands.literal(literal)
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
        .then(Commands.literal("other")
          .requires(source -> PermissionApi.hasPermission(
            source, "cobblests.other", 2
          ))
          .then(
            Commands.argument("player", EntityArgument.player())
              .executes(new CommandSTSOther())
          ))
    );

    // /sts reload
    dispatcher.register(base
      .then(Commands.literal("reload")
        .requires(source -> PermissionApi.hasPermission(source,
          "cobblests.reload", 2
        ))
        .executes(context -> {
          CobbleSTS.load();
          Objects.requireNonNull(context.getSource().getPlayer()).sendSystemMessage(AdventureTranslator.toNative(CobbleSTS.language.getReload().replace("%prefix%", CobbleSTS.language.getPrefix())));
          return 1;
        })));

    // /sts pc
    dispatcher.register(base.then(Commands.literal("pc")
        .requires(source -> PermissionApi.hasPermission(
          source, "cobblests.user", 2
        ))
        .executes(new CommandSTSPC())
      )
    );
  }
}
