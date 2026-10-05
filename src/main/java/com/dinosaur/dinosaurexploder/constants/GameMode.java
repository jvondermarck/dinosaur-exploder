/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.constants;

/**
 * Each game mode carries its own difficulty settings, so game logic can read them generically
 * instead of branching on a specific mode. To add a new mode, add a constant with its values.
 */
public enum GameMode {
  // lives, graceSec, bosses, enemies, enemiesInc, spawnRate, minSpawn, spawnDecay, speed,
  // speedInc, asteroidsRate, asteroidsRateInc
  EASY(5, 2.0, 1, 3, 2, 1.5, 0.8, 0.95, 0.8, 0.1, 2.5, 0.05),
  NORMAL(3, 1.5, 1, 5, 5, 0.75, 0.3, 0.9, 1.5, 0.2, 1.5, 0.1),
  EXPERT(3, 1.0, 2, 5, 5, 0.75, 0.3, 0.9, 1.5, 0.2, 1.5, 0.1);

  private final int startingLives;
  private final double damageGracePeriodSeconds;
  private final int bossesToDefeat;
  private final int initialEnemiesToDefeat;
  private final int enemiesToDefeatIncrement;
  private final double initialEnemySpawnRate;
  private final double minEnemySpawnRate;
  private final double enemySpawnRateDecay;
  private final double initialEnemySpeed;
  private final double enemySpeedIncrement;
  private final double initialAsteroidsSpawnRate;
  private final double asteroidsSpawnRateIncrement;

  GameMode(
      int startingLives,
      double damageGracePeriodSeconds,
      int bossesToDefeat,
      int initialEnemiesToDefeat,
      int enemiesToDefeatIncrement,
      double initialEnemySpawnRate,
      double minEnemySpawnRate,
      double enemySpawnRateDecay,
      double initialEnemySpeed,
      double enemySpeedIncrement,
      double initialAsteroidsSpawnRate,
      double asteroidsSpawnRateIncrement) {
    this.startingLives = startingLives;
    this.damageGracePeriodSeconds = damageGracePeriodSeconds;
    this.bossesToDefeat = bossesToDefeat;
    this.initialEnemiesToDefeat = initialEnemiesToDefeat;
    this.enemiesToDefeatIncrement = enemiesToDefeatIncrement;
    this.initialEnemySpawnRate = initialEnemySpawnRate;
    this.minEnemySpawnRate = minEnemySpawnRate;
    this.enemySpawnRateDecay = enemySpawnRateDecay;
    this.initialEnemySpeed = initialEnemySpeed;
    this.enemySpeedIncrement = enemySpeedIncrement;
    this.initialAsteroidsSpawnRate = initialAsteroidsSpawnRate;
    this.asteroidsSpawnRateIncrement = asteroidsSpawnRateIncrement;
  }

  public int getStartingLives() {
    return startingLives;
  }

  /** Seconds of invincibility after the player is hit. */
  public double getDamageGracePeriodSeconds() {
    return damageGracePeriodSeconds;
  }

  public int getBossesToDefeat() {
    return bossesToDefeat;
  }

  public int getInitialEnemiesToDefeat() {
    return initialEnemiesToDefeat;
  }

  public int getEnemiesToDefeatIncrement() {
    return enemiesToDefeatIncrement;
  }

  public double getInitialEnemySpawnRate() {
    return initialEnemySpawnRate;
  }

  public double getMinEnemySpawnRate() {
    return minEnemySpawnRate;
  }

  public double getEnemySpawnRateDecay() {
    return enemySpawnRateDecay;
  }

  public double getInitialEnemySpeed() {
    return initialEnemySpeed;
  }

  public double getEnemySpeedIncrement() {
    return enemySpeedIncrement;
  }

  public double getInitialAsteroidsSpawnRate() {
    return initialAsteroidsSpawnRate;
  }

  public double getAsteroidsSpawnRateIncrement() {
    return asteroidsSpawnRateIncrement;
  }
}
