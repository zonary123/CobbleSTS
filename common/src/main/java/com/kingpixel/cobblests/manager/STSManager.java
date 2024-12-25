package com.kingpixel.cobblests.manager;

import com.kingpixel.cobblests.CobbleSTS;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;


/**
 * @author Carlos Varas Alonso - 29/04/2024 1:31
 */
public class STSManager {
  private HashMap<UUID, UserInfo> userInfo;

  public class UserInfo {
    private boolean messagesend;
    private Date date;

    public UserInfo(Date date) {
      this.date = date;
      this.messagesend = false;
    }

    public boolean isMessagesend() {
      return messagesend;
    }

    public Date getDate() {
      return date;
    }

    public void setMessagesend(boolean messagesend) {
      this.messagesend = messagesend;
    }

    public void setDate(Date date) {
      this.date = date;
    }
  }

  public HashMap<UUID, UserInfo> getUserInfo() {
    return userInfo;
  }

  public STSManager() {
    userInfo = new HashMap<>();
  }


  public void addPlayer(Entity player) {
    userInfo.putIfAbsent(player.getUuid(), new UserInfo(new Date()));
  }

  public void init() {
    for (ServerPlayerEntity player : CobbleSTS.server.getPlayerManager().getPlayerList()) {
      addPlayer(player);
    }
  }

  public void addPlayerWithDate(Entity player, int minutes) {
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MINUTE, minutes);
    Date futureDate = calendar.getTime();
    userInfo.put(player.getUuid(), new UserInfo(futureDate));
    userInfo.get(player.getUuid()).setMessagesend(false);
  }

  public boolean hasCooldownEnded(Entity player) {
    UserInfo userDate = userInfo.get(player.getUuid());
    if (userDate == null) {
      return true;
    }

    Date now = new Date();
    return now.after(userDate.getDate());
  }

  public String formatTime(Entity player) {
    UserInfo userDate = userInfo.get(player.getUuid());
    if (userDate == null) {
      return "0";
    }

    Date now = new Date();
    long diff = userDate.getDate().getTime() - now.getTime();
    long diffSeconds = diff / 1000 % 60;
    long diffMinutes = diff / (60 * 1000) % 60;
    long diffHours = diff / (60 * 60 * 1000) % 24;
    long diffDays = diff / (24 * 60 * 60 * 1000);

    String messagecooldown = CobbleSTS.language.getMessagecooldown();
    messagecooldown = messagecooldown.replace("%day%", Long.toString(diffDays))
      .replace("%hour%", Long.toString(diffHours))
      .replace("%minut%", Long.toString(diffMinutes))
      .replace("%seconds%", Long.toString(diffSeconds));
    return messagecooldown;
  }

  @Override public String toString() {
    return "WonderTradeManager{" +
      "userInfo=" + userInfo +
      '}';
  }
}