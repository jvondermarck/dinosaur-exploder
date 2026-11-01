/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.persistence;

import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.model.HighScore;
import java.io.*;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Real, file-backed implementation used in production. */
public class FileHighScoreRepository implements HighScoreRepository {
  private static final Logger LOGGER = Logger.getLogger(FileHighScoreRepository.class.getName());
  private final Path file;

  public FileHighScoreRepository() {
    this(Path.of(GameConstants.HIGH_SCORE_FILE));
  }

  // Public: lets tests keep the save file in a temporary folder instead of the working directory.
  public FileHighScoreRepository(Path file) {
    this.file = file;
  }

  @Override
  public HighScore load() {
    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file.toFile()))) {
      return (HighScore) in.readObject();
    } catch (IOException | ClassNotFoundException e) {
      return new HighScore();
    }
  }

  @Override
  public void save(HighScore highScore) {
    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
      out.writeObject(highScore);
    } catch (IOException e) {
      LOGGER.log(Level.INFO, "Error saving high score: {0}", e.getMessage());
    }
  }
}
