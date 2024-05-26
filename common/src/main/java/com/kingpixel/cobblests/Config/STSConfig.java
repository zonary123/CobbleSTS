package com.kingpixel.cobblests.Config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;


public class STSConfig {

  public static Gson GSON = new GsonBuilder()
    .disableHtmlEscaping()
    .setPrettyPrinting()
    .create();
  @SerializedName("permissionlevels") public PermissionLevels permissionLevels = new PermissionLevels();

  public class PermissionLevels {
    // User
    @SerializedName("command.cobblests") public int COMMAND_COBBLESTS_PERMISSION_LEVEL = 0;
    // Admin


  }
}