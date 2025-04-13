package com.kingpixel.cobblests.database;

import com.kingpixel.cobblests.CobbleSTS;
import com.kingpixel.cobblests.model.UserInfo;
import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.Model.DataBaseConfig;
import com.kingpixel.cobbleutils.util.Utils;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * @author Carlos Varas Alonso - 22/02/2025 4:10
 */
public class DataBaseJSON extends DataBaseClient {


  public DataBaseJSON(DataBaseConfig config) {
    super();
  }

  @Override public void connect() {
    Utils.getAbsolutePath(CobbleSTS.PATH_DATA).mkdirs();
    CobbleUtils.LOGGER.info(CobbleSTS.MOD_ID, "Connecting to JSON database");
  }

  @Override public void disconnect() {
    CobbleUtils.LOGGER.info(CobbleSTS.MOD_ID, "Disconnecting from JSON database");
  }

  @Override public UserInfo getUserInfo(ServerPlayerEntity player) {
    UserInfo data = DataBaseFactory.users.get(player.getUuid());
    if (data != null) return data;

    var future = Utils.readFileAsync(CobbleSTS.PATH_DATA, player.getUuidAsString() + ".json", call -> {
      UserInfo userInfo = Utils.newWithoutSpacingGson().fromJson(call, UserInfo.class);
      if (userInfo != null) {
        DataBaseFactory.users.put(player.getUuid(), userInfo);
      }
    });

    if (!future.join()) {
      data = new UserInfo(player);
      DataBaseFactory.users.put(player.getUuid(), data);
    } else {
      data = DataBaseFactory.users.get(player.getUuid());
    }
    return data;
  }

  @Override public void updateUserInfo(UserInfo userInfo) {
    Utils.writeFileAsync(Utils.getAbsolutePath(CobbleSTS.PATH_DATA + userInfo.getUuid() + ".json"),
      Utils.newWithoutSpacingGson().toJson(userInfo));
  }

}
