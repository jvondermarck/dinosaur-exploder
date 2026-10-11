package com.dinosaur.dinosaurexploder.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dinosaur.dinosaurexploder.constants.GameMode;
import com.dinosaur.dinosaurexploder.specialties.Specialty;
import com.dinosaur.dinosaurexploder.specialties.SpecialtyManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class GameDataTest {

    @AfterEach
    void resetGameData() {
        GameData.setSelectedDifficulty(GameMode.NORMAL);
        GameData.setSelectedSpecialty(SpecialtyManager.NULL_SPECIALTY);
        GameData.setSelectedShip(1);
        GameData.setSelectedWeapon(1);
    }

    @Test
    void selectedDifficultyCanBeChanged() {
        GameData.setSelectedDifficulty(GameMode.EXPERT);
        assertEquals(GameMode.EXPERT, GameData.getSelectedDifficulty());
    }

    @Test
    void selectedSpecialtyCanBeChanged() {
        Specialty specialty = SpecialtyManager.NULL_SPECIALTY;
        GameData.setSelectedSpecialty(specialty);
        assertSame(specialty, GameData.getSelectedSpecialty());
    }

    @Test
    void shipOneCanBeSelected() {
        GameData.setSelectedShip(1);
        assertEquals(1, GameData.getSelectedShip());
    }

    @Test
    void weaponOneCanBeSelected() {
        GameData.setSelectedWeapon(1);
        assertEquals(1, GameData.getSelectedWeapon());
    }

    @Test
    void shipOneIsUnlocked() {
        assertTrue(GameData.checkUnlockedShip(1));
    }

    @Test
    void weaponOneIsUnlocked() {
        assertTrue(GameData.checkUnlockedWeapon(1));
    }

    @Test
    void unknownShipIdIsCurrentlyTreatedAsUnlocked() {
        assertTrue(GameData.checkUnlockedShip(999));
    }

    @Test
    void unknownWeaponIdIsCurrentlyTreatedAsUnlocked() {
        assertTrue(GameData.checkUnlockedWeapon(999));
    }

    @Test
    void maxHighScoreIsAtLeastAsHighAsEachModeScore() {
        int maxScore = GameData.getMaxHighScore();
        int normalScore = GameData.getHighScore(GameMode.NORMAL);
        int expertScore = GameData.getHighScore(GameMode.EXPERT);

        assertTrue(maxScore >= normalScore);
        assertTrue(maxScore >= expertScore);
    }

    @Test
    void highScoreMatchesSelectedDifficulty() {
        GameData.setSelectedDifficulty(GameMode.EXPERT);

        int expectedScore = GameData.getHighScore(GameMode.EXPERT);
        int actualScore = GameData.getHighScore();

        assertEquals(expectedScore, actualScore);
    }

    @Test
    void highScoreCanBeReadForBothGameModes() {
        assertDoesNotThrow(() -> GameData.getHighScore(GameMode.NORMAL));
        assertDoesNotThrow(() -> GameData.getHighScore(GameMode.EXPERT));
    }

    @Test
    void totalCoinsCanBeRead() {
        assertDoesNotThrow(() -> GameData.getTotalCoins());
    }

    @Test
    void lockedShipCheckDoesNotThrow() {
        assertDoesNotThrow(() -> GameData.checkUnlockedShip(8));
    }

    @Test
    void lockedWeaponCheckDoesNotThrow() {
        assertDoesNotThrow(() -> GameData.checkUnlockedWeapon(3));
    }
}