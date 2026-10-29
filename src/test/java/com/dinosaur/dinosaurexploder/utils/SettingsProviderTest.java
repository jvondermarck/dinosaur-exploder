/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dinosaur.dinosaurexploder.model.Settings;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Covers the recovery path in {@link SettingsProvider#loadSettings()} when the settings file exists
 * but is partial or malformed.
 *
 * <p>Contract being verified: {@link SettingsProvider#loadSettings loadSettings} must always return
 * a valid persisted settings or valid default settings, never {@code null}. A {@code null} return
 * value would propagate failures into other parts of the application.
 */
public class SettingsProviderTest {

  private static final Path SETTINGS_PATH = Path.of(SettingsProvider.SETTINGS_FILE);

  /**
   * Double rounding tolerance within one ten-thousandth (0.0001) of each other.
   *
   * <p>The maximum allowable difference between two double values to consider them equal,
   * compensating for floating-point rounding errors.
   */
  private static final double DOUBLE_ROUNDING_TOLERANCE = 1e-4;

  private static byte[] originalBytes;

  /**
   * Creates an in-memory backup of any existing settings before running any tests, ensuring that
   * the local settings are unaffected by the testing.
   *
   * @throws IOException if I/O operation failed or was interrupted
   */
  @BeforeAll
  static void backupExistingSettings() throws IOException {
    originalBytes = Files.exists(SETTINGS_PATH) ? Files.readAllBytes(SETTINGS_PATH) : null;
  }

  /**
   * Before every test, establish a known starting state.
   *
   * @throws IOException if I/O operation failed or was interrupted
   */
  @BeforeEach
  void resetSettings() throws IOException {
    Files.deleteIfExists(SETTINGS_PATH);
  }

  /**
   * Restores any existing settings after all the tests have run.
   *
   * @throws IOException if I/O operation failed or was interrupted
   */
  @AfterAll
  static void restoreExistingSettings() throws IOException {
    Files.deleteIfExists(SETTINGS_PATH);
    if (originalBytes != null) {
      Files.write(SETTINGS_PATH, originalBytes);
    }
  }

  /**
   * Verifies the recovery contract: {@code loadSettings(...)} must never return {@code null}.
   *
   * @param settings the settings to verify
   */
  private void assertReturnsValidSettings(Settings settings) {
    assertNotNull(
        settings,
        "loadSettings() must never return null. Corrupt settings should recover to default settings.");
  }

  /** Verifies the default settings values */
  private void assertDefaultSettings(Settings settings) {
    assertReturnsValidSettings(settings);

    assertEquals(
        1.0, settings.getVolume(), DOUBLE_ROUNDING_TOLERANCE, "Default music volume should be 1.0");
    assertEquals(
        1.0,
        settings.getSfxVolume(),
        DOUBLE_ROUNDING_TOLERANCE,
        "Default sound-effect volume should be 1.0");
    assertFalse(settings.isMuted(), "Music should be unmuted by default");
    assertFalse(settings.isSfxMuted(), "Sound effects should be unmuted by default");
    assertEquals("English", settings.getLanguage(), "Language should be English by default");
  }

  @Test
  @DisplayName("No persisted settings -> creates and persists defaults")
  void missingSettingsFileGeneratesAndPersistsDefaults() {
    assertDefaultSettings(SettingsProvider.loadSettings());
    assertTrue(Files.exists(SETTINGS_PATH), "defaults must be persisted locally");
  }

  /**
   * When local settings file is partial (missing a setting), it should return the default settings
   * through the recovery path.
   *
   * @throws IOException if I/O operation failed or was interrupted
   */
  @Test
  @DisplayName("partial settings file -> recovers to default settings")
  void missingVolumePartialSettingsReturnDefaultSettings() throws IOException {
    Files.writeString(
        SETTINGS_PATH,
        String.join(
            "\n",
            "soundMuted=false",
            "soundVolumeSfx=1.0",
            "soundMutedSfx=false",
            "selectedLanguage=English"));

    assertDefaultSettings(SettingsProvider.loadSettings());
  }

  /**
   * When local settings file is malformed, it should return the default settings through the
   * recovery path.
   *
   * @throws IOException if I/O operation failed or was interrupted
   */
  @Test
  @DisplayName("Malformed settings file with malformed numeric -> recover to default settings")
  void malformedSettingsReturnDefaultSettings() throws IOException {
    Files.writeString(
        SETTINGS_PATH,
        String.join(
            "\n",
            "soundVolume=not-a-number",
            "soundMuted=false",
            "soundVolumeSfx=1.0",
            "soundMutedSfx=false",
            "selectedLanguage=English"));

    assertDefaultSettings(SettingsProvider.loadSettings());
  }

  @Test
  @DisplayName("save -> load round-trip preserves every setting")
  void saveAndLoadShouldPreserveAllSettings() {
    Settings custom = new Settings();
    custom.setVolume(0.42);
    custom.setMuted(true);
    custom.setSfxVolume(0.73);
    custom.setSfxMuted(true);
    custom.setLanguage("French");

    SettingsProvider.saveSettings(custom);

    Settings loaded = SettingsProvider.loadSettings();

    assertReturnsValidSettings(loaded);

    assertEquals(custom.getVolume(), loaded.getVolume());
    assertEquals(custom.isMuted(), loaded.isMuted());
    assertEquals(custom.getSfxVolume(), loaded.getSfxVolume());
    assertEquals(custom.isSfxMuted(), loaded.isSfxMuted());
    assertEquals(custom.getLanguage(), loaded.getLanguage());
  }
}
