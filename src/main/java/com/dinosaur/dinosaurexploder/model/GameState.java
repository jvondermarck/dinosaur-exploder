/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.model;

import com.dinosaur.dinosaurexploder.constants.GameMode;
import java.util.HashMap;
import java.util.Map;

/**
 * The debug-editable game state — high scores per game mode and the total coins — as it is stored
 * in the one human-readable save file {@code savegame.json} (#543). Flat fields, so Jackson reads
 * and writes the file without any annotations and a person can edit it by hand.
 */
public class GameState {

  private Map<String, Integer> highScores = new HashMap<>();
  private int totalCoins;

  public GameState() {
    for (GameMode mode : GameMode.values()) {
      highScores.put(mode.name(), 0);
    }
  }

  public GameState(HighScore highScore, TotalCoins coins) {
    this.highScores = highScore.getAllScores();
    this.totalCoins = coins.getTotal();
  }

  public Map<String, Integer> getHighScores() {
    return highScores;
  }

  public void setHighScores(Map<String, Integer> highScores) {
    this.highScores = highScores == null ? new HashMap<>() : new HashMap<>(highScores);
  }

  public int getTotalCoins() {
    return totalCoins;
  }

  public void setTotalCoins(int totalCoins) {
    this.totalCoins = totalCoins;
  }

  public HighScore toHighScore() {
    return new HighScore(highScores);
  }

  public TotalCoins toTotalCoins() {
    return new TotalCoins(totalCoins);
  }
}
