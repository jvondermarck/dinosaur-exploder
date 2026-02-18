/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.achievements;

import com.almasb.fxgl.dsl.FXGL;

/** Achievement for defeating a boss for the first time. */
public class BossDefeatAchievement extends Achievement {

  public BossDefeatAchievement(int rewardCoins) {
    super(rewardCoins);
  }

  @Override
  public String getDescription() {
    return "Defeat your first boss";
  }

  @Override
  public void onBossDefeated() {
    if (completed) return;

    completed = true;
    onComplete();
  }

  @Override
  public void update(double tpf) {
    // Not needed for boss defeat achievement
  }

  @Override
  protected void onComplete() {
    FXGL.getNotificationService().pushNotification("Achievement unlocked: " + getDescription());
  }
}
