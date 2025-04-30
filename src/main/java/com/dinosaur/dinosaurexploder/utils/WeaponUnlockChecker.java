package com.dinosaur.dinosaurexploder.utils;

import com.dinosaur.dinosaurexploder.exception.LockedWeaponException;
import com.dinosaur.dinosaurexploder.model.HighScore;

import java.util.Map;

public class WeaponUnlockChecker {
    LanguageManager languageManager = LanguageManager.getInstance();

    private static final Map<Integer, Integer> scoreMap = Map.of( // key: weaponNumber, value: lower limit score
            1, 0,
            2, 50,
            3, 100);
    private HighScore highScore = new HighScore();

    private final DataProvider dataProvider;

    public WeaponUnlockChecker(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public int check(int weaponNumber) {
        highScore = dataProvider.getHighScore();
        checkScore(weaponNumber);
        return weaponNumber;
    }


    private void checkScore(int weaponNumber) {
        int lowerLimit = scoreMap.getOrDefault(weaponNumber, 0);
        if (lowerLimit <= highScore.getHigh())
            return;
        throw new LockedWeaponException(languageManager.getTranslation("weapon_locked") + "\n" +
                languageManager.getTranslation("unlock_highScore").replace("##", String.valueOf(lowerLimit)));
    }
}
