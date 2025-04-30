package com.dinosaur.dinosaurexploder.utils;

import com.dinosaur.dinosaurexploder.exception.LockedShipException;
import com.dinosaur.dinosaurexploder.model.HighScore;
import com.dinosaur.dinosaurexploder.model.TotalCoins;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

public class ShipUnlockChecker {
    public LanguageManager languageManager = LanguageManager.getInstance();

    private static final Map<Integer, Integer> scoreMap = Map.of( // key: shipNumber, value: lower limit score
            1, 0,
            2, 0,
            3, 100,
            4, 200,
            5, 300,
            6, 400,
            7, 600,
            8, 700);
    private HighScore highScore = new HighScore();
    private TotalCoins totalCoins = new TotalCoins();

    public int check(int shipNumber) {
        highScore = getHighScore();
        checkScore(shipNumber);
        return shipNumber;
    }

    public HighScore getHighScore() {
        try (FileInputStream file = new FileInputStream("highScore.ser");
                ObjectInputStream in = new ObjectInputStream(file)) {
            return (HighScore) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new HighScore();
        }
    }

    public TotalCoins getTotalCoins() {
        try (FileInputStream file = new FileInputStream("totalCoins.ser");
                ObjectInputStream in = new ObjectInputStream(file)) {
            return (TotalCoins) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new TotalCoins();
        }
    }

    private void checkScore(int shipNumber) {
        int lowerLimit = scoreMap.getOrDefault(shipNumber, 0);
        if (lowerLimit <= highScore.getHigh())
            return;
        throw new LockedShipException(languageManager.getTranslation("ship_locked") + "\n" +
                languageManager.getTranslation("unlock_highScore").replace("##", String.valueOf(lowerLimit)));
    }
}
