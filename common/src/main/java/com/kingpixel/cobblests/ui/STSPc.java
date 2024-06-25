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
import com.kingpixel.cobblests.utils.AdventureTranslator;
import com.kingpixel.cobblests.utils.Utils;
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
