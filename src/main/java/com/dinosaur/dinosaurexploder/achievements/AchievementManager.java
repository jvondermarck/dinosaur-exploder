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

  private final List<Achievement> allAchievements;
  private final List<Achievement> activeAchievements = new ArrayList<>();
  private static final Logger LOGGER = Logger.getLogger(AchievementManager.class.getName());

  public AchievementManager() {
    this(AchievementCatalog.defaults());
  }

  AchievementManager(AchievementCatalog catalog) {
    this.allAchievements = new ArrayList<>(catalog.createAchievements());
  }

  /** Called once when the game starts. Loads achievements from file or creates new ones. */
  public void init() {
    if (allAchievements.isEmpty()) {
      return;
    }

    activeAchievements.addAll(loadAchievement());
    if (activeAchievements.isEmpty()) {
      activeAchievements.addAll(allAchievements);
      saveAchievement(activeAchievements);
      return;
    }

    addMissingAchievements();
  }

  public void update(double tpf) {
    AchievementEvent timeElapsedEvent = AchievementEvent.timeElapsed(tpf);
    for (Achievement achievement : activeAchievements) {
      if (!achievement.isCompleted()) {
        achievement.handleEvent(timeElapsedEvent);
      }
    }
  }

  /** Called when a dinosaur is killed. Notifies all active kill-based achievements. */
  public void notifyDinosaurKilled() {
    dispatchAndSave(AchievementEvent.dinosaurKilled());
  }

  /**
   * Called when the player's score changes. Notifies all active score-based achievements.
   *
   * @param newScore The current score
   */
  public void notifyScoreChanged(int newScore) {
    dispatchAndSave(AchievementEvent.scoreChanged(newScore));
  }

  /**
   * Called when coins are collected. Notifies all active coin-based achievements.
   *
   * @param totalCoins The total number of coins collected
   */
  public void notifyCoinCollected(int totalCoins) {
    dispatchAndSave(AchievementEvent.coinCollected(totalCoins));
  }

  /** Called when a boss is defeated. Notifies all active boss-defeat achievements. */
  public void notifyBossDefeated() {
    dispatchAndSave(AchievementEvent.bossDefeated());
  }

  private void addMissingAchievements() {
    if (allAchievements.size() <= activeAchievements.size()) {
      return;
    }

    Set<String> activeDescriptions =
        activeAchievements.stream().map(Achievement::getDescription).collect(Collectors.toSet());

    List<Achievement> toAdd =
        allAchievements.stream()
            .filter(a -> !activeDescriptions.contains(a.getDescription()))
            .toList();

    if (toAdd.isEmpty()) {
      return;
    }

    activeAchievements.addAll(toAdd);
    saveAchievement(activeAchievements);
  }

  private void dispatchAndSave(AchievementEvent event) {
    for (Achievement achievement : activeAchievements) {
      achievement.handleEvent(event);
    }
    saveAchievement(activeAchievements);
  }

  public List<Achievement> getActiveAchievements() {
    return activeAchievements;
  }

  public List<Achievement> getAllAchievements() {
    return allAchievements;
  }

  public List<Achievement> getPendingAchievements() {
    return activeAchievements.stream().filter(achievement -> !achievement.isCompleted()).toList();
  }

  public List<Achievement> getCompletedAchievements() {
    return activeAchievements.stream().filter(Achievement::isCompleted).toList();
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
      LOGGER.log(Level.INFO, "Failed to load achievements from file");
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
