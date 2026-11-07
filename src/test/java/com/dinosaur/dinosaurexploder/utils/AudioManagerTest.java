/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import com.dinosaur.dinosaurexploder.model.Settings;
import com.jpro.webapi.WebAPI;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.junit.jupiter.api.Test;

class AudioManagerTest {
  private AudioManager newManager() throws ReflectiveOperationException {
    Settings settings = new Settings();
    settings.setVolume(0.5);
    try (var provider = mockStatic(SettingsProvider.class)) {
      provider.when(SettingsProvider::loadSettings).thenReturn(settings);
      var constructor = AudioManager.class.getDeclaredConstructor();
      constructor.setAccessible(true);
      return constructor.newInstance();
    }
  }

  @Test
  void browserPlaybackNeverConstructsNativeMediaPlayers() throws ReflectiveOperationException {
    try (var browser = mockConstruction(BrowserAudio.class);
        var nativePlayers = mockConstruction(MediaPlayer.class)) {
      AudioManager manager = newManager();
      manager.useBrowserAudio(mock(WebAPI.class));

      manager.playMusic("mainMenu.mp3");
      manager.playSound("mainMenu.wav");

      BrowserAudio audio = browser.constructed().getFirst();
      verify(audio).play("mainMenu.mp3", true, 0.5, false);
      verify(audio).play("mainMenu.mp3", false, 1.0, false);
      assertEquals(0, nativePlayers.constructed().size());
    }
  }

  @Test
  void browserControlsAndCleanupReachClient() throws ReflectiveOperationException {
    try (var browser = mockConstruction(BrowserAudio.class)) {
      AudioManager manager = newManager();
      manager.useBrowserAudio(mock(WebAPI.class));
      BrowserAudio audio = browser.constructed().getFirst();

      manager.setMuted(true);
      manager.setSfxMuted(true);
      manager.setVolume(0.2);
      manager.setSfxVolume(0.7);
      manager.stopMusic();
      manager.stopAllSounds();

      verify(audio).setMuted(true, true);
      verify(audio).setMuted(false, true);
      verify(audio).setVolume(true, 0.2);
      verify(audio).setVolume(false, 0.7);
      verify(audio).stopMusic();
      verify(audio).stopAll();
    }
  }

  @Test
  void desktopStillUsesAndDisposesJavaFxMedia() throws ReflectiveOperationException {
    try (var media = mockConstruction(Media.class);
        var players = mockConstruction(MediaPlayer.class)) {
      AudioManager manager = newManager();

      manager.playMusic("mainMenu.mp3");
      MediaPlayer music = players.constructed().getFirst();
      verify(music).setVolume(0.5);
      verify(music).setCycleCount(MediaPlayer.INDEFINITE);
      verify(music).play();
      manager.playSound("mainMenu.wav");
      MediaPlayer effect = players.constructed().get(1);
      verify(effect).play();
      manager.stopAllSounds();

      verify(music).stop();
      verify(music).dispose();
      verify(effect).stop();
      verify(effect).dispose();
      assertEquals(2, media.constructed().size());
    }
  }
}
