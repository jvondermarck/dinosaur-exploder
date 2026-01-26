package com.dinosaur.dinosaurexploder.achievements;

public abstract class Achievement {

  protected final String nameKey;
  protected final String descriptionKey;
  protected final int rewardCoins;
  protected boolean completed = false;

  public Achievement(String nameKey, String descriptionKey, int rewardCoins) {
    this.nameKey = nameKey;
    this.descriptionKey = descriptionKey;
    this.rewardCoins = rewardCoins;
  }

  public String getNameKey() {
    return nameKey;
  }

  public String getDescriptionKey() {
    return descriptionKey;
  }

  public int getRewardCoins() {
    return rewardCoins;
  }

  public boolean isCompleted() {
    return completed;
  }

  public abstract void update(double tpf);

  public abstract void onDinosaurKilled();
}
