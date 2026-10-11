/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.persistence;

import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.model.TotalCoins;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Real, file-backed implementation used in production. */
public class FileTotalCoinsRepository implements TotalCoinsRepository {
  private static final Logger LOGGER = Logger.getLogger(FileTotalCoinsRepository.class.getName());

  @Override
  public TotalCoins load() {
    try (ObjectInputStream in =
        new ObjectInputStream(new FileInputStream(GameConstants.TOTAL_COINS_FILE))) {
      return (TotalCoins) in.readObject();
    } catch (IOException | ClassNotFoundException e) {
      return new TotalCoins();
    }
  }

  @Override
  public void save(TotalCoins totalCoins) {
    try (ObjectOutputStream out =
        new ObjectOutputStream(new FileOutputStream(GameConstants.TOTAL_COINS_FILE))) {
      out.writeObject(totalCoins);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Error saving coins: {0}", e.getMessage());
    }
  }
}
