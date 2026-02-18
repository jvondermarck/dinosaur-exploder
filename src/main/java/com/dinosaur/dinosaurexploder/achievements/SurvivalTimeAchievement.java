package com.dinosaur.dinosaurexploder.achievements;

import com.almasb.fxgl.dsl.FXGL;

/** Achievement for surviving a specific amount of time. */
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
    if (minutes == 1) {
      return "Survive for 1 minute";
    }
    return "Survive for " + minutes + " minutes";
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
    FXGL.getNotificationService().pushNotification("Achievement unlocked: " + getDescription());
  }
}
