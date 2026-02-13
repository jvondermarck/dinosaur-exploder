/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.utils;

import javafx.util.Duration;

public interface GameTimer {
  void capture();

  boolean isElapsed(Duration duration);
}
