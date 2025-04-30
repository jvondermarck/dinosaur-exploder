package com.dinosaur.dinosaurexploder.model;

import com.dinosaur.dinosaurexploder.exception.LockedShipException;
import com.dinosaur.dinosaurexploder.utils.ShipUnlockChecker;
import com.dinosaur.dinosaurexploder.utils.WeaponUnlockChecker;
import com.dinosaur.dinosaurexploder.exception.LockedWeaponException;

public class GameData {
    // Static variable that stores the selected ship and weapon
    private static int selectedShip = 1; // Default ship
    private static int selectedWeapon = 1; // Default weapon
    private static final ShipUnlockChecker shipUnlockChecker = new ShipUnlockChecker();
    private static final WeaponUnlockChecker weaponUnlockChecker = new WeaponUnlockChecker();

    // Static variable that stores the high score
    private static int highScore;

    // Getter and setter for the selected ship
    public static int getSelectedShip() {
        return selectedShip;
    }

    // Static variable that stores total coins
    private static int totalCoins;

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

    // Getter for the high score
    public static int getHighScore() {
        highScore = shipUnlockChecker.getHighScore().getHigh();
        return highScore;
    }

    // Getter for total coins
    public static int getTotalCoins() {
        totalCoins = unlockChecker.getTotalCoins().getTotal();
        System.out.println("Total: " + totalCoins);
        return totalCoins;
    }
}
