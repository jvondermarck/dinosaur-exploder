package com.dinosaur.dinosaurexploder.achievements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AchievementManager {

  private final List<Achievement> allAchievements = new ArrayList<>();
  private final List<Achievement> activeAchievements = new ArrayList<>();

  public AchievementManager() {
    // Register all available achievements here
    allAchievements.add(new KillCountAchievement(10, 50));
    allAchievements.add(new KillCountAchievement(20, 100));
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

  public Achievement getActiveAchievement() {
    if (activeAchievements.isEmpty()) {
      return null;
    }

}
