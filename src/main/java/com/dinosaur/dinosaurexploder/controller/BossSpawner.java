/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.controller;

import static com.almasb.fxgl.dsl.FXGL.getAppCenter;
import static com.almasb.fxgl.dsl.FXGL.getAppHeight;
import static com.almasb.fxgl.dsl.FXGL.getAppWidth;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

import com.almasb.fxgl.entity.Entity;
import com.dinosaur.dinosaurexploder.components.HealthbarComponent;
import com.dinosaur.dinosaurexploder.components.OrangeDinoComponent;
import com.dinosaur.dinosaurexploder.components.RedDinoComponent;
import com.dinosaur.dinosaurexploder.constants.Direction;
import com.dinosaur.dinosaurexploder.model.GameData;
import com.dinosaur.dinosaurexploder.model.Settings;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import java.util.ArrayList;
import java.util.List;

public class BossSpawner {

  private final Settings settings;
  private final LevelManager levelManager;

  private final List<Entity> healthBars = new ArrayList<>();
  private final List<Entity> redDinos = new ArrayList<>();
  private final List<Entity> orangeDinos = new ArrayList<>();

  public BossSpawner(Settings settings, LevelManager levelManager) {
    this.settings = settings;
    this.levelManager = levelManager;
  }

  private void addRedDino(Direction direction) {
    Entity redDino =
        switch (direction) {
          case DOWN -> spawn("redDino", getAppCenter().getX() - 45, getAppHeight() - 110);
          case LEFT -> spawn("redDino", 50, getAppCenter().getY() - 45);
          case RIGHT -> spawn("redDino", getAppWidth() - 110, getAppCenter().getY() - 45);
          default -> spawn("redDino", getAppCenter().getX() - 45, 50);
        };
    redDino.getComponent(RedDinoComponent.class).setLevelManager(levelManager);
    redDino.getComponent(RedDinoComponent.class).updateDirection(direction);
    redDinos.add(redDino);

    Entity healthBar =
        spawn("healthBar", getAppWidth() - (double) 215, 15 + healthBars.size() * 35);
    healthBar
        .getComponent(HealthbarComponent.class)
        .setDinoComponent(redDino.getComponent(RedDinoComponent.class));
    healthBars.add(healthBar);

    redDino
        .getComponent(RedDinoComponent.class)
        .setHealthBar(healthBar.getComponent(HealthbarComponent.class));
  }

  private void addOrangeDino(Direction direction) {
    Entity orangeDino =
        switch (direction) {
          case DOWN -> spawn("orangeDino", getAppCenter().getX() - 45, getAppHeight() - 110);
          case LEFT -> spawn("orangeDino", 50, getAppCenter().getY() - 45);
          case RIGHT -> spawn("orangeDino", getAppWidth() - 110, getAppCenter().getY() - 45);
          default -> spawn("orangeDino", getAppCenter().getX() - 45, 50);
        };

    orangeDino.getComponent(OrangeDinoComponent.class).setLevelManager(levelManager);
    orangeDinos.add(orangeDino);

    Entity healthBar =
        spawn("healthBar", getAppWidth() - (double) 215, 15 + healthBars.size() * 35);
    healthBar
        .getComponent(HealthbarComponent.class)
        .setDinoComponent(orangeDino.getComponent(OrangeDinoComponent.class));
    healthBars.add(healthBar);

    orangeDino
        .getComponent(OrangeDinoComponent.class)
        .setHealthBar(healthBar.getComponent(HealthbarComponent.class));
  }

  public void spawnNewBoss(String name) {
    // direction is up for normal mode, random for expert mode
    Direction direction = Direction.modeDirection(GameData.getSelectedDifficulty());
    if (name.equals("red")) {
      addRedDino(direction);
    } else if (name.equals("orange")) {
      addOrangeDino(direction);
    }
  }

  public void updateHealthBars() {
    for (Entity healthBar : healthBars) {
      healthBar.getComponent(HealthbarComponent.class).updateBar();
    }
  }

  public void removeBossEntities() {
    for (Entity redDino : new ArrayList<>(redDinos)) {
      removeBossEntity(redDino);
    }
    for (Entity orangeDino : new ArrayList<>(orangeDinos)) {
      removeBossEntity(orangeDino);
    }
  }

  public void removeBossEntity(Entity dino) {
    if (dino == null) {
      return;
    }

    HealthbarComponent healthbar = null;

    if (dino.hasComponent(RedDinoComponent.class)) {
      healthbar = dino.getComponent(RedDinoComponent.class).getHealthBar();
      redDinos.remove(dino);
    } else if (dino.hasComponent(OrangeDinoComponent.class)) {
      healthbar = dino.getComponent(OrangeDinoComponent.class).getHealthBar();
      orangeDinos.remove(dino);
    }
    if (healthbar != null) {
      Entity healthbarEntity = healthbar.getEntity();
      healthBars.remove(healthbarEntity);
      healthbarEntity.removeFromWorld();
    }

    dino.removeFromWorld();
  }
}
