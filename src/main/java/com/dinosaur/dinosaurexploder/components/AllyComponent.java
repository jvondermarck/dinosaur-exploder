/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.components;

import com.almasb.fxgl.entity.component.Component;
import com.dinosaur.dinosaurexploder.constants.Direction;

/** Summary : This handles the behaviour of ALLYs in the game, and extends the Component class */
public class AllyComponent extends Component {
  private static final double ALLY_SPEED = 100.0;
  private double verticalSpeed = ALLY_SPEED;
  private double horizontalSpeed = 0;

  public void updateDirection(Direction direction) {
    switch (direction) {
      case DOWN -> {
        verticalSpeed = -ALLY_SPEED;
        horizontalSpeed = 0;
      }
      case LEFT -> {
        verticalSpeed = 0;
        horizontalSpeed = ALLY_SPEED;
      }
      case RIGHT -> {
        verticalSpeed = 0;
        horizontalSpeed = -ALLY_SPEED;
      }
      default -> {
        verticalSpeed = ALLY_SPEED;
        horizontalSpeed = 0;
      }
    }
  }

  /**
   * Summary : This method runs for every frame like a continues flow, and move the ALLY towards
   * direction
   */
  @Override
  public void onUpdate(double tpf) {
    // Move ALLY
    entity.translateX(horizontalSpeed * tpf);
    entity.translateY(verticalSpeed * tpf);
  }

  public void spawnAlly() {
    System.out.println("blob");
  }
}
