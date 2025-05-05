package com.dinosaur.dinosaurexploder.utils;

import com.dinosaur.dinosaurexploder.exception.LockedWeaponException;
import com.dinosaur.dinosaurexploder.model.HighScore;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

public class WeaponUnlockChecker {
    LanguageManager languageManager = LanguageManager.getInstance();

    private static final Map<Integer, Integer> scoreMap = Map.of( // key: weaponNumber, value: lower limit score
            1, 0,
            2, 50,
            3, 100);

    private static final Map<Integer, Integer> coinMap = Map.of( // key: weaponNumber, value: lower limit total coins
            1, 0,
            2, 5,
            3, 10);

    private HighScore highScore = new HighScore();
    private TotalCoins totalCoins = new TotalCoins();

    private final DataProvider dataProvider;

    public WeaponUnlockChecker(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public int check(int weaponNumber) {
        highScore = dataProvider.getHighScore();
        checkScoreAndCoins(weaponNumber);
        return weaponNumber;
    }


    public TotalCoins getTotalCoins() {
        try (FileInputStream file = new FileInputStream("totalCoins.ser");
             ObjectInputStream in = new ObjectInputStream(file)) {
            return (TotalCoins) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new TotalCoins();
        }
    }

    private void checkScoreAndCoins(int weaponNumber) {
        int lowerScoreLimit = scoreMap.getOrDefault(weaponNumber, 0);
        int lowerCoinLimit = coinMap.getOrDefault(weaponNumber, 0);

        if (lowerScoreLimit <= highScore.getHigh() && lowerCoinLimit <= totalCoins.getTotal())
            return;
        else if (lowerScoreLimit > highScore.getHigh() && lowerCoinLimit <= totalCoins.getTotal()) {
            throw new LockedWeaponException(languageManager.getTranslation("weapon_locked") + "\n" +
                    languageManager.getTranslation("unlock_highScore").replace("##", String.valueOf(lowerScoreLimit)));
        } else if (lowerScoreLimit <= highScore.getHigh() && lowerCoinLimit > totalCoins.getTotal()) {
            throw new LockedWeaponException(languageManager.getTranslation("weapon_locked") + "\n" +
                    languageManager.getTranslation("unlock_totalCoins").replace("##", String.valueOf(lowerCoinLimit)));
        } else {
            throw new LockedWeaponException(languageManager.getTranslation("weapon_locked") + "\n"
                    + languageManager.getTranslation("unlock_highScore").replace("##", String.valueOf(lowerScoreLimit))
                    + "\n" +
                    languageManager.getTranslation("unlock_totalCoins").replace("##", String.valueOf(lowerCoinLimit)));
        }

    }
}
