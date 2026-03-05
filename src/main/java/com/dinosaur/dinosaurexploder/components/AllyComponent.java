/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.components;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameTimer;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import java.util.Objects;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.util.Duration;

/** Summary : This handles the behaviour of ALLYs in the game, and extends the Component class */
public class AllyComponent extends Component {
  private int life = 2;
  private double duration = 30.0; // 30 seconde
  private final Image projectileImage =
      new Image(
          Objects.requireNonNull(
              getClass().getResourceAsStream("/assets/textures/projectiles/projectile1_1.png")));

  public void moveLeft(double movementSpeed) {
    entity.translateX(-movementSpeed);
  }

  public void moveRight(double movementSpeed) {
    entity.translateX(movementSpeed);
  }

  public void moveUp(double movementSpeed) {
    entity.translateY(-movementSpeed);
  }

  public void moveDown(double movementSpeed) {
    entity.translateY(movementSpeed);
  }

  public void shoot() {

    Point2D center = entity.getCenter();
    Vec2 direction = Vec2.fromAngle(entity.getRotation() - 90);
    spawn(
        "allyProjectile",
        new SpawnData(
                center.getX() - (projectileImage.getWidth() / 2) + 3,
                center.getY() - 25) // adjust Accordingly
            .put("direction", direction.toPoint2D()));
  }

  public int getLife() {
    return life;
  }

  public void setLife(int life) {
    this.life = life;
  }

  @Override
  public void onAdded() {
    getGameTimer()
        .runAtInterval(
            () -> {
              duration -= 0.1;
            },
            Duration.seconds(0.1));
  }

  public double getDuration() {
    return duration;
  }
}
