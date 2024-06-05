package com.kingpixel.cobblests.ui;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblests.CobbleSTS;
import com.kingpixel.cobblests.utils.STSUtil;
import com.kingpixel.cobblests.utils.TextUtil;
import com.kingpixel.cobblests.utils.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

/**
 * @author Carlos Varas Alonso - 26/05/2024 4:04
 */
public class STS {
  public static GooeyPage open(Player player) throws NoPokemonStoreException {
    try {
      PlayerPartyStore partyStore = Cobblemon.INSTANCE.getStorage().getParty(player.getUUID());


      GooeyButton fill = GooeyButton.builder()
        .display(Utils.parseItemId(CobbleSTS.language.getFill()))
        .title("")
        .build();

      GooeyButton poke1 = createButtonPokemon(partyStore.get(0));
      GooeyButton poke2 = createButtonPokemon(partyStore.get(1));
      GooeyButton poke3 = createButtonPokemon(partyStore.get(2));

      GooeyButton info = GooeyButton.builder()
        .display(Utils.parseItemId(CobbleSTS.language.getInfo().getId()))
        .title(TextUtil.parseHexCodes(CobbleSTS.language.getInfo().getTitle()))
        .lore(Component.class, TextUtil.parseHexCodes(CobbleSTS.language.getInfo().getLore()))
        .build();

      GooeyButton poke4 = createButtonPokemon(partyStore.get(3));
      GooeyButton poke5 = createButtonPokemon(partyStore.get(4));
      GooeyButton poke6 = createButtonPokemon(partyStore.get(5));


      ChestTemplate template = ChestTemplate.builder(3)
        .fill(fill)
        .set(1, 1, poke1)
        .set(1, 2, poke2)
        .set(1, 3, poke3)
        .set(1, 4, info)
        .set(1, 5, poke4)
        .set(1, 6, poke5)
        .set(1, 7, poke6)
        .build();
      GooeyPage page = GooeyPage.builder().template(template).title(TextUtil.parseHexCodes(CobbleSTS.language.getTitle())).build();
      page.update();
      return page;
    } catch (NoPokemonStoreException e) {
      e.printStackTrace();
    }
    return null;
  }

  private static GooeyButton createButtonPokemon(Pokemon pokemon) {
    try {
      if (pokemon == null) {
        return GooeyButton.builder()
          .display(Utils.parseItemId(CobbleSTS.language.getNopokemon().getId()))
          .title(TextUtil.parseHexCodes(CobbleSTS.language.getNopokemon().getTitle()))
          .lore(Component.class, TextUtil.parseHexCodes(CobbleSTS.language.getNopokemon().getLore()))
          .build();
      }
      if (pokemon.getShiny() && !CobbleSTS.config.isAllowshiny()) {
        return GooeyButton.builder()
          .display(Utils.parseItemId(CobbleSTS.language.getItemNotAllowShiny().getId()))
          .title(TextUtil.parseHexCodes(CobbleSTS.language.getItemNotAllowShiny().getTitle()))
          .lore(Component.class, TextUtil.parseHexCodes(CobbleSTS.language.getItemNotAllowShiny().getLore()))
          .build();
      }
      if ((pokemon.isLegendary() || CobbleSTS.config.getIslegends().contains(pokemon.getSpecies().getName())) && !CobbleSTS.config.isAllowlegendary()) {
        return GooeyButton.builder()
          .display(Utils.parseItemId(CobbleSTS.language.getItemNotAllowLegendary().getId()))
          .title(TextUtil.parseHexCodes(CobbleSTS.language.getItemNotAllowLegendary().getTitle()))
          .lore(Component.class, TextUtil.parseHexCodes(CobbleSTS.language.getItemNotAllowLegendary().getLore()))
          .build();
      }
      return GooeyButton.builder()
        .display(PokemonItem.from(pokemon))
        .title(TextUtil.parseHexCodes(CobbleSTS.language.getColorhexnamepoke() + pokemon.getSpecies().getName()))
        .lore(Component.class, TextUtil.parseHexCodes(STSUtil.formatPokemonLore(pokemon)))
        .onClick((action) -> {
          if (CobbleSTS.config.isHavecooldown()) {
            if (CobbleSTS.manager.hasCooldownEnded(action.getPlayer())) {
              UIManager.openUIForcefully(action.getPlayer(), Objects.requireNonNull(STSConfirm.open(
                pokemon)));
            } else {
              action.getPlayer().sendSystemMessage(TextUtil.parseHexCodes(CobbleSTS.manager.formatTime(action.getPlayer())));
            }
          } else {
            UIManager.openUIForcefully(action.getPlayer(), Objects.requireNonNull(STSConfirm.open(
              pokemon)));
          }
        })
        .build();

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
