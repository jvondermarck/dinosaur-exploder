/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.utils;

import javafx.util.Duration;

public class MockGameTimer implements GameTimer {
  private Duration simulatedTime = Duration.ZERO;
  private Duration capturedAt = Duration.ZERO;
  private Duration pausedAt = null;
  private Duration totalPaused = Duration.ZERO;

  public void advance(Duration duration) {
    simulatedTime = simulatedTime.add(duration);
  }

  @Override
  public void capture() {
    capturedAt = simulatedTime;
    pausedAt = null;
    totalPaused = Duration.ZERO;
  }

  @Override
  public boolean isElapsed(Duration duration) {
    return simulatedTime
        .subtract(capturedAt)
        .subtract(pausedDuration())
        .greaterThanOrEqualTo(duration);
  }

  @Override
  public void pause() {
    if (pausedAt == null) {
      pausedAt = simulatedTime;
    }
  }

  @Override
  public void resume() {
    if (pausedAt != null) {
      totalPaused = totalPaused.add(simulatedTime.subtract(pausedAt));
      pausedAt = null;
    }
  }

  /** Total paused time, including the pause currently in progress (if any). */
  private Duration pausedDuration() {
    if (pausedAt == null) {
      return totalPaused;
    }
    return totalPaused.add(simulatedTime.subtract(pausedAt));
  }
}
