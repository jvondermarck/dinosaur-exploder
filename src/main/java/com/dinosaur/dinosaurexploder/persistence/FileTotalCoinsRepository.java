/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.persistence;

import com.dinosaur.dinosaurexploder.model.GameState;
import com.dinosaur.dinosaurexploder.model.TotalCoins;

/**
 * Real, file-backed implementation used in production. The coins live in the one human-readable
 * save file {@code savegame.json} (#543); saves are a read-modify-write of that file so the high
 * scores in it are kept.
 */
public class FileTotalCoinsRepository implements TotalCoinsRepository {

  private final FileGameStateRepository gameState;

  public FileTotalCoinsRepository() {
    this(new FileGameStateRepository());
  }

  public FileTotalCoinsRepository(FileGameStateRepository gameState) {
    this.gameState = gameState;
  }

  @Override
  public TotalCoins load() {
    return gameState.load().toTotalCoins();
  }

  @Override
  public void save(TotalCoins totalCoins) {
    GameState state = gameState.load();
    state.setTotalCoins(totalCoins.getTotal());
    gameState.save(state);
  }
}
