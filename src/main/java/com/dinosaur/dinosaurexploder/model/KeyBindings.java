/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.model;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javafx.scene.input.KeyCode;

/** Immutable keyboard bindings for the three customizable gameplay actions. */
public final class KeyBindings {
  private static final Set<KeyCode> MOVEMENT_KEYS =
      Set.of(KeyCode.W, KeyCode.A, KeyCode.S, KeyCode.D);
  private static final Set<KeyCode> SYMBOL_KEYS =
      Set.of(
          KeyCode.SPACE,
          KeyCode.COMMA,
          KeyCode.PERIOD,
          KeyCode.SLASH,
          KeyCode.BACK_SLASH,
          KeyCode.SEMICOLON,
          KeyCode.QUOTE,
          KeyCode.OPEN_BRACKET,
          KeyCode.CLOSE_BRACKET,
          KeyCode.MINUS,
          KeyCode.EQUALS,
          KeyCode.BACK_QUOTE);

  private final Map<ControlAction, KeyCode> keys;

  public KeyBindings(Map<ControlAction, KeyCode> keys) {
    Set<KeyCode> used = new HashSet<>();
    for (ControlAction action : ControlAction.values()) {
      KeyCode key = keys.get(action);
      if (!isAllowed(key)) {
        throw new IllegalArgumentException("controls_reserved_key");
      }
      if (!used.add(key)) {
        throw new IllegalArgumentException("controls_duplicate_key");
      }
    }
    this.keys = Map.copyOf(keys);
  }

  public static KeyBindings defaults() {
    Map<ControlAction, KeyCode> keys = new EnumMap<>(ControlAction.class);
    for (ControlAction action : ControlAction.values()) {
      keys.put(action, action.defaultKey());
    }
    return new KeyBindings(keys);
  }

  public KeyCode keyFor(ControlAction action) {
    return keys.get(action);
  }

  public KeyBindings withKey(ControlAction action, KeyCode key) {
    Map<ControlAction, KeyCode> updated = new EnumMap<>(keys);
    updated.put(action, key);
    return new KeyBindings(updated);
  }

  public static boolean isAllowed(KeyCode key) {
    return key != null
        && !MOVEMENT_KEYS.contains(key)
        && (key.isLetterKey() || key.isDigitKey() || SYMBOL_KEYS.contains(key));
  }
}
