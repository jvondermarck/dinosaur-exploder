/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.dinosaur.dinosaurexploder.constants.GameMode;
import com.dinosaur.dinosaurexploder.exception.LockedShipException;
import com.dinosaur.dinosaurexploder.exception.LockedWeaponException;
import com.dinosaur.dinosaurexploder.model.HighScore;
import com.dinosaur.dinosaurexploder.model.TotalCoins;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Regression tests for issue #488 (unlock boundaries): ship/weapon unlock checks must treat the
 * configured score/coin thresholds as inclusive, so a value exactly at the threshold unlocks and
 * one below it stays locked.
 */
class UnlockBoundaryTest {

  private static DataProvider provider(int highScore, int totalCoins) {
    return new DataProvider() {
      @Override
      public HighScore getHighScore() {
        Map<String, Integer> scores = new HashMap<>();
        scores.put(GameMode.NORMAL.name(), highScore);
        scores.put(GameMode.EXPERT.name(), 0);
        return new HighScore(scores);
      }

      @Override
      public TotalCoins getTotalCoins() {
        return new TotalCoins(totalCoins);
      }
    };
  }

  // Ship 3 requires score >= 100 AND coins >= 10 (ShipUnlockChecker's scoreMap/coinMap).
  @Test
  void shipUnlocks_whenScoreAndCoinsExactlyAtThreshold() {
    ShipUnlockChecker checker = new ShipUnlockChecker(provider(100, 10));
    assertDoesNotThrow(() -> checker.check(3));
  }

  @Test
  void shipStaysLocked_whenScoreOneBelowThreshold() {
    ShipUnlockChecker checker = new ShipUnlockChecker(provider(99, 10));
    assertThrows(LockedShipException.class, () -> checker.check(3));
  }

  @Test
  void shipStaysLocked_whenCoinsOneBelowThreshold() {
    ShipUnlockChecker checker = new ShipUnlockChecker(provider(100, 9));
    assertThrows(LockedShipException.class, () -> checker.check(3));
  }

  // Weapon 2 requires score >= 50 AND coins >= 5 (WeaponUnlockChecker's scoreMap/coinMap).
  @Test
  void weaponUnlocks_whenScoreAndCoinsExactlyAtThreshold() {
    WeaponUnlockChecker checker = new WeaponUnlockChecker(provider(50, 5));
    assertDoesNotThrow(() -> checker.check(2));
  }

  @Test
  void weaponStaysLocked_whenScoreOneBelowThreshold() {
    WeaponUnlockChecker checker = new WeaponUnlockChecker(provider(49, 5));
    assertThrows(LockedWeaponException.class, () -> checker.check(2));
  }

  @Test
  void weaponStaysLocked_whenCoinsOneBelowThreshold() {
    WeaponUnlockChecker checker = new WeaponUnlockChecker(provider(50, 4));
    assertThrows(LockedWeaponException.class, () -> checker.check(2));
  }
}
