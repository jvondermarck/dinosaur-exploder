/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dinosaur.dinosaurexploder.utils.LevelManager;
import org.junit.jupiter.api.Test;

class LevelTest {

  @Test
  void levelUp() {
    LevelManager levelManager = new LevelManager();

    incrementDefeatedEnemiesManyTimes(levelManager, levelManager.getEnemiesToDefeat());

    assertTrue(levelManager.shouldAdvanceLevel());
  }

  @Test
  void levelUpManyTimes() {
    LevelManager levelManager = new LevelManager();

    incrementDefeatedEnemiesManyTimes(levelManager, levelManager.getEnemiesToDefeat());
    levelManager.nextLevel();
    incrementDefeatedEnemiesManyTimes(levelManager, 2 * levelManager.getEnemiesToDefeat());

    assertTrue(levelManager.shouldAdvanceLevel());
  }

  private void incrementDefeatedEnemiesManyTimes(LevelManager levelManager, int count) {
    for (int i = 0; i < count; i++) {
      levelManager.incrementDefeatedEnemies();
    }
  }
}
