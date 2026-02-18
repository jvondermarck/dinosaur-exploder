package com.dinosaur.dinosaurexploder.achievements;

import com.almasb.fxgl.dsl.FXGL;

/** Achievement for killing a specific number of dinosaurs. */
public class KillCountAchievement extends Achievement {

  private final int targetKills;
  private int currentKills = 0;

  public KillCountAchievement(int targetKills, int rewardCoins) {
    super(rewardCoins);
    this.targetKills = targetKills;
  }

  @Override
  public String getDescription() {
    return "Kill " + targetKills + " dinosaurs";
  }

  @Override
  public void onDinosaurKilled() {
    if (completed) return;

    currentKills++;

    if (currentKills >= targetKills) {
      completed = true;
      onComplete();
    }
  }

  @Override
  public void update(double tpf) {
    // Not needed for count-based achievement
  }

  @Override
  protected void onComplete() {
    FXGL.getNotificationService().pushNotification("Achievement unlocked: " + getDescription());
  }
}
