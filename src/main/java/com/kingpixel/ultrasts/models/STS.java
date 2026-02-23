package com.kingpixel.ultrasts.models;

import com.kingpixel.cobbleutils.Model.DurationValue;
import com.kingpixel.cobbleutils.Model.ItemModel;
import com.kingpixel.cobbleutils.Model.PokemonBlackList;
import com.kingpixel.cobbleutils.Model.PokemonFormula;
import com.kingpixel.cobbleutils.Model.economy.EconomySelector;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class STS {
  private transient String id;
  @Builder.Default
  private ItemModel display = ItemModel.builder()
    .slot(1)
    .item("minecraft:diamond_sword")
    .displayname("&bSTS")
    .lore(List.of(
      "&7Click to entrer the category STS",
      "&7Cooldown: %cooldown%"
    ))
    .build();
  @Builder.Default
  private EconomySelector economy = new EconomySelector("IMPACTOR", "impactor:dollars");
  @Builder.Default
  private DurationValue cooldown = DurationValue.parse("5m");
  @Builder.Default
  private Map<String, DurationValue> cooldownPermissions = Map.of(
    "ultrasts.cooldown.reduce", DurationValue.parse("1m"),
    "ultrasts.cooldown.bypass", DurationValue.parse("0s")
  );
  @Builder.Default
  private String permission = "";
  @Builder.Default
  private PokemonFormula formula = new PokemonFormula();
  @Builder.Default
  private PokemonBlackList blackList = new PokemonBlackList();

}
