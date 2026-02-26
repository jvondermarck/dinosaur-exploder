/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.components;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.dinosaur.dinosaurexploder.constants.Direction;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.interfaces.Dinosaur;
import com.dinosaur.dinosaurexploder.utils.AudioManager;
import com.dinosaur.dinosaurexploder.utils.GameTimer;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import com.dinosaur.dinosaurexploder.view.DinosaurGUI;
import javafx.geometry.Point2D;
import javafx.util.Duration;

/**
 * Summary : This class extends Component and Implements the Dinosaur Classes and Handles the
 * Shooting and Updating the Dino
 */
public class RedDinoComponent extends Component implements Dinosaur {
  private double movementSpeed = 1.5;
  private double verticalSpeed = 0;
  private double horizontalSpeed = movementSpeed;
  private int lives = 10;
  private Direction direction = Direction.UP;
  private final GameTimer gameTimer;
  private HealthbarComponent healthBar;

  public RedDinoComponent(GameTimer gameTimer) {
    this.gameTimer = gameTimer;
  }

  private boolean isPaused = false;

  boolean firstTime = true;
  private LevelManager levelManager;

  public void setLevelManager(LevelManager levelManager) {
    this.levelManager = levelManager;
  }

  public int getLives() {
    return lives;
  }

  public void setLives(int lives) {
    this.lives = lives;
  }

  public void setPaused(boolean paused) {
    isPaused = paused;
  }

  public void setHealthBar(HealthbarComponent healthBar) {
    this.healthBar = healthBar;
  }

  public HealthbarComponent getHealthBar() {
    return healthBar;
  }

  public void updateDirection(Direction direction) {
    this.direction = direction;

    switch (direction) {
      case DOWN -> {
        verticalSpeed = 0;
        horizontalSpeed = -movementSpeed;
        entity.setRotation(180);
      }
      case LEFT -> {
        verticalSpeed = -movementSpeed;
        horizontalSpeed = 0;
        entity.setRotation(270);
      }
      case RIGHT -> {
        verticalSpeed = movementSpeed;
        horizontalSpeed = 0;
        entity.setRotation(90);
      }
      default -> {
        verticalSpeed = 0;
        horizontalSpeed = movementSpeed;
        entity.setRotation(0);
      }
    }
  }

  public Direction getDirection() {
    return direction;
  }

  @Override
  public void onAdded() {
    // Get the current enemy speed from the level manager
    // levelManager = FXGL.geto("levelManager");
    firstTime = true;
  }

  /**
   * Summary : This method runs for every frame like a continues flow , without any stop until we
   * put stop to it. Parameters : double ptf
   */
  @Override
  public void onUpdate(double ptf) {
    if (isPaused) return;

    if (firstTime) {
      System.out.println("level: " + levelManager.getCurrentLevel());
      movementSpeed = levelManager.getEnemySpeed();
      updateDirection(direction);
      lives = levelManager.getCurrentLevel() * 2;
      firstTime = false;
    }

    // the Dino turns around just before he hits a site of the screen
    if (direction == Direction.LEFT || direction == Direction.RIGHT) {
      if (entity.getY() < 0 || entity.getY() > DinosaurGUI.HEIGHT - entity.getWidth() - 40) {
        verticalSpeed *= -1;
      }
    } else {
      if (entity.getX() < 0 || entity.getX() > DinosaurGUI.WIDTH - entity.getWidth() - 40) {
        horizontalSpeed *= -1;
      }
    }

    entity.translateX(horizontalSpeed);
    entity.translateY(verticalSpeed);

    // The dinosaur shoots depending on the absolute value of its own speed on the current Level

    if (gameTimer.isElapsed(Duration.seconds(levelManager.getEnemySpawnRate() * 1.3))) {
      shoot();
      gameTimer.capture();
    }
  }

  /** Summary : This handles with the shooting of the dinosaur and spawning of the new bullet */
  @Override
  public void shoot() {

    AudioManager.getInstance().playSound(GameConstants.SHOOT_SOUND);

    Point2D center = entity.getCenter();
    Vec2 angle = Vec2.fromAngle(entity.getRotation() + 90 + random(-45, 45));
    spawn(
        "basicEnemyProjectile",
        new SpawnData(center.getX() + 50 + 3, center.getY()).put("direction", angle.toPoint2D()));
  }

  /**
   * Summary : This method damages the red dino
   *
   * @param damage
   */
  public void damage(int damage) {
    lives -= damage;
  }
}
