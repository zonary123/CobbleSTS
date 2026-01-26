package com.kingpixel.ultrasts.database;

import com.kingpixel.ultrasts.model.UserInfo;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * @author Carlos Varas Alonso - 22/02/2025 3:52
 */
public abstract class DataBaseClient {

  public abstract void connect();

  public abstract void disconnect();

  public abstract UserInfo getUserInfo(ServerPlayerEntity player);

  public abstract void updateUserInfo(UserInfo userInfo);

  public void removeIfNecessary(ServerPlayerEntity player) {
    if (player == null) return;
    DataBaseFactory.users.remove(player.getUuid());
  }
}
