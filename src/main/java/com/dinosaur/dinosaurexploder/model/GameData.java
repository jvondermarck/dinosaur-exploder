package com.dinosaur.dinosaurexploder.model;

import com.dinosaur.dinosaurexploder.exception.LockedShipException;
import com.dinosaur.dinosaurexploder.utils.ShipUnlockChecker;

public class GameData {
    // Static variable that stores the selected ship
    private static int selectedShip = 1; // Default ship
    private static final ShipUnlockChecker unlockChecker = new ShipUnlockChecker();

    // Getter and setter for the selected ship
    public static int getSelectedShip() {
        return selectedShip;
    }

    public static void setSelectedShip(int shipNumber) {
        selectedShip = unlockChecker.check(shipNumber);
    }

    public static boolean checkUnlockedShip(int shipNumber) {
        try {
            unlockChecker.check(shipNumber);
            return true;
        } catch (LockedShipException e) {
            return false;
        }
    }
}
