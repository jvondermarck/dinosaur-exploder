/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.achievements;

record AchievementEvent(AchievementEventType type, int intValue, double doubleValue) {

  static AchievementEvent dinosaurKilled() {
    return new AchievementEvent(AchievementEventType.DINOSAUR_KILLED, 0, 0);
  }

  static AchievementEvent scoreChanged(int newScore) {
    return new AchievementEvent(AchievementEventType.SCORE_CHANGED, newScore, 0);
  }

  static AchievementEvent coinCollected(int totalCoins) {
    return new AchievementEvent(AchievementEventType.COIN_COLLECTED, totalCoins, 0);
  }

  static AchievementEvent bossDefeated() {
    return new AchievementEvent(AchievementEventType.BOSS_DEFEATED, 0, 0);
  }

  static AchievementEvent timeElapsed(double tpf) {
    return new AchievementEvent(AchievementEventType.TIME_ELAPSED, 0, tpf);
  }
}
