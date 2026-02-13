/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.utils;

import javafx.util.Duration;

public class MockGameTimer implements GameTimer {
  private Duration simulatedTime = Duration.ZERO;
  private Duration capturedAt = Duration.ZERO;

  public void advance(Duration duration) {
    simulatedTime = simulatedTime.add(duration);
  }

  @Override
  public void capture() {
    capturedAt = simulatedTime;
  }

  @Override
  public boolean isElapsed(Duration duration) {
    return simulatedTime.subtract(capturedAt).greaterThanOrEqualTo(duration);
  }
}
