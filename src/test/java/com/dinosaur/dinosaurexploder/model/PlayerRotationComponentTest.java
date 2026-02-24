/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.dinosaur.dinosaurexploder.components.PlayerRotationComponent;

import javafx.geometry.Point2D;

class PlayerRotationComponentTest {
  @Test
  void shouldRotateUp() {
    Point2D player = new Point2D(0, 0);
    Point2D mouse = new Point2D(0, -10);

    double rotation = PlayerRotationComponent.calculateAngle(player, mouse);

    assertEquals(-90, rotation, 0.0001);
  }

  @Test
  void shouldRotateRight() {
    Point2D player = new Point2D(0, 0);
    Point2D mouse = new Point2D(10, 0);

    double rotation = PlayerRotationComponent.calculateAngle(player, mouse);

    assertEquals(0, rotation, 0.0001);
  }

  @Test
  void shouldRotateDown() {
    Point2D player = new Point2D(0, 0);
    Point2D mouse = new Point2D(0, 10);

    double rotation = PlayerRotationComponent.calculateAngle(player, mouse);

    assertEquals(90, rotation, 0.0001);
  }

  @Test
  void shouldRotateLeft() {
    Point2D player = new Point2D(0, 0);
    Point2D mouse = new Point2D(-10, 0);

    double rotation = PlayerRotationComponent.calculateAngle(player, mouse);

    assertEquals(180, rotation, 0.0001);
  }
}
