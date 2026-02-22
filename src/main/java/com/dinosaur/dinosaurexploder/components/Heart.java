/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.components;

import com.almasb.fxgl.entity.component.Component;
import com.dinosaur.dinosaurexploder.constants.Direction;

/**
 * Summary : This handles the behaviour of dropped hearts in the game, and extends the Component
 * class
 */
public class Heart extends Component {
  private static final double HEART_SPEED = 100.0; // Match with other game speeds if needed
  private double verticalSpeed = HEART_SPEED;
  private double horizontalSpeed = 0;

  public void updateDirection(Direction direction) {
    switch (direction) {
      case DOWN -> {
        verticalSpeed = -HEART_SPEED;
        horizontalSpeed = 0;
      }
      case LEFT -> {
        verticalSpeed = 0;
        horizontalSpeed = HEART_SPEED;
      }
      case RIGHT -> {
        verticalSpeed = 0;
        horizontalSpeed = -HEART_SPEED;
      }
      default -> {
        verticalSpeed = HEART_SPEED;
        horizontalSpeed = 0;
      }
    }
  }

  /**
   * Summary : This method runs for every frame like a continues flow, and move the heart downward
   */
  @Override
  public void onUpdate(double tpf) {
    entity.translateX(horizontalSpeed * tpf);
    entity.translateY(verticalSpeed * tpf);
  }
}
