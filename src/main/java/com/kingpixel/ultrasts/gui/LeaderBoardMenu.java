package com.kingpixel.ultrasts.gui;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.Model.ItemModel;
import com.kingpixel.cobbleutils.Model.PanelsConfig;
import com.kingpixel.cobbleutils.Model.Rectangle;
import com.kingpixel.cobbleutils.util.AdventureTranslator;
import com.kingpixel.cobbleutils.util.PlayerUtils;
import com.kingpixel.ultrasts.UltraSTS;
import com.kingpixel.ultrasts.models.STS;
import com.kingpixel.ultrasts.models.User;
import lombok.Data;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Data
public class LeaderBoardMenu {
  private int rows = 6;
  private String title = "&6STS Leaderboard";
  private Rectangle rectangle = new Rectangle(rows);
  private ItemModel user = ItemModel.builder()
    .item("minecraft:player_head")
    .displayname("&6%username%")
    .lore(List.of(
      "&7Rank: %rank%",
      "&7Username: %username%",
      "&7Money Earned: %money_earned%"
    ))
    .build();
  private ItemModel previousPage = ItemModel.builder()
    .slot(rows * 9 - 7)
    .item("minecraft:arrow")
    .displayname("&6Previous Page")
    .lore(List.of("&7Go to the previous page of the leaderboard"))
    .build();
  private ItemModel close = ItemModel.builder()
    .slot(rows * 9 - 5)
    .item("minecraft:barrier")
    .displayname("&6Close")
    .lore(List.of("&7Close the leaderboard menu"))
    .build();
  private ItemModel nextPage = ItemModel.builder()
    .slot(rows * 9 - 3)
    .item("minecraft:arrow")
    .displayname("&6Next Page")
    .lore(List.of("&7Go to the next page of the leaderboard"))
    .build();
  private List<PanelsConfig> panels = List.of(new PanelsConfig(rows));

  public CompletableFuture<Void> open(ServerPlayerEntity player, STS value, int numPage) {
    return UltraSTS.database.findTopUsers(rectangle.getTotalSlots(), numPage, value)
      .exceptionally(e -> {
        e.printStackTrace();
        return null;
      })
      .thenCompose((users) -> {
        try {
          ChestTemplate template = ChestTemplate.builder(rows)
            .build();

          PanelsConfig.applyConfig(template, panels);
          int index = numPage * rectangle.getTotalSlots() + 1;
          List<GooeyButton> buttons = new ArrayList<>();
          for (User user : users) {
            buttons.add(getButton(user, index, value));
          }
          rectangle.apply(template, buttons);

          if (numPage > 0) {
            previousPage.applyTemplate(template, previousPage.getButton(action -> open(player, value, numPage - 1), 1, TimeUnit.SECONDS, 1));
          }

          close.applyTemplate(template, close.getButton(action -> UltraSTS.lang.getMenu().open(player), 1, TimeUnit.SECONDS, 1));

          if (users.size() == rectangle.getTotalSlots()) {
            nextPage.applyTemplate(template, nextPage.getButton(action -> open(player, value, numPage + 1), 1, TimeUnit.SECONDS, 1));
          }

          GooeyPage page = GooeyPage.builder()
            .template(template)
            .title(AdventureTranslator.toNative(title))
            .build();

          CobbleUtils.server.execute(() -> UIManager.openUIForcefully(player, page));
          return null;
        } catch (Exception e) {
          e.printStackTrace();
          return null;
        }
      });
  }

  private GooeyButton getButton(User user, int index, STS value) {
    String displayName = user.getUsername()
      .replace("%rank%", String.valueOf(index))
      .replace("%username%", user.getUsername())
      .replace("%money_earned%", String.valueOf(user.getMoneyEarned(value)));
    List<String> lore = new ArrayList<>(getUser().getLore());
    lore.replaceAll(s -> s
      .replace("%rank%", String.valueOf(index))
      .replace("%username%", user.getUsername())
      .replace("%money_earned%", String.valueOf(user.getMoneyEarned(value))));
    return GooeyButton.builder()
      .display(PlayerUtils.getHeadItem(user.getUuid()))
      .with(DataComponentTypes.CUSTOM_NAME, AdventureTranslator.toNative(displayName))
      .with(DataComponentTypes.LORE, new LoreComponent(
        AdventureTranslator.toNativeL(
          lore
        )
      ))
      .build();
  }
}
