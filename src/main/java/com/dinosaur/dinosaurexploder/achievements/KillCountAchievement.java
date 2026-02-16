package com.dinosaur.dinosaurexploder.achievements;

import com.almasb.fxgl.dsl.FXGL;

public class KillCountAchievement extends Achievement {

  private final int targetKills;
  private final int rewardCoins;

  private int currentKills = 0;
  private boolean completed = false;

  public String getId() {
    return "kill_" + targetKills;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  public KillCountAchievement(int targetKills, int rewardCoins) {
    this.targetKills = targetKills;
    this.rewardCoins = rewardCoins;
  }

  public String getDescription() {
    return "Kill " + targetKills + " dinosaurs";
  }

  public boolean isCompleted() {
    return completed;
  }

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

  public void onComplete() {
    AchievementPersistence.save(getId());

    FXGL.getNotificationService().pushNotification("Achievement unlocked: " + getDescription());
  }

  public int getRewardCoins() {
    return rewardCoins;
  }
}
