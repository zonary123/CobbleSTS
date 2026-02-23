package com.kingpixel.ultrasts.commands.base;

import com.kingpixel.cobbleutils.api.PermissionApi;
import com.kingpixel.ultrasts.UltraSTS;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class STSCommand {

  public static void register(LiteralArgumentBuilder<ServerCommandSource> base) {
    base
      .requires(source -> PermissionApi.hasPermission(source, "ultrasts.user", 2))
      .executes(context -> {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        if (player == null) return 0;
        UltraSTS.lang.getMenu().open(player);
        return 1;
      }).then(
        CommandManager.literal("other")
          .then(
            CommandManager.argument("player", StringArgumentType.string())
              .requires(source -> PermissionApi.hasPermission(source, "ultrasts.admin", 4))
              .executes(context -> {
                String targetName = StringArgumentType.getString(context, "player");
                ServerPlayerEntity target = context.getSource().getServer().getPlayerManager().getPlayer(targetName);
                if (target == null) return 0;
                UltraSTS.lang.getMenu().open(target);
                return 1;
              })
          )
      );
  }
}
