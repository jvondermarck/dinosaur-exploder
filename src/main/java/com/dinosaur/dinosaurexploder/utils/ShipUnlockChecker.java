package com.dinosaur.dinosaurexploder.utils;

import com.dinosaur.dinosaurexploder.exception.LockedShipException;
import com.dinosaur.dinosaurexploder.model.HighScore;
import com.dinosaur.dinosaurexploder.model.TotalCoins;
import java.util.Map;

public class ShipUnlockChecker {
  public LanguageManager languageManager = LanguageManager.getInstance();

  private static final Map<Integer, Integer> scoreMap =
      Map.of( // key: shipNumber, value: lower limit score
          1, 0,
          2, 0,
          3, 100,
          4, 200,
          5, 300,
          6, 400,
          7, 600,
          8, 700);

  private static final Map<Integer, Integer> coinMap =
      Map.of( // key: shipNumber, value: lower limit total coins
          1, 0,
          2, 0,
          3, 10,
          4, 50,
          5, 100,
          6, 150,
          7, 200,
          8, 250);

  private HighScore highScore = new HighScore();
  private TotalCoins totalCoins = new TotalCoins();

  private final DataProvider dataProvider;

  public ShipUnlockChecker(DataProvider dataProvider) {
    this.dataProvider = dataProvider;
  }

  public int check(int shipNumber) {
    highScore = dataProvider.getHighScore();
    totalCoins = dataProvider.getTotalCoins();
    checkScoreAndCoins(shipNumber);
    return shipNumber;
  }

  private void checkScoreAndCoins(int shipNumber) {
    totalCoins = dataProvider.getTotalCoins();

    int lowerScoreLimit = scoreMap.getOrDefault(shipNumber, 0);
    int lowerCoinLimit = coinMap.getOrDefault(shipNumber, 0);

    if (lowerScoreLimit <= highScore.getHigh() && lowerCoinLimit <= totalCoins.getTotal()) return;
    else if (lowerScoreLimit > highScore.getHigh() && lowerCoinLimit <= totalCoins.getTotal()) {
      throw new LockedShipException(
          languageManager.getTranslation("ship_locked")
              + "\n"
              + languageManager
                  .getTranslation("unlock_highScore")
                  .replace("##", String.valueOf(lowerScoreLimit)));
    } else if (lowerScoreLimit <= highScore.getHigh() && lowerCoinLimit > totalCoins.getTotal()) {
      throw new LockedShipException(
          languageManager.getTranslation("ship_locked")
              + "\n"
              + languageManager
                  .getTranslation("unlock_totalCoins")
                  .replace("##", String.valueOf(lowerCoinLimit)));
    } else {
      throw new LockedShipException(
          languageManager.getTranslation("ship_locked")
              + "\n"
              + languageManager
                  .getTranslation("unlock_highScore")
                  .replace("##", String.valueOf(lowerScoreLimit))
              + "\n"
              + languageManager
                  .getTranslation("unlock_totalCoins")
                  .replace("##", String.valueOf(lowerCoinLimit)));
    }
  }
}
