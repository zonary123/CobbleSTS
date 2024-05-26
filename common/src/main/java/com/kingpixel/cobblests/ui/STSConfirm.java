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
import com.kingpixel.cobblests.utils.TextUtil;
import com.kingpixel.cobblests.utils.Utils;

import java.util.Objects;

/**
 * @author Carlos Varas Alonso - 26/05/2024 16:17
 */
public class STSConfirm {
  public static GooeyPage open(Pokemon pokemon, int index) {
    GooeyButton fill = GooeyButton.builder()
      .display(Utils.parseItemId(CobbleSTS.language.getFill()))
      .title("")
      .build();
    GooeyButton confirm = GooeyButton.builder()
      .display(Utils.parseItemId(CobbleSTS.language.getConfirm().getId()))
      .title(TextUtil.parseHexCodes(CobbleSTS.language.getConfirm().getTitle()))
      .lore(CobbleSTS.language.getConfirm().getLore())
      .onClick(action -> {
        STSUtil.Sell(pokemon, true, action.getPlayer(), index);
        try {
          UIManager.openUIForcefully(action.getPlayer(), Objects.requireNonNull(STS.open(action.getPlayer())));
        } catch (NoPokemonStoreException e) {
          e.printStackTrace();
        }
      })
      .build();

    GooeyButton pokebutton = GooeyButton.builder()
      .display(PokemonItem.from(pokemon))
      .title(pokemon.getSpecies().getName())
      .lore(STSUtil.formatPokemonLore(pokemon))
      .build();

    GooeyButton cancel = GooeyButton.builder()
      .display(Utils.parseItemId(CobbleSTS.language.getCancel().getId()))
      .title(TextUtil.parseHexCodes(CobbleSTS.language.getCancel().getTitle()))
      .lore(CobbleSTS.language.getCancel().getLore())
      .build();


    ChestTemplate template = ChestTemplate.builder(3)
      .fill(fill)
      .set(1, 2, confirm)
      .set(1, 4, pokebutton)
      .set(1, 6, cancel)
      .build();

    GooeyPage page =
      GooeyPage.builder().title(TextUtil.parseHexCodes(CobbleSTS.language.getTitleconfirm())).template(template).build();
    return page;
  }
}
