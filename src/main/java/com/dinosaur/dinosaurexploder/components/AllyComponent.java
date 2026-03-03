/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.components;

import static com.almasb.fxgl.dsl.FXGL.*;

import com.almasb.fxgl.entity.component.Component;

/** Summary : This handles the behaviour of ALLYs in the game, and extends the Component class */
public class AllyComponent extends Component {

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
    entity.translateX(movementSpeed);
  }
}
