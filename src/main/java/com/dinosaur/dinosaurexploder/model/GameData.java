/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.model;

import com.dinosaur.dinosaurexploder.constants.GameMode;
import com.dinosaur.dinosaurexploder.exception.LockedShipException;
import com.dinosaur.dinosaurexploder.exception.LockedWeaponException;
import com.dinosaur.dinosaurexploder.specialties.Specialty;
import com.dinosaur.dinosaurexploder.specialties.SpecialtyManager;
import com.dinosaur.dinosaurexploder.utils.FileDataProvider;
import com.dinosaur.dinosaurexploder.utils.ShipUnlockChecker;
import com.dinosaur.dinosaurexploder.utils.WeaponUnlockChecker;
import java.util.logging.Logger;

public class GameData {
  // Static variable that stores the selected ship, weapon and difficulty
  private static int selectedShip = 1; // Default ship
  private static int selectedWeapon = 1; // Default weapon
  private static GameMode selectedDifficulty = GameMode.NORMAL; // Default difficulty
  private static Specialty selectedSpecialty = SpecialtyManager.NULL_SPECIALTY;

  private static final ShipUnlockChecker shipUnlockChecker =
      new ShipUnlockChecker(new FileDataProvider());
  private static final WeaponUnlockChecker weaponUnlockChecker =
      new WeaponUnlockChecker(new FileDataProvider());

  // Static variable that stores the high score
  private static int highScore;

  // Static variable that stores total coins
  private static int totalCoins;

  private static Logger logger = Logger.getLogger(GameData.class.getName());

  private GameData() {}

  // Getter and setter for the selected ship
  public static int getSelectedShip() {
    return selectedShip;
  }

  public static void setSelectedShip(int shipNumber) {
    selectedShip = shipUnlockChecker.check(shipNumber);
  }

  public static boolean checkUnlockedShip(int shipNumber) {
    try {
      shipUnlockChecker.check(shipNumber);
      return true;
    } catch (LockedShipException e) {
      return false;
    }
  }

  // Getter and setter for the selected ship
  public static int getSelectedWeapon() {
    return selectedWeapon;
  }

  public static void setSelectedWeapon(int weaponNumber) {
    selectedWeapon = weaponUnlockChecker.check(weaponNumber);
  }

  public static boolean checkUnlockedWeapon(int weaponNumber) {
    try {
      weaponUnlockChecker.check(weaponNumber);
      return true;
    } catch (LockedWeaponException e) {
      return false;
    }
  }

  // Getter and setter for the selected difficulty
  public static GameMode getSelectedDifficulty() {
    return selectedDifficulty;
  }

  public static void setSelectedDifficulty(GameMode gameMode) {
    selectedDifficulty = gameMode;
  }

  public static Specialty getSelectedSpecialty() {
    return selectedSpecialty;
  }

  public static void setSelectedSpecialty(Specialty specialty) {
    selectedSpecialty = specialty;
  }

  public static int getMaxHighScore() {
    return new FileDataProvider().getHighScore().getHigh();
  }

  // Getter for the high score
  public static int getHighScore() {
    highScore = new FileDataProvider().getHighScore().getHigh(selectedDifficulty.name());

    return highScore;
  }

  public static int getHighScore(GameMode mode) {
    highScore = new FileDataProvider().getHighScore().getHigh(mode.name());

    return highScore;
  }

  // Getter for total coins
  public static int getTotalCoins() {
    totalCoins = new FileDataProvider().getTotalCoins().getTotal();
    logger.info(() -> String.format("Total: %s", totalCoins));
    return totalCoins;
  }
}
