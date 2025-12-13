package com.dinosaur.dinosaurexploder.utils;

import javafx.scene.media. Media;
import javafx.scene.media.MediaPlayer;
import java.util.Objects;

public class AudioWrapper {

    private MediaPlayer player;
    private final boolean enabled;

    public AudioWrapper(String resourcePath) {
        // Désactiver en mode web/JPro/Docker
        boolean isWeb = System.getProperty("jpro.web. mode") != null
                || System.getProperty("jpro.deployment") != null
                || System.getProperty("fxgl.isBrowser") != null;

        this.enabled = !isWeb;

        if (enabled) {
            try {
                Media media = new Media(
                        Objects.requireNonNull(getClass().getResource(resourcePath)).toExternalForm()
                );
                player = new MediaPlayer(media);
                System.out.println("✅ Audio créé:  " + resourcePath);
            } catch (Exception e) {
                System.err.println("⚠️ Erreur audio:  " + e.getMessage());
                player = null;
            }
        } else {
            System.out.println("🌐 Mode Web - Audio désactivé pour:  " + resourcePath);
            player = null;
        }
    }

    public void setMute(boolean mute) {
        if (enabled && player != null) {
            player.setMute(mute);
        }
    }

    public void setVolume(double volume) {
        if (enabled && player != null) {
            player.setVolume(volume);
        }
    }

    public void play() {
        if (enabled && player != null) {
            player.play();
        }
    }

    public void stop() {
        if (enabled && player != null) {
            player.stop();
        }
    }

    public void pause() {
        if (enabled && player != null) {
            player.pause();
        }
    }

    public boolean isEnabled() {
        return enabled && player != null;
    }
}