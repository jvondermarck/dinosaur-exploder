/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.components;

import static com.almasb.fxgl.core.math.FXGLMath.random;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.dinosaur.dinosaurexploder.interfaces.Asteroids;
import com.dinosaur.dinosaurexploder.utils.LevelManager;

public class AsteroidsComponent extends Component implements Asteroids {

  double verticalSpeed = 1;
  double horizontalSpeed = 1;
  private boolean isPaused = false;
  int lives;

  public AsteroidsComponent(int lives) {
    this.lives = lives;
  }

  public void setPaused(boolean paused) {
    isPaused = paused;
  }

  @Override
  public void onAdded() {
    // Get the current asteroids vertical and horizontal speed from the level manager
    LevelManager levelManager = FXGL.geto("levelManager");
    verticalSpeed = levelManager.getAsteroidsVerticalSpeed() * random(1, 10);
    horizontalSpeed = levelManager.getAsteroidsHorizontalSpeed() * random(-3, 3);
  }

  public int getLives() {
    return lives;
  }

  public void setLives(int lives) {
    this.lives = lives;
  }

  @Override
  public void damage(int damage) {
    lives -= damage;
  }

  /**
   * Summary : This method runs for every frame like a continues flow , without any stop until we
   * put stop to it. Parameters : double ptf
   */
  @Override
  public void onUpdate(double ptf) {
    if (isPaused) return;

    entity.translateY(verticalSpeed);
    entity.translateX(horizontalSpeed);
  }
}
