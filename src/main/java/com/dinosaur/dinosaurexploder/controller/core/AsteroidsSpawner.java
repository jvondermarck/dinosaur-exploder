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
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import java.util.Arrays;
import java.util.List;

public class AsteroidsSpawner {

  private final LevelManager levelManager;
  private TimerAction asteroidsSpawnTimer;
  private boolean isSpawningPaused = false;
  private final List<String> listOfAsteroids =
      Arrays.asList("littleAsteroids", "mediumAsteroids", "bigAsteroids");

  public AsteroidsSpawner(GameInitializer gameInitializer) {
    this.levelManager = gameInitializer.getLevelManager();
  }

  public void spawnAsteroids() {
    if (asteroidsSpawnTimer != null) {
      asteroidsSpawnTimer.expire();
    }

    asteroidsSpawnTimer =
        run(
            () -> {
              if (!isSpawningPaused && !(levelManager.getCurrentLevel() % 10 == 0)) {
                Entity asteroids =
                    spawn(listOfAsteroids.get(random(0, 2)), random(30, getAppWidth() - 30), -100);
              }
            },
            seconds(levelManager.getAsteroidsSpawnRate()));
  }

  /** Summary : This method is used to pause the asteroids spawning */
  public void pauseAsteroidsSpawning() {
    isSpawningPaused = true;
    if (asteroidsSpawnTimer != null) {
      asteroidsSpawnTimer.pause();
    }
  }

  /** Summary : This method is used to resume the asteroids spawning */
  public void resumeAsteroidsSpawning() {
    isSpawningPaused = false;
    if (asteroidsSpawnTimer != null) {
      asteroidsSpawnTimer.resume();
    } else {
      spawnAsteroids();
    }
  }
}
