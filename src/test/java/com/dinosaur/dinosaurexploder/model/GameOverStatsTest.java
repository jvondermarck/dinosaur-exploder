/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests the formatting of elapsed time in GameOverStats.
 */
class GameOverStatsTest {

  /**
   * Verifies that 125 elapsed seconds are formatted as two minutes and five seconds.
   */
  @Test
  @DisplayName("125 seconds elapsed should be formatted as 2m 05s")
  void shouldFormatElapsedTimeWithMinutesAndSeconds() {
    GameOverStats stats = new GameOverStats(120, 300, 7, 125);

    assertEquals("2m 05s", stats.formatElapsedTime());
  }

  /**
   * Verifies that single-digit seconds are padded with a leading zero.
   */
  @Test
  void shouldPadSingleDigitSeconds() {
    GameOverStats stats = new GameOverStats(50, 70, 2, 9);

    assertEquals("0m 09s", stats.formatElapsedTime());
  }


  /**
   * Verifies that zero elapsed seconds are formatted with zero minutes and padded seconds.
   */
  @Test
  @DisplayName("Zero seconds elapsed should be formatted as 0m 00s")
  void shouldHandleZeroElapsedTime() {
    GameOverStats stats = new GameOverStats(0, 0, 1, 0);

    assertEquals("0m 00s", stats.formatElapsedTime());
  }

  /**
   * Verifies that one hour of elapsed time is displayed as 60 minutes.
   */
  @Test
  @DisplayName("3600 seconds elapsed should be formatted in minutes as 60m 00s")
  void shouldFormatOneHourElapsedInMinutes() {
    GameOverStats stats = new GameOverStats(70, 70, 5, 3600);

    assertEquals("60m 00s", stats.formatElapsedTime());
  }

  /**
   * Verifies that 59 elapsed seconds remain below the one-minute boundary.
   */
  @Test
  @DisplayName("59 seconds elapsed should be formatted as 0m 59s")
  void shouldHandleSecondsBelowOneMinute() {
    GameOverStats stats = new GameOverStats(70, 70, 5, 59);

    assertEquals("0m 59s", stats.formatElapsedTime());
  }

  /**
   * Verifies that exactly 60 elapsed seconds are formatted as one minute and zero seconds.
   */
  @Test
  @DisplayName("60 seconds elapsed should be formatted as 1m 00s")
  void shouldFormatSixtySecondsAsOneMinute() {
    GameOverStats stats = new GameOverStats(70, 70, 5, 60);

    assertEquals("1m 00s", stats.formatElapsedTime());
  }
}
