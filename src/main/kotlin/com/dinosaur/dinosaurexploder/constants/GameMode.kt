/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */
package com.dinosaur.dinosaurexploder.constants

/**
 * Each game mode carries its own difficulty settings, so game logic can read them generically
 * instead of branching on a specific mode. To add a new mode, add a constant with its values.
 *
 * Properties are grouped as: player settings, level goals (bosses and enemies per level), enemy
 * spawn rate (seconds between spawns) and speed with their per-level scaling, and asteroid spawn
 * rate with its per-level increment.
 */
enum class GameMode(
    val startingLives: Int,
    /** Seconds of invincibility after the player is hit. */
    val damageGracePeriodSeconds: Double,
    val bossesToDefeat: Int,
    val initialEnemiesToDefeat: Int,
    val enemiesToDefeatIncrement: Int,
    val initialEnemySpawnRate: Double,
    val minEnemySpawnRate: Double,
    val enemySpawnRateDecay: Double,
    val initialEnemySpeed: Double,
    val enemySpeedIncrement: Double,
    val initialAsteroidsSpawnRate: Double,
    val asteroidsSpawnRateIncrement: Double,
) {
    EASY(
        startingLives = 5,
        damageGracePeriodSeconds = 2.0,
        bossesToDefeat = 1,
        initialEnemiesToDefeat = 3,
        enemiesToDefeatIncrement = 2,
        initialEnemySpawnRate = 1.5,
        minEnemySpawnRate = 0.8,
        enemySpawnRateDecay = 0.95,
        initialEnemySpeed = 0.8,
        enemySpeedIncrement = 0.1,
        initialAsteroidsSpawnRate = 2.5,
        asteroidsSpawnRateIncrement = 0.05,
    ),
    NORMAL(
        startingLives = 3,
        damageGracePeriodSeconds = 1.5,
        bossesToDefeat = 1,
        initialEnemiesToDefeat = 5,
        enemiesToDefeatIncrement = 5,
        initialEnemySpawnRate = 0.75,
        minEnemySpawnRate = 0.3,
        enemySpawnRateDecay = 0.9,
        initialEnemySpeed = 1.5,
        enemySpeedIncrement = 0.2,
        initialAsteroidsSpawnRate = 1.5,
        asteroidsSpawnRateIncrement = 0.1,
    ),
    EXPERT(
        startingLives = 3,
        damageGracePeriodSeconds = 1.0,
        bossesToDefeat = 2,
        initialEnemiesToDefeat = 5,
        enemiesToDefeatIncrement = 5,
        initialEnemySpawnRate = 0.75,
        minEnemySpawnRate = 0.3,
        enemySpawnRateDecay = 0.9,
        initialEnemySpeed = 1.5,
        enemySpeedIncrement = 0.2,
        initialAsteroidsSpawnRate = 1.5,
        asteroidsSpawnRateIncrement = 0.1,
    ),
}
