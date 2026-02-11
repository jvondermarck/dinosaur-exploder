/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */
package com.dinosaur.dinosaurexploder.components;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/** Summary: This class extends Component and handles filling the level progress bar */
public class LevelProgressBarComponent extends Component {
  private static final double MAX_WIDTH = 99;
  private static final Duration UPDATE_DURATION = Duration.seconds(0.3);
  private static final Duration RESET_DURATION = Duration.seconds(0.5);

  private final Rectangle fill;
  private final LevelManager levelManager;
  private boolean isLocked = false;

  public LevelProgressBarComponent(Rectangle fill, LevelManager levelManager) {
    this.fill = fill;
    this.levelManager = levelManager;
  }

  public void updateProgress() {
    if (isLocked) return;

    float progress = Math.min(levelManager.getLevelProgress(), 1);
    updateWidth(MAX_WIDTH * progress, UPDATE_DURATION);

    if (progress >= 1) isLocked = true;
  }

  public void resetProgress() {
    isLocked = false;

    float progress = Math.min(levelManager.getLevelProgress(), 1);
    updateWidth(MAX_WIDTH * progress, RESET_DURATION);
  }

  private void updateWidth(double targetWidth, Duration duration) {
    FXGL.animationBuilder()
        .duration(duration)
        .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
        .animate(fill.widthProperty())
        .from(fill.getWidth())
        .to(targetWidth)
        .buildAndPlay();
  }
}
