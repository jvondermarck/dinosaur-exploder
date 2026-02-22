/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.constants;

import static com.almasb.fxgl.core.math.FXGLMath.random;

public enum Direction {
  UP,
  DOWN,
  LEFT,
  RIGHT;

  public static Direction randomDirection() {
    Direction[] values = Direction.values();
    int index = random(0, values.length - 1);
    return values[index];
  }
}
