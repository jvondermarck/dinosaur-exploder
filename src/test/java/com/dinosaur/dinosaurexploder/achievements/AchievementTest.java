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
  void shouldRegisterDefaultAchievementsFromAnnotations() {
    // 5 annotated classes: 1 + 2 + 3 + 2 + 2 = 10 instances total
    assertEquals(10, achievementManager.getAllAchievements().size());
  }

  @Test
  void annotationsShouldProduceExpectedDescriptions() {
    List<String> descriptions =
        achievementManager.getAllAchievements().stream().map(Achievement::getDescription).toList();

    assertTrue(descriptions.contains("Kill 10 dinosaurs"));
    assertTrue(descriptions.contains("Kill 50 dinosaurs"));
    assertTrue(descriptions.contains("Reach 5000 points"));
    assertTrue(descriptions.contains("Collect 100 coins"));
    assertTrue(descriptions.contains("Survive for 1 minute"));
    assertTrue(descriptions.contains("Defeat your first boss"));
  }

  @Test
  void addAchievementInAlreadyExistingAchievements() {
    // Seed the file with a known 2-achievement catalog
    AchievementManager baseManager =
        new AchievementManager(
            AchievementCatalog.of(
                () -> new KillCountAchievement(10, 50), () -> new KillCountAchievement(20, 100)));
    baseManager.saveAchievement(emptyAchievements);
    baseManager.init();
    int baseCount = baseManager.loadAchievement().size(); // 2

    // A new manager with the same 2 + 1 extra achievement
    AchievementManager managerWithExtraAchievement =
        new AchievementManager(
            AchievementCatalog.of(
                () -> new KillCountAchievement(10, 50),
                () -> new KillCountAchievement(20, 100),
                () -> new KillCountAchievement(99, 999)));
    managerWithExtraAchievement.init();

    assertEquals(baseCount + 1, managerWithExtraAchievement.loadAchievement().size());
  }

  @Test
  void shouldDispatchEventsThroughBus() {
    AchievementManager singleAchievementManager =
        new AchievementManager(AchievementCatalog.of(() -> new KillCountAchievement(1, 10)));
    singleAchievementManager.saveAchievement(emptyAchievements);
    singleAchievementManager.init();

    singleAchievementManager.dispatch(AchievementEvent.dinosaurKilled());

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

    singleAchievementManager.dispatch(AchievementEvent.dinosaurKilled());

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
