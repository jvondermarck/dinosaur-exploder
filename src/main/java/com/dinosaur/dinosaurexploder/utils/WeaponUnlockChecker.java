package com.dinosaur.dinosaurexploder.utils;

import com.dinosaur.dinosaurexploder.exception.LockedWeaponException;
import com.dinosaur.dinosaurexploder.model.HighScore;
import com.dinosaur.dinosaurexploder.model.LanguageManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

public class WeaponUnlockChecker {
    LanguageManager languageManager = LanguageManager.getInstance();

    private static final Map<Integer, Integer> scoreMap = Map.of(   //key: weaponNumber, value: lower limit score
            1, 0,
            2, 50,
            3, 100
    );
    private HighScore highScore = new HighScore();

    public int check(int weaponNumber) {
        highScore = getHighScore();
        checkScore(weaponNumber);
        return weaponNumber;
    }

    public HighScore getHighScore() {
        try (FileInputStream file = new FileInputStream("highScore.ser");
             ObjectInputStream in = new ObjectInputStream(file)) {
            return (HighScore) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new HighScore();
        }
    }

    private void checkScore(int weaponNumber) {
        int lowerLimit = scoreMap.getOrDefault(weaponNumber, 0);
        if (lowerLimit <= highScore.getHigh()) return;
        throw new LockedWeaponException(languageManager.getTranslation("weapon_locked") + "\n" +
               languageManager.getTranslation("unlock_highScore").replace("##", String.valueOf(lowerLimit)));
    }
}
