package com.kingpixel.cobblests.command;

import ca.landonjw.gooeylibs2.api.UIManager;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.kingpixel.cobblests.ui.STSPc;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * @author Carlos Varas Alonso - 25/06/2024 4:18
 */
public class CommandSTSPC implements Command<ServerCommandSource> {

  @Override public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
    ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
    try {
      UIManager.openUIForcefully(player, STSPc.open(player));
    } catch (NoPokemonStoreException e) {
      throw new RuntimeException(e);
    }
    return 1;
  }
}
