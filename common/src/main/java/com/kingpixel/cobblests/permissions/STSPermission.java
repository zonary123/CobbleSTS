package com.kingpixel.cobblests.permissions;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.permission.CobblemonPermission;
import com.cobblemon.mod.common.api.permission.PermissionLevel;
import com.kingpixel.cobblests.CobbleSTS;
import net.minecraft.commands.CommandSourceStack;

/**
 * @author Carlos Varas Alonso - 10/05/2024 20:37
 */
public class STSPermission {

  public final CobblemonPermission STS_BASE_PERMISSION;
  public final CobblemonPermission STS_RELOAD_PERMISSION;

  public STSPermission() {
    this.STS_BASE_PERMISSION = new CobblemonPermission("cobblests.command.cobblests.base",
      toPermLevel(CobbleSTS.dexpermission.permissionLevels.COMMAND_COBBLESTS_PERMISSION_LEVEL));
    // Admin
    this.STS_RELOAD_PERMISSION = new CobblemonPermission("cobblests.command.cobblests.reload",
      PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS);
  }

  public PermissionLevel toPermLevel(int permLevel) {
    for (PermissionLevel value : PermissionLevel.values()) {
      if (value.ordinal() == permLevel) {
        return value;
      }
    }
    return PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS;
  }

  public static boolean checkPermission(CommandSourceStack source, CobblemonPermission permission) {
    return Cobblemon.INSTANCE.getPermissionValidator().hasPermission(source, permission);
  }
}

