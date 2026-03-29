/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.model;

import com.dinosaur.dinosaurexploder.constants.GameMode;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HighScore implements Serializable {
  private final Map<String, Integer> highScores;

  public HighScore() {
    this.highScores = new HashMap<>();
    this.highScores.put(GameMode.NORMAL.name(), 0);
    this.highScores.put(GameMode.EXPERT.name(), 0);
  }

  public HighScore(Map<String, Integer> scores) {
    this.highScores = new HashMap<>(scores);
    this.highScores.putIfAbsent(GameMode.NORMAL.name(), 0);
    this.highScores.putIfAbsent(GameMode.EXPERT.name(), 0);
  }

  public Integer getHigh(String mode) {
    return highScores.getOrDefault(mode, 0);
  }

  public Integer getHigh() {
    return highScores.values().stream().max(Integer::compareTo).orElse(0);
  }

  public void setHigh(String mode, Integer score) {
    highScores.put(mode, score);
  }

  public Map<String, Integer> getAllScores() {
    return new HashMap<>(highScores);
  }
}
