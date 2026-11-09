/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.utils;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jpro.webapi.JSVariable;
import com.jpro.webapi.WebAPI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javafx.application.Platform;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class BrowserAudioTest {
  private WebAPI webAPI;
  private BrowserAudio audio;
  private MockedStatic<Platform> platform;

  @BeforeEach
  void setUp() {
    platform = mockStatic(Platform.class);
    platform.when(Platform::isFxApplicationThread).thenReturn(true);
    webAPI = mock(WebAPI.class);
    JSVariable player = mock(JSVariable.class);
    when(player.getName()).thenReturn("browserAudio");
    when(webAPI.executeScriptWithVariable(anyString())).thenReturn(player);
    audio = new BrowserAudio(webAPI);
  }

  @AfterEach
  void tearDown() {
    platform.close();
  }

  @Test
  void initializesClientPlaybackAndAutoplayHandling() {
    verify(webAPI).executeScriptWithVariable(contains("NotAllowedError"));
  }

  @Test
  void dispatchesAudioCommandsFromFxglInitializationThread() {
    platform.when(Platform::isFxApplicationThread).thenReturn(false);
    platform
        .when(() -> Platform.runLater(any(Runnable.class)))
        .thenAnswer(
            invocation -> {
              platform.when(Platform::isFxApplicationThread).thenReturn(true);
              invocation.<Runnable>getArgument(0).run();
              return null;
            });

    audio.play("mainMenu.mp3", true, 0.5, false);

    platform.verify(() -> Platform.runLater(any(Runnable.class)));
    verify(webAPI, never()).createPublicFile(any(URL.class));
    verify(webAPI).executeScript(contains(".play("));
  }

  @Test
  void usesDurableStaticUrlsInsteadOfExpiringPublicationPermissions() {
    audio.play("mainMenu.mp3", true, 0.5, false);
    audio.play("mainMenu.mp3", false, 0.25, true);

    String encoded =
        Base64.getEncoder().encodeToString("/audio/mainMenu.mp3".getBytes(StandardCharsets.UTF_8));
    verify(webAPI, never()).createPublicFile(any(URL.class));
    verify(webAPI).executeScript("browserAudio.play(atob('" + encoded + "'),true,0.5,false);");
    verify(webAPI).executeScript("browserAudio.play(atob('" + encoded + "'),false,0.25,true);");
  }

  @Test
  void keepsMusicAndEffectsControlsSeparate() {
    audio.setMuted(true, true);
    audio.setMuted(false, false);
    audio.setVolume(true, 0.2);
    audio.setVolume(false, 0.7);

    verify(webAPI).executeScript("browserAudio.setMuted(true,true);");
    verify(webAPI).executeScript("browserAudio.setMuted(false,false);");
    verify(webAPI).executeScript("browserAudio.setVolume(true,0.2);");
    verify(webAPI).executeScript("browserAudio.setVolume(false,0.7);");
  }

  @Test
  void exposesExplicitMusicAndAllSoundsCleanup() {
    audio.stopMusic();
    audio.stopAll();

    verify(webAPI).executeScript("browserAudio.stopMusic();");
    verify(webAPI).executeScript("browserAudio.stopAll();");
  }

  @Test
  void reportsMissingResourcesInsteadOfCreatingSilentPlayers() {
    assertThrows(
        NullPointerException.class, () -> audio.play("missing-audio.mp3", false, 1, false));

    verify(webAPI, never()).createPublicFile(any(URL.class));
    verify(webAPI, never()).executeScript(anyString());
  }
}
