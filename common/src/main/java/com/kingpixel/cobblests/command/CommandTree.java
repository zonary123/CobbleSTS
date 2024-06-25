package com.kingpixel.cobblests.command;

import com.kingpixel.cobblests.CobbleSTS;
import com.kingpixel.cobblests.Config.STSPermission;
import com.kingpixel.cobblests.utils.AdventureTranslator;
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
      .requires(source -> STSPermission.checkPermission(source, CobbleSTS.permissions.STS_BASE_PERMISSION));
    // /sts
    dispatcher.register(
      base.executes(new CommandSTS())
    );

    // /sts other <player>
    dispatcher.register(
      base
        .requires(source -> STSPermission.checkPermission(source, CobbleSTS.permissions.STS_RELOAD_PERMISSION))
        .then(Commands.literal("other")
          .requires(source -> STSPermission.checkPermission(source, CobbleSTS.permissions.STS_RELOAD_PERMISSION))
          .then(
            Commands.argument("player", EntityArgument.player())
              .executes(new CommandSTSOther())
          ))
    );

    // /sts reload
    dispatcher.register(base
      .requires(source -> STSPermission.checkPermission(source, CobbleSTS.permissions.STS_RELOAD_PERMISSION))
      .then(Commands.literal("reload")
        .requires(source -> STSPermission.checkPermission(source, CobbleSTS.permissions.STS_RELOAD_PERMISSION))
        .executes(context -> {
          CobbleSTS.load();
          Objects.requireNonNull(context.getSource().getPlayer()).sendSystemMessage(AdventureTranslator.toNative(CobbleSTS.language.getReload().replace("%prefix%", CobbleSTS.language.getPrefix())));
          return 1;
        })));

    // /sts pc
    dispatcher.register(base.then(Commands.literal("pc")
        .requires(source -> STSPermission.checkPermission(source, CobbleSTS.permissions.STS_BASE_PERMISSION))
        .executes(new CommandSTSPC())
      )
    );
  }
}
