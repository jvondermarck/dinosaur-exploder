/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.persistence;

import com.dinosaur.dinosaurexploder.model.HighScore;

/** Abstracts how a {@link HighScore} is loaded from and persisted to storage. */
public interface HighScoreRepository {
  HighScore load();

  void save(HighScore highScore);
}
