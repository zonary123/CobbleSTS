package com.kingpixel.cobblests.ui.Info;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.button.PlaceholderButton;
import ca.landonjw.gooeylibs2.api.button.linked.LinkType;
import ca.landonjw.gooeylibs2.api.button.linked.LinkedPageButton;
import ca.landonjw.gooeylibs2.api.helpers.PaginationHelper;
import ca.landonjw.gooeylibs2.api.page.LinkedPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.kingpixel.cobblests.CobbleSTS;
import com.kingpixel.cobblests.utils.AdventureTranslator;
import com.kingpixel.cobblests.utils.Utils;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Carlos Varas Alonso - 25/06/2024 17:51
 */
public class STSAbility {
  public static LinkedPage open() {
    ChestTemplate template = ChestTemplate.builder(6).build();
    List<Button> buttons = new ArrayList<>();

    CobbleSTS.config.getAbility().forEach((key, value) -> {
      buttons.add(GooeyButton.builder()
        .display(Utils.parseItemId(CobbleSTS.language.getItemAbility().getId()))
        .title(AdventureTranslator.toNative(CobbleSTS.language.getColorhexItem() + CobbleSTS.language.getAbility().getOrDefault(key, key) + CobbleSTS.language.getColorSeparator() +
          CobbleSTS.language.getSeparator() + CobbleSTS.language.getColorPrice() + value))
        .build());
    });

    LinkedPageButton previus = LinkedPageButton.builder()
      .display(Utils.parseItemId(CobbleSTS.language.getItempreviouspage().getId()))
      .title(AdventureTranslator.toNative(CobbleSTS.language.getItempreviouspage().getTitle()))
      .linkType(LinkType.Previous)
      .build();

    LinkedPageButton next = LinkedPageButton.builder()
      .display(Utils.parseItemId(CobbleSTS.language.getItemnextpage().getId()))
      .title(AdventureTranslator.toNative(CobbleSTS.language.getItemnextpage().getTitle()))
      .linkType(LinkType.Next)
      .build();

    GooeyButton close = GooeyButton.builder()
      .display(Utils.parseItemId(CobbleSTS.language.getItemclose().getId()))
      .title(AdventureTranslator.toNative(CobbleSTS.language.getItemclose().getTitle()))
      .lore(Component.class, AdventureTranslator.toNativeL(CobbleSTS.language.getItemclose().getLore()))
      .onClick((action) -> {
        UIManager.openUIForcefully(action.getPlayer(), STSInfo.open());
      })
      .build();

    PlaceholderButton placeholder = new PlaceholderButton();
    GooeyButton fill =
      GooeyButton.builder().display(Utils.parseItemId(CobbleSTS.language.getFill()).setHoverName(Component.literal(""))).build();
    template.fill(fill)
      .rectangle(0, 0, 5, 9, placeholder)
      .fillFromList(buttons)
      .set(5, 4, close)
      .set(5, 0, previus)
      .set(5, 8, next);

    LinkedPage.Builder linkedPageBuilder = LinkedPage.builder()
      .title(CobbleSTS.language.getTitleAbility());

    LinkedPage firstPage = PaginationHelper.createPagesFromPlaceholders(template, buttons, linkedPageBuilder);
    return firstPage;
  }
}
