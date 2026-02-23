package com.kingpixel.ultrasts.commands.base;

import com.kingpixel.ultrasts.UltraSTS;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ReloadCommand {

  public static void register(LiteralArgumentBuilder<ServerCommandSource> base) {
    base
      .then(
        CommandManager.literal("reload")
          .executes(context -> {
            UltraSTS.reload();
            context.getSource().sendMessage(
              Text.literal(
                "UltraSTS reloaded successfully!"
              )
            );
            return 1;
          })

      );
  }
}
