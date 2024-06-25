package com.kingpixel.cobblests.ui;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.button.RateLimitedButton;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblests.CobbleSTS;
import com.kingpixel.cobblests.ui.Info.STSInfo;
import com.kingpixel.cobblests.utils.AdventureTranslator;
import com.kingpixel.cobblests.utils.STSUtil;
import com.kingpixel.cobblests.utils.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

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

      RateLimitedButton poke1 = createButtonPokemon(partyStore.get(0));
      RateLimitedButton poke2 = createButtonPokemon(partyStore.get(1));
      RateLimitedButton poke3 = createButtonPokemon(partyStore.get(2));

      GooeyButton info = GooeyButton.builder()
        .display(Utils.parseItemId(CobbleSTS.language.getInfo().getId()))
        .title(AdventureTranslator.toNative(CobbleSTS.language.getInfo().getTitle()))
        .lore(Component.class, AdventureTranslator.toNativeL(CobbleSTS.language.getInfo().getLore()))
        .onClick((action) -> {
          if (CobbleSTS.config.isHiperinfo())
            UIManager.openUIForcefully(action.getPlayer(), Objects.requireNonNull(STSInfo.open()));
        })
        .build();

      GooeyButton pc = GooeyButton.builder()
        .display(Utils.parseItemId(CobbleSTS.language.getPc().getId()))
        .title(AdventureTranslator.toNative(CobbleSTS.language.getPc().getTitle()))
        .lore(Component.class, AdventureTranslator.toNativeL(CobbleSTS.language.getPc().getLore()))
        .onClick((action) -> {
          try {
            UIManager.openUIForcefully(action.getPlayer(), Objects.requireNonNull(STSPc.open(player)));
          } catch (NoPokemonStoreException e) {
            throw new RuntimeException(e);
          }
        })
        .build();

      RateLimitedButton poke4 = createButtonPokemon(partyStore.get(3));
      RateLimitedButton poke5 = createButtonPokemon(partyStore.get(4));
      RateLimitedButton poke6 = createButtonPokemon(partyStore.get(5));


      ChestTemplate template = ChestTemplate.builder(3)
        .fill(fill)
        .set(1, 1, poke1)
        .set(1, 2, poke2)
        .set(1, 3, poke3)
        .set(0, 4, pc)
        .set(1, 4, info)
        .set(1, 5, poke4)
        .set(1, 6, poke5)
        .set(1, 7, poke6)
        .build();
      GooeyPage page = GooeyPage.builder().template(template).title(AdventureTranslator.toNative(CobbleSTS.language.getTitle())).build();
      page.update();
      return page;
    } catch (NoPokemonStoreException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static RateLimitedButton createButtonPokemon(Pokemon pokemon) {
    GooeyButton button;
    try {
      if (pokemon == null) {
        button = GooeyButton.builder()
          .display(Utils.parseItemId(CobbleSTS.language.getNopokemon().getId()))
          .title(AdventureTranslator.toNative(CobbleSTS.language.getNopokemon().getTitle()))
          .lore(Component.class, AdventureTranslator.toNativeL(CobbleSTS.language.getNopokemon().getLore()))
          .build();
      } else if (pokemon.getShiny() && !CobbleSTS.config.isAllowshiny()) {
        button = GooeyButton.builder()
          .display(Utils.parseItemId(CobbleSTS.language.getItemNotAllowShiny().getId()))
          .title(AdventureTranslator.toNative(CobbleSTS.language.getItemNotAllowShiny().getTitle()))
          .lore(Component.class, AdventureTranslator.toNativeL(CobbleSTS.language.getItemNotAllowShiny().getLore()))
          .build();
      } else if ((pokemon.isLegendary() || CobbleSTS.config.getIslegends().contains(pokemon.getSpecies().getName())) && !CobbleSTS.config.isAllowlegendary()) {
        button = GooeyButton.builder()
          .display(Utils.parseItemId(CobbleSTS.language.getItemNotAllowLegendary().getId()))
          .title(AdventureTranslator.toNative(CobbleSTS.language.getItemNotAllowLegendary().getTitle()))
          .lore(Component.class, AdventureTranslator.toNativeL(CobbleSTS.language.getItemNotAllowLegendary().getLore()))
          .build();
      } else {
        button = GooeyButton.builder()
          .display(PokemonItem.from(pokemon))
          .title(AdventureTranslator.toNative(CobbleSTS.language.getColorhexnamepoke() + pokemon.getSpecies().getName()))
          .lore(Component.class, AdventureTranslator.toNativeL(STSUtil.formatPokemonLore(pokemon)))
          .onClick((action) -> {
            if (CobbleSTS.config.isHavecooldown()) {
              if (CobbleSTS.manager.hasCooldownEnded(action.getPlayer())) {
                UIManager.openUIForcefully(action.getPlayer(), Objects.requireNonNull(STSConfirm.open(
                  pokemon)));
              } else {
                action.getPlayer().sendSystemMessage(AdventureTranslator.toNative(CobbleSTS.manager.formatTime(action.getPlayer())));
              }
            } else {
              UIManager.openUIForcefully(action.getPlayer(), Objects.requireNonNull(STSConfirm.open(
                pokemon)));
            }
          })
          .build();
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    return RateLimitedButton.builder().button(button).limit(1).interval(3, TimeUnit.SECONDS).build();
  }
}
