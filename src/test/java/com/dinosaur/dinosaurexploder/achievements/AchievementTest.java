/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.achievements;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AchievementTest {

  private final List<Achievement> emptyAchievements = new ArrayList<>();
  private List<Achievement> currentAchievement = new ArrayList<>();
  private AchievementManager achievementManager;

  @BeforeEach
  void setUp() {
    achievementManager = new AchievementManager();
    currentAchievement = achievementManager.loadAchievement();
    achievementManager.saveAchievement(emptyAchievements);
    achievementManager.init();
  }

  @Test
  void achievementSaveInFile() {
    List<Achievement> listToCheck;

    achievementManager.getActiveAchievement().completed = true;
    achievementManager.saveAchievement(achievementManager.getActiveAchievements());
    listToCheck = achievementManager.loadAchievement();
    assertTrue(listToCheck.getFirst().isCompleted());
  }

  @Test
  void shouldRegisterDefaultAchievementsFromCatalog() {
    assertEquals(10, achievementManager.getAllAchievements().size());
  }

  @Test
  void addAchievementInAlreadyExistingAchievements() {
    int numberOfStartAchievement = achievementManager.loadAchievement().size();

    AchievementManager managerWithExtraAchievement =
        new AchievementManager(
            AchievementCatalog.defaults().with(() -> new KillCountAchievement(99, 999)));

    managerWithExtraAchievement.init();

    assertEquals(
        numberOfStartAchievement + 1, managerWithExtraAchievement.loadAchievement().size());
  }

  @Test
  void shouldDispatchEventsThroughSharedEventPath() {
    AchievementManager singleAchievementManager =
        new AchievementManager(AchievementCatalog.of(() -> new KillCountAchievement(1, 10)));
    singleAchievementManager.saveAchievement(emptyAchievements);
    singleAchievementManager.init();

    singleAchievementManager.notifyDinosaurKilled();

    assertTrue(singleAchievementManager.getActiveAchievement().isCompleted());
  }

  @Test
  void shouldSplitAchievementsIntoPendingAndCompletedLists() {
    AchievementManager singleAchievementManager =
        new AchievementManager(
            AchievementCatalog.of(
                () -> new KillCountAchievement(1, 10), () -> new ScoreAchievement(9999, 10)));
    singleAchievementManager.saveAchievement(emptyAchievements);
    singleAchievementManager.init();

    singleAchievementManager.notifyDinosaurKilled();

    assertEquals(1, singleAchievementManager.getCompletedAchievements().size());
    assertEquals(1, singleAchievementManager.getPendingAchievements().size());
    assertTrue(singleAchievementManager.getCompletedAchievements().getFirst().isCompleted());
    assertFalse(singleAchievementManager.getPendingAchievements().getFirst().isCompleted());
  }

  @AfterEach
  void setAchievementBack() {
    achievementManager.saveAchievement(currentAchievement);
  }
}
