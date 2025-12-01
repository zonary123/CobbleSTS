package com.kingpixel.cobblests.model;

import com.kingpixel.cobblests.CobbleSTS;
import com.kingpixel.cobbleutils.util.PlayerUtils;
import lombok.Data;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.concurrent.TimeUnit;

/**
 * @author Carlos Varas Alonso - 13/04/2025 1:46
 */
@Data
public class UserInfo {
  private String uuid;
  private String playerName;
  private long cooldown;

  public UserInfo(ServerPlayerEntity player) {
    this.uuid = player.getUuid().toString();
    this.playerName = player.getName().getString();
    this.cooldown = 0;
  }

  public UserInfo(String uuid, String playerName, long cooldown) {
    this.uuid = uuid;
    this.playerName = playerName;
    this.cooldown = cooldown;
  }

  public boolean check(ServerPlayerEntity player) {
    boolean update = false;


    // Actualizar en la base de datos si hubo cambios
    return update;
  }

  public boolean hasCooldown() {
    return System.currentTimeMillis() <= cooldown;
  }

  public void setCooldown(long cooldown) {
    this.cooldown = cooldown;
  }

  public void setCooldown(ServerPlayerEntity player) {
    int c = PlayerUtils.getCooldown(CobbleSTS.config.getCooldowns(), CobbleSTS.config.getCooldown(), player);
    this.cooldown = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(c);
  }
}
