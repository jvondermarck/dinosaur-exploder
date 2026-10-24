/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.utils;

import com.dinosaur.dinosaurexploder.model.Settings;
import com.dinosaur.dinosaurexploder.persistence.FileSettingsFileProvider;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
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
}
