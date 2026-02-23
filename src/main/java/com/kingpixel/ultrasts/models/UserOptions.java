package com.kingpixel.ultrasts.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserOptions {
  @Builder.Default
  private boolean notificationsEnabled = true;


}
