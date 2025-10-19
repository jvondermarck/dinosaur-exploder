package com.dinosaur.dinosaurexploder.featureTest;

import com.dinosaur.dinosaurexploder.exception.LockedShipException;
import com.dinosaur.dinosaurexploder.model.HighScore;
import com.dinosaur.dinosaurexploder.model.TotalCoins;
import com.dinosaur.dinosaurexploder.utils.DataProvider;
import com.dinosaur.dinosaurexploder.utils.ShipUnlockChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ShipUnlockChecker verifying unlock logic using coins and high scores.
 * Adjusted to work independently of LanguageManager translations.
 */
public class ShopTest {

    private ShipUnlockChecker shipChecker;
    private MockDataProvider dataProvider;

    static class MockDataProvider implements DataProvider {
        private int score;
        private int coins;

        public void setHighScore(int score) {
            this.score = score;
        }

        public void setTotalCoins(int coins) {
            this.coins = coins;
        }

        @Override
        public HighScore getHighScore() {
            return new HighScore(score);
        }

        @Override
        public TotalCoins getTotalCoins() {
            return new TotalCoins(coins);
        }
    }

    @BeforeEach
    void setUp() {
        dataProvider = new MockDataProvider();
        shipChecker = new ShipUnlockChecker(dataProvider);
    }

    @Test
    void shipUnlocks_whenHighScoreAndCoinsAreEnough() {
        dataProvider.setHighScore(350);
        dataProvider.setTotalCoins(150);
        assertDoesNotThrow(() -> shipChecker.check(5));
    }

    @Test
    void shipLocked_whenLowScoreButEnoughCoins() {
        dataProvider.setHighScore(200);
        dataProvider.setTotalCoins(200);
        LockedShipException ex = assertThrows(LockedShipException.class, () -> shipChecker.check(5));
        assertNotNull(ex.getMessage());
        assertFalse(ex.getMessage().isEmpty());
    }

    @Test
    void shipLocked_whenEnoughScoreButLowCoins() {
        dataProvider.setHighScore(350);
        dataProvider.setTotalCoins(50);
        LockedShipException ex = assertThrows(LockedShipException.class, () -> shipChecker.check(5));
        assertNotNull(ex.getMessage());
        assertFalse(ex.getMessage().isEmpty());
    }

    @Test
    void shipLocked_whenLowScoreAndLowCoins() {
        dataProvider.setHighScore(100);
        dataProvider.setTotalCoins(100);
        LockedShipException ex = assertThrows(LockedShipException.class, () -> shipChecker.check(6));
        assertNotNull(ex.getMessage());
        assertFalse(ex.getMessage().isEmpty());
    }

    @Test
    void shipUnlocks_whenNoRequirements() {
        dataProvider.setHighScore(0);
        dataProvider.setTotalCoins(0);
        assertDoesNotThrow(() -> shipChecker.check(1));
    }
}
