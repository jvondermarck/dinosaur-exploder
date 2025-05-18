package com.dinosaur.dinosaurexploder.utils;

import com.dinosaur.dinosaurexploder.model.Settings;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Properties;

public class SettingsProvider {

    public static final String SETTINGS_FILE = "settings.properties";
    public static final String SETTING_VOLUME = "soundVolume";
    public static final String SETTINGS_MUTED = "soundMuted";
    public static final String SETTING_VOLUME_VFX = "soundVolumeVFX";
    public static final String SETTINGS_MUTED_VFX= "soundMutedVFX";
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

        return createSettingsFromProperties(properties);
    }

    public static void saveSettings(Settings settings) {
        Properties properties = createPropertiesFormSettings(settings);

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
        settings.setVolumeVFX(Double.parseDouble(props.getProperty(SETTING_VOLUME_VFX)));
        settings.setVfxMuted(Boolean.parseBoolean(props.getProperty(SETTINGS_MUTED_VFX)));
        settings.setLanguage(props.getProperty(SETTINGS_LANGUAGE));

        return settings;
    }

    private static Properties createPropertiesFormSettings(Settings settings){
        Properties properties = new Properties();
        properties.put(SETTING_VOLUME, String.valueOf(settings.getVolume()));
        properties.put(SETTINGS_MUTED, String.valueOf(settings.isMuted()));
        properties.put(SETTING_VOLUME_VFX, String.valueOf(settings.getVolumeVFX()));
        properties.put(SETTINGS_MUTED_VFX, String.valueOf(settings.isVfxMuted()));
        properties.put(SETTINGS_LANGUAGE, settings.getLanguage());

        return properties;
    }

    private static Settings generateDefaultSettings(){
        Settings defaultSettings = new Settings();
        defaultSettings.setVolume(1.0);
        defaultSettings.setMuted(false);
        defaultSettings.setVolumeVFX(1.0);
        defaultSettings.setVfxMuted(true);
        defaultSettings.setLanguage("English");

        return defaultSettings;
    }

}
