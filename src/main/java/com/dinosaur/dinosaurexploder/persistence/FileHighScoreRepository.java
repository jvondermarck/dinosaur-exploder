/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.persistence;

import com.dinosaur.dinosaurexploder.model.GameState;
import com.dinosaur.dinosaurexploder.model.HighScore;

/**
 * Real, file-backed implementation used in production. The high scores live in the one
 * human-readable save file {@code savegame.json} (#543); saves are a read-modify-write of that file
 * so the coins in it are kept.
 */
public class FileHighScoreRepository implements HighScoreRepository {

  private final FileGameStateRepository gameState;

  public FileHighScoreRepository() {
    this(new FileGameStateRepository());
  }

  public FileHighScoreRepository(FileGameStateRepository gameState) {
    this.gameState = gameState;
  }

  @Override
  public HighScore load() {
    return gameState.load().toHighScore();
  }

  @Override
  public void save(HighScore highScore) {
    GameState state = gameState.load();
    state.setHighScores(highScore.getAllScores());
    gameState.save(state);
  }
}
