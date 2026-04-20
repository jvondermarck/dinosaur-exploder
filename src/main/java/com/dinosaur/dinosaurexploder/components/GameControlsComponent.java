/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.components;

import com.dinosaur.dinosaurexploder.constants.GameMode;
import com.dinosaur.dinosaurexploder.model.GameData;
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
    final String translationKey;

    ControlMapping(String translationKey) {
      this.translationKey = translationKey;
    }

    String getDisplayText(
        ControlType controlType, LanguageManager languageManager, DisplayMode mode) {
      String text =
          getKeys(controlType)
              + ": "
              + formatActionLabel(languageManager.getTranslation(translationKey));

      return mode == DisplayMode.DIALOG ? text + "\n" : text;
    }

    private static String getKeys(ControlType controlType) {
      return switch (controlType) {
        case MOVE_UP -> "↑ / W";
        case MOVE_DOWN -> "↓ / S";
        case MOVE_LEFT -> "← / A";
        case MOVE_RIGHT -> "→ / D";
        case PAUSE_GAME -> "ESC";
        case SHOOT -> isExpertMode() ? "Space / Left Click" : "Space";
        case BOMB -> "B";
        case SHIELD -> "E";
      };
    }
  }

  // Master control mappings

  private static final Map<ControlType, ControlMapping> CONTROLS = new LinkedHashMap<>();

  static {
    CONTROLS.put(ControlType.MOVE_UP, new ControlMapping("move_up"));
    CONTROLS.put(ControlType.MOVE_DOWN, new ControlMapping("move_down"));
    CONTROLS.put(ControlType.MOVE_LEFT, new ControlMapping("move_left"));
    CONTROLS.put(ControlType.MOVE_RIGHT, new ControlMapping("move_right"));
    CONTROLS.put(ControlType.PAUSE_GAME, new ControlMapping("pause_game"));
    CONTROLS.put(ControlType.SHOOT, new ControlMapping("shoot"));
    CONTROLS.put(ControlType.BOMB, new ControlMapping("bomb"));
    CONTROLS.put(ControlType.SHIELD, new ControlMapping("shield"));
  }

  private static boolean isExpertMode() {
    return GameData.getSelectedDifficulty() == GameMode.EXPERT;
  }

  static String formatActionLabel(String translation) {
    int separatorIndex = translation.indexOf(':');
    return separatorIndex >= 0 ? translation.substring(separatorIndex + 1).trim() : translation;
  }

  /**
   * Generates formatted controls text for dialogs.
   *
   * @return Formatted controls text with newlines
   */
  public static String generateControlsDialogText() {
    LanguageManager languageManager = LanguageManager.getInstance();

    StringBuilder controlsText = new StringBuilder();

    for (Map.Entry<ControlType, ControlMapping> entry : CONTROLS.entrySet()) {
      controlsText.append(
          entry.getValue().getDisplayText(entry.getKey(), languageManager, DisplayMode.DIALOG));
    }

    // Remove trailing newline
    if (!controlsText.isEmpty()) {
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
    return mapping.getDisplayText(
        controlType, LanguageManager.getInstance(), DisplayMode.BUTTON_LIST);
  }

  /**
   * Gets all control texts in order.
   *
   * @return Array of control texts for buttons
   */
  public static String[] getAllControlTexts() {
    LanguageManager languageManager = LanguageManager.getInstance();
    return CONTROLS.entrySet().stream()
        .map(
            entry ->
                entry
                    .getValue()
                    .getDisplayText(entry.getKey(), languageManager, DisplayMode.BUTTON_LIST))
        .toArray(String[]::new);
  }
}
