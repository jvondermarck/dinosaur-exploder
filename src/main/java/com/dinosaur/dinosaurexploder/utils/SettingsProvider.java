package com.dinosaur.dinosaurexploder.utils;

import com.dinosaur.dinosaurexploder.model.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Properties;

public class SettingsProvider {

  public static final String SETTINGS_FILE = "settings.properties";
  public static final String SETTING_VOLUME = "soundVolume";
  public static final String SETTINGS_MUTED = "soundMuted";
  public static final String SETTING_VOLUME_SFX = "soundVolumeSfx";
  public static final String SETTINGS_MUTED_SFX = "soundMutedSfx";
  public static final String SETTINGS_LANGUAGE = "selectedLanguage";

  public static Settings loadSettings() {
    Properties properties = new Properties();

    try {
      FileInputStream in = new FileInputStream(SETTINGS_FILE);
      properties.load(in);
      in.close();
    } catch (Exception ex) {
      Settings defaultSettings = generateDefaultSettings();
      saveSettings(defaultSettings);
      return defaultSettings;
    }

    try { // handling missing properties from settings.properties file
        return createSettingsFromProperties(properties);
    } catch (Exception e) {
        File file = new File(SETTINGS_FILE);
        if(file.delete()) loadSettings();
    }
    return null;
  }

  public static void saveSettings(Settings settings) {
    Properties properties = createPropertiesFormSettings(settings);

    try {
      properties.store(new FileWriter(SETTINGS_FILE), "store properties");
    } catch (Exception ex) {
      System.err.println("Error saving settings " + ex.getMessage());
    }
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

  private static Properties createPropertiesFormSettings(Settings settings) {
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
