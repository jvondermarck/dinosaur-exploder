package com.dinosaur.dinosaurexploder.model;

import com.dinosaur.dinosaurexploder.exception.LockedShipException;
import com.dinosaur.dinosaurexploder.utils.DataProvider;
import com.dinosaur.dinosaurexploder.utils.ShipUnlockChecker;
import com.dinosaur.dinosaurexploder.utils.WeaponUnlockChecker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class SelectionTest {

    public static final int HIGH_SCORE = 100;
    public static final int UNLOCKED_WEAPON_HIGHER_LIMIT = 2;
    public static final int UNLOCKED_SHIP_HIGHER_LIMIT = 3;
    public static final int TOTAL_COINS = 10;

    @Test
    void cannotSelectLockedShip() {
        ShipUnlockChecker shipUnlockChecker = new ShipUnlockChecker(new MockDataProvider());
        assertThrows(LockedShipException.class, () -> shipUnlockChecker.check(UNLOCKED_SHIP_HIGHER_LIMIT + 1));
    }

    @Test
    void selectUnlockedShip() {
        ShipUnlockChecker shipUnlockChecker = new ShipUnlockChecker(new MockDataProvider());
        for (int i = 1; i <= UNLOCKED_SHIP_HIGHER_LIMIT; i++) shipUnlockChecker.check(i);
    }

    @Test
    void cannotSelectLockedWeapon() {
        WeaponUnlockChecker weaponUnlockChecker = new WeaponUnlockChecker(new MockDataProvider());
        weaponUnlockChecker.check(UNLOCKED_WEAPON_HIGHER_LIMIT + 1);
    }

    @Test
    void selectUnlockedWeapon() {
        WeaponUnlockChecker weaponUnlockChecker = new WeaponUnlockChecker(new MockDataProvider());
        for (int i = 1; i <= UNLOCKED_WEAPON_HIGHER_LIMIT; i++) weaponUnlockChecker.check(i);
    }

    class MockDataProvider implements DataProvider {
        @Override
        public HighScore getHighScore() {
            return new HighScore(HIGH_SCORE);
        }

        @Override
        public TotalCoins getTotalCoins() {
            return new TotalCoins(TOTAL_COINS);
        }
    }
}
