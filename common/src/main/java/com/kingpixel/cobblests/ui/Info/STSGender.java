package com.kingpixel.cobblests.ui.Info;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.button.PlaceholderButton;
import ca.landonjw.gooeylibs2.api.helpers.PaginationHelper;
import ca.landonjw.gooeylibs2.api.page.LinkedPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.kingpixel.cobblests.CobbleSTS;
import com.kingpixel.cobbleutils.util.AdventureTranslator;
import com.kingpixel.cobbleutils.util.Utils;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Carlos Varas Alonso - 25/06/2024 17:51
 */
public class STSGender {
  public static LinkedPage open() {
    ChestTemplate template = ChestTemplate.builder(2).build();
    List<Button> buttons = new ArrayList<>();

    CobbleSTS.config.getGender().forEach((key, value) -> {
      buttons.add(GooeyButton.builder()
        .display(CobbleSTS.language.getItemGender().getItemStack())
        .title(AdventureTranslator.toNative(CobbleSTS.language.getColorhexItem() + CobbleSTS.language.getGender().getOrDefault(key, key) + CobbleSTS.language.getColorSeparator() +
          CobbleSTS.language.getSeparator() + CobbleSTS.language.getColorPrice() + value))
        .build());
    });

    GooeyButton close = GooeyButton.builder()
      .display(CobbleSTS.language.getItemclose().getItemStack())
      .title(AdventureTranslator.toNative(CobbleSTS.language.getItemclose().getDisplayname()))
      .lore(Component.class, AdventureTranslator.toNativeL(CobbleSTS.language.getItemclose().getLore()))
      .onClick((action) -> {
        UIManager.openUIForcefully(action.getPlayer(), STSInfo.open());
      })
      .build();

    PlaceholderButton placeholder = new PlaceholderButton();
    GooeyButton fill =
      GooeyButton.builder().display(Utils.parseItemId(CobbleSTS.language.getFill()).setHoverName(Component.literal(""))).build();
    template.fill(fill)
      .rectangle(0, 0, 1, 9, placeholder)
      .fillFromList(buttons)
      .set(STSInfo.getMidle(2), close);

    LinkedPage.Builder linkedPageBuilder = LinkedPage.builder()
      .title(CobbleSTS.language.getTitleGender());

    LinkedPage firstPage = PaginationHelper.createPagesFromPlaceholders(template, buttons, linkedPageBuilder);
    return firstPage;
  }
}
