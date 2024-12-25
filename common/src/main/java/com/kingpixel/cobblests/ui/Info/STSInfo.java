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
import com.kingpixel.cobbleutils.util.AdventureTranslator;
import com.kingpixel.cobbleutils.util.Utils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;

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
      .with(DataComponentTypes.ITEM_NAME, AdventureTranslator.toNative(""))
      .build();

    List<String> loregender = new ArrayList<>(CobbleSTS.language.getItemGender().getLore());
    loregender.replaceAll(s -> s.replace("%base%", String.valueOf(CobbleSTS.config.getDefaultgender())));

    GooeyButton gender = GooeyButton.builder()
      .display(CobbleSTS.language.getItemGender().getItemStack())
      .with(DataComponentTypes.ITEM_NAME,
        AdventureTranslator.toNative(CobbleSTS.language.getItemGender().getDisplayname()))
      .with(DataComponentTypes.LORE, new LoreComponent(AdventureTranslator.toNativeL(loregender)))
      .onClick(action -> UIManager.openUIForcefully(action.getPlayer(), Objects.requireNonNull(STSGender.open())))
      .build();

    List<String> loreform = new ArrayList<>(CobbleSTS.language.getItemForm().getLore());
    loreform.replaceAll(s -> s.replace("%base%", String.valueOf(CobbleSTS.config.getDefaultform())));

    GooeyButton form = GooeyButton.builder()
      .display(CobbleSTS.language.getItemForm().getItemStack())
      .with(DataComponentTypes.ITEM_NAME,
        AdventureTranslator.toNative(CobbleSTS.language.getItemForm().getDisplayname()))
      .with(DataComponentTypes.LORE, new LoreComponent(AdventureTranslator.toNativeL(loreform)))
      .onClick(action -> UIManager.openUIForcefully(action.getPlayer(), Objects.requireNonNull(STSForm.open())))
      .build();

    List<String> lorenature = new ArrayList<>(CobbleSTS.language.getItemNature().getLore());
    lorenature.replaceAll(s -> s.replace("%base%", String.valueOf(CobbleSTS.config.getDefaultnature())));

    GooeyButton nature = GooeyButton.builder()
      .display(CobbleSTS.language.getItemNature().getItemStack())
      .with(DataComponentTypes.ITEM_NAME,
        AdventureTranslator.toNative(CobbleSTS.language.getItemNature().getDisplayname()))
      .with(DataComponentTypes.LORE, new LoreComponent(AdventureTranslator.toNativeL(lorenature)))
      .onClick(action -> UIManager.openUIForcefully(action.getPlayer(), Objects.requireNonNull(STSNature.open())))
      .build();

    List<String> loreability = new ArrayList<>(CobbleSTS.language.getItemAbility().getLore());
    loreability.replaceAll(s -> s.replace("%base%", String.valueOf(CobbleSTS.config.getDefaultability())));

    GooeyButton ability = GooeyButton.builder()
      .display(CobbleSTS.language.getItemAbility().getItemStack())
      .with(DataComponentTypes.ITEM_NAME,
        AdventureTranslator.toNative(CobbleSTS.language.getItemAbility().getDisplayname()))
      .with(DataComponentTypes.LORE, new LoreComponent(AdventureTranslator.toNativeL(loreability)))
      .onClick(action -> UIManager.openUIForcefully(action.getPlayer(), Objects.requireNonNull(STSAbility.open())))
      .build();

    List<String> loreball = new ArrayList<>(CobbleSTS.language.getItemBall().getLore());
    loreball.replaceAll(s -> s.replace("%base%", String.valueOf(CobbleSTS.config.getDefaultball())));

    GooeyButton ball = GooeyButton.builder()
      .display(CobbleSTS.language.getItemBall().getItemStack())
      .with(DataComponentTypes.ITEM_NAME,
        AdventureTranslator.toNative(CobbleSTS.language.getItemBall().getDisplayname()))
      .with(DataComponentTypes.LORE, new LoreComponent(AdventureTranslator.toNativeL(loreball)))
      .onClick(action -> UIManager.openUIForcefully(action.getPlayer(), Objects.requireNonNull(STSBall.open())))
      .build();

    Pokemon pokemon = PokemonProperties.Companion.parse(getKey(CobbleSTS.config.getPokemon())).create();


    List<String> lorepokemon = new ArrayList<>(CobbleSTS.language.getItemPokemon().getLore());
    lorepokemon.replaceAll(s -> s.replace("%base%", String.valueOf(CobbleSTS.config.getBase())));

    GooeyButton pokemonb = GooeyButton.builder()
      .display(PokemonItem.from(pokemon))
      .with(DataComponentTypes.ITEM_NAME,
        AdventureTranslator.toNative(CobbleSTS.language.getItemPokemon().getDisplayname()))
      .with(DataComponentTypes.LORE, new LoreComponent(AdventureTranslator.toNativeL(lorepokemon)))
      .onClick(action -> UIManager.openUIForcefully(action.getPlayer(), Objects.requireNonNull(STSPokemon.open())))
      .build();

    GooeyButton cancel = GooeyButton.builder()
      .display(CobbleSTS.language.getItemclose().getItemStack())
      .with(DataComponentTypes.ITEM_NAME,
        AdventureTranslator.toNative(CobbleSTS.language.getItemclose().getDisplayname()))
      .with(DataComponentTypes.LORE,
        new LoreComponent(AdventureTranslator.toNativeL(CobbleSTS.language.getItemclose().getLore())))
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
      .set(CobbleSTS.language.getItemPokemon().getSlot(), pokemonb);


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
