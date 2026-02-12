package com.dinosaur.dinosaurexploder.achievements;


public class KillCountAchievement extends Achievement {

  private final int targetKills;
  private int currentKills = 0;

  public KillCountAchievement(int targetKills, int rewardCoins) {
    this.targetKills = targetKills;
    this.rewardCoins = rewardCoins;
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
