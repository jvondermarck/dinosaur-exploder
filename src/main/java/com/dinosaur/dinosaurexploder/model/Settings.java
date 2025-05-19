package com.dinosaur.dinosaurexploder.model;

public class Settings {

    private VolumeControl musicVolume;
    private VolumeControl soundVolume;
    private String language;

    public VolumeControl getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(VolumeControl musicVolume) {
        this.musicVolume = musicVolume;
    }

    public VolumeControl getSoundVolume() {
        return soundVolume;
    }

    public void setSoundVolume(VolumeControl soundVolume) {
        this.soundVolume = soundVolume;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
