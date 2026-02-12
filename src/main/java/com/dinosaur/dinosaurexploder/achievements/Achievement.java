package com.dinosaur.dinosaurexploder.achievements;

import com.almasb.fxgl.dsl.FXGL;
import java.io.Serializable;

public abstract class Achievement implements Serializable {

  protected boolean completed = false;
  protected int rewardCoins;


  public boolean isCompleted() {
    return completed;
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
