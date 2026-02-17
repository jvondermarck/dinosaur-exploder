/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.utils;

import static com.almasb.fxgl.dsl.FXGL.getAppHeight;
import static com.almasb.fxgl.dsl.FXGL.getAppWidth;

import javafx.scene.text.Text;

public class TextUtils {
  /** Summary : Center the text on the screen */
  public static void centerText(Text text) {
    text.setX((getAppWidth() - text.getLayoutBounds().getWidth()) / 2.0);
    text.setY(getAppHeight() / 2.0);
  }
}
