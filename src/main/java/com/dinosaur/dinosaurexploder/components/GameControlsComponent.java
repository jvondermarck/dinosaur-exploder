/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */
package com.dinosaur.dinosaurexploder.components;

import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Reusable component for managing game controls display. Eliminates duplication between
 * SettingsMenu and PauseMenu.
 */
public class GameControlsComponent {

  /** Control types in the game */
  public enum ControlType {
    MOVE_UP,
    MOVE_DOWN,
    MOVE_RIGHT,
    MOVE_LEFT,
    PAUSE_GAME,
    SHOOT,
    BOMB,
    SHIELD
  }

  /** Display modes for controls */
  public enum DisplayMode {
    DIALOG, // for SettingsMenu dialog (with \n)
    BUTTON_LIST // for PauseMenu options buttons (no \n)
  }

  /** Inner class to represent a control mapping */
  private static class ControlMapping {
    final String keys;
    final String translationKey;

    ControlMapping(String keys, String translationKey) {
      this.keys = keys;
      this.translationKey = translationKey;
    }

    String getDisplayText(LanguageManager languageManager, DisplayMode mode) {
      String translation = languageManager.getTranslation(translationKey);

      String text = keys + ": " + translation;

      return mode == DisplayMode.DIALOG ? text + "\n" : text;
    }
  }

  // Master control mappings

  private static final Map<ControlType, ControlMapping> CONTROLS = new LinkedHashMap<>();

  static {
    CONTROLS.put(ControlType.MOVE_UP, new ControlMapping("↑ / W", "move_up"));
    CONTROLS.put(ControlType.MOVE_DOWN, new ControlMapping("↓ / S", "move_down"));
    CONTROLS.put(ControlType.MOVE_LEFT, new ControlMapping("← / A", "move_left"));
    CONTROLS.put(ControlType.MOVE_RIGHT, new ControlMapping("→ / D", "move_right"));
    CONTROLS.put(ControlType.PAUSE_GAME, new ControlMapping("ESC", "pause_game"));
    CONTROLS.put(ControlType.SHOOT, new ControlMapping("Space", "shoot"));
    CONTROLS.put(ControlType.BOMB, new ControlMapping("B", "bomb"));
    CONTROLS.put(ControlType.SHIELD, new ControlMapping("E", "shield"));
  }

  /**
   * Generates formatted controls text for dialogs.
   *
   * @return Formatted controls text with newlines
   */
  public static String generateControlsDialogText() {
    LanguageManager languageManager = LanguageManager.getInstance();

    StringBuilder controlsText = new StringBuilder();

    for (ControlMapping mapping : CONTROLS.values()) {
      controlsText.append(mapping.getDisplayText(languageManager, DisplayMode.DIALOG));
    }

    // Remove trailing newline
    if (controlsText.length() > 0) {
      controlsText.setLength(controlsText.length() - 1);
    }

    return controlsText.toString();
  }

  /**
   * Gets the display text for a specific control type.
   *
   * @param controlType The control to get text for
   * @return Formatted control text (no newline)
   */
  public static String getControlText(ControlType controlType) {
    ControlMapping mapping = CONTROLS.get(controlType);
    if (mapping == null) {
      return "";
    }
    return mapping.getDisplayText(LanguageManager.getInstance(), DisplayMode.BUTTON_LIST);
  }

  /**
   * Gets all control texts in order.
   *
   * @return Array of control texts for buttons
   */
  public static String[] getAllControlTexts() {
    LanguageManager languageManager = LanguageManager.getInstance();
    return CONTROLS.values().stream()
        .map(mapping -> mapping.getDisplayText(languageManager, DisplayMode.BUTTON_LIST))
        .toArray(String[]::new);
  }
}
