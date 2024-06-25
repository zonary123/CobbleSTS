package com.kingpixel.cobblests.ui.Info;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblests.CobbleSTS;
import com.kingpixel.cobblests.ui.STS;
import com.kingpixel.cobblests.utils.AdventureTranslator;
import com.kingpixel.cobblests.utils.Utils;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Carlos Varas Alonso - 25/06/2024 16:50
 */
public class STSInfo {
  public static GooeyPage open() {
    ChestTemplate template = ChestTemplate.builder(CobbleSTS.config.getGuiinforows()).build();

    GooeyButton fill = GooeyButton.builder()
      .display(Utils.parseItemId(CobbleSTS.language.getFill()))
      .title("")
      .build();

    List<String> loregender = new ArrayList<>(CobbleSTS.language.getItemGender().getLore());
    loregender.replaceAll(s -> s.replace("%base%", String.valueOf(CobbleSTS.config.getDefaultgender())));

    GooeyButton gender = GooeyButton.builder()
      .display(Utils.parseItemId(CobbleSTS.language.getItemGender().getId()))
      .title(AdventureTranslator.toNative(CobbleSTS.language.getItemGender().getTitle()))
      .lore(Component.class, AdventureTranslator.toNativeL(loregender))
      .onClick(action -> UIManager.openUIForcefully(action.getPlayer(), Objects.requireNonNull(STSGender.open())))
      .build();

    List<String> loreform = new ArrayList<>(CobbleSTS.language.getItemForm().getLore());
    loreform.replaceAll(s -> s.replace("%base%", String.valueOf(CobbleSTS.config.getDefaultform())));

    GooeyButton form = GooeyButton.builder()
      .display(Utils.parseItemId(CobbleSTS.language.getItemForm().getId()))
      .title(AdventureTranslator.toNative(CobbleSTS.language.getItemForm().getTitle()))
      .lore(Component.class, AdventureTranslator.toNativeL(loreform))
      .onClick(action -> UIManager.openUIForcefully(action.getPlayer(), Objects.requireNonNull(STSForm.open())))
      .build();

    List<String> lorenature = new ArrayList<>(CobbleSTS.language.getItemNature().getLore());
    lorenature.replaceAll(s -> s.replace("%base%", String.valueOf(CobbleSTS.config.getDefaultnature())));

    GooeyButton nature = GooeyButton.builder()
      .display(Utils.parseItemId(CobbleSTS.language.getItemNature().getId()))
      .title(AdventureTranslator.toNative(CobbleSTS.language.getItemNature().getTitle()))
      .lore(Component.class, AdventureTranslator.toNativeL(lorenature))
      .onClick(action -> UIManager.openUIForcefully(action.getPlayer(), Objects.requireNonNull(STSNature.open())))
      .build();

    List<String> loreability = new ArrayList<>(CobbleSTS.language.getItemAbility().getLore());
    loreability.replaceAll(s -> s.replace("%base%", String.valueOf(CobbleSTS.config.getDefaultability())));

    GooeyButton ability = GooeyButton.builder()
      .display(Utils.parseItemId(CobbleSTS.language.getItemAbility().getId()))
      .title(AdventureTranslator.toNative(CobbleSTS.language.getItemAbility().getTitle()))
      .lore(Component.class, AdventureTranslator.toNativeL(loreability))
      .onClick(action -> UIManager.openUIForcefully(action.getPlayer(), Objects.requireNonNull(STSAbility.open())))
      .build();

    List<String> loreball = new ArrayList<>(CobbleSTS.language.getItemBall().getLore());
    loreball.replaceAll(s -> s.replace("%base%", String.valueOf(CobbleSTS.config.getDefaultball())));

    GooeyButton ball = GooeyButton.builder()
      .display(Utils.parseItemId(CobbleSTS.language.getItemBall().getId()))
      .title(AdventureTranslator.toNative(CobbleSTS.language.getItemBall().getTitle()))
      .lore(Component.class, AdventureTranslator.toNativeL(loreball))
      .onClick(action -> UIManager.openUIForcefully(action.getPlayer(), Objects.requireNonNull(STSBall.open())))
      .build();

    Pokemon pokemon;
    if (CobbleSTS.language.getItemLegends().getId().contains("pokemon:")) {
      pokemon =
        PokemonProperties.Companion.parse(CobbleSTS.language.getItemPokemon().getId().replace("pokemon:", "")).create();
    } else {
      pokemon = PokemonProperties.Companion.parse(getKey(CobbleSTS.config.getPokemon())).create();
    }

    List<String> lorepokemon = new ArrayList<>(CobbleSTS.language.getItemPokemon().getLore());
    lorepokemon.replaceAll(s -> s.replace("%base%", String.valueOf(CobbleSTS.config.getBase())));

    GooeyButton pokemonb = GooeyButton.builder()
      .display(PokemonItem.from(pokemon))
      .title(AdventureTranslator.toNative(CobbleSTS.language.getItemPokemon().getTitle()))
      .lore(Component.class, AdventureTranslator.toNativeL(lorepokemon))
      .onClick(action -> UIManager.openUIForcefully(action.getPlayer(), Objects.requireNonNull(STSPokemon.open())))
      .build();

    Pokemon pLegend;
    if (CobbleSTS.language.getItemLegends().getId().contains("pokemon:")) {
      pLegend =
        PokemonProperties.Companion.parse(CobbleSTS.language.getItemLegends().getId().replace("pokemon:", "")).create();
    } else {
      pLegend = PokemonProperties.Companion.parse(getKey(CobbleSTS.config.getLegends())).create();
    }

    List<String> lorelegends = new ArrayList<>(CobbleSTS.language.getItemLegends().getLore());
    lorelegends.replaceAll(s -> s.replace("%base%", String.valueOf(CobbleSTS.config.getLegendary())));

    GooeyButton legends = GooeyButton.builder()
      .display(PokemonItem.from(pLegend))
      .title(AdventureTranslator.toNative(CobbleSTS.language.getItemLegends().getTitle()))
      .lore(Component.class, AdventureTranslator.toNativeL(lorelegends))
      .onClick(action -> UIManager.openUIForcefully(action.getPlayer(), Objects.requireNonNull(STSLegend.open())))
      .build();

    GooeyButton cancel = GooeyButton.builder()
      .display(Utils.parseItemId(CobbleSTS.language.getItemclose().getId()))
      .title(AdventureTranslator.toNative(CobbleSTS.language.getItemclose().getTitle()))
      .lore(Component.class, AdventureTranslator.toNativeL(CobbleSTS.language.getItemclose().getLore()))
      .onClick(action -> {
        try {
          UIManager.openUIForcefully(action.getPlayer(), Objects.requireNonNull(STS.open(action.getPlayer())));
        } catch (NoPokemonStoreException e) {
          throw new RuntimeException(e);
        }
      })
      .build();


    template.fill(fill)
      .set(getMidle(CobbleSTS.config.getGuiinforows()), cancel)
      .set(CobbleSTS.language.getItemGender().getSlot(), gender)
      .set(CobbleSTS.language.getItemForm().getSlot(), form)
      .set(CobbleSTS.language.getItemNature().getSlot(), nature)
      .set(CobbleSTS.language.getItemAbility().getSlot(), ability)
      .set(CobbleSTS.language.getItemBall().getSlot(), ball)
      .set(CobbleSTS.language.getItemPokemon().getSlot(), pokemonb)
      .set(CobbleSTS.language.getItemLegends().getSlot(), legends);


    return GooeyPage.builder().title(AdventureTranslator.toNative(CobbleSTS.language.getTitleInfo())).template(template).build();
  }

  private static <K, V> K getKey(Map<K, V> map) {
    List<K> keys = new ArrayList<>(map.keySet());
    int randomIndex = Utils.RANDOM.nextInt(keys.size());
    return keys.get(randomIndex);
  }

  public static int getMidle(int row) {
    return (9 * row) - 5;
  }
}
