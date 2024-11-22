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
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.item.PokemonItem;
import com.kingpixel.cobblests.CobbleSTS;
import com.kingpixel.cobbleutils.util.AdventureTranslator;
import com.kingpixel.cobbleutils.util.Utils;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Carlos Varas Alonso - 25/06/2024 17:51
 */
public class STSLegend {
  public static LinkedPage open() {
    ChestTemplate template = ChestTemplate.builder(6).build();
    List<Button> buttons = new ArrayList<>();

    CobbleSTS.config.getLegends().forEach((key, value) -> {
      buttons.add(GooeyButton.builder()
        .display(PokemonItem.from(PokemonProperties.Companion.parse(key).create()))
        .title(AdventureTranslator.toNative(CobbleSTS.language.getColorhexnamepoke() + key + CobbleSTS.language.getColorSeparator() +
          CobbleSTS.language.getSeparator() + CobbleSTS.language.getColorPrice() + value))
        .build());
    });

    LinkedPageButton previus = LinkedPageButton.builder()
      .display(CobbleSTS.language.getItempreviouspage().getItemStack())
      .title(AdventureTranslator.toNative(CobbleSTS.language.getItempreviouspage().getDisplayname()))
      .linkType(LinkType.Previous)
      .build();

    LinkedPageButton next = LinkedPageButton.builder()
      .display(CobbleSTS.language.getItemnextpage().getItemStack())
      .title(AdventureTranslator.toNative(CobbleSTS.language.getItemnextpage().getDisplayname()))
      .linkType(LinkType.Next)
      .build();

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
      .rectangle(0, 0, 5, 9, placeholder)
      .fillFromList(buttons)
      .set(5, 4, close)
      .set(5, 0, previus)
      .set(5, 8, next);

    LinkedPage.Builder linkedPageBuilder = LinkedPage.builder()
      .title(CobbleSTS.language.getTitleLegends());

    LinkedPage firstPage = PaginationHelper.createPagesFromPlaceholders(template, buttons, linkedPageBuilder);
    return firstPage;
  }
}
