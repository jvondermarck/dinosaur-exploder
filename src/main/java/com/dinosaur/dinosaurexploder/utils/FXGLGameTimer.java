/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.utils;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.time.LocalTimer;
import javafx.util.Duration;

public class FXGLGameTimer implements GameTimer {
  private static final double NANOS_PER_MILLI = 1_000_000.0;
  private static final long NOT_PAUSED = -1L;

  private final LocalTimer shootTimer = FXGL.newLocalTimer();

  // Paused time is tracked separately rather than by re-capturing the LocalTimer: a re-capture
  // would discard the time already survived. Instead isElapsed() asks the LocalTimer for the
  // requested duration *plus* everything spent paused, which is equivalent to comparing against
  // the active-only elapsed time.
  private long pausedAtNanos = NOT_PAUSED;
  private long totalPausedNanos = 0L;

  @Override
  public void capture() {
    shootTimer.capture();
    pausedAtNanos = NOT_PAUSED;
    totalPausedNanos = 0L;
  }

  @Override
  public boolean isElapsed(Duration duration) {
    return shootTimer.elapsed(duration.add(Duration.millis(pausedNanos() / NANOS_PER_MILLI)));
  }

  @Override
  public void pause() {
    if (pausedAtNanos == NOT_PAUSED) {
      pausedAtNanos = System.nanoTime();
    }
  }

  @Override
  public void resume() {
    if (pausedAtNanos != NOT_PAUSED) {
      totalPausedNanos += System.nanoTime() - pausedAtNanos;
      pausedAtNanos = NOT_PAUSED;
    }
  }

  /** Total paused time, including the pause currently in progress (if any). */
  private long pausedNanos() {
    if (pausedAtNanos == NOT_PAUSED) {
      return totalPausedNanos;
    }
    return totalPausedNanos + (System.nanoTime() - pausedAtNanos);
  }
}
