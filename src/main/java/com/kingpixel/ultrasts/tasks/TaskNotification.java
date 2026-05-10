package com.kingpixel.ultrasts.tasks;

import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.util.PlayerUtils;
import com.kingpixel.cobbleutils.util.TypeMessage;
import com.kingpixel.ultrasts.UltraSTS;
import com.kingpixel.ultrasts.database.DatabaseClient;
import com.kingpixel.ultrasts.models.User;

import java.util.concurrent.TimeUnit;

public class TaskNotification {
  public static void register() {
    var notification = UltraSTS.config.getNotificationCooldown().toMillis();
    UltraSTS.getAsyncContext().scheduleAtFixedRate(() -> {
      var users = DatabaseClient.USERS.asMap().values();
      for (User user : users) {
        if (!user.getOptions().isNotificationsEnabled()) continue;
        if (user.hasSomeWithoutCooldown()) {
          CobbleUtils.server.execute(() -> {
            var player = CobbleUtils.server.getPlayerManager().getPlayer(user.getUuid());
            if (player == null) return;
            PlayerUtils.sendMessage(
              user.getUuid(),
              UltraSTS.lang.getNotificationSTSAvailable(),
              UltraSTS.lang.getPrefix(),
              TypeMessage.CHAT
            );
          });
        }
      }
    }, notification, notification, TimeUnit.MILLISECONDS);
  }
}
