/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.utils;

import com.almasb.fxgl.dsl.FXGL;
import java.util.function.DoubleSupplier;
import javafx.util.Duration;

public class FXGLGameTimer implements GameTimer {
  private static final double NOT_PAUSED = -1.0;

  // Every reading comes from the same clock. FXGL's game clock stops while the pause menu is open,
  // so mixing it with a wall clock (System.nanoTime) for the paused intervals removed the menu time
  // twice when the menu was opened during a pause() (e.g. the level-up message), see issue #526.
  private final DoubleSupplier nowSeconds;

  private double capturedAtSeconds;
  private double pausedAtSeconds = NOT_PAUSED;
  private double totalPausedSeconds = 0.0;

  public FXGLGameTimer() {
    this(() -> FXGL.getGameTimer().getNow());
  }

  /** Package-private so tests can drive the timer with a simulated game clock. */
  FXGLGameTimer(DoubleSupplier nowSeconds) {
    this.nowSeconds = nowSeconds;
    this.capturedAtSeconds = nowSeconds.getAsDouble();
  }

  @Override
  public void capture() {
    capturedAtSeconds = nowSeconds.getAsDouble();
    pausedAtSeconds = NOT_PAUSED;
    totalPausedSeconds = 0.0;
  }

  @Override
  public boolean isElapsed(Duration duration) {
    double now = nowSeconds.getAsDouble();
    double activeSeconds = now - capturedAtSeconds - pausedSeconds(now);
    return activeSeconds >= duration.toSeconds();
  }

  @Override
  public void pause() {
    if (pausedAtSeconds == NOT_PAUSED) {
      pausedAtSeconds = nowSeconds.getAsDouble();
    }
  }

  @Override
  public void resume() {
    if (pausedAtSeconds != NOT_PAUSED) {
      totalPausedSeconds += nowSeconds.getAsDouble() - pausedAtSeconds;
      pausedAtSeconds = NOT_PAUSED;
    }
  }

  /** Total paused time, including the pause currently in progress (if any). */
  private double pausedSeconds(double now) {
    if (pausedAtSeconds == NOT_PAUSED) {
      return totalPausedSeconds;
    }
    return totalPausedSeconds + (now - pausedAtSeconds);
  }
}
