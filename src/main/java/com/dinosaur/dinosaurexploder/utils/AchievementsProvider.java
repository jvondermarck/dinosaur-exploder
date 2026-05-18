/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class AchievementsProvider {

  public static final String ACHIEVEMENT_PREFIX = "achievement.";

  public static Set<String> loadCompletedAchievements() {
    return loadCompletedAchievements(new File(SettingsProvider.SETTINGS_FILE));
  }

  static Set<String> loadCompletedAchievements(File settingsFile) {
    Properties properties = new Properties();
    Set<String> completed = new HashSet<>();

    if (settingsFile.exists()) {
      try (FileInputStream in = new FileInputStream(settingsFile)) {
        properties.load(in);
      } catch (Exception ex) {
        System.err.println("Error loading achievements from settings: " + ex.getMessage());
      }
    }

    for (String name : properties.stringPropertyNames()) {
      if (name.startsWith(ACHIEVEMENT_PREFIX)
          && Boolean.parseBoolean(properties.getProperty(name))) {
        completed.add(name.substring(ACHIEVEMENT_PREFIX.length()));
      }
    }

    return completed;
  }

  public static void saveCompletedAchievement(String achievementId) {
    saveCompletedAchievement(new File(SettingsProvider.SETTINGS_FILE), achievementId);
  }

  static void saveCompletedAchievement(File settingsFile, String achievementId) {
    Properties properties = new Properties();

    if (settingsFile.exists()) {
      try (FileInputStream in = new FileInputStream(settingsFile)) {
        properties.load(in);
      } catch (Exception ex) {
        System.err.println("Error loading existing settings for achievements: " + ex.getMessage());
      }
    }

    properties.setProperty(ACHIEVEMENT_PREFIX + achievementId, "true");

    try (FileWriter writer = new FileWriter(settingsFile)) {
      properties.store(writer, "store properties");
    } catch (Exception ex) {
      System.err.println("Error saving achievement status: " + ex.getMessage());
    }
  }
}
