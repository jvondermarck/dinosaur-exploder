/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.persistence;

import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.model.HighScore;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Real, file-backed implementation used in production. */
public class FileHighScoreRepository implements HighScoreRepository {
  private static final Logger LOGGER = Logger.getLogger(FileHighScoreRepository.class.getName());

  @Override
  public HighScore load() {
    try (ObjectInputStream in =
        new ObjectInputStream(new FileInputStream(GameConstants.HIGH_SCORE_FILE))) {
      return (HighScore) in.readObject();
    } catch (IOException | ClassNotFoundException e) {
      return new HighScore();
    }
  }

  @Override
  public void save(HighScore highScore) {
    try (ObjectOutputStream out =
        new ObjectOutputStream(new FileOutputStream(GameConstants.HIGH_SCORE_FILE))) {
      out.writeObject(highScore);
    } catch (IOException e) {
      LOGGER.log(Level.INFO, "Error saving high score: {0}", e.getMessage());
    }
  }
}
