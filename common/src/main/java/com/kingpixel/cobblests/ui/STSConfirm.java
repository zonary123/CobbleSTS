package com.kingpixel.cobblests.ui;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblests.CobbleSTS;
import com.kingpixel.cobblests.utils.STSUtil;
import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.Model.ItemModel;
import com.kingpixel.cobbleutils.util.AdventureTranslator;
import com.kingpixel.cobbleutils.util.Utils;
import net.minecraft.network.chat.Component;

import java.util.Objects;

/**
 * @author Carlos Varas Alonso - 26/05/2024 16:17
 */
public class STSConfirm {
  public static GooeyPage open(Pokemon pokemon) {
    GooeyButton fill = GooeyButton.builder()
      .display(Utils.parseItemId(CobbleSTS.language.getFill()))
      .title("")
      .build();

    ItemModel itemConfirm = CobbleSTS.language.getConfirm();
    if (CobbleSTS.config.isUseCobbleUtilsItems()) {
      itemConfirm = CobbleUtils.language.getItemConfirm();
    }

    GooeyButton confirm = GooeyButton.builder()
      .display(itemConfirm.getItemStack())
      .onClick(action -> {
        STSUtil.Sell(pokemon, true, action.getPlayer(), false);
        try {
          UIManager.openUIForcefully(action.getPlayer(), Objects.requireNonNull(STS.open(action.getPlayer())));
        } catch (NoPokemonStoreException e) {
          e.printStackTrace();
        }
      })
      .build();

    GooeyButton pokebutton = GooeyButton.builder()
      .display(PokemonItem.from(pokemon))
      .title(AdventureTranslator.toNative(CobbleSTS.language.getColorhexnamepoke() + pokemon.getSpecies().getName()))
      .lore(Component.class, AdventureTranslator.toNativeL(STSUtil.formatPokemonLore(pokemon)))
      .build();

    ItemModel itemCancel = CobbleSTS.language.getCancel();
    if (CobbleSTS.config.isUseCobbleUtilsItems()) {
      itemCancel = CobbleUtils.language.getItemCancel();
    }
    GooeyButton cancel = GooeyButton.builder()
      .display(itemCancel.getItemStack())
      .onClick(action -> {
        try {
          UIManager.openUIForcefully(action.getPlayer(), Objects.requireNonNull(STS.open(action.getPlayer())));
        } catch (NoPokemonStoreException e) {
          throw new RuntimeException(e);
        }
      })
      .build();


    ChestTemplate template = ChestTemplate.builder(3)
      .fill(fill)
      .set(1, 2, confirm)
      .set(1, 4, pokebutton)
      .set(1, 6, cancel)
      .build();

    return GooeyPage.builder().title(AdventureTranslator.toNative(CobbleSTS.language.getTitleconfirm())).template(template).build();
  }
}
