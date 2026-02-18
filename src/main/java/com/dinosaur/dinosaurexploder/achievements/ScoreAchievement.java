/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.achievements;

import com.almasb.fxgl.dsl.FXGL;

public class ScoreAchievement extends Achievement {

  private final int targetScore;

  public ScoreAchievement(int targetScore, int rewardCoins) {
    super(rewardCoins);
    this.targetScore = targetScore;
  }

  @Override
  public String getDescription() {
    return "Reach " + targetScore + " points";
  }

  @Override
  public void onScoreChanged(int newScore) {
    if (completed) return;

    if (newScore >= targetScore) {
      completed = true;
      onComplete();
    }
  }

  @Override
  public void update(double tpf) {
    // Not needed for score-based achievement
  }

  @Override
  protected void onComplete() {
    FXGL.getNotificationService().pushNotification("Achievement unlocked: " + getDescription());
  }
}
