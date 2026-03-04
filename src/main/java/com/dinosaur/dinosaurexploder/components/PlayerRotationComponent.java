/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.components;

import static com.almasb.fxgl.dsl.FXGL.getInput;

import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

public class PlayerRotationComponent extends Component {

  @Override
  public void onUpdate(double tpf) {
    // Get mouse position in the game world
    Point2D mousePoint = getInput().getMousePositionWorld();
    Point2D playerCenterPoint = entity.getCenter();

    double angle = calculateAngle(playerCenterPoint, mousePoint);

    // Convert and set rotation (+90 to align with the up facing sprites)
    entity.setRotation(angle + 90);
    PlayerComponent player = entity.getComponent(PlayerComponent.class);
    System.out.println(player.getAlly());
  }

  public static double calculateAngle(Point2D from, Point2D to) {
    double dx = to.getX() - from.getX();
    double dy = to.getY() - from.getY();
    double angle = Math.atan2(dy, dx);

    return Math.toDegrees(angle);
  }
}
