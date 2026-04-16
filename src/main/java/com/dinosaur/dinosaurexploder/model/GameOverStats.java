/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.model;

public record GameOverStats(int finalScore, int highScore, int levelReached, long survivedSeconds) {

  public String formatElapsedTime() {
    long minutes = survivedSeconds / 60;
    long seconds = survivedSeconds % 60;
    return String.format("%dm %02ds", minutes, seconds);
  }
}
