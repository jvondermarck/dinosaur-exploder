/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Test;

class KeyBindingsTest {
  @Test
  void defaultsUseTheCentralActionDefaults() {
    KeyBindings bindings = KeyBindings.defaults();
    assertEquals(KeyCode.SPACE, bindings.keyFor(ControlAction.SHOOT));
    assertEquals(KeyCode.X, bindings.keyFor(ControlAction.BOMB));
    assertEquals(KeyCode.C, bindings.keyFor(ControlAction.SHIELD));
    for (ControlAction action : ControlAction.values()) {
      assertEquals(action.defaultKey(), bindings.keyFor(action));
    }
  }

  @Test
  void changingAKeyDoesNotMutateTheOriginal() {
    KeyBindings original = KeyBindings.defaults();
    KeyBindings changed = original.withKey(ControlAction.SHOOT, KeyCode.J);

    assertEquals(ControlAction.SHOOT.defaultKey(), original.keyFor(ControlAction.SHOOT));
    assertEquals(KeyCode.J, changed.keyFor(ControlAction.SHOOT));
    assertEquals(original.keyFor(ControlAction.BOMB), changed.keyFor(ControlAction.BOMB));
  }

  @Test
  void duplicateKeysAreRejected() {
    KeyBindings bindings = KeyBindings.defaults();
    IllegalArgumentException error =
        assertThrows(
            IllegalArgumentException.class,
            () -> bindings.withKey(ControlAction.SHOOT, bindings.keyFor(ControlAction.BOMB)));
    assertEquals("controls_duplicate_key", error.getMessage());
  }

  @Test
  void movementMenuAndModifierKeysAreReserved() {
    for (KeyCode key :
        new KeyCode[] {
          KeyCode.W,
          KeyCode.A,
          KeyCode.S,
          KeyCode.D,
          KeyCode.UP,
          KeyCode.DOWN,
          KeyCode.LEFT,
          KeyCode.RIGHT,
          KeyCode.ESCAPE,
          KeyCode.TAB,
          KeyCode.ENTER,
          KeyCode.SHIFT,
          KeyCode.CONTROL,
          KeyCode.ALT,
          KeyCode.META,
          KeyCode.F22,
          KeyCode.UNDEFINED
        }) {
      assertFalse(KeyBindings.isAllowed(key), key.name());
      assertThrows(
          IllegalArgumentException.class,
          () -> KeyBindings.defaults().withKey(ControlAction.SHOOT, key));
    }
    assertFalse(KeyBindings.isAllowed(null));
  }

  @Test
  void lettersNumbersAndPunctuationCanBeUsed() {
    for (KeyCode key : new KeyCode[] {KeyCode.J, KeyCode.DIGIT1, KeyCode.COMMA, KeyCode.SPACE}) {
      assertTrue(KeyBindings.isAllowed(key), key.name());
    }
  }

  @Test
  void incompleteBindingsAreRejected() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new KeyBindings(Map.of(ControlAction.SHOOT, KeyCode.J)));
  }
}
