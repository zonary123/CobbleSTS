package com.kingpixel.cobblests.utils;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.minecraft.server.level.ServerPlayer;

public class CommandUtils {

  public static boolean hasPermission(ServerPlayer source, String permission) {
    if (source.hasPermissions(2)) return true;
    LuckPerms luckperms = LuckPermsProvider.get();
    if (luckperms == null) return false;
    User user = luckperms.getUserManager().getUser(source.getUUID());
    if (user == null) return false;
    return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
  }

  private static boolean isLuckPermsPresent() {
    try {
      Class.forName("net.luckperms.api.LuckPerms");
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }
}