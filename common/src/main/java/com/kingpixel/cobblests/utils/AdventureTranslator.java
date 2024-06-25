package com.kingpixel.cobblests.utils;

/*
 * This file is part of Impactor, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018-2022 NickImpact
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AdventureTranslator {
  private static final MiniMessage miniMessage = MiniMessage.miniMessage();


  public static Component toNative(String displayname) {
    return toNative(miniMessage.deserialize(replaceNative(displayname)));
  }

  public static Component toNative(net.kyori.adventure.text.Component component) {
    return Component.Serializer.fromJson(GsonComponentSerializer.gson().serialize(component));
  }

  public static List<Component> toNativeL(List<String> lore) {
    List<net.kyori.adventure.text.Component> loreString = new ArrayList<>();
    for (String loreLine : lore) {
      loreString.add(miniMessage.deserialize(replaceNative(loreLine)));
    }
    return toNative(loreString);
  }

  public static List<Component> toNative(List<net.kyori.adventure.text.Component> components) {
    List<Component> nativeComponents = new ArrayList<>();
    for (net.kyori.adventure.text.Component component : components) {
      nativeComponents.add(toNative(component));
    }
    return nativeComponents;
  }

  public static net.kyori.adventure.text.Component fromNative(Component component) {
    return GsonComponentSerializer.gson().deserialize(Component.Serializer.toJson(component));
  }

  public static SoundSource asVanilla(final Sound.Source source) {
    switch (source) {
      case MASTER:
        return SoundSource.MASTER;
      case MUSIC:
        return SoundSource.MUSIC;
      case RECORD:
        return SoundSource.RECORDS;
      case WEATHER:
        return SoundSource.WEATHER;
      case BLOCK:
        return SoundSource.BLOCKS;
      case HOSTILE:
        return SoundSource.HOSTILE;
      case NEUTRAL:
        return SoundSource.NEUTRAL;
      case PLAYER:
        return SoundSource.PLAYERS;
      case AMBIENT:
        return SoundSource.AMBIENT;
      case VOICE:
        return SoundSource.VOICE;
    }

    throw new IllegalArgumentException(source.name());
  }

  public static @Nullable SoundSource asVanillaNullable(final Sound.Source source) {
    if (source == null) {
      return null;
    }

    return asVanilla(source);
  }

  public static net.kyori.adventure.text.Component toNativeFromString(String displayname) {
    return miniMessage.deserialize(replaceNative(displayname));
  }

  private static String replaceNative(String displayname) {
    if (displayname == null) {
      return null;
    }
    displayname = displayname.replace("&", "§").replace("§0", "<black>")
      .replace("§1", "<dark_blue>")
      .replace("§2", "<dark_green>")
      .replace("§3", "<dark_aqua>")
      .replace("§4", "<dark_red>")
      .replace("§5", "<dark_purple>")
      .replace("§6", "<gold>")
      .replace("§7", "<gray>")
      .replace("§8", "<dark_gray>")
      .replace("§9", "<blue>")
      .replace("§a", "<green>")
      .replace("§b", "<aqua>")
      .replace("§c", "<red>")
      .replace("§d", "<light_purple>")
      .replace("§e", "<yellow>")
      .replace("§f", "<white>")
      .replace("§k", "<obfuscated>")
      .replace("§l", "<bold>")
      .replace("§m", "<strikethrough>")
      .replace("§n", "<underline>")
      .replace("§o", "<italic>")
      .replace("§r", "<reset>");
    return displayname;
  }

}
