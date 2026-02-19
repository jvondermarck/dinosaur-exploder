/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.achievements;

import com.dinosaur.dinosaurexploder.constants.GameConstants;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AchievementManager {

  private final List<Achievement> allAchievements = new ArrayList<>();
  private final List<Achievement> activeAchievements = new ArrayList<>();
  private static final Logger LOGGER = Logger.getLogger(AchievementManager.class.getName());

  public AchievementManager() {
    // Register all available achievements here
    allAchievements.add(new KillCountAchievement(10, 50));
    allAchievements.add(new KillCountAchievement(20, 100));
  }

  // Called once when the game starts
  public void init() {
    if (allAchievements.isEmpty()) return;

    activeAchievements.addAll(loadAchievement());
    if (activeAchievements.isEmpty()) {
      saveAchievement(allAchievements);
      activeAchievements.addAll(allAchievements);
    }
    if (allAchievements.size() > activeAchievements.size()) {

      Set<String> activeDescriptions =
          activeAchievements.stream().map(Achievement::getDescription).collect(Collectors.toSet());

      List<Achievement> toAdd =
          allAchievements.stream()
              .filter(a -> !activeDescriptions.contains(a.getDescription()))
              .toList();

      activeAchievements.addAll(toAdd);
      saveAchievement(activeAchievements);
    }
  }

  // Called every frame
  public void update(double tpf) {
    for (Achievement achievement : activeAchievements) {
      if (!achievement.isCompleted()) {
        achievement.update(tpf);
      }
    }
  }

  // Called when a dinosaur is killed
  public void notifyDinosaurKilled() {
    for (Achievement achievement : activeAchievements) {
      achievement.onDinosaurKilled();
    }
    saveAchievement(activeAchievements);
  }

  public List<Achievement> getActiveAchievements() {
    return activeAchievements;
  }

  public Achievement getActiveAchievement() {
    if (activeAchievements.isEmpty()) {
      return null;
    }
    return activeAchievements.getFirst();
  }

  /// Get the list of achievement save in the achievement.ser file
  public List<Achievement> loadAchievement() {
    List<Achievement> achievementFromFile = new ArrayList<>();
    try (ObjectInputStream in =
        new ObjectInputStream(new FileInputStream(GameConstants.ACHIEVEMENTS_FILE))) {
      achievementFromFile = (List<Achievement>) in.readObject();
    } catch (IOException | ClassNotFoundException e) {
      LOGGER.log(Level.FINE, "Failed to load achievements from file");
    }
    return achievementFromFile;
  }

  /// Save activeAchievement in the achievement.ser file
  public void saveAchievement(List<Achievement> listToSave) {
    try (ObjectOutputStream out =
        new ObjectOutputStream(new FileOutputStream(GameConstants.ACHIEVEMENTS_FILE))) {
      out.writeObject(listToSave);
    } catch (IOException e) {
      LOGGER.log(Level.WARNING, "Error saving achievement : ", e.getMessage());
    }
  }
}
