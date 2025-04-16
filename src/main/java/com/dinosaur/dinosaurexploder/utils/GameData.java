package com.dinosaur.dinosaurexploder.utils;

public class GameData {
    // Static variable that stores the selected ship
    private static int selectedShip = 1; // Default ship

    // Getter and setter for the selected ship
    public static int getSelectedShip() {
        return selectedShip;
    }

    public static void setSelectedShip(int shipNumber) {
        ShipUnlockChecker rentalService = new ShipUnlockChecker();
        selectedShip = rentalService.check(shipNumber);
    }
}
