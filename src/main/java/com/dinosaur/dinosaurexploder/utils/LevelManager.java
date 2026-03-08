/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.utils;

import com.dinosaur.dinosaurexploder.constants.GameMode;

/**
 * This class manages the game levels, including the current level, number of enemies to defeat,
 * enemy spawn rate, and enemy speed. It provides methods to advance levels, reset the game, and
 * check if the player can advance to the next level.
 */
public class LevelManager {
  private int currentLevel = 1;
  private int enemiesToDefeat = 5;
  private int defeatedEnemies = 0;
  private int bossesToDefeat = 1;
  private int defeatedBosses = 0;
  private double enemySpawnRate = 0.75;
  private double enemySpeed = 1.5;
  private double asteroidsSpawnRate = 1.5;
  private double asteroidsVerticalSpeed = 0.8;
  private double asteroidsHorizontalSpeed = 0.2;
  private GameMode gameMode = GameMode.NORMAL;

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
    return Math.max(
        (float) defeatedEnemies / enemiesToDefeat, (float) defeatedBosses / bossesToDefeat);
  }

  public void incrementDefeatedEnemies() {
    defeatedEnemies++;
  }

  public void incrementDefeatedBosses() {
    defeatedBosses++;
  }

  public boolean shouldAdvanceLevel() {
    return defeatedEnemies >= enemiesToDefeat || defeatedBosses >= bossesToDefeat;
  }

  public double getAsteroidsVerticalSpeed() {
    return asteroidsVerticalSpeed;
  }

  public double getAsteroidsHorizontalSpeed() {
    return asteroidsHorizontalSpeed;
  }

  public double getAsteroidsSpawnRate() {
    return asteroidsSpawnRate;
  }

  public void nextLevel() {
    currentLevel++;
    defeatedEnemies = 0;
    enemiesToDefeat += 5;
    defeatedBosses = 0;

    enemySpawnRate = Math.max(0.3, enemySpawnRate * 0.9);
    enemySpeed += 0.2;
    asteroidsSpawnRate += 0.1;
  }

  public int getEnemiesToDefeat() {
    return enemiesToDefeat;
  }

  public void setBossesToDefeat(int bossesToDefeat) {
    this.bossesToDefeat = bossesToDefeat;
  }

  public int getBossesToDefeat() {
    return bossesToDefeat;
  }

  public void setGameMode(GameMode mode) {
    gameMode = mode;
  }

  public GameMode getGameMode() {
    return gameMode;
  }
}
