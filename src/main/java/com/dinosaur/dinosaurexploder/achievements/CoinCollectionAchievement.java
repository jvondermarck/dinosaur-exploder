/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.achievements;

import com.almasb.fxgl.dsl.FXGL;

/** Achievement for collecting a specific number of coins. */
public class CoinCollectionAchievement extends Achievement {

  private final int targetCoins;

  public CoinCollectionAchievement(int targetCoins, int rewardCoins) {
    super(rewardCoins);
    this.targetCoins = targetCoins;
  }

  @Override
  public String getDescription() {
    return "Collect " + targetCoins + " coins";
  }

  @Override
  public void onCoinCollected(int totalCoins) {
    if (completed) return;

    if (totalCoins >= targetCoins) {
      completed = true;
      onComplete();
    }
  }

  @Override
  public void update(double tpf) {
    // Not needed for coin collection achievement
  }

  @Override
  protected void onComplete() {
    FXGL.getNotificationService().pushNotification("Achievement unlocked: " + getDescription());
  }
}
