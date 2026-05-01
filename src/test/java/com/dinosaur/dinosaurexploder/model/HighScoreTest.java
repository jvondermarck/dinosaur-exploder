/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.model;

import static org.junit.jupiter.api.Assertions.*;

import com.dinosaur.dinosaurexploder.constants.GameMode;
import org.junit.jupiter.api.Test;

class HighScoreTest {

  @Test
  void newHighScoreStartsAtZero() {
    HighScore hs = new HighScore();
    assertEquals(0, hs.getHigh(GameMode.NORMAL.name()));
    assertEquals(0, hs.getHigh(GameMode.EXPERT.name()));
  }

  @Test
  void setHighUpdatesScore() {
    HighScore hs = new HighScore();
    hs.setHigh(GameMode.NORMAL.name(), 1500);
    assertEquals(1500, hs.getHigh(GameMode.NORMAL.name()));
  }

  @Test
  void getHighReturnsMaxAcrossModes() {
    HighScore hs = new HighScore();
    hs.setHigh(GameMode.NORMAL.name(), 200);
    hs.setHigh(GameMode.EXPERT.name(), 950);
    assertEquals(950, hs.getHigh());
  }

  @Test
  void unknownModeReturnsZero() {
    HighScore hs = new HighScore();
    assertEquals(0, hs.getHigh("not-a-mode"));
  }
}
