package com.dinosaur.dinosaurexploder.utils;

/**
 * This class manages the game levels, including the current level, number of enemies to defeat,
 * enemy spawn rate, and enemy speed. It provides methods to advance levels, reset the game, and
 * check if the player can advance to the next level.
 */
public class LevelManager {
    private int currentLevel = 1;
    private int enemiesToDefeat = 5;
    private int defeatedEnemies = 0;
    private double enemySpawnRate = 0.75;
    private double enemySpeed = 1.5;

    public int getCurrentLevel() {
        return currentLevel;
    }

    public double getEnemySpawnRate() {
        return enemySpawnRate;
    }
    public double getEnemySpeed() {
        return enemySpeed;
    }

    public float getLevelProgress() {
        return (float) defeatedEnemies / enemiesToDefeat;
    }

    public void incrementDefeatedEnemies() {
        defeatedEnemies++;
    }

    public boolean shouldAdvanceLevel() {
        return defeatedEnemies >= enemiesToDefeat;
    }

    public void nextLevel() {
        currentLevel++;
        defeatedEnemies = 0;
        enemiesToDefeat += 5;

        enemySpawnRate = Math.max(0.3, enemySpawnRate * 0.9);
        enemySpeed += 0.2;
    }
}