/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.constants.GameMode;
import com.dinosaur.dinosaurexploder.model.HighScore;
import com.dinosaur.dinosaurexploder.model.TotalCoins;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Serialization tests for the high-score and coin save files (issue #488). Each test gets a fresh
 * temporary folder, so the player's real save files are never read or written.
 */
class FileProgressRepositoriesTest {

  @TempDir Path tempDir;

  @Test
  void load_noHighScoreFile_startsEveryModeAtZero() {
    HighScore loaded =
        new FileHighScoreRepository(tempDir.resolve(GameConstants.HIGH_SCORE_FILE)).load();

    for (GameMode mode : GameMode.values()) {
      assertEquals(0, loaded.getHigh(mode.name()), mode.name());
    }
  }

  @Test
  void save_highScoresForEveryMode_loadsThemBack() {
    Path file = tempDir.resolve(GameConstants.HIGH_SCORE_FILE);
    Map<String, Integer> scores = Map.of("EASY", 7, "NORMAL", 20, "EXPERT", 2);

    new FileHighScoreRepository(file).save(new HighScore(scores));

    assertEquals(scores, new FileHighScoreRepository(file).load().getAllScores());
  }

  @Test
  void load_noCoinsFile_startsAtZero() {
    TotalCoins loaded =
        new FileTotalCoinsRepository(tempDir.resolve(GameConstants.TOTAL_COINS_FILE)).load();

    assertEquals(0, loaded.getTotal());
  }

  @Test
  void save_totalCoins_loadsTotalBack() {
    Path file = tempDir.resolve(GameConstants.TOTAL_COINS_FILE);

    new FileTotalCoinsRepository(file).save(new TotalCoins(29));

    assertEquals(29, new FileTotalCoinsRepository(file).load().getTotal());
  }
}
