/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.utils;

import com.dinosaur.dinosaurexploder.model.ControlAction;
import com.dinosaur.dinosaurexploder.model.KeyBindings;
import com.dinosaur.dinosaurexploder.model.Settings;
import com.dinosaur.dinosaurexploder.persistence.FileSettingsFileProvider;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class SettingsProviderTest {

  @TempDir Path tempDir;

  @Test
  void loadSettings_createsDefaultsWhenFileDoesNotExist() {
    Path settingsFile = tempDir.resolve("settings.properties");

    SettingsProvider provider = new SettingsProvider(new FileSettingsFileProvider(settingsFile));

    Settings settings = provider.load();

    Assertions.assertNotNull(settings);
    Assertions.assertEquals("English", settings.getLanguage());
    Assertions.assertTrue(Files.exists(settingsFile));
  }

  @Test
  void loadSettings_returnsDefaultsWhenVolumeIsMalformed() throws Exception {
    Path settingsFile = tempDir.resolve("settings.properties");

    Files.writeString(
        settingsFile,
        "soundVolume=not-a-number\n"
            + "soundMuted=false\n"
            + "soundVolumeSfx=1.0\n"
            + "soundMutedSfx=false\n"
            + "selectedLanguage=English\n");

    SettingsProvider provider = new SettingsProvider(new FileSettingsFileProvider(settingsFile));

    Settings settings = provider.load();

    Assertions.assertNotNull(settings);
    Assertions.assertEquals(1.0, settings.getVolume());
    Assertions.assertFalse(settings.isMuted());
    Assertions.assertEquals(1.0, settings.getSfxVolume());
    Assertions.assertFalse(settings.isSfxMuted());
    Assertions.assertEquals("English", settings.getLanguage());

    Assertions.assertTrue(Files.exists(settingsFile));

    Properties properties = new Properties();
    try (FileInputStream in = new FileInputStream(settingsFile.toFile())) {
      properties.load(in);
    }

    Assertions.assertEquals("1.0", properties.getProperty(SettingsProvider.SETTING_VOLUME));
  }

  @Test
  void loadSettings_returnsDefaultsWhenUnicodeEscapeIsMalformed() throws Exception {
    Path settingsFile = tempDir.resolve("settings.properties");

    // Invalid Unicode escape: DINO contains non-hexadecimal characters.
    Files.writeString(settingsFile, "selectedLanguage=\\uDINO\n");

    SettingsProvider provider = new SettingsProvider(new FileSettingsFileProvider(settingsFile));

    Settings settings = provider.load();

    Assertions.assertEquals("English", settings.getLanguage());

    Properties savedProperties = new Properties();
    try (FileInputStream in = new FileInputStream(settingsFile.toFile())) {
      savedProperties.load(in);
    }

    Assertions.assertEquals(
        "English", savedProperties.getProperty(SettingsProvider.SETTINGS_LANGUAGE));
  }

  @Test
  void updateMuteStatePreservesLatestLanguage() {
    Path settingsFile = tempDir.resolve("settings.properties");

    SettingsProvider provider = new SettingsProvider(new FileSettingsFileProvider(settingsFile));

    Settings initialSettings = new Settings();
    initialSettings.setVolume(1.0);
    initialSettings.setMuted(false);
    initialSettings.setSfxVolume(1.0);
    initialSettings.setSfxMuted(false);
    initialSettings.setLanguage("Japanese");
    provider.save(initialSettings);

    Settings olderSettings = provider.load();

    Settings latestSettings = provider.load();
    latestSettings.setLanguage("English");
    provider.save(latestSettings);

    provider.updateMute(true);

    Settings savedSettings = provider.load();
    Assertions.assertEquals("English", savedSettings.getLanguage());
    Assertions.assertTrue(savedSettings.isMuted());
    Assertions.assertEquals(1.0, savedSettings.getVolume());
    Assertions.assertEquals(1.0, savedSettings.getSfxVolume());
    Assertions.assertFalse(savedSettings.isSfxMuted());
    Assertions.assertEquals("Japanese", olderSettings.getLanguage());
  }

  @Test
  void customKeysSurviveSavingOlderAudioSettings() throws Exception {
    Settings olderSettings = SettingsProvider.loadSettings();
    SettingsProvider.saveKeyBindings(
        KeyBindings.defaults().withKey(ControlAction.SHOOT, KeyCode.J));
    olderSettings.setVolume(0.4);
    SettingsProvider.saveSettings(olderSettings);

    Assertions.assertEquals(
        KeyCode.J, SettingsProvider.loadKeyBindings().keyFor(ControlAction.SHOOT));
    Assertions.assertEquals(0.4, SettingsProvider.loadSettings().getVolume());
  }

  @Test
  void savingKeysPreservesLatestAudioAndLanguage() throws Exception {
    Settings settings = SettingsProvider.loadSettings();
    settings.setVolume(0.3);
    settings.setSfxVolume(0.6);
    settings.setMuted(true);
    settings.setSfxMuted(true);
    settings.setLanguage("Spanish");
    SettingsProvider.saveSettings(settings);

    KeyBindings custom =
        new KeyBindings(
            java.util.Map.of(
                ControlAction.SHOOT, KeyCode.J,
                ControlAction.BOMB, KeyCode.K,
                ControlAction.SHIELD, KeyCode.L));
    SettingsProvider.saveKeyBindings(custom);

    for (ControlAction action : ControlAction.values()) {
      Assertions.assertEquals(
          custom.keyFor(action), SettingsProvider.loadKeyBindings().keyFor(action));
    }
    Settings saved = SettingsProvider.loadSettings();
    Assertions.assertEquals(0.3, saved.getVolume());
    Assertions.assertEquals(0.6, saved.getSfxVolume());
    Assertions.assertTrue(saved.isMuted());
    Assertions.assertTrue(saved.isSfxMuted());
    Assertions.assertEquals("Spanish", saved.getLanguage());
  }

  @Test
  void legacySettingsWithoutControlsUseDefaults() throws Exception {
    Files.writeString(
        Path.of(SettingsProvider.SETTINGS_FILE),
        "soundVolume=0.5\nsoundMuted=false\nsoundVolumeSfx=1.0\n"
            + "soundMutedSfx=false\nselectedLanguage=Spanish\n");
    assertDefaultKeys();
    Assertions.assertEquals(0.5, SettingsProvider.loadSettings().getVolume());
    Assertions.assertEquals("Spanish", SettingsProvider.loadSettings().getLanguage());
  }

  @Test
  void invalidKeysUseDefaultsWithoutErasingOtherSettings() throws Exception {
    for (String invalid : new String[] {"NOT_A_KEY", "W", "B"}) {
      Files.writeString(
          Path.of(SettingsProvider.SETTINGS_FILE),
          "soundVolume=0.5\nsoundMuted=false\nsoundVolumeSfx=1.0\n"
              + "soundMutedSfx=false\nselectedLanguage=Spanish\ncontrols.shoot="
              + invalid
              + "\n");
      assertDefaultKeys();
      Assertions.assertEquals("Spanish", SettingsProvider.loadSettings().getLanguage());
      Assertions.assertEquals(0.5, SettingsProvider.loadSettings().getVolume());
    }
  }

  @Test
  void resetPersistsDefaultKeys() throws Exception {
    SettingsProvider.saveKeyBindings(
        KeyBindings.defaults().withKey(ControlAction.SHOOT, KeyCode.J));
    SettingsProvider.saveKeyBindings(KeyBindings.defaults());
    assertDefaultKeys();
  }

  @Test
  void firstSaveAlsoCreatesValidGeneralSettings() throws Exception {
    Files.deleteIfExists(Path.of(SettingsProvider.SETTINGS_FILE));
    SettingsProvider.saveKeyBindings(
        KeyBindings.defaults().withKey(ControlAction.SHOOT, KeyCode.J));
    Assertions.assertEquals(1.0, SettingsProvider.loadSettings().getVolume());
    Assertions.assertEquals(
        KeyCode.J, SettingsProvider.loadKeyBindings().keyFor(ControlAction.SHOOT));
  }

  private void assertDefaultKeys() {
    KeyBindings loaded = SettingsProvider.loadKeyBindings();
    for (ControlAction action : ControlAction.values()) {
      Assertions.assertEquals(action.defaultKey(), loaded.keyFor(action));
    }
  }
}
