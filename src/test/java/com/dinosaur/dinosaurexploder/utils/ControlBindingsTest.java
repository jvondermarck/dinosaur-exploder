/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.utils;

import static org.junit.jupiter.api.Assertions.*;

import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.KeyTrigger;
import com.almasb.fxgl.input.UserAction;
import com.dinosaur.dinosaurexploder.model.ControlAction;
import com.dinosaur.dinosaurexploder.model.KeyBindings;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ControlBindingsTest {
  private final Input input = new Input();
  private final Map<ControlAction, AtomicInteger> calls = new EnumMap<>(ControlAction.class);

  private void register() {
    Map<ControlAction, Runnable> handlers = new EnumMap<>(ControlAction.class);
    for (ControlAction action : ControlAction.values()) {
      AtomicInteger count = new AtomicInteger();
      calls.put(action, count);
      handlers.put(action, count::incrementAndGet);
    }
    ControlBindings.register(input, KeyBindings.defaults(), handlers);
  }

  private void press(KeyCode key) {
    input.mockTriggerPress(new KeyTrigger(key));
    input.update(0.016);
    input.mockTriggerRelease(new KeyTrigger(key));
    input.update(0.016);
  }

  @Test
  void eachBindingInvokesOnlyItsOwnAction() {
    register();
    for (ControlAction action : ControlAction.values()) {
      press(action.defaultKey());
      assertEquals(1, calls.get(action).get());
    }
  }

  @Test
  void applyingACustomKeyDisablesTheOldKeyImmediately() {
    register();
    ControlBindings.apply(input, KeyBindings.defaults().withKey(ControlAction.SHOOT, KeyCode.J));

    press(ControlAction.SHOOT.defaultKey());
    assertEquals(0, calls.get(ControlAction.SHOOT).get());
    press(KeyCode.J);
    assertEquals(1, calls.get(ControlAction.SHOOT).get());
    press(ControlAction.BOMB.defaultKey());
    assertEquals(1, calls.get(ControlAction.BOMB).get());
  }

  @Test
  void swappedBindingsAndResetWorkWithoutTransientConflicts() {
    register();
    KeyBindings swapped =
        new KeyBindings(
            Map.of(
                ControlAction.SHOOT, ControlAction.BOMB.defaultKey(),
                ControlAction.BOMB, ControlAction.SHOOT.defaultKey(),
                ControlAction.SHIELD, ControlAction.SHIELD.defaultKey()));
    ControlBindings.apply(input, swapped);
    press(ControlAction.SHOOT.defaultKey());
    assertEquals(1, calls.get(ControlAction.BOMB).get());
    assertEquals(0, calls.get(ControlAction.SHOOT).get());

    ControlBindings.apply(input, KeyBindings.defaults());
    press(ControlAction.SHOOT.defaultKey());
    assertEquals(1, calls.get(ControlAction.SHOOT).get());
    assertEquals(1, calls.get(ControlAction.BOMB).get());
  }

  @Test
  void unrelatedBindingsArePreserved() {
    register();
    AtomicInteger moves = new AtomicInteger();
    input.addAction(
        new UserAction("move-up") {
          @Override
          protected void onActionBegin() {
            moves.incrementAndGet();
          }
        },
        KeyCode.W);
    ControlBindings.apply(input, KeyBindings.defaults().withKey(ControlAction.SHOOT, KeyCode.J));
    press(KeyCode.W);
    assertEquals(1, moves.get());
  }

  @Test
  void anUnexpectedConflictDoesNotPartiallyRebindControls() {
    register();
    input.addAction(new UserAction("another-action") {}, KeyCode.J);
    assertThrows(
        IllegalStateException.class,
        () ->
            ControlBindings.apply(
                input, KeyBindings.defaults().withKey(ControlAction.SHOOT, KeyCode.J)));
    press(ControlAction.SHOOT.defaultKey());
    assertEquals(1, calls.get(ControlAction.SHOOT).get());
  }

  @Test
  void aFailedSaveRestoresTheActualPreviousRuntimeBindings() throws Exception {
    register();
    ControlBindings.apply(input, KeyBindings.defaults().withKey(ControlAction.SHOOT, KeyCode.K));
    KeyBindings next = KeyBindings.defaults().withKey(ControlAction.SHOOT, KeyCode.J);
    try (var settings = Mockito.mockStatic(SettingsProvider.class)) {
      settings
          .when(() -> SettingsProvider.saveKeyBindings(next))
          .thenThrow(new IOException("Cannot write settings"));
      assertThrows(IOException.class, () -> ControlBindings.save(input, next));
    }
    press(KeyCode.J);
    assertEquals(0, calls.get(ControlAction.SHOOT).get());
    press(KeyCode.K);
    assertEquals(1, calls.get(ControlAction.SHOOT).get());
  }
}
