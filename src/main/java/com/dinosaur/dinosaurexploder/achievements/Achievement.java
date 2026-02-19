/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.achievements;

import com.almasb.fxgl.dsl.FXGL;
import java.io.Serializable;

public abstract class Achievement implements Serializable {

  protected boolean completed = false;
  protected int rewardCoins;
  protected String description;

  public boolean isCompleted() {
    return completed;
  }

  public void onComplete() {
    FXGL.getNotificationService().pushNotification("Achievement unlocked: " + description);
  }

  public int getRewardCoins() {
    return rewardCoins;
  }

  public String getDescription() {
    return description;
  }

  public abstract void update(double tpf);

  public abstract void onDinosaurKilled();
}
