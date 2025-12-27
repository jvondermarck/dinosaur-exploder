package com.dinosaur.dinosaurexploder.controller;

import static com.almasb.fxgl.dsl.FXGL.getAppCenter;
import static com.almasb.fxgl.dsl.FXGL.getAppWidth;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

import com.almasb.fxgl.entity.Entity;
import com.dinosaur.dinosaurexploder.components.HealthbarComponent;
import com.dinosaur.dinosaurexploder.components.OrangeDinoComponent;
import com.dinosaur.dinosaurexploder.components.RedDinoComponent;
import com.dinosaur.dinosaurexploder.model.Settings;
import com.dinosaur.dinosaurexploder.utils.LevelManager;

public class BossSpawner {

  private final Settings settings;
  private final LevelManager levelManager;
  private Entity healthBar;
  private Entity redDino;
  private Entity orangeDino;

  public BossSpawner(Settings settings, LevelManager levelManager) {
    this.settings = settings;
    this.levelManager = levelManager;
  }

  public void spawnNewBoss(String name) {
    if (name.equals("red")) {
      redDino = spawn("redDino", getAppCenter().getX() - 45, 50);

      redDino.getComponent(RedDinoComponent.class).setLevelManager(levelManager);

      healthBar = spawn("healthBar", getAppWidth() - (double) 215, 15);
      healthBar
          .getComponent(HealthbarComponent.class)
          .setDinoComponent(redDino.getComponent(RedDinoComponent.class));
    } else if (name.equals("orange")) {
      orangeDino = spawn("orangeDino", getAppCenter().getX() - 45, 50);

      orangeDino.getComponent(OrangeDinoComponent.class).setLevelManager(levelManager);

      healthBar = spawn("healthBar", getAppWidth() - (double)215, 15);
      healthBar
          .getComponent(HealthbarComponent.class)
          .setDinoComponent(orangeDino.getComponent(OrangeDinoComponent.class));
    }
  }

  public void updateHealthBar() {
    healthBar.getComponent(HealthbarComponent.class).updateBar();
  }

  public void removeBossEntities() {
    healthBar.removeFromWorld();
    if (redDino != null) {
      redDino.removeFromWorld();
    }
    if (orangeDino != null) {
      orangeDino.removeFromWorld();
    }
  }
}
