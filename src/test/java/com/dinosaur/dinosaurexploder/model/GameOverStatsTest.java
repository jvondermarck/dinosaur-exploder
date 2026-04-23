/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class GameOverStatsTest {

  @Test
  void shouldFormatElapsedTimeWithMinutesAndSeconds() {
    GameOverStats stats = new GameOverStats(120, 300, 7, 125);

    assertEquals("2m 05s", stats.formatElapsedTime());
  }

  @Test
  void shouldPadSingleDigitSeconds() {
    GameOverStats stats = new GameOverStats(50, 70, 2, 9);

    assertEquals("0m 09s", stats.formatElapsedTime());
  }
}
