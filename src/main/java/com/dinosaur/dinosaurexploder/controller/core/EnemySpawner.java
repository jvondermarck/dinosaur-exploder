/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.controller.core;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;
import static javafx.util.Duration.seconds;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.time.TimerAction;
import com.dinosaur.dinosaurexploder.components.GreenDinoComponent;
import com.dinosaur.dinosaurexploder.constants.Direction;
import com.dinosaur.dinosaurexploder.controller.BossSpawner;
import com.dinosaur.dinosaurexploder.model.GameData;
import com.dinosaur.dinosaurexploder.utils.LevelManager;

public class EnemySpawner {

  private final LevelManager levelManager;
  private final BossSpawner bossSpawner;
  private TimerAction enemySpawnTimer;
  private boolean isSpawningPaused = false;
  private static final String GREEN_DINO_STRING = "greenDino";

  public EnemySpawner(GameInitializer gameInitializer) {
    this.levelManager = gameInitializer.getLevelManager();
    this.bossSpawner = gameInitializer.getBossSpawner();
  }

  /** Summary : This method is used to spawn the enemies and set the spawn rate of the enemies */
  public void spawnEnemies() {
    if (enemySpawnTimer != null) {
      enemySpawnTimer.expire();
    }

    enemySpawnTimer =
        run(
            () -> {
              if (levelManager.getCurrentLevel() % 10 == 0) {
                pauseEnemySpawning();
                bossSpawner.spawnNewBoss("orange");
              } else if (levelManager.getCurrentLevel() % 5 == 0) {
                pauseEnemySpawning();
                bossSpawner.spawnNewBoss("red");
              } else {
                if (!isSpawningPaused && random(0, 2) < 2) {
                  // direction is up for normal mode, random for expert mode
                  Direction direction = Direction.modeDirection(GameData.getSelectedDifficulty());
                  Entity greenDino =
                      switch (direction) {
                        case DOWN ->
                            spawn(
                                GREEN_DINO_STRING,
                                random(0, getAppWidth() - 80),
                                getAppHeight() - 30);
                        case LEFT -> spawn(GREEN_DINO_STRING, -50, random(0, getAppHeight() - 80));
                        case RIGHT ->
                            spawn(
                                GREEN_DINO_STRING,
                                getAppWidth() - 40,
                                random(0, getAppHeight() - 80));
                        default -> spawn(GREEN_DINO_STRING, random(0, getAppWidth() - 80), -50);
                      };
                  // Apply direction to component
                  greenDino.getComponent(GreenDinoComponent.class).updateDirection(direction);
                }
              }
            },
            seconds(levelManager.getEnemySpawnRate()));
  }

  /** Summary : This method is used to pause the enemy spawning */
  public void pauseEnemySpawning() {
    isSpawningPaused = true;
    if (enemySpawnTimer != null) {
      enemySpawnTimer.pause();
    }
  }

  /** Summary : This method is used to resume the enemy spawning */
  public void resumeEnemySpawning() {
    isSpawningPaused = false;
    if (enemySpawnTimer != null) {
      enemySpawnTimer.resume();
    } else {
      spawnEnemies();
    }
  }
}
