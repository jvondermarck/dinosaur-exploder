package com.dinosaur.dinosaurexploder.achievements;

import com.almasb.fxgl.dsl.FXGL;

public abstract class Achievement {

  protected boolean completed = false;
  protected String fileName;
  protected int rewardCoins;


  public boolean isCompleted() {
    return completed;
  }

  public String getFileName() {
    return fileName;
  }

  public void onComplete(String description) {
    FXGL.getNotificationService().pushNotification("Achievement unlocked: " + description);
  }

  public int getRewardCoins() {
    return rewardCoins;
  }

  public abstract void update(double tpf);

  public abstract Boolean onDinosaurKilled();
}
