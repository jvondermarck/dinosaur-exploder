/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.utils;

import com.jpro.webapi.JSVariable;
import com.jpro.webapi.WebAPI;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

final class BrowserAudio {
  private final WebAPI webAPI;
  private final JSVariable player;

  BrowserAudio(WebAPI webAPI) {
    this.webAPI = Objects.requireNonNull(webAPI);
    try (var script =
        Objects.requireNonNull(
            getClass().getResourceAsStream("/jpro/browser-audio.js"),
            "Missing browser audio script")) {
      player =
          webAPI.executeScriptWithVariable(
              new String(script.readAllBytes(), StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new UncheckedIOException("Cannot load browser audio script", e);
    }
  }

  void play(String soundFile, boolean music, double volume, boolean muted) {
    Objects.requireNonNull(
        getClass().getResource("/jpro/html/audio/" + soundFile),
        "Missing public audio resource: " + soundFile);
    // createPublicFile permissions expire; HTML audio can request byte ranges much later.
    String url =
        "/audio/" + URLEncoder.encode(soundFile, StandardCharsets.UTF_8).replace("+", "%20");
    String encoded = Base64.getEncoder().encodeToString(url.getBytes(StandardCharsets.UTF_8));
    call("play(atob('" + encoded + "')," + music + "," + volume + "," + muted + ")");
  }

  void setMuted(boolean music, boolean muted) {
    call("setMuted(" + music + "," + muted + ")");
  }

  void setVolume(boolean music, double volume) {
    call("setVolume(" + music + "," + volume + ")");
  }

  void stopMusic() {
    call("stopMusic()");
  }

  void stopAll() {
    call("stopAll()");
  }

  private void call(String operation) {
    FxThread.runAndWait(() -> webAPI.executeScript(player.getName() + "." + operation + ";"));
  }
}
