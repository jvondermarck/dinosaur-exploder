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
    registerAchievements();
  }

  /**
   * Registers all available achievements. Add new achievements here to make them available in the
   * game.
   */
  private void registerAchievements() {
    // Kill Count Achievements
    allAchievements.add(new KillCountAchievement(10, 50));
    allAchievements.add(new KillCountAchievement(20, 100));
    allAchievements.add(new KillCountAchievement(50, 250));

    // Score Achievements
    allAchievements.add(new ScoreAchievement(5000, 75));
    allAchievements.add(new ScoreAchievement(10000, 150));

    // Coin Collection Achievements
    allAchievements.add(new CoinCollectionAchievement(100, 50));
    allAchievements.add(new CoinCollectionAchievement(500, 200));

    // Survival Time Achievements
    allAchievements.add(new SurvivalTimeAchievement(1, 50)); // 1 minute
    allAchievements.add(new SurvivalTimeAchievement(3, 150)); // 3 minutes

    // Boss Defeat Achievement
    allAchievements.add(new BossDefeatAchievement(200));
  }

  /** Called once when the game starts. Loads achievements from file or creates new ones. */
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

  public void update(double tpf) {
    for (Achievement achievement : activeAchievements) {
      if (!achievement.isCompleted()) {
        achievement.update(tpf);
      }
    }
  }

  /** Called when a dinosaur is killed. Notifies all active kill-based achievements. */
  public void notifyDinosaurKilled() {
    for (Achievement achievement : activeAchievements) {
      achievement.onDinosaurKilled();
    }
    saveAchievement(activeAchievements);
  }

  /**
   * Called when the player's score changes. Notifies all active score-based achievements.
   *
   * @param newScore The current score
   */
  public void notifyScoreChanged(int newScore) {
    for (Achievement achievement : activeAchievements) {
      achievement.onScoreChanged(newScore);
    }
    saveAchievement(activeAchievements);
  }

  /**
   * Called when coins are collected. Notifies all active coin-based achievements.
   *
   * @param totalCoins The total number of coins collected
   */
  public void notifyCoinCollected(int totalCoins) {
    for (Achievement achievement : activeAchievements) {
      achievement.onCoinCollected(totalCoins);
    }
    saveAchievement(activeAchievements);
  }

  /** Called when a boss is defeated. Notifies all active boss-defeat achievements. */
  public void notifyBossDefeated() {
    for (Achievement achievement : activeAchievements) {
      achievement.onBossDefeated();
    }
    saveAchievement(activeAchievements);
  }

  public List<Achievement> getActiveAchievements() {
    return activeAchievements;
  }

  public List<Achievement> getAllAchievements() {
    return allAchievements;
  }

  public Achievement getActiveAchievement() {
    if (activeAchievements.isEmpty()) {
      return null;
    }
    return activeAchievements.getFirst();
  }

  /** Get the list of achievements saved in the achievement.ser file */
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

  /** Save activeAchievement in the achievement.ser file */
  public void saveAchievement(List<Achievement> listToSave) {
    try (ObjectOutputStream out =
        new ObjectOutputStream(new FileOutputStream(GameConstants.ACHIEVEMENTS_FILE))) {
      out.writeObject(listToSave);
    } catch (IOException e) {
      LOGGER.log(Level.WARNING, "Error saving achievement : {0}", e.getMessage());
    }
  }
}
