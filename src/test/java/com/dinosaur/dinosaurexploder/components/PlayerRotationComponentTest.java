/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.components;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

/** Tests direction angle calculations and the application of player and ally rotations. */
class PlayerRotationComponentTest {

  /** Verifies that a mouse position above the player gives an angle of -90 degrees. */
  @Test
  @DisplayName("Mouse above player should give an angle of -90 degrees")
  void shouldRotateUp() {
    Point2D player = new Point2D(0, 0);
    Point2D mouse = new Point2D(0, -10);

    double rotation = PlayerRotationComponent.calculateAngle(player, mouse);

    assertEquals(-90, rotation, 0.0001);
  }

  /** Verifies that a mouse position right of the player gives an angle of zero degrees. */
  @Test
  @DisplayName("Mouse right of player should give an angle of 0 degrees")
  void shouldRotateRight() {
    Point2D player = new Point2D(0, 0);
    Point2D mouse = new Point2D(10, 0);

    double rotation = PlayerRotationComponent.calculateAngle(player, mouse);

    assertEquals(0, rotation, 0.0001);
  }

  /** Verifies that a mouse position below the player gives an angle of 90 degrees. */
  @Test
  @DisplayName("Mouse below player should give an angle of 90 degrees")
  void shouldRotateDown() {
    Point2D player = new Point2D(0, 0);
    Point2D mouse = new Point2D(0, 10);

    double rotation = PlayerRotationComponent.calculateAngle(player, mouse);

    assertEquals(90, rotation, 0.0001);
  }

  /** Verifies that a mouse position left of the player gives an angle of 180 degrees. */
  @Test
  @DisplayName("Mouse left of player should give an angle of 180 degrees")
  void shouldRotateLeft() {
    Point2D player = new Point2D(0, 0);
    Point2D mouse = new Point2D(-10, 0);

    double rotation = PlayerRotationComponent.calculateAngle(player, mouse);

    assertEquals(180, rotation, 0.0001);
  }

  /** Verifies that a target above a non-origin player position gives -90 degrees. */
  @Test
  @DisplayName("Target above player at (100, 50) should give -90 degrees")
  void shouldCalculateMinus90DegreesForTargetAboveNonOriginPlayer() {
    Point2D player = new Point2D(100, 50);
    Point2D mouse = new Point2D(100, 0);

    assertEquals(-90.0, PlayerRotationComponent.calculateAngle(player, mouse), 0.001);
  }

  /** Verifies that a target right of a non-origin player position gives zero degrees. */
  @Test
  @DisplayName("Target right of player at (100, 50) should give 0 degrees")
  void shouldCalculateZeroDegreesForTargetRightOfNonOriginPlayer() {
    Point2D player = new Point2D(100, 50);
    Point2D mouse = new Point2D(200, 50);

    assertEquals(0.0, PlayerRotationComponent.calculateAngle(player, mouse), 0.001);
  }

  /** Verifies that a target below a non-origin player position gives 90 degrees. */
  @Test
  @DisplayName("Target below player at (100, 50) should give 90 degrees")
  void shouldCalculate90DegreesForTargetBelowNonOriginPlayer() {
    Point2D player = new Point2D(100, 50);
    Point2D mouse = new Point2D(100, 150);

    assertEquals(90.0, PlayerRotationComponent.calculateAngle(player, mouse), 0.001);
  }

  /** Verifies that a target left of a non-origin player position gives 180 degrees. */
  @Test
  @DisplayName("Target left of player at (100, 50) should give 180 degrees")
  void shouldCalculate180DegreesForTargetLeftOfNonOriginPlayer() {
    Point2D player = new Point2D(100, 50);
    Point2D mouse = new Point2D(0, 50);

    assertEquals(180.0, PlayerRotationComponent.calculateAngle(player, mouse), 0.001);
  }

  /** Verifies that an equally rightward and downward target gives 45 degrees. */
  @Test
  @DisplayName("Target at (10, 10) from the origin should give 45 degrees")
  void shouldCalculate45DegreesForDiagonalTargetDownAndRight() {
    Point2D player = new Point2D(0, 0);
    Point2D mouse = new Point2D(10, 10);

    assertEquals(45.0, PlayerRotationComponent.calculateAngle(player, mouse), 0.001);
  }

  /** Verifies that an equally leftward and upward target gives -135 degrees. */
  @Test
  @DisplayName("Target at (-10, -10) from the origin should give -135 degrees")
  void shouldCalculateMinus135DegreesForDiagonalTargetUpAndLeft() {
    Point2D player = new Point2D(0, 0);
    Point2D mouse = new Point2D(-10, -10);

    assertEquals(-135.0, PlayerRotationComponent.calculateAngle(player, mouse), 0.001);
  }

  /** Verifies that identical player and mouse positions give zero degrees. */
  @Test
  @DisplayName("Mouse on the player at the origin should give 0 degrees")
  void shouldCalculateZeroDegreesWhenMouseIsOnPlayer() {
    Point2D player = new Point2D(0, 0);
    Point2D mouse = new Point2D(0, 0);

    assertEquals(0.0, PlayerRotationComponent.calculateAngle(player, mouse), 0.001);
  }

  /** Verifies that a mouse position right of the player produces a 90-degree sprite rotation. */
  @Test
  @DisplayName("Mouse right of player should set player rotation to 90 degrees without an ally")
  void shouldRotatePlayerTowardsMouseWithoutAlly() {
    Input input = mock(Input.class);
    PlayerComponent player = mock(PlayerComponent.class);
    when(player.getAlly()).thenReturn(null);

    Entity playerEntity = new Entity();
    playerEntity.setPosition(100, 50);
    playerEntity.addComponent(player);

    PlayerRotationComponent rotation = new PlayerRotationComponent();
    playerEntity.addComponent(rotation);

    Point2D mousePosition = playerEntity.getCenter().add(100, 0);
    when(input.getMousePositionWorld()).thenReturn(mousePosition);

    try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class)) {
      fxgl.when(FXGL::getInput).thenReturn(input);

      rotation.onUpdate(0.0);

      assertEquals(90.0, playerEntity.getRotation(), 0.001);
    }
  }

  /** Verifies that both the player and its ally receive the adjusted rotation. */
  @Test
  @DisplayName("Mouse below player should set player and ally rotation to 180 degrees")
  void shouldRotatePlayerAndAllyTowardsMouse() {
    Input input = mock(Input.class);
    PlayerComponent player = mock(PlayerComponent.class);
    AllyComponent ally = mock(AllyComponent.class);
    Entity allyEntity = new Entity();
    when(player.getAlly()).thenReturn(ally);
    when(ally.getEntity()).thenReturn(allyEntity);

    Entity playerEntity = new Entity();
    playerEntity.setPosition(100, 50);
    playerEntity.addComponent(player);

    PlayerRotationComponent rotation = new PlayerRotationComponent();
    playerEntity.addComponent(rotation);

    Point2D mousePosition = playerEntity.getCenter().add(0, 100);
    when(input.getMousePositionWorld()).thenReturn(mousePosition);

    try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class)) {
      fxgl.when(FXGL::getInput).thenReturn(input);

      rotation.onUpdate(0.0);

      assertEquals(180.0, playerEntity.getRotation(), 0.001);
      assertEquals(180.0, allyEntity.getRotation(), 0.001);
    }
  }
}
