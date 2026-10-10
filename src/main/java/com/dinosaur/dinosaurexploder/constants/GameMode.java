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
  EASY(
      5,
      2.0,
      new LevelGoals(1, 3, 2),
      new EnemyTuning(1.5, 0.8, 0.95, 0.8, 0.1),
      new AsteroidTuning(2.5, 0.05, 0.4, 0.1))
  NORMAL(
      3,
      1.5,
      new LevelGoals(1, 5, 5),
      new EnemyTuning(0.75, 0.3, 0.9, 1.5, 0.2),
      new AsteroidTuning(1.5, 0.1)),
  EXPERT(
      3,
      1.0,
      new LevelGoals(2, 5, 5),
      new EnemyTuning(0.75, 0.3, 0.9, 1.5, 0.2),
      new AsteroidTuning(1.5, 0.1));

  /** Level progression goals: bosses to defeat and enemies required per level. */
  private record LevelGoals(int bossesToDefeat, int initialEnemiesToDefeat, int enemiesIncrement) {}

  /** Enemy spawn rate (seconds between spawns) and speed, with their per-level scaling. */
  private record EnemyTuning(
      double initialSpawnRate,
      double minSpawnRate,
      double spawnRateDecay,
      double initialSpeed,
      double speedIncrement) {}

  /** Asteroid spawn rate, its per-level increment, and speeds. */
  private record AsteroidTuning(
      double initialSpawnRate,
      double spawnRateIncrement,
      double verticalSpeed,
      double horizontalSpeed) {}

  private final int startingLives;
  private final double damageGracePeriodSeconds;
  private final LevelGoals levelGoals;
  private final EnemyTuning enemyTuning;
  private final AsteroidTuning asteroidTuning;

  GameMode(
      int startingLives,
      double damageGracePeriodSeconds,
      LevelGoals levelGoals,
      EnemyTuning enemyTuning,
      AsteroidTuning asteroidTuning) {
    this.startingLives = startingLives;
    this.damageGracePeriodSeconds = damageGracePeriodSeconds;
    this.levelGoals = levelGoals;
    this.enemyTuning = enemyTuning;
    this.asteroidTuning = asteroidTuning;
  }

  public int getStartingLives() {
    return startingLives;
  }

  /** Seconds of invincibility after the player is hit. */
  public double getDamageGracePeriodSeconds() {
    return damageGracePeriodSeconds;
  }

  public int getBossesToDefeat() {
    return levelGoals.bossesToDefeat();
  }

  public int getInitialEnemiesToDefeat() {
    return levelGoals.initialEnemiesToDefeat();
  }

  public int getEnemiesToDefeatIncrement() {
    return levelGoals.enemiesIncrement();
  }

  public double getInitialEnemySpawnRate() {
    return enemyTuning.initialSpawnRate();
  }

  public double getMinEnemySpawnRate() {
    return enemyTuning.minSpawnRate();
  }

  public double getEnemySpawnRateDecay() {
    return enemyTuning.spawnRateDecay();
  }

  public double getInitialEnemySpeed() {
    return enemyTuning.initialSpeed();
  }

  public double getEnemySpeedIncrement() {
    return enemyTuning.speedIncrement();
  }

  public double getInitialAsteroidsSpawnRate() {
    return asteroidTuning.initialSpawnRate();
  }

  public double getAsteroidsSpawnRateIncrement() {
    return asteroidTuning.spawnRateIncrement();
  }

  public double getAsteroidsVerticalSpeed() {
    return asteroidTuning.verticalSpeed();
  }

  public double getAsteroidsHorizontalSpeed() {
    return asteroidTuning.horizontalSpeed();
  }
}
