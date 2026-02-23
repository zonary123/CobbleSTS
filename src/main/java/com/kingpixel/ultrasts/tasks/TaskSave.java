package com.kingpixel.ultrasts.tasks;

import com.kingpixel.ultrasts.UltraSTS;

import java.util.concurrent.TimeUnit;

public class TaskSave {
  public static void register() {
    UltraSTS.ASYNC.scheduleAtFixedRate(() -> UltraSTS.database.saveAll(), 30, 30, TimeUnit.SECONDS);
  }
}
