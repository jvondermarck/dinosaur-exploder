package com.dinosaur.dinosaurexploder.achievements;

import com.almasb.fxgl.dsl.FXGL;

public class KillCountAchievement extends Achievement {

  private final int targetKills;
  private int currentKills = 0;

  public KillCountAchievement(String nameKey, String descriptionKey, int targetKills, int rewardCoins) {
    super(nameKey, descriptionKey, rewardCoins);
    this.targetKills = targetKills;
  }

  public String getDescription() {
    return com.dinosaur.dinosaurexploder.utils.LanguageManager.getInstance().getTranslation(descriptionKey)
        .replace("##", String.valueOf(targetKills));
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
    String achievementName = com.dinosaur.dinosaurexploder.utils.LanguageManager.getInstance().getTranslation(nameKey);
    FXGL.getNotificationService().pushNotification("Achievement unlocked: " + achievementName);
  }

  public int getTargetKills() {
    return targetKills;
  }

  public int getCurrentKills() {
    return currentKills;
  }
}
