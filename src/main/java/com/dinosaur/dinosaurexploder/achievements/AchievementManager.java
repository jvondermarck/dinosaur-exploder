package com.dinosaur.dinosaurexploder.achievements;

import com.dinosaur.dinosaurexploder.utils.AchievementProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AchievementManager {

  //private final Achievement achievement = AchievementProvider.loadSettings();
  private final List<Achievement> allAchievements = new ArrayList<>();
  private final List<Achievement> activeAchievements = new ArrayList<>();

  public AchievementManager() {
    // Register all available achievements here
    allAchievements.add(new KillCountAchievement(1, 50,"achievement10Kill"));
    allAchievements.add(new KillCountAchievement(2, 100,"achievement20Kill"));
  }

  // Called once when the game starts
  public void init() {
    if (allAchievements.isEmpty()) return;
    AchievementProvider.loadAchievements(allAchievements);

    activeAchievements.addAll(allAchievements);

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
      Boolean complete = achievement.onDinosaurKilled();
      if (complete) {
        AchievementProvider.saveAchivement(achievement);
      }
    }
  }

  public List<Achievement> getActiveAchievements() {
    return activeAchievements;
  }

  public Achievement getActiveAchievement() {
    if (activeAchievements.isEmpty()) {
      return null;
    }
    return activeAchievements.get(0);
  }
}
