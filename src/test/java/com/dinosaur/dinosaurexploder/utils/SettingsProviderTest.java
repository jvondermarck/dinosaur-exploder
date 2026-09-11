/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.utils;

import com.dinosaur.dinosaurexploder.model.Settings;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
