package com.kingpixel.cobblests.ui;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.button.PlaceholderButton;
import ca.landonjw.gooeylibs2.api.button.linked.LinkType;
import ca.landonjw.gooeylibs2.api.button.linked.LinkedPageButton;
import ca.landonjw.gooeylibs2.api.helpers.PaginationHelper;
import ca.landonjw.gooeylibs2.api.page.LinkedPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.api.storage.pc.PCStore;
import com.kingpixel.cobblests.CobbleSTS;
import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.Model.ItemModel;
import com.kingpixel.cobbleutils.util.AdventureTranslator;
import com.kingpixel.cobbleutils.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Carlos Varas Alonso - 25/06/2024 4:02
 */
public class STSPc {
  public static LinkedPage open(Player player) throws NoPokemonStoreException {
    ChestTemplate template = ChestTemplate.builder(6).build();
    List<Button> buttons = new ArrayList<>();
    PCStore pcStore = Cobblemon.INSTANCE.getStorage().getPC(player.getUUID());

    pcStore.forEach((pokemon) -> {
      buttons.add(STS.createButtonPokemon(pokemon));
    });

    ItemModel itemPrevious = CobbleSTS.language.getItempreviouspage();
    if (CobbleSTS.config.isUseCobbleUtilsItems()) {
      itemPrevious = CobbleUtils.language.getItemPrevious();
    }
    LinkedPageButton previus = LinkedPageButton.builder()
      .display(itemPrevious.getItemStack())
      .title(AdventureTranslator.toNative(itemPrevious.getDisplayname()))
      .linkType(LinkType.Previous)
      .build();

    ItemModel itemNext = CobbleSTS.language.getItemnextpage();
    if (CobbleSTS.config.isUseCobbleUtilsItems()) {
      itemNext = CobbleUtils.language.getItemNext();
    }

    LinkedPageButton next = LinkedPageButton.builder()
      .display(itemNext.getItemStack())
      .title(AdventureTranslator.toNative(itemNext.getDisplayname()))
      .linkType(LinkType.Next)
      .build();

    ItemModel itemClose = CobbleSTS.language.getItemclose();
    if (CobbleSTS.config.isUseCobbleUtilsItems()) {
      itemClose = CobbleUtils.language.getItemClose();
    }
    GooeyButton close = GooeyButton.builder()
      .display(itemClose.getItemStack())
      .title(AdventureTranslator.toNative(itemClose.getDisplayname()))
      .lore(Component.class, AdventureTranslator.toNativeL(itemClose.getLore()))
      .onClick((action) -> {
        try {
          UIManager.openUIForcefully(action.getPlayer(), STS.open(player));
        } catch (NoPokemonStoreException e) {
          throw new RuntimeException(e);
        }
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
      .title(CobbleSTS.language.getTitlePc());

    LinkedPage firstPage = PaginationHelper.createPagesFromPlaceholders(template, buttons, linkedPageBuilder);
    return firstPage;
  }
}
