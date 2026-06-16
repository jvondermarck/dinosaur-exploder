/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.utils;

import static org.junit.jupiter.api.Assertions.*;

import com.dinosaur.dinosaurexploder.constants.GameMode;
import org.junit.jupiter.api.Test;

class LevelManagerTest {

  @Test
  void startsAtLevelOne() {
    LevelManager lm = new LevelManager();
    assertEquals(1, lm.getCurrentLevel());
    assertEquals(GameMode.NORMAL, lm.getGameMode());
  }

  @Test
  void killingABossAdvancesLevel() {
    LevelManager lm = new LevelManager();
    lm.incrementDefeatedBosses();
    assertTrue(lm.shouldAdvanceLevel());
  }

  @Test
  void nextLevelIncreasesEnemyTarget() {
    LevelManager lm = new LevelManager();
    int before = lm.getEnemiesToDefeat();
    lm.nextLevel();
    assertEquals(2, lm.getCurrentLevel());
    assertEquals(before + 5, lm.getEnemiesToDefeat());
  }

  @Test
  void spawnRateIsClampedAtFloor() {
    LevelManager lm = new LevelManager();
    for (int i = 0; i < 100; i++) {
      lm.nextLevel();
    }
    assertTrue(lm.getEnemySpawnRate() >= 0.3);
  }
}
