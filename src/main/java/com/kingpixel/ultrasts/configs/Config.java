package com.kingpixel.ultrasts.configs;

import com.kingpixel.cobbleutils.Model.DataBaseConfig;
import com.kingpixel.cobbleutils.Model.DataBaseType;
import com.kingpixel.cobbleutils.Model.DurationValue;
import com.kingpixel.cobbleutils.util.UtilsFile;
import com.kingpixel.ultrasts.UltraSTS;
import lombok.Data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Data
public class Config {
  private boolean debug = false;
  private String lang = "en_us";
  private List<String> commands = List.of(UltraSTS.MOD_ID, "sts");
  private DataBaseConfig database = DataBaseConfig.builder()
          .database(UltraSTS.MOD_ID)
          .type(DataBaseType.JSON)
          .build();
  private DurationValue notificationCooldown = DurationValue.parse("15m");

  public static void init() {
    Path file = UltraSTS.getPath().resolve("config.json");
    try {
      Config config = UtilsFile.read(file, Config.class);
      if (config == null) config = new Config();
      config.fix();
      UltraSTS.config = config;
      UtilsFile.writeAsync(file, config);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void fix() {

  }
}
