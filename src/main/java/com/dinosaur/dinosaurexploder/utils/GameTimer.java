/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.utils;

import javafx.util.Duration;

public interface GameTimer {
  void capture();

  boolean isElapsed(Duration duration);

  /**
   * Stops counting elapsed time until {@link #resume()} is called. Time spent between pause() and
   * resume() is excluded from {@link #isElapsed(Duration)}, so gameplay freezes (level-up
   * transitions, the game-over sequence) do not inflate the measured session duration. Calling
   * pause() while already paused has no effect.
   */
  void pause();

  /** Resumes counting elapsed time. Has no effect when the timer is not paused. */
  void resume();
}
