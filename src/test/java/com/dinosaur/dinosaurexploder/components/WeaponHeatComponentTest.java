/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Tests the heat indicator's width and colour at heat thresholds and endpoints. */
class WeaponHeatComponentTest {

  private PlayerComponent player;
  private Rectangle fill;
  private WeaponHeatComponent component;

  @BeforeEach
  void setUp() {
    player = mock(PlayerComponent.class);
    fill = new Rectangle();
    component = new WeaponHeatComponent(fill, player);
  }

  /** Verifies that the indicator is yellow and has width 50 at the 50% heat threshold. */
  @Test
  @DisplayName("50% heat should set the indicator to yellow with width 50")
  void shouldSetHalfHeatAsYellowWithHalfWidth() {
    when(player.getWeaponHeatPercentage()).thenReturn(50.0);

    component.onUpdate(0.0);

    assertEquals(50.0, fill.getWidth(), 0.001);
    assertEquals(Color.YELLOW, fill.getFill());
  }

  /**
   * Verifies that the indicator is lime green and has width 49.9 at 49.9% heat just below the 50%
   * threshold.
   */
  @Test
  @DisplayName("49.9% heat should set the indicator to lime green with width 49.9")
  void shouldSetJustBelowHalfHeatAsLimeGreenWithJustBelowHalfWidth() {
    when(player.getWeaponHeatPercentage()).thenReturn(49.9);

    component.onUpdate(0.0);

    assertEquals(49.9, fill.getWidth(), 0.001);
    assertEquals(Color.LIMEGREEN, fill.getFill());
  }

  /** Verifies that the indicator is lime green and has zero width when heat is zero. */
  @Test
  @DisplayName("0% heat should set the indicator to lime green with width 0")
  void shouldSetZeroHeatAsLimeGreenWithZeroWidth() {
    when(player.getWeaponHeatPercentage()).thenReturn(0.0);

    component.onUpdate(0.0);

    assertEquals(0.0, fill.getWidth(), 0.001);
    assertEquals(Color.LIMEGREEN, fill.getFill());
  }

  /** Verifies that the indicator is orange and has width 75 at the 75% heat threshold. */
  @Test
  @DisplayName("75% heat should set the indicator to orange with width 75")
  void shouldSet75PercentHeatAsOrangeWith75Width() {
    when(player.getWeaponHeatPercentage()).thenReturn(75.0);

    component.onUpdate(0.0);

    assertEquals(75.0, fill.getWidth(), 0.001);
    assertEquals(Color.ORANGE, fill.getFill());
  }

  /**
   * Verifies that the indicator is yellow and has width 74.9 at the 74.9% heat just below the 75%
   * heat threshold.
   */
  @Test
  @DisplayName("74.9% heat should set the indicator to yellow with width 74.9")
  void shouldSetJustBelow75PercentHeatAsYellowWithJustBelow75Width() {
    when(player.getWeaponHeatPercentage()).thenReturn(74.9);

    component.onUpdate(0.0);

    assertEquals(74.9, fill.getWidth(), 0.001);
    assertEquals(Color.YELLOW, fill.getFill());
  }

  /** Verifies that the indicator is red and has width 90 at the 90% heat threshold. */
  @Test
  @DisplayName("90% heat should set the indicator to red with width 90")
  void shouldSet90PercentHeatAsRedWith90Width() {
    when(player.getWeaponHeatPercentage()).thenReturn(90.0);

    component.onUpdate(0.0);

    assertEquals(90.0, fill.getWidth(), 0.001);
    assertEquals(Color.RED, fill.getFill());
  }

  /**
   * Verifies that the indicator is orange and has width 89.9 at heat 89.9 just below the 90% heat
   * threshold.
   */
  @Test
  @DisplayName("89.9% heat should set the indicator to orange with width 89.9")
  void shouldSetJustBelow90PercentHeatAsOrangeWithJustBelow90Width() {
    when(player.getWeaponHeatPercentage()).thenReturn(89.9);

    component.onUpdate(0.0);

    assertEquals(89.9, fill.getWidth(), 0.001);
    assertEquals(Color.ORANGE, fill.getFill());
  }

  /** Verifies that the indicator is red and has width 100 at 100% heat. */
  @Test
  @DisplayName("100% heat should set the indicator to red with width 100")
  void shouldSet100PercentHeatAsRedWith100Width() {
    when(player.getWeaponHeatPercentage()).thenReturn(100.0);

    component.onUpdate(0.0);

    assertEquals(100.0, fill.getWidth(), 0.001);
    assertEquals(Color.RED, fill.getFill());
  }
}
