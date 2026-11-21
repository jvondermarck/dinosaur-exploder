/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javafx.util.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests FXGLGameTimer against a simulated FXGL game clock. The game clock stands still while the
 * pause menu is open, which is modelled here by simply not advancing it.
 */
class FXGLGameTimerTest {

  private double gameClockSeconds;
  private FXGLGameTimer timer;

  @BeforeEach
  void setUp() {
    gameClockSeconds = 100.0; // the game clock does not start at zero
    timer = new FXGLGameTimer(() -> gameClockSeconds);
    timer.capture();
  }

  private void advance(double seconds) {
    gameClockSeconds += seconds;
  }

  @Test
  void countsActiveTime() {
    advance(15);

    assertTrue(timer.isElapsed(Duration.seconds(15)));
    assertFalse(timer.isElapsed(Duration.seconds(16)));
  }

  @Test
  void excludesTimeBetweenPauseAndResume() {
    advance(10);
    timer.pause();
    advance(2); // level-up message
    timer.resume();
    advance(5);

    assertTrue(timer.isElapsed(Duration.seconds(15)));
    assertFalse(timer.isElapsed(Duration.seconds(16)));
  }

  @Test
  void excludesPauseStillInProgress() {
    advance(10);
    timer.pause();
    advance(4);

    assertTrue(timer.isElapsed(Duration.seconds(10)));
    assertFalse(timer.isElapsed(Duration.seconds(11)));
  }

  /**
   * Issue #526: opening the pause menu during the level-up message must not subtract the menu time
   * from the time played. The game clock is frozen while the menu is open, so the menu time is
   * already left out and must not be removed a second time.
   */
  @Test
  void pauseMenuDuringLevelUpMessage_doesNotSubtractMenuTime() {
    advance(12); // play until "LEVEL 2"
    timer.pause(); // level-up message starts
    advance(1); // message on screen
    // ESC: pause menu open for a long time, game clock frozen (no advance)
    advance(1); // rest of the message after resuming from the menu
    timer.resume(); // level-up message ends
    advance(8); // play until game over

    assertTrue(timer.isElapsed(Duration.seconds(20)));
    assertFalse(timer.isElapsed(Duration.seconds(21)));
  }

  @Test
  void captureResetsPauses() {
    advance(5);
    timer.pause();
    advance(3);
    timer.capture();
    advance(4);

    assertTrue(timer.isElapsed(Duration.seconds(4)));
    assertFalse(timer.isElapsed(Duration.seconds(5)));
  }

  @Test
  void repeatedPauseAndResumeAreIgnored() {
    advance(5);
    timer.pause();
    advance(1);
    timer.pause(); // already paused: keeps the first pause start
    advance(1);
    timer.resume();
    timer.resume(); // not paused: no effect
    advance(3);

    assertTrue(timer.isElapsed(Duration.seconds(8)));
    assertFalse(timer.isElapsed(Duration.seconds(9)));
  }
}
