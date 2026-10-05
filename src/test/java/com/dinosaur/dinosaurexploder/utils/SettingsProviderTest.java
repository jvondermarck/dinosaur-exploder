/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.utils;

import com.dinosaur.dinosaurexploder.model.Settings;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SettingsProviderTest {

  private Path settingsBackup;

  @BeforeEach
  void backupSettingsFile() throws Exception {
    Path settingsFile = Path.of(SettingsProvider.SETTINGS_FILE);
    if (Files.exists(settingsFile)) {
      settingsBackup = Files.createTempFile("settings", ".properties");
      Files.copy(settingsFile, settingsBackup, StandardCopyOption.REPLACE_EXISTING);
    }
  }

  @AfterEach
  void restoreSettingsFile() throws Exception {
    Path settingsFile = Path.of(SettingsProvider.SETTINGS_FILE);
    if (settingsBackup != null) {
      Files.copy(settingsBackup, settingsFile, StandardCopyOption.REPLACE_EXISTING);
      Files.deleteIfExists(settingsBackup);
    } else {
      Files.deleteIfExists(settingsFile);
    }
  }

  @Test
  public void testLoadSettings() {
    Settings settings = SettingsProvider.loadSettings();
    Assertions.assertNotNull(
        settings, "Settings should always be available. At least the default settings");
  }

  @Test
  public void loadSettings_returnsDefaultsWhenVolumeIsMalformed() throws Exception {
    try (FileWriter writer = new FileWriter(SettingsProvider.SETTINGS_FILE)) {
      writer.write("soundVolume=not-a-number\n");
      writer.write("soundMuted=false\n");
      writer.write("soundVolumeSfx=1.0\n");
      writer.write("soundMutedSfx=false\n");
      writer.write("selectedLanguage=English\n");
    }

    Settings settings = SettingsProvider.loadSettings();

    Assertions.assertNotNull(settings);
    Assertions.assertEquals(1.0, settings.getVolume());
    Assertions.assertFalse(settings.isMuted());
    Assertions.assertEquals(1.0, settings.getSfxVolume());
    Assertions.assertFalse(settings.isSfxMuted());
    Assertions.assertEquals("English", settings.getLanguage());

    File regenerated = new File(SettingsProvider.SETTINGS_FILE);
    Assertions.assertTrue(regenerated.exists());
    Properties properties = new Properties();
    try (FileInputStream in = new FileInputStream(regenerated)) {
      properties.load(in);
    }
    Assertions.assertEquals("1.0", properties.getProperty(SettingsProvider.SETTING_VOLUME));
  }

  @Test
  void updateMuteStatePreservesLatestLanguage() {
    Settings initialSettings = new Settings();
    initialSettings.setVolume(1.0);
    initialSettings.setMuted(false);
    initialSettings.setSfxVolume(1.0);
    initialSettings.setSfxMuted(false);
    initialSettings.setLanguage("Japanese");
    SettingsProvider.saveSettings(initialSettings);

    Settings olderSettings = SettingsProvider.loadSettings();

    Settings latestSettings = SettingsProvider.loadSettings();
    latestSettings.setLanguage("English");
    SettingsProvider.saveSettings(latestSettings);

    SettingsProvider.updateMuteState(true);

    Settings savedSettings = SettingsProvider.loadSettings();
    Assertions.assertEquals("English", savedSettings.getLanguage());
    Assertions.assertTrue(savedSettings.isMuted());
    Assertions.assertEquals(1.0, savedSettings.getVolume());
    Assertions.assertEquals(1.0, savedSettings.getSfxVolume());
    Assertions.assertFalse(savedSettings.isSfxMuted());
    Assertions.assertEquals("Japanese", olderSettings.getLanguage());
  }
}
