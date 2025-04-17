package com.dinosaur.dinosaurexploder.controller;

import com.dinosaur.dinosaurexploder.model.Settings;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Properties;

public class SettingsController {

    public static final String SETTINGS_FILE = "settings.properties";
    public static final String SETTING_VOLUME = "soundVolume";
    public static final String SETTINGS_MUTED = "soundMuted";

    public static Settings loadSettings() {
        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream(SETTINGS_FILE));
        }catch(Exception ex){
            System.err.println("Error loading settings file " + ex.getMessage());
            return generateDefaultSettings();
        }

        return createSettingsFromProperties(properties);
    }

    public static void saveSettings(Settings settings) {
        Properties properties = new Properties();
        properties.put(SETTING_VOLUME, String.valueOf(settings.getVolume()));
        properties.put(SETTINGS_MUTED, String.valueOf(settings.isMuted()));

        try {
            properties.store(new FileWriter(SETTINGS_FILE), "store properties");
        }catch(Exception ex){
            System.err.println("Error saving settings " + ex.getMessage());
        }
    }

    private static Settings createSettingsFromProperties(Properties props){
        Settings settings = new Settings();
        settings.setVolume(Double.parseDouble(props.getProperty(SETTING_VOLUME)));
        settings.setMuted(Boolean.parseBoolean(props.getProperty(SETTINGS_MUTED)));

        return settings;
    }

    private static Settings generateDefaultSettings(){
        Settings defaultSettings = new Settings();
        defaultSettings.setVolume(1.0);
        defaultSettings.setMuted(false);

        return defaultSettings;
    }

}
