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
import com.kingpixel.cobblests.utils.STSUtil;
import com.kingpixel.cobbleutils.Model.ItemModel;
import com.kingpixel.cobbleutils.util.AdventureTranslator;
import com.kingpixel.cobbleutils.util.PlayerUtils;
import com.kingpixel.cobbleutils.util.Utils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Carlos Varas Alonso - 26/05/2024 4:04
 */
public class STS {
  public static GooeyPage open(ServerPlayerEntity player) throws NoPokemonStoreException {

    PlayerPartyStore partyStore = Cobblemon.INSTANCE.getStorage().getParty(player);


    GooeyButton fill = GooeyButton.builder()
      .display(Utils.parseItemId(CobbleSTS.language.getFill()))
      .with(DataComponentTypes.ITEM_NAME, AdventureTranslator.toNative(""))
      .build();

    RateLimitedButton poke1 = createButtonPokemon(partyStore.get(0));
    RateLimitedButton poke2 = createButtonPokemon(partyStore.get(1));
    RateLimitedButton poke3 = createButtonPokemon(partyStore.get(2));

    GooeyButton info = GooeyButton.builder()
      .display(CobbleSTS.language.getInfo().getItemStack())
      .with(DataComponentTypes.ITEM_NAME, AdventureTranslator.toNative(CobbleSTS.language.getInfo().getDisplayname()))
      .with(DataComponentTypes.LORE,
        new LoreComponent(AdventureTranslator.toNativeL(CobbleSTS.language.getInfo().getLore())))
      .onClick((action) -> {
        if (CobbleSTS.config.isHiperinfo())
          UIManager.openUIForcefully(action.getPlayer(), Objects.requireNonNull(STSInfo.open()));
      })
      .build();

    ItemModel itemPc = CobbleSTS.language.getPc();

    GooeyButton pc = GooeyButton.builder()
      .display(itemPc.getItemStack())
      .with(DataComponentTypes.ITEM_NAME, AdventureTranslator.toNative(itemPc.getDisplayname()))
      .with(DataComponentTypes.LORE, new LoreComponent(AdventureTranslator.toNativeL(itemPc.getLore())))
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
    GooeyPage page = GooeyPage.builder()
      .template(template)
      .title(AdventureTranslator.toNative(CobbleSTS.language.getTitle()))
      .build();
    page.update();
    return page;

  }

  public static RateLimitedButton createButtonPokemon(Pokemon pokemon) {
    GooeyButton button;
    boolean isblacklist = false;
    if (pokemon != null) {
      isblacklist = CobbleSTS.config.getBlacklisted().contains(pokemon.showdownId());
    }
    try {
      if (pokemon == null) {
        button = GooeyButton.builder()
          .display(CobbleSTS.language.getNopokemon().getItemStack())
          .with(DataComponentTypes.ITEM_NAME,
            AdventureTranslator.toNative(CobbleSTS.language.getNopokemon().getDisplayname()))
          .with(DataComponentTypes.LORE, new LoreComponent(
            AdventureTranslator.toNativeL(CobbleSTS.language.getNopokemon().getLore())))
          .build();
      } else if (isblacklist) {
        button = GooeyButton.builder()
          .display(CobbleSTS.language.getItemBlacklisted().getItemStack())
          .with(DataComponentTypes.ITEM_NAME, AdventureTranslator.toNative(CobbleSTS.language.getItemBlacklisted().getDisplayname()))
          .with(DataComponentTypes.LORE, new LoreComponent(AdventureTranslator.toNativeL(CobbleSTS.language.getItemBlacklisted().getLore())))
          .build();
      } else if (pokemon.getShiny() && !CobbleSTS.config.isAllowshiny()) {
        button = GooeyButton.builder()
          .display(CobbleSTS.language.getItemNotAllowShiny().getItemStack())
          .with(DataComponentTypes.ITEM_NAME, AdventureTranslator.toNative(CobbleSTS.language.getItemNotAllowShiny().getDisplayname()))
          .with(DataComponentTypes.LORE, new LoreComponent(AdventureTranslator.toNativeL(CobbleSTS.language.getItemNotAllowShiny().getLore())))
          .build();
      } else if ((pokemon.isLegendary() || CobbleSTS.config.getIslegends().contains(pokemon.getSpecies().getName())) && !CobbleSTS.config.isAllowlegendary()) {
        button = GooeyButton.builder()
          .display(CobbleSTS.language.getItemNotAllowLegendary().getItemStack())
          .with(DataComponentTypes.ITEM_NAME, AdventureTranslator.toNative(CobbleSTS.language.getItemNotAllowLegendary().getDisplayname()))
          .with(DataComponentTypes.LORE, new LoreComponent(AdventureTranslator.toNativeL(CobbleSTS.language.getItemNotAllowLegendary().getLore())))
          .build();
      } else {
        button = GooeyButton.builder()
          .display(PokemonItem.from(pokemon))
          .with(DataComponentTypes.ITEM_NAME, AdventureTranslator.toNative(CobbleSTS.language.getColorhexnamepoke() + pokemon.getSpecies().getName()))
          .with(DataComponentTypes.LORE, new LoreComponent(AdventureTranslator.toNativeL(STSUtil.formatPokemonLore(pokemon))))
          .onClick((action) -> {
            if (CobbleSTS.config.isHavecooldown()) {
              if (CobbleSTS.manager.hasCooldownEnded(action.getPlayer())) {
                UIManager.openUIForcefully(action.getPlayer(), Objects.requireNonNull(STSConfirm.open(
                  pokemon)));
              } else {
                PlayerUtils.sendMessage(
                  action.getPlayer(),
                  CobbleSTS.manager.formatTime(action.getPlayer()),
                  CobbleSTS.language.getPrefix()
                );
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
