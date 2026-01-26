package com.kingpixel.ultrasts.database;

import com.kingpixel.ultrasts.UltraSTS;
import com.kingpixel.ultrasts.model.UserInfo;
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
    Utils.getAbsolutePath(UltraSTS.PATH_DATA).mkdirs();
    CobbleUtils.LOGGER.info(UltraSTS.MOD_ID, "Connecting to JSON database");
  }

  @Override public void disconnect() {
    CobbleUtils.LOGGER.info(UltraSTS.MOD_ID, "Disconnecting from JSON database");
  }

  @Override public UserInfo getUserInfo(ServerPlayerEntity player) {
    UserInfo userinfo = DataBaseFactory.users.get(player.getUuid());
    if (userinfo != null) return userinfo;

    var future = Utils.readFileAsync(UltraSTS.PATH_DATA, player.getUuidAsString() + ".json", call -> {
      UserInfo userInfo = Utils.newWithoutSpacingGson().fromJson(call, UserInfo.class);
      if (userInfo != null) {
        DataBaseFactory.users.put(player.getUuid(), userInfo);
      }
    });

    if (Boolean.FALSE.equals(future.join())) {
      userinfo = new UserInfo(player);
      DataBaseFactory.users.put(player.getUuid(), userinfo);
    } else {
      userinfo = DataBaseFactory.users.get(player.getUuid());
    }
    return userinfo;
  }

  @Override public void updateUserInfo(UserInfo userInfo) {
    Utils.writeFileAsync(Utils.getAbsolutePath(UltraSTS.PATH_DATA + userInfo.getUuid() + ".json"),
      Utils.newWithoutSpacingGson().toJson(userInfo));
  }

}
