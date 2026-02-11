/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */
package com.dinosaur.dinosaurexploder.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.FXGLForKtKt;
import com.almasb.fxgl.entity.Entity;
import com.dinosaur.dinosaurexploder.components.HealthbarComponent;
import com.dinosaur.dinosaurexploder.components.OrangeDinoComponent;
import com.dinosaur.dinosaurexploder.components.RedDinoComponent;
import com.dinosaur.dinosaurexploder.model.Settings;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BossSpawnerTest {

  @Test
  public void spawnNewBoss_red_shouldSpawnRedBossAndWireHealthbar() {

    Settings settings = mock(Settings.class);
    LevelManager levelManager = mock(LevelManager.class);

    BossSpawner spawner = new BossSpawner(settings, levelManager);

    Entity redEntity = mock(Entity.class);
    Entity healthBarEntity = mock(Entity.class);

    RedDinoComponent redComp = mock(RedDinoComponent.class);
    HealthbarComponent hbComp = mock(HealthbarComponent.class);

    when(redEntity.getComponent(RedDinoComponent.class)).thenReturn(redComp);
    when(healthBarEntity.getComponent(HealthbarComponent.class)).thenReturn(hbComp);

    try (MockedStatic<FXGL> fxglMock = mockStatic(FXGL.class);
        MockedStatic<FXGLForKtKt> spawnMock = mockStatic(FXGLForKtKt.class)) {

      fxglMock.when(FXGL::getAppCenter).thenReturn(new Point2D(400, 300));
      fxglMock.when(FXGL::getAppWidth).thenReturn(800);

      spawnMock
          .when(() -> FXGLForKtKt.spawn(eq("redDino"), anyDouble(), anyDouble()))
          .thenReturn(redEntity);

      spawnMock
          .when(() -> FXGLForKtKt.spawn(eq("healthBar"), anyDouble(), anyDouble()))
          .thenReturn(healthBarEntity);

      spawner.spawnNewBoss("red");

      verify(redComp).setLevelManager(levelManager);

      verify(hbComp).setDinoComponent(redComp);

      spawnMock.verify(() -> FXGLForKtKt.spawn("redDino", 355.0, 50.0));

      spawnMock.verify(() -> FXGLForKtKt.spawn("healthBar", 585.0, 15.0));
    }
  }

  @Test
  public void spawnNewBoss_orange_shouldSpawnOrangeBossAndWireHealthbar() {

    Settings settings = mock(Settings.class);
    LevelManager levelManager = mock(LevelManager.class);

    BossSpawner spawner = new BossSpawner(settings, levelManager);

    Entity orangeEntity = mock(Entity.class);
    Entity healthBarEntity = mock(Entity.class);

    OrangeDinoComponent orangeComp = mock(OrangeDinoComponent.class);
    HealthbarComponent hbComp = mock(HealthbarComponent.class);

    when(orangeEntity.getComponent(OrangeDinoComponent.class)).thenReturn(orangeComp);
    when(healthBarEntity.getComponent(HealthbarComponent.class)).thenReturn(hbComp);

    try (MockedStatic<FXGL> fxglMock = mockStatic(FXGL.class);
        MockedStatic<FXGLForKtKt> spawnMock = mockStatic(FXGLForKtKt.class)) {

      fxglMock.when(FXGL::getAppCenter).thenReturn(new Point2D(500, 300));
      fxglMock.when(FXGL::getAppWidth).thenReturn(1000);

      spawnMock
          .when(() -> FXGLForKtKt.spawn(eq("orangeDino"), anyDouble(), anyDouble()))
          .thenReturn(orangeEntity);

      spawnMock
          .when(() -> FXGLForKtKt.spawn(eq("healthBar"), anyDouble(), anyDouble()))
          .thenReturn(healthBarEntity);

      spawner.spawnNewBoss("orange");

      verify(orangeComp).setLevelManager(levelManager);
      verify(hbComp).setDinoComponent(orangeComp);

      spawnMock.verify(() -> FXGLForKtKt.spawn("orangeDino", 455.0, 50.0));

      spawnMock.verify(() -> FXGLForKtKt.spawn("healthBar", 785.0, 15.0));
    }
  }

  @Test
  public void updateHealthBar_shouldCallUpdateBarOnHealthbarComponent() {

    Settings settings = mock(Settings.class);
    LevelManager levelManager = mock(LevelManager.class);
    BossSpawner spawner = new BossSpawner(settings, levelManager);

    Entity healthBarEntity = mock(Entity.class);
    HealthbarComponent hbComp = mock(HealthbarComponent.class);
    when(healthBarEntity.getComponent(HealthbarComponent.class)).thenReturn(hbComp);

    try (MockedStatic<FXGL> fxglMock = mockStatic(FXGL.class);
        MockedStatic<FXGLForKtKt> spawnMock = mockStatic(FXGLForKtKt.class)) {

      fxglMock.when(FXGL::getAppCenter).thenReturn(new Point2D(400, 300));
      fxglMock.when(FXGL::getAppWidth).thenReturn(800);

      Entity redEntity = mock(Entity.class);
      RedDinoComponent redComp = mock(RedDinoComponent.class);
      when(redEntity.getComponent(RedDinoComponent.class)).thenReturn(redComp);

      spawnMock
          .when(() -> FXGLForKtKt.spawn(eq("redDino"), anyDouble(), anyDouble()))
          .thenReturn(redEntity);
      spawnMock
          .when(() -> FXGLForKtKt.spawn(eq("healthBar"), anyDouble(), anyDouble()))
          .thenReturn(healthBarEntity);

      spawner.spawnNewBoss("red");

      spawner.updateHealthBar();

      verify(hbComp).updateBar();
    }
  }

  @Test
  public void removeBossEntities_shouldRemoveHealthbarAndSpawnedBoss() {
    // Arrange
    Settings settings = mock(Settings.class);
    LevelManager levelManager = mock(LevelManager.class);
    BossSpawner spawner = new BossSpawner(settings, levelManager);

    Entity redEntity = mock(Entity.class);
    Entity healthBarEntity = mock(Entity.class);

    RedDinoComponent redComp = mock(RedDinoComponent.class);
    HealthbarComponent hbComp = mock(HealthbarComponent.class);

    when(redEntity.getComponent(RedDinoComponent.class)).thenReturn(redComp);
    when(healthBarEntity.getComponent(HealthbarComponent.class)).thenReturn(hbComp);

    try (MockedStatic<FXGL> fxglMock = mockStatic(FXGL.class);
        MockedStatic<FXGLForKtKt> spawnMock = mockStatic(FXGLForKtKt.class)) {

      fxglMock.when(FXGL::getAppCenter).thenReturn(new Point2D(400, 300));
      fxglMock.when(FXGL::getAppWidth).thenReturn(800);

      spawnMock
          .when(() -> FXGLForKtKt.spawn(eq("redDino"), anyDouble(), anyDouble()))
          .thenReturn(redEntity);
      spawnMock
          .when(() -> FXGLForKtKt.spawn(eq("healthBar"), anyDouble(), anyDouble()))
          .thenReturn(healthBarEntity);

      spawner.spawnNewBoss("red");

      spawner.removeBossEntities();

      verify(healthBarEntity).removeFromWorld();
      verify(redEntity).removeFromWorld();
    }
  }
}
