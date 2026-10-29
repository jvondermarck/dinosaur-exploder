/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.achievements;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Verifies that in-flight achievement progress, not just the {@code completed} flag, survives the
 * save/load cycle, and that completion is idempotent across restarts.
 */
public class AchievementPersistenceTest {

  private List<Achievement> saveFileBackupSnapshot;

  /**
   * Backs up the current on-disk achievement state so that it can be restored later and clears the
   * file so each test starts from a known baseline
   */
  @BeforeEach
  void backupAndClearSaveFile() {
    AchievementManager manager = new AchievementManager();
    saveFileBackupSnapshot = manager.loadAchievement();
    manager.saveAchievement(new ArrayList<>());

    // Fixes the Windows OS handle lock so the test can safely delete/manipulate the file later on
    System.gc();
  }

  /** Restores the state after the tests have run */
  @AfterEach
  void restoreSaveFile() {
    new AchievementManager().saveAchievement(saveFileBackupSnapshot);
  }

  /** Kill counter must persist, even when reloaded mid-progress. */
  @Test
  void killCountProgressSurvivesSaveLoadCycle() {
    AchievementCatalog catalogue = AchievementCatalog.of(() -> new KillCountAchievement(10, 50));

    AchievementManager initialManager = new AchievementManager(catalogue);
    initialManager.saveAchievement(new ArrayList<>());
    initialManager.init();

    for (int i = 0; i < 3; i++) initialManager.dispatch(AchievementEvent.dinosaurKilled());

    assertFalse(
        initialManager.getActiveAchievement().isCompleted(),
        "3 kills should not complete the 10 kill achievement");

    AchievementManager reloadedManager = new AchievementManager(catalogue);
    reloadedManager.init();

    Achievement loaded = reloadedManager.getActiveAchievement();
    assertFalse(loaded.isCompleted(), "Reloaded achievement must not be completed yet");

    for (int i = 0; i < 7; i++) reloadedManager.dispatch(AchievementEvent.dinosaurKilled());
    assertTrue(
        reloadedManager.getActiveAchievement().isCompleted(),
        "3 persisted + 7 fresh kills must reach the target of 10");
  }

  /** Survival timer must persist, even when reloaded mid-progress. */
  @Test
  void survivalProgressSurvivesSaveLoadCycle() {
    AchievementCatalog catalogue = AchievementCatalog.of(() -> new SurvivalTimeAchievement(1, 50));

    AchievementManager initialManager = new AchievementManager(catalogue);
    initialManager.saveAchievement(new ArrayList<>());
    initialManager.init();

    initialManager.update(30.0);
    assertFalse(
        initialManager.getActiveAchievement().isCompleted(),
        "30 of 60 seconds must not complete the achievement");

    initialManager.saveAchievement(initialManager.getActiveAchievements());

    AchievementManager reloadedManager = new AchievementManager(catalogue);
    reloadedManager.init();

    reloadedManager.update(30.0);
    assertTrue(
        reloadedManager.getActiveAchievement().isCompleted(),
        "30 persisted + 30 fresh seconds must complete the 1-minute achievement");
  }

  /**
   * Test-scoped subclass designed to track behavioural side effects across Java serialisation
   * boundaries without relying on fragile Mockito spies.
   */
  private static class IdempotentTestKillCountAchievement extends KillCountAchievement {

    /**
     * Encapsulated onComplete invocation tracker (Must be static to survive object deserialisation)
     */
    public static int invocationCount = 0;

    public IdempotentTestKillCountAchievement(int targetKills, int rewardCoins) {
      super(targetKills, rewardCoins);
    }

    /**
     * Overrides the original method to add an invocation count. Intercepts the execution loop and
     * increments the invocation count.
     *
     * <p>Maintains default game notification routines.
     */
    @Override
    protected void onComplete() {
      invocationCount++;
      super.onComplete();
    }
  }

  /** Completing an achievement twice must not trigger {@code onComplete()} twice. */
  @Test
  void completedAchievementIsIdempotentAcrossRestart() {
    IdempotentTestKillCountAchievement.invocationCount =
        0; // Clear state before invocation triggers
    AchievementCatalog catalogue =
        AchievementCatalog.of(() -> new IdempotentTestKillCountAchievement(1, 50));

    AchievementManager initialManager = new AchievementManager(catalogue);
    initialManager.saveAchievement(new ArrayList<>());
    initialManager.init();

    initialManager.dispatch(AchievementEvent.dinosaurKilled());
    assertTrue(
        initialManager.getActiveAchievement().isCompleted(), "Initial baseline must be complete");
    assertEquals(
        1,
        IdempotentTestKillCountAchievement.invocationCount,
        "onComplete must run exactly once on initial unlock.");

    AchievementManager reloadedManager = new AchievementManager(catalogue);
    reloadedManager.init();

    assertTrue(
        reloadedManager.getActiveAchievement().isCompleted(),
        "completed achievement must survive persistence");

    for (int i = 0; i < 3; i++) reloadedManager.dispatch(AchievementEvent.dinosaurKilled());

    assertTrue(
        reloadedManager.getActiveAchievement().isCompleted(),
        "the achievement must remain completed");
    assertEquals(
        1,
        IdempotentTestKillCountAchievement.invocationCount,
        "Idempotency violation: onComplete() ran a duplicate time after a game reload!");
  }
}
