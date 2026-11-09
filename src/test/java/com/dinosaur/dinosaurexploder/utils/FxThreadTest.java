/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.utils;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.concurrent.CompletionException;
import javafx.application.Platform;
import org.junit.jupiter.api.Test;

class FxThreadTest {
  @Test
  void runsImmediatelyOnFxThread() {
    try (var platform = mockStatic(Platform.class)) {
      platform.when(Platform::isFxApplicationThread).thenReturn(true);
      Runnable action = mock(Runnable.class);

      FxThread.runAndWait(action);

      verify(action).run();
      platform.verify(() -> Platform.runLater(any()), never());
    }
  }

  @Test
  void waitsForDispatchedInitialization() {
    try (var platform = mockStatic(Platform.class)) {
      platform.when(Platform::isFxApplicationThread).thenReturn(false);
      platform
          .when(() -> Platform.runLater(any()))
          .thenAnswer(
              invocation -> {
                new Thread(invocation.<Runnable>getArgument(0)).start();
                return null;
              });
      Runnable action = mock(Runnable.class);

      FxThread.runAndWait(action);

      verify(action).run();
      platform.verify(() -> Platform.runLater(any()));
    }
  }

  @Test
  void propagatesDispatchedInitializationFailure() {
    try (var platform = mockStatic(Platform.class)) {
      platform.when(Platform::isFxApplicationThread).thenReturn(false);
      platform
          .when(() -> Platform.runLater(any()))
          .thenAnswer(
              invocation -> {
                invocation.<Runnable>getArgument(0).run();
                return null;
              });
      var failure = new IllegalStateException("Initialization failed");

      CompletionException thrown =
          assertThrows(
              CompletionException.class,
              () ->
                  FxThread.runAndWait(
                      () -> {
                        throw failure;
                      }));

      assertSame(failure, thrown.getCause());
    }
  }

  @Test
  void propagatesImmediateInitializationFailure() {
    try (var platform = mockStatic(Platform.class)) {
      platform.when(Platform::isFxApplicationThread).thenReturn(true);
      var failure = new IllegalStateException("Initialization failed");

      IllegalStateException thrown =
          assertThrows(
              IllegalStateException.class,
              () ->
                  FxThread.runAndWait(
                      () -> {
                        throw failure;
                      }));

      assertSame(failure, thrown);
    }
  }
}
