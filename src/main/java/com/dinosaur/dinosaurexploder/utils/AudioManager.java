package com.dinosaur.dinosaurexploder.utils;

import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.dsl.FXGL;
import com.dinosaur.dinosaurexploder.model.VolumeControl;

public class AudioManager {
    private static AudioManager instance;
    private final VolumeControl musicVolume = new VolumeControl(1.0, false);
    private final VolumeControl soundVolume = new VolumeControl(1.0, false);

    private AudioManager() {}

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    public void setMuted(boolean muted) {
        if (this.musicVolume.isMuted() != muted) {
            this.musicVolume.setMuted(muted);
            updateMusicVolume();
        }
    }

    public boolean isMuted() {
        return this.musicVolume.isMuted();
    }

    public void setVolume(double volume) {
        if (this.musicVolume.getVolume() != volume) {
            this.musicVolume.setVolume(volume);
            updateMusicVolume();
        }
    }

    public double getVolume() {
        return this.musicVolume.getVolume();
    }

    public void setSoundVolume(double volume) {
        if (this.soundVolume.getVolume() != volume) {
            this.soundVolume.setVolume(volume);
            updateSoundVolume();
        }
    }

    public void setSoundMuted(boolean muted) {
        if (this.soundVolume.isMuted() != muted) {
            this.soundVolume.setMuted(muted);
            updateSoundVolume();
        }
    }

    private void updateSoundVolume() {
        double effectiveVolume = this.soundVolume.isMuted() ? 0.0 : this.soundVolume.getVolume();
        FXGL.getSettings().setGlobalSoundVolume(effectiveVolume);
    }

    private void updateMusicVolume() {
        double effectiveVolume = this.musicVolume.isMuted() ? 0.0 : this.musicVolume.getVolume();
        FXGL.getSettings().setGlobalMusicVolume(effectiveVolume);
    }

    public void playSound(String soundFile) {
        try {
            String resourcePath = "/assets/sounds/" + soundFile;
            var url = getClass().getResource(resourcePath);
            if (url == null) {
                System.err.println("Sound resource not found: " + resourcePath);
                return;
            }
            Sound sound = FXGL.getAssetLoader().loadSound(url);
            FXGL.getAudioPlayer().playSound(sound);
            FXGL.getSettings().setGlobalSoundVolume(this.soundVolume.isMuted() ? 0.0 : this.soundVolume.getVolume());
        } catch (Exception e) {
            System.err.println("Could not play sound: " + soundFile);
            e.printStackTrace();
        }
    }

    public void playMusic(String soundFile) {
        try {
            String resourcePath = "/assets/sounds/" + soundFile;
            var url = getClass().getResource(resourcePath);
            if (url == null) {
                System.err.println("Music resource not found: " + resourcePath);
                return;
            }
            Music music = FXGL.getAssetLoader().loadMusic(url);
            FXGL.getAudioPlayer().playMusic(music);
            FXGL.getSettings().setGlobalMusicVolume(this.musicVolume.isMuted() ? 0.0 : this.musicVolume.getVolume());
        } catch (Exception e) {
            System.err.println("Could not play music: " + soundFile);
            e.printStackTrace();
        }
    }

    public void stopAllMusic() {
        FXGL.getAudioPlayer().stopAllMusic();
    }

    public void stopAllSounds() {
        FXGL.getAudioPlayer().stopAllSounds();
    }
}