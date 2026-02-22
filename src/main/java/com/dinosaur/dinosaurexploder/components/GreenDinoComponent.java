/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.components;

import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import com.dinosaur.dinosaurexploder.constants.Direction;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.interfaces.Dinosaur;
import com.dinosaur.dinosaurexploder.utils.AudioManager;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import javafx.geometry.Point2D;
import javafx.util.Duration;

/**
 * Summary : This class extends Component and Implements the Dinosaur Classes and Handles the
 * Shooting and Updating the Dino
 */
public class GreenDinoComponent extends Component implements Dinosaur {
  private double verticalSpeed = 1.5;
  private double horizontalSpeed = 0;
  private final LocalTimer timer = FXGL.newLocalTimer();
  private boolean isPaused = false;
  private int lives = 1;
  private Direction direction = Direction.UP;

  public int getLives() {
    return lives;
  }

  public void setPaused(boolean paused) {
    isPaused = paused;
  }

  public void updateDirection(Direction direction) {
    this.direction = direction;

    // Get the current enemy speed from the level manager
    LevelManager levelManager = FXGL.geto("levelManager");
    switch (direction) {
      case DOWN -> {
        verticalSpeed = -levelManager.getEnemySpeed();
        horizontalSpeed = 0;
        entity.setRotation(180);
      }
      case LEFT -> {
        verticalSpeed = 0;
        horizontalSpeed = levelManager.getEnemySpeed();
        entity.setRotation(270);
      }
      case RIGHT -> {
        verticalSpeed = 0;
        horizontalSpeed = -levelManager.getEnemySpeed();
        entity.setRotation(90);
      }
      default -> {
        verticalSpeed = levelManager.getEnemySpeed();
        horizontalSpeed = 0;
        entity.setRotation(0);
      }
    }
  }

  public Direction getDirection() {
    return direction;
  }

  @Override
  public void onAdded() {
    updateDirection(direction);
  }

  /**
   * Summary : This method runs for every frame like a continues flow , without any stop until we
   * put stop to it. Parameters : double ptf
   */
  @Override
  public void onUpdate(double ptf) {
    if (isPaused) return;

    entity.translateX(horizontalSpeed);
    entity.translateY(verticalSpeed);

    // The dinosaur shoots every 2 seconds
    if (timer.elapsed(Duration.seconds(1.5)) && entity.getPosition().getY() > 0) {
      shoot();
      timer.capture();
    }
  }

  /** Summary : This handles with the shooting of the dinosaur and spawning of the new bullet */
  @Override
  public void shoot() {

    AudioManager.getInstance().playSound(GameConstants.SHOOT_SOUND);

    Point2D center = entity.getCenter();
    Vec2 direction = Vec2.fromAngle(entity.getRotation() + 90);
    spawn(
        "basicEnemyProjectile",
        new SpawnData(center.getX(), center.getY()).put("direction", direction.toPoint2D()));
  }

  public void damage(int damage) {
    lives -= damage;
  }
}
