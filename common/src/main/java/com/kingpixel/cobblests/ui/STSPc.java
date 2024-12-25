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
import com.kingpixel.cobbleutils.Model.ItemModel;
import com.kingpixel.cobbleutils.util.AdventureTranslator;
import com.kingpixel.cobbleutils.util.Utils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Carlos Varas Alonso - 25/06/2024 4:02
 */
public class STSPc {
  public static LinkedPage open(ServerPlayerEntity player) throws NoPokemonStoreException {
    ChestTemplate template = ChestTemplate.builder(6).build();
    List<Button> buttons = new ArrayList<>();
    PCStore pcStore = Cobblemon.INSTANCE.getStorage().getPC(player);

    pcStore.forEach((pokemon) -> {
      buttons.add(STS.createButtonPokemon(pokemon));
    });

    ItemModel itemPrevious = CobbleSTS.language.getItempreviouspage();
    LinkedPageButton previus = LinkedPageButton.builder()
      .display(itemPrevious.getItemStack())
      .linkType(LinkType.Previous)
      .build();

    ItemModel itemNext = CobbleSTS.language.getItemnextpage();
    LinkedPageButton next = LinkedPageButton.builder()
      .display(itemNext.getItemStack())
      .linkType(LinkType.Next)
      .build();

    ItemModel itemClose = CobbleSTS.language.getItemclose();
    GooeyButton close = GooeyButton.builder()
      .display(itemClose.getItemStack())
      .onClick((action) -> {
        try {
          UIManager.openUIForcefully(action.getPlayer(), STS.open(player));
        } catch (NoPokemonStoreException e) {
          throw new RuntimeException(e);
        }
      })
      .build();

    PlaceholderButton placeholder = new PlaceholderButton();
    ItemStack itemFill = Utils.parseItemId(CobbleSTS.language.getFill());
    itemFill.set(DataComponentTypes.ITEM_NAME, AdventureTranslator.toNative(""));
    GooeyButton fill =
      GooeyButton.builder()
        .display(itemFill)
        .build();
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
