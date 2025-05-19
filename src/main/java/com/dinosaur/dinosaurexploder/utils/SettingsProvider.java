package com.dinosaur.dinosaurexploder.utils;

import com.dinosaur.dinosaurexploder.model.Settings;
import com.dinosaur.dinosaurexploder.model.VolumeControl;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Properties;

public class SettingsProvider {

    public static final String SETTINGS_FILE = "settings.properties";
    public static final String SETTING_MUSIC_VOLUME = "musicVolume";
    public static final String SETTINGS_MUSIC_MUTED = "musicMuted";
    public static final String SETTING_SOUND_VOLUME = "soundVolume";
    public static final String SETTINGS_SOUND_MUTED = "soundMuted";
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

        VolumeControl musicControl = new VolumeControl(Double.parseDouble(props.getProperty(SETTING_MUSIC_VOLUME)), Boolean.parseBoolean(props.getProperty(SETTINGS_MUSIC_MUTED)));
        VolumeControl soundControl = new VolumeControl(Double.parseDouble(props.getProperty(SETTING_SOUND_VOLUME)), Boolean.parseBoolean(props.getProperty(SETTINGS_SOUND_MUTED)));
        settings.setMusicVolume(musicControl);
        settings.setSoundVolume(soundControl);
        settings.setLanguage(props.getProperty(SETTINGS_LANGUAGE));

        return settings;
    }

    private static Properties createPropertiesFormSettings(Settings settings){
        Properties properties = new Properties();
        properties.put(SETTING_MUSIC_VOLUME, String.valueOf(settings.getMusicVolume().getVolume()));
        properties.put(SETTINGS_MUSIC_MUTED, String.valueOf(settings.getMusicVolume().isMuted()));
        properties.put(SETTING_SOUND_VOLUME, String.valueOf(settings.getSoundVolume().getVolume()));
        properties.put(SETTINGS_SOUND_MUTED, String.valueOf(settings.getSoundVolume().isMuted()));
        properties.put(SETTINGS_LANGUAGE, settings.getLanguage());

        return properties;
    }

    private static Settings generateDefaultSettings(){
        Settings defaultSettings = new Settings();
        defaultSettings.setMusicVolume(new VolumeControl());
        defaultSettings.setSoundVolume(new VolumeControl());
        defaultSettings.setLanguage("English");

        return defaultSettings;
    }

}
