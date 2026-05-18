/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AchievementsProviderTest {

  @Test
  public void testSaveAndLoadCompletedAchievement() throws Exception {
    Path tempFile = Files.createTempFile("achievements", ".properties");
    try {
      AchievementsProvider.saveCompletedAchievement(tempFile.toFile(), "kill_count_10");

      Set<String> loaded = AchievementsProvider.loadCompletedAchievements(tempFile.toFile());
      Assertions.assertTrue(loaded.contains("kill_count_10"));
      Assertions.assertEquals(1, loaded.size());
    } finally {
      Files.deleteIfExists(tempFile);
    }
  }
}
