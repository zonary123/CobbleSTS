package com.kingpixel.cobblests.ui;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblests.CobbleSTS;
import com.kingpixel.cobblests.utils.AdventureTranslator;
import com.kingpixel.cobblests.utils.STSUtil;
import com.kingpixel.cobblests.utils.Utils;
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
    GooeyButton confirm = GooeyButton.builder()
      .display(Utils.parseItemId(CobbleSTS.language.getConfirm().getId()))
      .title(AdventureTranslator.toNative(CobbleSTS.language.getConfirm().getTitle()))
      .lore(Component.class, AdventureTranslator.toNativeL(CobbleSTS.language.getConfirm().getLore()))
      .onClick(action -> {
        STSUtil.Sell(pokemon, true, action.getPlayer());
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

    GooeyButton cancel = GooeyButton.builder()
      .display(Utils.parseItemId(CobbleSTS.language.getCancel().getId()))
      .title(AdventureTranslator.toNative(CobbleSTS.language.getCancel().getTitle()))
      .lore(Component.class, AdventureTranslator.toNativeL(CobbleSTS.language.getCancel().getLore()))
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
