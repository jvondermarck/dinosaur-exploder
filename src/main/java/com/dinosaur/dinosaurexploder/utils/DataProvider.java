package com.dinosaur.dinosaurexploder.utils;

import com.dinosaur.dinosaurexploder.model.HighScore;
import com.dinosaur.dinosaurexploder.model.TotalCoins;

public interface DataProvider {
  HighScore getHighScore();

  TotalCoins getTotalCoins();
}
