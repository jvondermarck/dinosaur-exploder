/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */
package com.dinosaur.dinosaurexploder.controller.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.time.TimerAction;
import com.dinosaur.dinosaurexploder.controller.BossSpawner;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import javafx.util.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EnemySpawnerPauseResumeTest {

  @Mock GameInitializer gameInitializer;

  @Mock LevelManager levelManager;

  @Mock BossSpawner bossSpawner;

  @Mock TimerAction timerAction;

  private EnemySpawner enemySpawner;

  @BeforeEach
  public void setup() {
    when(gameInitializer.getLevelManager()).thenReturn(levelManager);
    when(gameInitializer.getBossSpawner()).thenReturn(bossSpawner);
    when(levelManager.getEnemySpawnRate()).thenReturn(1.0);

    enemySpawner = new EnemySpawner(gameInitializer);
  }

  @Test
  public void pauseEnemySpawning_shouldPauseTimer_ifTimerExists() {
    try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class)) {

      fxgl.when(() -> FXGL.run(any(Runnable.class), any(Duration.class))).thenReturn(timerAction);

      enemySpawner.spawnEnemies();

      enemySpawner.pauseEnemySpawning();

      verify(timerAction).pause();
    }
  }

  @Test
  public void resumeEnemySpawning_shouldResumeTimer_ifTimerExists() {
    try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class)) {

      fxgl.when(() -> FXGL.run(any(Runnable.class), any(Duration.class))).thenReturn(timerAction);

      enemySpawner.spawnEnemies();

      enemySpawner.pauseEnemySpawning();

      enemySpawner.resumeEnemySpawning();

      verify(timerAction).resume();
    }
  }

  @Test
  public void resumeEnemySpawning_shouldCallSpawnEnemies_ifTimerIsNull() {
    try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class)) {

      fxgl.when(() -> FXGL.run(any(Runnable.class), any(Duration.class))).thenReturn(timerAction);

      assertDoesNotThrow(() -> enemySpawner.resumeEnemySpawning());

      fxgl.verify(() -> FXGL.run(any(Runnable.class), any(Duration.class)));
    }
  }
}
