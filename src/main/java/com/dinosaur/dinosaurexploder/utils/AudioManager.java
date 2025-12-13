package com.dinosaur.dinosaurexploder.utils;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class AudioManager {
  private static AudioManager instance;
  private boolean isMuted = false;
  private double volume = 1.0;
  private final List<MediaPlayer> activePlayers = new ArrayList<>();
  private MediaPlayer backgroundPlayer;

  // Détecter si on est en mode web
  private static final boolean IS_WEB =
          System.getProperty("jpro. web.mode") != null ||
                  System.getProperty("jpro.deployment") != null ||
                  System.getProperty("fxgl.isBrowser") != null;

  private AudioManager() {
    if (IS_WEB) {
      System.out.println("🌐 AudioManager en mode Web - Audio désactivé");
    }
  }

  public static AudioManager getInstance() {
    if (instance == null) {
      instance = new AudioManager();
    }
    return instance;
  }

  public void setMuted(boolean muted) {
    this.isMuted = muted;
    if (! IS_WEB && backgroundPlayer != null) {
      backgroundPlayer. setMute(muted);
    }
    // Mute/unmute all active sound effects
    if (!IS_WEB) {
      for (MediaPlayer player : activePlayers) {
        player.setMute(muted);
      }
    }
  }

  public boolean isMuted() {
    return isMuted;
  }

  public void setVolume(double volume) {
    this.volume = volume;
    if (!IS_WEB && backgroundPlayer != null) {
      backgroundPlayer.setVolume(volume);
    }
    // Update volume for all currently playing sound effects
    if (!IS_WEB) {
      for (MediaPlayer player : activePlayers) {
        player.setVolume(volume);
      }
    }
  }

  public double getVolume() {
    return volume;
  }

  public void playSound(String soundFile) {
    if (IS_WEB) {
      // Mode web: ne rien faire, pas d'erreur
      return;
    }

    if (isMuted) return;

    try {
      String resourcePath = "/assets/sounds/" + soundFile;
      var url = getClass().getResource(resourcePath);
      if (url == null) {
        System.err.println("Sound resource not found: " + resourcePath);
        return;
      }
      MediaPlayer player = new MediaPlayer(new Media(url.toExternalForm()));
      player.setVolume(volume);
      player.setMute(isMuted);
      player.play();
      activePlayers.add(player);
      player.setOnEndOfMedia(
              () -> {
                player.dispose();
                activePlayers.remove(player);
              });
    } catch (Exception e) {
      System.err.println("Could not play sound: " + soundFile);
      // Ne pas printStackTrace en production
      // e.printStackTrace();
    }
  }

  public void playMusic(String soundFile) {
    if (IS_WEB) {
      // Mode web: ne rien faire, pas d'erreur
      return;
    }

    stopMusic();

    try {
      String resourcePath = "/assets/sounds/" + soundFile;
      var url = getClass().getResource(resourcePath);
      if (url == null) {
        System.err.println("Music resource not found: " + resourcePath);
        return;
      }
      backgroundPlayer = new MediaPlayer(new Media(url. toExternalForm()));
      backgroundPlayer.setMute(isMuted);
      backgroundPlayer.setVolume(volume);
      backgroundPlayer.setCycleCount(MediaPlayer.INDEFINITE);
      backgroundPlayer.play();
    } catch (Exception e) {
      System.err.println("Could not play music: " + soundFile);
      // Ne pas printStackTrace en production
      // e.printStackTrace();
    }
  }

  public void stopMusic() {
    if (! IS_WEB && backgroundPlayer != null) {
      backgroundPlayer. stop();
      backgroundPlayer.dispose();
      backgroundPlayer = null;
    }
  }

  public void stopAllSounds() {
    stopMusic();
    if (!IS_WEB) {
      for (MediaPlayer player : new ArrayList<>(activePlayers)) {
        player.stop();
        player.dispose();
      }
      activePlayers.clear();
    }
  }
}