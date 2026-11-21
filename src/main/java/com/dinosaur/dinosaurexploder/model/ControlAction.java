/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.model;

import javafx.scene.input.KeyCode;

public enum ControlAction {
  SHOOT(KeyCode.SPACE),
  BOMB(KeyCode.X),
  SHIELD(KeyCode.C);

  private final KeyCode defaultKey;

  ControlAction(KeyCode defaultKey) {
    this.defaultKey = defaultKey;
  }

  public KeyCode defaultKey() {
    return defaultKey;
  }

  public String translationKey() {
    return name().toLowerCase(java.util.Locale.ROOT);
  }

  public String inputActionName() {
    return "custom-controls-" + translationKey();
  }
}
