package com.kingpixel.cobblests.Model;

import java.util.List;

/**
 * @author Carlos Varas Alonso - 26/05/2024 4:15
 */
public class ItemModel {
  private String id;
  private String title;
  private List<String> lore;

  public ItemModel(String id, String title, List<String> lore) {
    this.id = id;
    this.title = title;
    this.lore = lore;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<String> getLore() {
    return lore;
  }

  public void setLore(List<String> lore) {
    this.lore = lore;
  }
}
