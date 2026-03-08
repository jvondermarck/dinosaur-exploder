/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.components;

import com.almasb.fxgl.entity.component.Component;
import com.dinosaur.dinosaurexploder.constants.Direction;
import com.dinosaur.dinosaurexploder.interfaces.Coin;

/** Summary : This handles the behaviour of coins in the game, and extends the Component class */
public class CoinComponent extends Component implements Coin {
  private static final double COIN_SPEED = 100.0;
  private double verticalSpeed = COIN_SPEED;
  private double horizontalSpeed = 0;

  public void updateDirection(Direction direction) {
    switch (direction) {
      case DOWN -> {
        verticalSpeed = -COIN_SPEED;
        horizontalSpeed = 0;
      }
      case LEFT -> {
        verticalSpeed = 0;
        horizontalSpeed = COIN_SPEED;
      }
      case RIGHT -> {
        verticalSpeed = 0;
        horizontalSpeed = -COIN_SPEED;
      }
      default -> {
        verticalSpeed = COIN_SPEED;
        horizontalSpeed = 0;
      }
    }
  }

  /**
   * Summary : This method runs for every frame like a continues flow, and move the coin towards
   * direction
   */
  @Override
  public void onUpdate(double tpf) {
    // Move coin
    entity.translateX(horizontalSpeed * tpf);
    entity.translateY(verticalSpeed * tpf);
  }
}
