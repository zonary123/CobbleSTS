package com.kingpixel.cobblests.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {
  private static final Pattern HEXPATTERN = Pattern.compile("\\{(#[a-fA-F0-9]{3,6})}");
  private static final Pattern GRADIENTPATTERN = Pattern.compile("\\{(#[a-fA-F0-9]{3,6})>(#[a-fA-F0-9]{3,6})\\}");
  private static final String SPLITPATTERN = "((?=\\{#[a-fA-F0-9]{3,6}})|(?=\\{#[a-fA-F0-9]{3,6}>#[a-fA-F0-9]{3,6}\\}))";

  public static Component parseHexCodes(String text) {
    if (text == null) return null;
    text = text.replace("&", "§"); // Replace & with §
    MutableComponent comp = Component.empty();
    String[] temp = text.split(SPLITPATTERN);

    Style currentStyle = Style.EMPTY;
    for (String s : temp) {
      Matcher gradientMatcher = GRADIENTPATTERN.matcher(s);
      Matcher hexMatcher = HEXPATTERN.matcher(s);

      if (gradientMatcher.find()) {
        String startColor = gradientMatcher.group(1);
        String endColor = gradientMatcher.group(2);
        s = gradientMatcher.replaceAll(""); // Eliminate gradient code
        comp.append(applyGradient(s, TextColor.parseColor(startColor), TextColor.parseColor(endColor), currentStyle));
      } else if (hexMatcher.find()) {
        TextColor color = TextColor.parseColor(hexMatcher.group(1));
        s = hexMatcher.replaceAll(""); // Eliminate hex code
        currentStyle = currentStyle.withColor(color);
        comp.append(Component.literal(s).setStyle(currentStyle));
      } else {
        comp.append(Component.literal(s).setStyle(currentStyle));
      }
    }

    return comp;
  }

  private static Component applyGradient(String text, TextColor startColor, TextColor endColor, Style baseStyle) {
    int length = text.length();
    int r1 = startColor.getValue() >> 16 & 0xFF;
    int g1 = startColor.getValue() >> 8 & 0xFF;
    int b1 = startColor.getValue() & 0xFF;
    int r2 = endColor.getValue() >> 16 & 0xFF;
    int g2 = endColor.getValue() >> 8 & 0xFF;
    int b2 = endColor.getValue() & 0xFF;

    MutableComponent comp = Component.empty();

    for (int i = 0; i < length; i++) {
      float ratio = (float) i / (length - 1);
      int r = (int) (r1 + ratio * (r2 - r1));
      int g = (int) (g1 + ratio * (g2 - g1));
      int b = (int) (b1 + ratio * (b2 - b1));
      TextColor color = TextColor.fromRgb((r << 16) + (g << 8) + b);
      String letter = String.valueOf(text.charAt(i));
      Style currentStyle = baseStyle.withColor(color);
      comp.append(Component.literal(letter).setStyle(currentStyle));
    }

    return comp;
  }

  public static List<Component> parseHexCodes(List<String> textList) {
    if (textList == null) return null;

    List<Component> result = new ArrayList<>();

    for (String text : textList) {
      text = text.replace("&", "§"); // Replace & with §
      Component comp = parseHexCodes(text);
      result.add(comp);
    }
    return result;
  }

  public static final TextColor BLUE = TextColor.parseColor("#00AFFC");
  public static final TextColor ORANGE = TextColor.parseColor("#FF6700");
}