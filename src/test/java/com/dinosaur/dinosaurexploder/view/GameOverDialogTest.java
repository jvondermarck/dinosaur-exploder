/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import com.dinosaur.dinosaurexploder.utils.MockGameTimer;
import javafx.util.Duration;
import org.junit.jupiter.api.Test;

/**
 * Regression tests for issue #488 (active gameplay time): survived-seconds must be computed via
 * the injected GameTimer seam, not a direct wall-clock read, and never depend on real sleeps.
 */
class GameOverDialogTest {

  @Test
  void survivedSeconds_reflectsInjectedTimerElapsedTime() {
    MockGameTimer timer = new MockGameTimer();
    timer.capture();
    GameOverDialog dialog =
        new GameOverDialog(LanguageManager.getInstance(), new LevelManager(), timer);

    timer.advance(Duration.seconds(15));

    assertEquals(15, dialog.calculateSurvivedSeconds());
  }

  @Test
  void survivedSeconds_truncatesPartialSecond() {
    MockGameTimer timer = new MockGameTimer();
    timer.capture();
    GameOverDialog dialog =
        new GameOverDialog(LanguageManager.getInstance(), new LevelManager(), timer);

    timer.advance(Duration.seconds(4.9));

    assertEquals(4, dialog.calculateSurvivedSeconds());
  }

  @Test
  void survivedSeconds_isZero_beforeAnyTimeAdvances() {
    MockGameTimer timer = new MockGameTimer();
    timer.capture();
    GameOverDialog dialog =
        new GameOverDialog(LanguageManager.getInstance(), new LevelManager(), timer);

    assertEquals(0, dialog.calculateSurvivedSeconds());
  }
}
