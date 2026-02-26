/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.controller.core;

import static com.almasb.fxgl.dsl.FXGL.spawn;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.FXGLForKtKt;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.time.TimerAction;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import javafx.util.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AsteroidsSpawnerTest {

  @Mock GameInitializer gameInitializer;

  @Mock LevelManager levelManager;

  @Mock TimerAction timer1;

  private AsteroidsSpawner spawner;

  @BeforeEach
  public void setup() {
    when(gameInitializer.getLevelManager()).thenReturn(levelManager);
    when(levelManager.getAsteroidsSpawnRate()).thenReturn(0.1);

    spawner = new AsteroidsSpawner(gameInitializer);
  }

  @Test
  public void spawnAsteroids_LevelOtherThan10_spawnAsteroids() {
    when(levelManager.getCurrentLevel()).thenReturn(1);

    ArgumentCaptor<Runnable> tick = ArgumentCaptor.forClass(Runnable.class);

    try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class);
        MockedStatic<FXGLForKtKt> fxglKt = mockStatic(FXGLForKtKt.class)) { // ← ajouter ceci

      fxgl.when(() -> FXGL.run(tick.capture(), any(Duration.class))).thenReturn(timer1);
      fxgl.when(() -> FXGL.random(anyInt(), anyInt())).thenReturn(1);

      fxglKt
          .when(() -> FXGLForKtKt.spawn(anyString(), anyDouble(), anyDouble()))
          .thenReturn(mock(Entity.class));

      spawner.spawnAsteroids();
      tick.getValue().run();

      verify(levelManager, atLeastOnce()).getCurrentLevel();
    }
  }

  @Test
  public void spawnAsteroids_levelMultipleOf10_doesNotSpawn() {
    when(levelManager.getCurrentLevel()).thenReturn(10);

    ArgumentCaptor<Runnable> tick = ArgumentCaptor.forClass(Runnable.class);

    try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class)) {
      fxgl.when(() -> FXGL.run(tick.capture(), any(Duration.class))).thenReturn(timer1);

      spawner.spawnAsteroids();
      tick.getValue().run();

      fxgl.verify(() -> spawn(anyString(), anyInt(), anyInt()), never());
    }
  }

  @Test
  public void pauseAsteroidsSpawning_pausesTimer() {
    ArgumentCaptor<Runnable> tick = ArgumentCaptor.forClass(Runnable.class);

    try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class)) {
      fxgl.when(() -> FXGL.run(tick.capture(), any(Duration.class))).thenReturn(timer1);

      spawner.spawnAsteroids();
      spawner.pauseAsteroidsSpawning();

      verify(timer1).pause();
    }
  }
}
