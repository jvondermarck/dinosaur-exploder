/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.achievements;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class KillCountAchievementTest {

  @Test
  void newAchievementIsNotCompleted() {
    KillCountAchievement a = new KillCountAchievement(3, 50);
    assertFalse(a.isCompleted());
    assertEquals(50, a.getRewardCoins());
  }

  @Test
  void completesAfterEnoughKills() {
    KillCountAchievement a = new KillCountAchievement(3, 50);
    a.onDinosaurKilled();
    a.onDinosaurKilled();
    assertFalse(a.isCompleted());
    a.onDinosaurKilled();
    assertTrue(a.isCompleted());
  }

  @Test
  void extraKillsAfterCompletionDontMatter() {
    KillCountAchievement a = new KillCountAchievement(1, 10);
    a.onDinosaurKilled();
    a.onDinosaurKilled();
    a.onDinosaurKilled();
    assertTrue(a.isCompleted());
  }
}
