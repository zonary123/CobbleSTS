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
import com.kingpixel.cobbleutils.util.AdventureTranslator;
import com.kingpixel.cobbleutils.util.Utils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

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
        .display(CobbleSTS.language.getItemAbility().getItemStack())
        .with(DataComponentTypes.ITEM_NAME,
          AdventureTranslator.toNative(CobbleSTS.language.getColorhexItem() + CobbleSTS.language.getAbility().getOrDefault(key, key) + CobbleSTS.language.getColorSeparator() +
            CobbleSTS.language.getSeparator() + CobbleSTS.language.getColorPrice() + value))
        .build());
    });

    ItemStack itemPrevious = CobbleSTS.language.getItempreviouspage().getItemStack();
    LinkedPageButton previus = LinkedPageButton.builder()
      .display(itemPrevious)
      .linkType(LinkType.Previous)
      .build();

    ItemStack itemNext = CobbleSTS.language.getItemnextpage().getItemStack();
    LinkedPageButton next = LinkedPageButton.builder()
      .display(itemNext)
      .linkType(LinkType.Next)
      .build();

    ItemStack itemClose = CobbleSTS.language.getItemclose().getItemStack();
    GooeyButton close = GooeyButton.builder()
      .display(itemClose)
      .onClick((action) -> {
        UIManager.openUIForcefully(action.getPlayer(), STSInfo.open());
      })
      .build();

    PlaceholderButton placeholder = new PlaceholderButton();
    ItemStack itemFill = Utils.parseItemId(CobbleSTS.language.getFill());
    itemFill.set(DataComponentTypes.ITEM_NAME, Text.of(""));
    GooeyButton fill =
      GooeyButton
        .builder()
        .display(itemFill)
        .build();
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
