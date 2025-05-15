package com.dinosaur.dinosaurexploder.utils;

import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.dsl.FXGL;
import com.dinosaur.dinosaurexploder.model.VolumeControl;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.util.ArrayList;
import java.util.List;

public class AudioManager {
    private static AudioManager instance;
    private final VolumeControl musicVolume = new VolumeControl(1.0, false);
    private final VolumeControl soundVolume = new VolumeControl(1.0, false);
    private final List<MediaPlayer> activePlayers = new ArrayList<>();
    private MediaPlayer backgroundPlayer;

    private AudioManager() {}

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    public void setMuted(boolean muted) {
        this.musicVolume.setMuted(muted);
        if (backgroundPlayer != null) backgroundPlayer.setMute(muted);
        // Mute/unmute all active sound effects
        for (MediaPlayer player : activePlayers) {
            player.setMute(muted);
        }
    }

    public boolean isMuted() {
        return this.musicVolume.isMuted();
    }

    public void setVolume(double volume) {
        this.musicVolume.setVolume(volume);
        if (backgroundPlayer != null) backgroundPlayer.setVolume(volume);
        // Update volume for all currently playing sound effects
        for (MediaPlayer player : activePlayers) {
            player.setVolume(volume);
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
        stopMusic();
        try {
            String resourcePath = "/assets/sounds/" + soundFile;
            var url = getClass().getResource(resourcePath);
            if (url == null) {
                System.err.println("Music resource not found: " + resourcePath);
                return;
            }
            backgroundPlayer = new MediaPlayer(new Media(url.toExternalForm()));
            backgroundPlayer.setMute(this.musicVolume.isMuted());
            backgroundPlayer.setVolume(this.musicVolume.getVolume());
            backgroundPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            backgroundPlayer.play();
        } catch (Exception e) {
            System.err.println("Could not play music: " + soundFile);
            e.printStackTrace();
        }
    }

    public void stopMusic() {
        if (backgroundPlayer != null) {
            backgroundPlayer.stop();
            backgroundPlayer.dispose();
            backgroundPlayer = null;
        }
    }

    public void stopAllSounds() {
        stopMusic();
        for (MediaPlayer player : new ArrayList<>(activePlayers)) {
            player.stop();
            player.dispose();
        }
        activePlayers.clear();
    }
}