/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.dinosaur.dinosaurexploder.constants.GameMode;
import com.dinosaur.dinosaurexploder.model.ControlAction;
import com.dinosaur.dinosaurexploder.model.GameData;
import com.dinosaur.dinosaurexploder.model.KeyBindings;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.SettingsProvider;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class GameControlsComponentTest {

  private GameMode originalDifficulty;
  private String originalLanguage;
  private final LanguageManager languageManager = LanguageManager.getInstance();
  private MockedStatic<SettingsProvider> settingsProvider;

  @BeforeEach
  void setUp() {
    settingsProvider = Mockito.mockStatic(SettingsProvider.class);
    settingsProvider.when(SettingsProvider::loadKeyBindings).thenReturn(KeyBindings.defaults());
    originalDifficulty = GameData.getSelectedDifficulty();
    originalLanguage = languageManager.selectedLanguageProperty().get();
    languageManager.setSelectedLanguage(LanguageManager.DEFAULT_LANGUAGE);
  }

  @AfterEach
  void tearDown() {
    settingsProvider.close();
    GameData.setSelectedDifficulty(originalDifficulty);
    languageManager.setSelectedLanguage(originalLanguage);
  }

  @Test
  void shouldShowNormalShootControlsOutsideExpertMode() {
    GameData.setSelectedDifficulty(GameMode.NORMAL);

    assertEquals(
        ControlAction.SHOOT.defaultKey().getName() + ": Shoot",
        GameControlsComponent.getControlText(GameControlsComponent.ControlType.SHOOT));
  }

  @Test
  void shouldShowLeftClickShootControlsInExpertMode() {
    GameData.setSelectedDifficulty(GameMode.EXPERT);

    assertEquals(
        ControlAction.SHOOT.defaultKey().getName() + " / Left Click: Shoot",
        GameControlsComponent.getControlText(GameControlsComponent.ControlType.SHOOT));
  }

  @Test
  void shouldRemoveLegacyKeyPrefixFromTranslatedActionLabels() {
    assertEquals("Pause the game", GameControlsComponent.formatActionLabel("ESC: Pause the game"));
    assertEquals("Shoot", GameControlsComponent.formatActionLabel("SPACE: Shoot"));
    assertEquals("Move spaceship up", GameControlsComponent.formatActionLabel("Move spaceship up"));
  }

  @Test
  void shouldGenerateControlsDialogTextWithoutTrailingNewline() {
    String controlsText = GameControlsComponent.generateControlsDialogText();

    assertFalse(controlsText.endsWith("\n"));
  }

  @Test
  void shouldReturnAllControlTextsInDisplayOrder() {
    String[] controlTexts = GameControlsComponent.getAllControlTexts();

    assertEquals(GameControlsComponent.ControlType.values().length, controlTexts.length);
    assertEquals(
        GameControlsComponent.getControlText(GameControlsComponent.ControlType.MOVE_UP),
        controlTexts[0]);
    assertEquals(
        GameControlsComponent.getControlText(GameControlsComponent.ControlType.SHIELD),
        controlTexts[controlTexts.length - 1]);
  }

  @Test
  void shouldDisplayTheConfiguredKeyAndPreserveExpertMouseHint() {
    settingsProvider
        .when(SettingsProvider::loadKeyBindings)
        .thenReturn(KeyBindings.defaults().withKey(ControlAction.SHOOT, KeyCode.J));
    GameData.setSelectedDifficulty(GameMode.EXPERT);
    assertEquals(
        "J / Left Click: Shoot",
        GameControlsComponent.getControlText(GameControlsComponent.ControlType.SHOOT));
  }
}
