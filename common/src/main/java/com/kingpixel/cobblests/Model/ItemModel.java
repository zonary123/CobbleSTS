package com.kingpixel.cobblests.Model;

import lombok.Getter;

import java.util.List;

/**
 * @author Carlos Varas Alonso - 26/05/2024 4:15
 */
@Getter
public class ItemModel {
  private Integer slot;
  private String id;
  private String title;
  private List<String> lore;

  public ItemModel(String id, String title, List<String> lore) {
    this.id = id;
    this.title = title;
    this.lore = lore;
  }

  public ItemModel(Integer slot, String id, String title, List<String> lore) {
    this.slot = slot;
    this.id = id;
    this.title = title;
    this.lore = lore;
  }
}
