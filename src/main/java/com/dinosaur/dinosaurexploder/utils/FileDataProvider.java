/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.utils;

import com.dinosaur.dinosaurexploder.model.HighScore;
import com.dinosaur.dinosaurexploder.model.TotalCoins;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class FileDataProvider implements DataProvider {
  @Override
  public HighScore getHighScore() {
    try (FileInputStream file = new FileInputStream("highScore.ser");
        ObjectInputStream in = new ObjectInputStream(file)) {
      return (HighScore) in.readObject();
    } catch (IOException | ClassNotFoundException e) {
      return new HighScore();
    }
  }

  @Override
  public TotalCoins getTotalCoins() {
    try (FileInputStream file = new FileInputStream("totalCoins.ser");
        ObjectInputStream in = new ObjectInputStream(file)) {
      return (TotalCoins) in.readObject();
    } catch (IOException | ClassNotFoundException e) {
      return new TotalCoins();
    }
  }
}
