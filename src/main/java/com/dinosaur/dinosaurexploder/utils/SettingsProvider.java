/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.utils;

import com.dinosaur.dinosaurexploder.model.Settings;
import com.dinosaur.dinosaurexploder.persistence.FileSettingsFileProvider;
import com.dinosaur.dinosaurexploder.persistence.SettingsFileProvider;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SettingsProvider {

  public static final String SETTINGS_FILE = "settings.properties";
  public static final String SETTING_VOLUME = "soundVolume";
  public static final String SETTINGS_MUTED = "soundMuted";
  public static final String SETTING_VOLUME_SFX = "soundVolumeSfx";
  public static final String SETTINGS_MUTED_SFX = "soundMutedSfx";
  public static final String SETTINGS_LANGUAGE = "selectedLanguage";
  private static final Logger logger = Logger.getLogger(SettingsProvider.class.getName());

  private static final SettingsProvider DEFAULT = new SettingsProvider(new FileSettingsFileProvider(Path.of(SETTINGS_FILE)));

  private final SettingsFileProvider fileProvider;

  SettingsProvider(SettingsFileProvider fileProvider) {
    this.fileProvider = Objects.requireNonNull(fileProvider);
  }

  public static Settings loadSettings() {
    return DEFAULT.load();
  }

  Settings load() {
    Properties properties = fileProvider.load();

    if (!properties.isEmpty()) {
      try {
        return createSettingsFromProperties(properties);
      } catch (Exception e) {
        logger.log(Level.INFO, "Malformed settings, restoring defaults: {0}", e.getMessage());
        fileProvider.delete();
      }
    }

    return saveDefaultSettings();
  }

  public static void saveSettings(Settings settings) {
    DEFAULT.save(settings);
  }

  void save(Settings settings) {
    fileProvider.save(createPropertiesFromSettings(settings));
  }

  void updateMute(boolean muted) {
    Settings settings = load();
    settings.setMuted(muted);
    save(settings);
  }

  public static void updateMuteState(boolean muted) {
    DEFAULT.updateMute(muted);
  }

  private Settings saveDefaultSettings() {
    Settings defaults = generateDefaultSettings();
    save(defaults);
    return defaults;
  }

  private static Settings createSettingsFromProperties(Properties props) {
    Settings settings = new Settings();
    settings.setVolume(Double.parseDouble(props.getProperty(SETTING_VOLUME)));
    settings.setMuted(Boolean.parseBoolean(props.getProperty(SETTINGS_MUTED)));
    settings.setSfxVolume(Double.parseDouble(props.getProperty(SETTING_VOLUME_SFX)));
    settings.setSfxMuted(Boolean.parseBoolean(props.getProperty(SETTINGS_MUTED_SFX)));
    settings.setLanguage(props.getProperty(SETTINGS_LANGUAGE));

    return settings;
  }

  private static Properties createPropertiesFromSettings(Settings settings) {
    Properties properties = new Properties();
    properties.put(SETTING_VOLUME, String.valueOf(settings.getVolume()));
    properties.put(SETTINGS_MUTED, String.valueOf(settings.isMuted()));
    properties.put(SETTING_VOLUME_SFX, String.valueOf(settings.getSfxVolume()));
    properties.put(SETTINGS_MUTED_SFX, String.valueOf(settings.isSfxMuted()));
    properties.put(SETTINGS_LANGUAGE, settings.getLanguage());

    return properties;
  }

  private static Settings generateDefaultSettings() {
    Settings defaultSettings = new Settings();
    defaultSettings.setVolume(1.0);
    defaultSettings.setMuted(false);
    defaultSettings.setSfxVolume(1.0);
    defaultSettings.setSfxMuted(false);
    defaultSettings.setLanguage("English");

    return defaultSettings;
  }
}
