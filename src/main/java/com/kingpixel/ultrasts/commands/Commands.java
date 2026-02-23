package com.kingpixel.ultrasts.commands;

import com.kingpixel.ultrasts.UltraSTS;
import com.kingpixel.ultrasts.commands.base.ReloadCommand;
import com.kingpixel.ultrasts.commands.base.STSCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class Commands {
  public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
    var commands = UltraSTS.config.getCommands();
    for (String command : commands) {
      var base = CommandManager.literal(command);
      STSCommand.register(base);
      ReloadCommand.register(base);
      dispatcher.register(base);
    }
  }
}
