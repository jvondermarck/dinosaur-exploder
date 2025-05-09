package com.dinosaur.dinosaurexploder.model;

import com.dinosaur.dinosaurexploder.utils.LevelManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

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