/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.utils;

import java.util.concurrent.CompletableFuture;
import javafx.application.Platform;

public final class FxThread {
  private FxThread() {}

  public static void runAndWait(Runnable action) {
    if (Platform.isFxApplicationThread()) {
      action.run();
    } else {
      // Preserve FXGL initialization order and propagate callback failures to its caller.
      CompletableFuture.runAsync(action, Platform::runLater).join();
    }
  }
}
