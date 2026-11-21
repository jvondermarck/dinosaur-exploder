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
    SettingsProvider provider = createProvider();
    Settings olderSettings = provider.load();
    provider.saveBindings(KeyBindings.defaults().withKey(ControlAction.SHOOT, KeyCode.J));
    olderSettings.setVolume(0.4);
    provider.save(olderSettings);

    Assertions.assertEquals(KeyCode.J, provider.loadBindings().keyFor(ControlAction.SHOOT));
    Assertions.assertEquals(0.4, provider.load().getVolume());
  }

  @Test
  void savingKeysPreservesLatestAudioAndLanguage() throws Exception {
    SettingsProvider provider = createProvider();
    Settings settings = provider.load();
    settings.setVolume(0.3);
    settings.setSfxVolume(0.6);
    settings.setMuted(true);
    settings.setSfxMuted(true);
    settings.setLanguage("Spanish");
    provider.save(settings);

    KeyBindings custom =
        new KeyBindings(
            java.util.Map.of(
                ControlAction.SHOOT, KeyCode.J,
                ControlAction.BOMB, KeyCode.K,
                ControlAction.SHIELD, KeyCode.L));
    provider.saveBindings(custom);

    for (ControlAction action : ControlAction.values()) {
      Assertions.assertEquals(custom.keyFor(action), provider.loadBindings().keyFor(action));
    }
    Settings saved = provider.load();
    Assertions.assertEquals(0.3, saved.getVolume());
    Assertions.assertEquals(0.6, saved.getSfxVolume());
    Assertions.assertTrue(saved.isMuted());
    Assertions.assertTrue(saved.isSfxMuted());
    Assertions.assertEquals("Spanish", saved.getLanguage());
  }

  @Test
  void legacySettingsWithoutControlsUseDefaults() throws Exception {
    SettingsProvider provider = createProvider();
    Files.writeString(
        tempDir.resolve(SettingsProvider.SETTINGS_FILE),
        "soundVolume=0.5\nsoundMuted=false\nsoundVolumeSfx=1.0\n"
            + "soundMutedSfx=false\nselectedLanguage=Spanish\n");
    assertDefaultKeys(provider);
    Assertions.assertEquals(0.5, provider.load().getVolume());
    Assertions.assertEquals("Spanish", provider.load().getLanguage());
  }

  @Test
  void invalidKeysUseDefaultsWithoutErasingOtherSettings() throws Exception {
    SettingsProvider provider = createProvider();
    for (String invalid : new String[] {"NOT_A_KEY", "W", "X"}) {
      Files.writeString(
          tempDir.resolve(SettingsProvider.SETTINGS_FILE),
          "soundVolume=0.5\nsoundMuted=false\nsoundVolumeSfx=1.0\n"
              + "soundMutedSfx=false\nselectedLanguage=Spanish\ncontrols.shoot="
              + invalid
              + "\n");
      assertDefaultKeys(provider);
      Assertions.assertEquals("Spanish", provider.load().getLanguage());
      Assertions.assertEquals(0.5, provider.load().getVolume());
    }
  }

  @Test
  void resetPersistsDefaultKeys() throws Exception {
    SettingsProvider provider = createProvider();
    provider.saveBindings(KeyBindings.defaults().withKey(ControlAction.SHOOT, KeyCode.J));
    provider.saveBindings(KeyBindings.defaults());
    assertDefaultKeys(provider);
  }

  @Test
  void firstSaveAlsoCreatesValidGeneralSettings() throws Exception {
    SettingsProvider provider = createProvider();
    provider.saveBindings(KeyBindings.defaults().withKey(ControlAction.SHOOT, KeyCode.J));
    Assertions.assertEquals(1.0, provider.load().getVolume());
    Assertions.assertEquals(KeyCode.J, provider.loadBindings().keyFor(ControlAction.SHOOT));
  }

  @Test
  void customKeysArePersistedByTheSettingsFileProvider() throws Exception {
    SettingsProvider provider = createProvider();
    provider.saveBindings(
        new KeyBindings(
            java.util.Map.of(
                ControlAction.SHOOT, KeyCode.J,
                ControlAction.BOMB, KeyCode.K,
                ControlAction.SHIELD, KeyCode.L)));
    Properties properties = new Properties();
    try (FileInputStream in =
        new FileInputStream(tempDir.resolve(SettingsProvider.SETTINGS_FILE).toFile())) {
      properties.load(in);
    }
    Assertions.assertEquals("J", properties.getProperty("controls.shoot"));
    Assertions.assertEquals("K", properties.getProperty("controls.bomb"));
    Assertions.assertEquals("L", properties.getProperty("controls.shield"));
  }

  @Test
  void keySaveReportsWriteFailures() throws Exception {
    SettingsProvider provider =
        new SettingsProvider(
            new FileSettingsFileProvider(tempDir.resolve("missing/settings.properties")));
    Assertions.assertThrows(
        java.io.IOException.class, () -> provider.saveBindings(KeyBindings.defaults()));
  }

  private SettingsProvider createProvider() {
    return new SettingsProvider(
        new FileSettingsFileProvider(tempDir.resolve(SettingsProvider.SETTINGS_FILE)));
  }

  private void assertDefaultKeys(SettingsProvider provider) {
    KeyBindings loaded = provider.loadBindings();
    for (ControlAction action : ControlAction.values()) {
      Assertions.assertEquals(action.defaultKey(), loaded.keyFor(action));
    }
  }
}
