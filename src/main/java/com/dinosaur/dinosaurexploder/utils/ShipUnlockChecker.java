package com.dinosaur.dinosaurexploder.utils;

import com.dinosaur.dinosaurexploder.exception.LockedShipException;
import com.dinosaur.dinosaurexploder.model.HighScore;

import java.util.Map;

public class ShipUnlockChecker {
    public LanguageManager languageManager = LanguageManager.getInstance();

    private static final Map<Integer, Integer> scoreMap = Map.of(   //key: shipNumber, value: lower limit score
            1, 0,
            2, 0,
            3, 100,
            4, 200,
            5, 300,
            6, 400,
            7, 600,
            8, 700
    );
    private HighScore highScore = new HighScore();

    private final DataProvider dataProvider;

    public ShipUnlockChecker(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public int check(int shipNumber) {
        highScore = dataProvider.getHighScore();
        checkScore(shipNumber);
        return shipNumber;
    }

    private void checkScore(int shipNumber) {
        int lowerLimit = scoreMap.getOrDefault(shipNumber, 0);
        if (lowerLimit <= highScore.getHigh()) return;
        throw new LockedShipException(languageManager.getTranslation("ship_locked") + "\n" +
                languageManager.getTranslation("unlock_highScore").replace("##", String.valueOf(lowerLimit)));
    }
}
