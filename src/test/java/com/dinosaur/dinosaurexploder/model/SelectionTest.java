/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.dinosaur.dinosaurexploder.constants.GameMode;
import com.dinosaur.dinosaurexploder.exception.LockedShipException;
import com.dinosaur.dinosaurexploder.exception.LockedWeaponException;
import com.dinosaur.dinosaurexploder.utils.DataProvider;
import com.dinosaur.dinosaurexploder.utils.ShipUnlockChecker;
import com.dinosaur.dinosaurexploder.utils.WeaponUnlockChecker;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests the unlock rules of {@link ShipUnlockChecker} and {@link WeaponUnlockChecker}.
 *
 * <p>Rules (read from the production code, not changed): a ship or weapon is unlocked when the
 * player's best high score AND total coins are both at or above its limits.
 *
 * <ul>
 *   <li>Weapons: 1 needs 0 / 0, 2 needs 50 / 5, 3 needs 100 / 10
 *   <li>Ships: 1 and 2 need 0 / 0, 3 needs 100 / 10, 4 needs 200 / 50
 * </ul>
 *
 * Each stub below is built with exactly the score and coins a case needs, so a test can only pass
 * for the reason it names.
 */
class SelectionTest {

  // Weapon limits (score / coins)
  private static final int WEAPON_2_SCORE = 50;
  private static final int WEAPON_2_COINS = 5;
  private static final int WEAPON_3_SCORE = 100;
  private static final int WEAPON_3_COINS = 10;

  // Ship limits (score / coins)
  private static final int SHIP_3_SCORE = 100;
  private static final int SHIP_3_COINS = 10;
  private static final int SHIP_4_SCORE = 200;
  private static final int SHIP_4_COINS = 50;

  private static final int PLENTY = 100_000;

  @Test
  void weaponStubWithScore50AndCoins5_unlocksWeapon2ButNotWeapon3() {
    WeaponUnlockChecker checker = weapons(WEAPON_2_SCORE, WEAPON_2_COINS);

    assertEquals(2, checker.check(2));
    assertThrows(LockedWeaponException.class, () -> checker.check(3));
  }

  @Test
  void weapon1IsAlwaysUnlocked() {
    assertEquals(1, weapons(0, 0).check(1));
  }

  @Test
  void weapon2_exactLimitsAreAccepted() {
    assertEquals(2, weapons(WEAPON_2_SCORE, WEAPON_2_COINS).check(2));
  }

  @Test
  void weapon2_oneScorePointShort_throws() {
    assertLockedWeapon(weapons(WEAPON_2_SCORE - 1, WEAPON_2_COINS), 2);
  }

  @Test
  void weapon2_oneCoinShort_throws() {
    assertLockedWeapon(weapons(WEAPON_2_SCORE, WEAPON_2_COINS - 1), 2);
  }

  @Test
  void weapon2_scoreTooLowButCoinsEnough_throws() {
    assertLockedWeapon(weapons(0, PLENTY), 2);
  }

  @Test
  void weapon2_coinsTooLowButScoreEnough_throws() {
    assertLockedWeapon(weapons(PLENTY, 0), 2);
  }

  @Test
  void weapon2_bothTooLow_throws() {
    assertLockedWeapon(weapons(0, 0), 2);
  }

  @Test
  void weapon3_exactLimitsAreAccepted() {
    assertEquals(3, weapons(WEAPON_3_SCORE, WEAPON_3_COINS).check(3));
  }

  @Test
  void weapon3_oneScorePointShort_throws() {
    assertLockedWeapon(weapons(WEAPON_3_SCORE - 1, WEAPON_3_COINS), 3);
  }

  @Test
  void weapon3_oneCoinShort_throws() {
    assertLockedWeapon(weapons(WEAPON_3_SCORE, WEAPON_3_COINS - 1), 3);
  }

  @Test
  void weapon3_scoreTooLowButCoinsEnough_throws() {
    assertLockedWeapon(weapons(0, PLENTY), 3);
  }

  @Test
  void weapon3_coinsTooLowButScoreEnough_throws() {
    assertLockedWeapon(weapons(PLENTY, 0), 3);
  }

  @Test
  void weapon3_bothTooLow_throws() {
    assertLockedWeapon(weapons(0, 0), 3);
  }

  @Test
  void ships1And2AreAlwaysUnlocked() {
    ShipUnlockChecker checker = ships(0, 0);

    assertEquals(1, checker.check(1));
    assertEquals(2, checker.check(2));
  }

  @Test
  void ship3_exactLimitsAreAccepted() {
    assertEquals(3, ships(SHIP_3_SCORE, SHIP_3_COINS).check(3));
  }

  @Test
  void ship3_oneScorePointShort_throws() {
    assertLockedShip(ships(SHIP_3_SCORE - 1, SHIP_3_COINS), 3);
  }

  @Test
  void ship3_oneCoinShort_throws() {
    assertLockedShip(ships(SHIP_3_SCORE, SHIP_3_COINS - 1), 3);
  }

  @Test
  void ship3_scoreTooLowButCoinsEnough_throws() {
    assertLockedShip(ships(0, PLENTY), 3);
  }

  @Test
  void ship3_coinsTooLowButScoreEnough_throws() {
    assertLockedShip(ships(PLENTY, 0), 3);
  }

  @Test
  void ship3_bothTooLow_throws() {
    assertLockedShip(ships(0, 0), 3);
  }

  @Test
  void ship4_exactLimitsAreAccepted() {
    assertEquals(4, ships(SHIP_4_SCORE, SHIP_4_COINS).check(4));
  }

  @Test
  void ship4_oneScorePointShort_throws() {
    assertLockedShip(ships(SHIP_4_SCORE - 1, SHIP_4_COINS), 4);
  }

  @Test
  void ship4_oneCoinShort_throws() {
    assertLockedShip(ships(SHIP_4_SCORE, SHIP_4_COINS - 1), 4);
  }

  @Test
  void ship4_scoreTooLowButCoinsEnough_throws() {
    assertLockedShip(ships(0, PLENTY), 4);
  }

  @Test
  void ship4_coinsTooLowButScoreEnough_throws() {
    assertLockedShip(ships(PLENTY, 0), 4);
  }

  @Test
  void ship4_bothTooLow_throws() {
    assertLockedShip(ships(0, 0), 4);
  }

  @Test
  void shipStubThatUnlocksShip3_keepsShip4Locked() {
    // Same stub as the original SelectionTest (score 100, coins 10).
    ShipUnlockChecker checker = ships(SHIP_3_SCORE, SHIP_3_COINS);

    assertEquals(3, checker.check(3));
    assertLockedShip(checker, 4);
  }

  @Test
  void highScoreFromAnyGameModeCounts() {
    // The checker uses HighScore.getHigh() = the best score over all modes, so a score that
    // exists only in EXPERT mode must still unlock the ship.
    DataProvider onlyExpertScore = provider(0, SHIP_3_SCORE, SHIP_3_COINS);

    assertEquals(3, new ShipUnlockChecker(onlyExpertScore).check(3));
  }

  @Test
  void unknownWeaponNumberIsCurrentlyTreatedAsUnlocked() {
    // Documents the current behaviour, not a requirement: numbers missing from the limit maps
    // fall back to limits of 0 / 0 (getOrDefault), so they are accepted even with nothing earned.
    // If the rule is ever tightened to reject unknown numbers, this test should be changed.
    WeaponUnlockChecker checker = weapons(0, 0);

    assertDoesNotThrow(() -> checker.check(0));
    assertDoesNotThrow(() -> checker.check(-1));
    assertEquals(99, checker.check(99));
  }

  @Test
  void unknownShipNumberIsCurrentlyTreatedAsUnlocked() {
    // Documents the current behaviour, not a requirement (see the weapon test above).
    ShipUnlockChecker checker = ships(0, 0);

    assertDoesNotThrow(() -> checker.check(0));
    assertDoesNotThrow(() -> checker.check(-1));
    assertEquals(99, checker.check(99));
  }

  private static void assertLockedWeapon(WeaponUnlockChecker checker, int weaponNumber) {
    assertThrows(LockedWeaponException.class, () -> checker.check(weaponNumber));
  }

  private static void assertLockedShip(ShipUnlockChecker checker, int shipNumber) {
    assertThrows(LockedShipException.class, () -> checker.check(shipNumber));
  }

  private static WeaponUnlockChecker weapons(int highScore, int totalCoins) {
    return new WeaponUnlockChecker(provider(highScore, 0, totalCoins));
  }

  private static ShipUnlockChecker ships(int highScore, int totalCoins) {
    return new ShipUnlockChecker(provider(highScore, 0, totalCoins));
  }

  private static DataProvider provider(int normalScore, int expertScore, int totalCoins) {
    return new StubDataProvider(normalScore, expertScore, totalCoins);
  }

  /** Returns exactly the numbers it was given; touches no files. */
  private static final class StubDataProvider implements DataProvider {
    private final int normalScore;
    private final int expertScore;
    private final int totalCoins;

    StubDataProvider(int normalScore, int expertScore, int totalCoins) {
      this.normalScore = normalScore;
      this.expertScore = expertScore;
      this.totalCoins = totalCoins;
    }

    @Override
    public HighScore getHighScore() {
      Map<String, Integer> scores = new HashMap<>();
      scores.put(GameMode.NORMAL.name(), normalScore);
      scores.put(GameMode.EXPERT.name(), expertScore);
      return new HighScore(scores);
    }

    @Override
    public TotalCoins getTotalCoins() {
      return new TotalCoins(totalCoins);
    }
  }
}
