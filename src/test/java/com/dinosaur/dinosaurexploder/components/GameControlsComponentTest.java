/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.components;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.dinosaur.dinosaurexploder.constants.GameMode;
import com.dinosaur.dinosaurexploder.model.GameData;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameControlsComponentTest {

  private GameMode originalDifficulty;
  private String originalLanguage;
  private final LanguageManager languageManager = LanguageManager.getInstance();

  @BeforeEach
  void setUp() {
    originalDifficulty = GameData.getSelectedDifficulty();
    originalLanguage = languageManager.selectedLanguageProperty().get();
    languageManager.setSelectedLanguage(LanguageManager.DEFAULT_LANGUAGE);
  }

  @AfterEach
  void tearDown() {
    GameData.setSelectedDifficulty(originalDifficulty);
    languageManager.setSelectedLanguage(originalLanguage);
  }

  @Test
  void shouldShowNormalShootControlsOutsideExpertMode() {
    GameData.setSelectedDifficulty(GameMode.NORMAL);

    assertEquals(
        "Space: Shoot",
        GameControlsComponent.getControlText(GameControlsComponent.ControlType.SHOOT));
  }

  @Test
  void shouldShowLeftClickShootControlsInExpertMode() {
    GameData.setSelectedDifficulty(GameMode.EXPERT);

    assertEquals(
        "Space / Left Click: Shoot",
        GameControlsComponent.getControlText(GameControlsComponent.ControlType.SHOOT));
  }

  @Test
  void shouldRemoveLegacyKeyPrefixFromTranslatedActionLabels() {
    assertEquals("Pause the game", GameControlsComponent.formatActionLabel("ESC: Pause the game"));
    assertEquals("Shoot", GameControlsComponent.formatActionLabel("SPACE: Shoot"));
    assertEquals("Move spaceship up", GameControlsComponent.formatActionLabel("Move spaceship up"));
  }
}
