package com.dinosaur.dinosaurexploder.achievements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AchievementManager {

  private final List<Achievement> allAchievements = new ArrayList<>();
  private final List<Achievement> activeAchievements = new ArrayList<>();

  public AchievementManager() {
    registerAchievements();
  }

  private void registerAchievements() {
    allAchievements.add(new KillCountAchievement("ach_kill_10_name", "ach_kill_10_desc", 10, 50));
    allAchievements.add(new KillCountAchievement("ach_kill_20_name", "ach_kill_20_desc", 20, 100));
    allAchievements.add(new KillCountAchievement("ach_kill_50_name", "ach_kill_50_desc", 50, 250));
    allAchievements.add(new KillCountAchievement("ach_kill_100_name", "ach_kill_100_desc", 100, 500));
  }

  // Called once when the game starts
  public void init() {
    if (allAchievements.isEmpty()) return;

    Collections.shuffle(allAchievements);
    activeAchievements.add(allAchievements.get(0));
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
    return activeAchievements.get(0);
  }
}
