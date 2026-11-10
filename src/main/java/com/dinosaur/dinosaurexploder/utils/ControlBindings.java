/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.utils;

import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.KeyTrigger;
import com.almasb.fxgl.input.UserAction;
import com.dinosaur.dinosaurexploder.model.ControlAction;
import com.dinosaur.dinosaurexploder.model.KeyBindings;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javafx.scene.input.KeyCode;

public final class ControlBindings {
  private static final KeyCode[] STAGING_KEYS = {KeyCode.F22, KeyCode.F23, KeyCode.F24};

  private ControlBindings() {}

  public static void save(Input input, KeyBindings bindings) throws IOException {
    Map<ControlAction, KeyCode> previous = new EnumMap<>(ControlAction.class);
    for (ControlAction action : ControlAction.values()) {
      var trigger = input.getAllBindings().get(input.getActionByName(action.inputActionName()));
      if (!(trigger instanceof KeyTrigger keyTrigger)) {
        throw new IllegalStateException("Expected a keyboard binding for " + action);
      }
      previous.put(action, keyTrigger.getKey());
    }
    apply(input, bindings);
    try {
      SettingsProvider.saveKeyBindings(bindings);
    } catch (IOException | IllegalArgumentException ex) {
      apply(input, new KeyBindings(previous));
      throw ex;
    }
  }

  public static void register(
      Input input, KeyBindings bindings, Map<ControlAction, Runnable> handlers) {
    for (ControlAction action : ControlAction.values()) {
      Runnable handler = handlers.get(action);
      if (handler == null) {
        throw new IllegalArgumentException("Missing handler for " + action);
      }
      input.addAction(
          new UserAction(action.inputActionName()) {
            @Override
            protected void onActionBegin() {
              handler.run();
            }
          },
          bindings.keyFor(action));
    }
  }

  public static void apply(Input input, KeyBindings bindings) {
    Map<ControlAction, UserAction> actions = new EnumMap<>(ControlAction.class);
    for (ControlAction action : ControlAction.values()) {
      UserAction inputAction = input.getActionByName(action.inputActionName());
      actions.put(action, inputAction);
    }

    Set<KeyCode> needed = new HashSet<>(java.util.List.of(STAGING_KEYS));
    for (ControlAction action : ControlAction.values()) {
      needed.add(bindings.keyFor(action));
    }
    input
        .getAllBindings()
        .forEach((action, trigger) -> {
          if (!actions.containsValue(action)
              && trigger instanceof KeyTrigger keyTrigger
              && needed.contains(keyTrigger.getKey())) {
            throw new IllegalStateException("Key is already used by " + action.getName());
          }
        });

    // Free all three old keys first, so Apply and Reset also support key swaps.
    for (ControlAction action : ControlAction.values()) {
      rebind(input, actions.get(action), STAGING_KEYS[action.ordinal()]);
    }
    for (ControlAction action : ControlAction.values()) {
      rebind(input, actions.get(action), bindings.keyFor(action));
    }
  }

  private static void rebind(Input input, UserAction action, KeyCode key) {
    if (!input.rebind(action, key)) {
      throw new IllegalStateException("Cannot bind " + action.getName() + " to " + key);
    }
  }
}
