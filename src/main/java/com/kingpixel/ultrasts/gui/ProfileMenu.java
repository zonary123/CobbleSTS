package com.kingpixel.ultrasts.gui;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.Model.ItemModel;
import com.kingpixel.cobbleutils.Model.PanelsConfig;
import com.kingpixel.cobbleutils.util.AdventureTranslator;
import com.kingpixel.ultrasts.UltraSTS;
import com.kingpixel.ultrasts.models.User;
import lombok.Data;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Data
public class ProfileMenu {
  private int rows = 3;
  private String title = "Profile Menu";
  private ItemModel notifications = ItemModel.builder()
    .slot(13)
    .item("minecraft:bell")
    .displayname("&6Notifications")
    .lore(List.of("&7Toggle notifications for STS updates", "67Currently: %status%"))
    .build();
  private ItemModel close = ItemModel.builder()
    .slot(22)
    .item("minecraft:barrier")
    .displayname("&6Close")
    .lore(List.of("&7Close the profile menu"))
    .build();
  private List<PanelsConfig> panels = List.of(new PanelsConfig());

  public CompletableFuture<Void> open(ServerPlayerEntity player) {
    User user = UltraSTS.database.getUser(player.getUuid());
    if (user == null) {
      player.sendMessage(AdventureTranslator.toNative("&cAn error occurred while loading your profile. Please try again later."), false);
      return CompletableFuture.completedFuture(null);
    }
    return UltraSTS.ASYNC.runAsync(() -> {
      ChestTemplate template = ChestTemplate.builder(rows)
        .build();

      PanelsConfig.applyConfig(template, panels);


      List<String> loreNotifications = new ArrayList<>(notifications.getLore());
      loreNotifications.replaceAll(s -> s.replace("%status%", user.getOptions().isNotificationsEnabled() ? CobbleUtils.language.getYes() : CobbleUtils.language.getNo()));
      notifications.applyTemplate(template, notifications.getButton(1, null, loreNotifications, action -> {
        user.getOptions().setNotificationsEnabled(!user.getOptions().isNotificationsEnabled());
        user.markDirty();
      }, 1, TimeUnit.SECONDS, 1));

      close.applyTemplate(template, close.getButton(action -> UltraSTS.lang.getMenu().open(player), 1, TimeUnit.SECONDS, 1));

      GooeyPage page = GooeyPage.builder()
        .template(template)
        .title(AdventureTranslator.toNative(title))
        .build();

      CobbleUtils.server.execute(() -> UIManager.openUIForcefully(player, page));
    });
  }
}
