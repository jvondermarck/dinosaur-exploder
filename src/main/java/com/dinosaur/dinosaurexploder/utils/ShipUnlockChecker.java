package com.dinosaur.dinosaurexploder.utils;

import com.dinosaur.dinosaurexploder.exception.LockedShipException;
import com.dinosaur.dinosaurexploder.model.HighScore;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

public class ShipUnlockChecker {
    private static final Map<Integer, Integer> scoreMap = Map.of(   //key: shipNumber, value: lower limit score
            1, 200,
            2, 400,
            3, 600,
            4, 800,
            5, 0,
            6, 0,
            7, 400,
            8, 600
    );
    private HighScore highScore = new HighScore();

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

    private void checkScore(int shipNumber) {
        int lowerLimit = scoreMap.getOrDefault(shipNumber, 0);
        if (lowerLimit <= highScore.getHigh()) return;
        throw new LockedShipException("This ship is locked.\n" +
                "Reach a high score of " + lowerLimit + " to unlock it.");
    }
}
