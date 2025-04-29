package com.dinosaur.dinosaurexploder.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LevelManagerTest {

    @Test
    void levelUp() {
        LevelManager levelManager = new LevelManager();

        for (int i = 0; i < levelManager.getEnemiesToDefeat(); i++) {
            levelManager.incrementDefeatedEnemies();
        }
        
        assertTrue(levelManager.shouldAdvanceLevel());
    }

    @Test
    void levelUpManyTimes() {
        LevelManager levelManager = new LevelManager();

        for (int i = 0; i < levelManager.getEnemiesToDefeat(); i++) {
            levelManager.incrementDefeatedEnemies();
        }
        levelManager.nextLevel();
        for (int i = 0; i < 2 * levelManager.getEnemiesToDefeat(); i++) {
            levelManager.incrementDefeatedEnemies();
        }

        assertTrue(levelManager.shouldAdvanceLevel());
    }

}