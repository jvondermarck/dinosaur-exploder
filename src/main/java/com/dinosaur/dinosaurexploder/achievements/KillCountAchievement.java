package com.dinosaur.dinosaurexploder.achievements;

import com.almasb.fxgl.dsl.FXGL;
import com.dinosaur.dinosaurexploder.utils.AchievementProvider;

public class KillCountAchievement extends Achievement {

  private final int targetKills;
  private int currentKills = 0;

  public KillCountAchievement(int targetKills, int rewardCoins,String fileName) {
    this.targetKills = targetKills;
    this.rewardCoins = rewardCoins;
    this.fileName = fileName;
  }

  public String getDescription() {
    return "Kill " + targetKills + " dinosaurs";
  }

	public Boolean onDinosaurKilled() {
    if (completed) return(true);

    currentKills++;

    if (currentKills >= targetKills) {
      completed = true;
      onComplete(getDescription());
      return(true);
    }
    return(false);
  }

  @Override
  public void update(double tpf) {
    // Not needed for count-based achievement
  }


}
