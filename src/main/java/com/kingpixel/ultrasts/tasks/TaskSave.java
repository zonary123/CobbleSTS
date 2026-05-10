package com.kingpixel.ultrasts.tasks;

import com.kingpixel.ultrasts.UltraSTS;

import java.util.concurrent.TimeUnit;

public class TaskSave {
  public static void register() {
    UltraSTS.getAsyncContext().scheduleAtFixedRate(() -> UltraSTS.database.saveAll(), 60L, 60L, TimeUnit.SECONDS);
  }
}
