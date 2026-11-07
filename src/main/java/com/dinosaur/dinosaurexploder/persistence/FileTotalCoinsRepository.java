/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.persistence;

import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.model.TotalCoins;
import java.io.*;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Real, file-backed implementation used in production. */
public class FileTotalCoinsRepository implements TotalCoinsRepository {
  private static final Logger LOGGER = Logger.getLogger(FileTotalCoinsRepository.class.getName());
  private final Path file;

  public FileTotalCoinsRepository() {
    this(Path.of(GameConstants.TOTAL_COINS_FILE));
  }

  // Public: lets tests keep the save file in a temporary folder instead of the working directory.
  public FileTotalCoinsRepository(Path file) {
    this.file = file;
  }

  @Override
  public TotalCoins load() {
    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file.toFile()))) {
      return (TotalCoins) in.readObject();
    } catch (IOException | ClassNotFoundException e) {
      return new TotalCoins();
    }
  }

  @Override
  public void save(TotalCoins totalCoins) {
    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
      out.writeObject(totalCoins);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Error saving coins: {0}", e.getMessage());
    }
  }
}
