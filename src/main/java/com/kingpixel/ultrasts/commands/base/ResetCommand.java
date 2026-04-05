package com.kingpixel.ultrasts.commands.base;

import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.api.PermissionApi;
import com.kingpixel.ultrasts.UltraSTS;
import com.kingpixel.ultrasts.models.User;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class ResetCommand {
  public static void register(LiteralArgumentBuilder<ServerCommandSource> base) {
    base.then(
      CommandManager.literal("reset")
        .requires(source -> PermissionApi.hasPermission(source, "ultrasts.admin", 2))
        .executes(context -> {
          ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
          User user = UltraSTS.database.getUser(player);
          if (user == null) return 0;
          user.reset();
          return 1;
        }).then(
          CommandManager.argument("player", StringArgumentType.string())
            .requires(source -> PermissionApi.hasPermission(source, "ultrasts.admin", 2))
            .executes(context -> {
              String targetName = StringArgumentType.getString(context, "player");
              var userCache = CobbleUtils.server.getUserCache();
              if (userCache == null) return 0;
              var byName = userCache.findByName(targetName);
              if (byName.isEmpty()) return 0;
              UltraSTS.database.findUser(byName.get().getId())
                .whenComplete((user, throwable) -> {
                  if (throwable != null) {
                    throwable.printStackTrace();
                    return;
                  }
                  if (user == null) return;
                  user.reset();
                  user.save();
                });
              return 1;
            })
        )
    );
  }
}
