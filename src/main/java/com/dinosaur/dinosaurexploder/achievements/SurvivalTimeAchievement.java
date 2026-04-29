/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.achievements;

import com.almasb.fxgl.dsl.FXGL;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;

/** Achievement for surviving a specific amount of time. */
@RegisterAchievement(target = 1, reward = 50)
@RegisterAchievement(target = 3, reward = 150)
public class SurvivalTimeAchievement extends Achievement {

  private final double targetSeconds;
  private double currentSeconds = 0.0;

  public SurvivalTimeAchievement(int targetMinutes, int rewardCoins) {
    super(rewardCoins);
    this.targetSeconds = targetMinutes * 60.0;
  }

  @Override
  public String getDescription() {
    int minutes = (int) (targetSeconds / 60);
    LanguageManager lm = LanguageManager.getInstance();
    if (minutes == 1) {
      return lm.getTranslation("ach_survive_1min");
    }
    return lm.getTranslation("ach_survive_mins").replace("##", String.valueOf(minutes));
  }

  @Override
  public void update(double tpf) {
    if (completed) return;

    currentSeconds += tpf;

    if (currentSeconds >= targetSeconds) {
      completed = true;
      onComplete();
    }
  }

  @Override
  protected void onComplete() {
    try {
      FXGL.getNotificationService().pushNotification("Achievement unlocked: " + getDescription());
    } catch (Exception e) {
      // FXGL not initialized (e.g., in tests) - skip notification
    }
  }
}
