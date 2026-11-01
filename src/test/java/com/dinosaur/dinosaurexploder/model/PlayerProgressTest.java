/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.dinosaur.dinosaurexploder.achievements.AchievementManager;
import com.dinosaur.dinosaurexploder.components.CollectedCoinsComponent;
import com.dinosaur.dinosaurexploder.components.ScoreComponent;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.constants.GameMode;
import com.dinosaur.dinosaurexploder.persistence.FileHighScoreRepository;
import com.dinosaur.dinosaurexploder.persistence.FileTotalCoinsRepository;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Regression tests for issue #488 (player progress). The score and coin rules run against the real
 * file repositories pointed at a temporary folder, so saving and reloading go through real
 * serialization without touching the player's save files.
 */
class PlayerProgressTest {

  private static final List<Path> PRODUCTION_SAVE_FILES =
      List.of(
          Path.of(GameConstants.HIGH_SCORE_FILE),
          Path.of(GameConstants.TOTAL_COINS_FILE),
          Path.of(GameConstants.ACHIEVEMENTS_FILE));

  @TempDir Path tempDir;
  private Path highScoreFile;
  private Path totalCoinsFile;
  private GameMode previousDifficulty;

  @BeforeEach
  void setUp() {
    highScoreFile = tempDir.resolve(GameConstants.HIGH_SCORE_FILE);
    totalCoinsFile = tempDir.resolve(GameConstants.TOTAL_COINS_FILE);

    // The selected difficulty is static, so put it back after each test.
    previousDifficulty = GameData.getSelectedDifficulty();
    GameData.setSelectedDifficulty(GameMode.NORMAL);
  }

  @AfterEach
  void tearDown() {
    GameData.setSelectedDifficulty(previousDifficulty);
  }

  /** Fails if a lower session score replaces the saved high score. */
  @Test
  void incrementScore_belowSavedHighScore_keepsSavedHighScore() {
    seedHighScores(20, 2);
    ScoreComponent score = new ScoreComponent(new FileHighScoreRepository(highScoreFile));

    score.incrementScore(5);

    assertEquals(20, savedHighScores().getHigh("NORMAL"));
  }

  @Test
  void incrementScore_newNormalRecord_keepsExpertHighScore() {
    seedHighScores(20, 2);
    ScoreComponent score = new ScoreComponent(new FileHighScoreRepository(highScoreFile));

    score.incrementScore(25);

    HighScore saved = savedHighScores();
    assertEquals(25, saved.getHigh("NORMAL"));
    assertEquals(2, saved.getHigh("EXPERT"));
  }

  @Test
  void incrementCoin_oneCoin_addsOneToSessionAndSavedTotal() {
    seedTotalCoins(29);
    CollectedCoinsComponent coins = coinsWithoutHud();

    coins.incrementCoin();

    assertEquals(1, coins.getCoin());
    assertEquals(30, savedTotalCoins());
  }

  /** The concrete regression from #488 section 1, step by step. */
  @Test
  void onPlayerGetCoin_seededProgress_keepsHighScoresAndCountsCoinOnce() throws IOException {
    Map<Path, String> productionFilesBefore = productionSaveFileState();

    // 1. Seed Normal high score 20, Expert high score 2 and 29 total coins.
    seedHighScores(20, 2);
    seedTotalCoins(29);
    ScoreComponent score = new ScoreComponent(new FileHighScoreRepository(highScoreFile));
    CollectedCoinsComponent coins = coinsWithoutHud();
    CollisionHandler collisionHandler =
        new CollisionHandler(new LevelManager(), achievementsInTempDir());

    // 2. A lower Normal score leaves both saved high scores alone.
    score.incrementScore(5);
    assertEquals(20, savedHighScores().getHigh("NORMAL"));
    assertEquals(2, savedHighScores().getHigh("EXPERT"));

    // 3. Collecting one coin (which also adds 2 points) counts it once.
    collisionHandler.onPlayerGetCoin(coins, score, null);
    assertEquals(1, coins.getCoin());
    assertEquals(30, savedTotalCoins());

    // 4. Reloading from the temporary folder gives the same values.
    HighScore reloadedScores = new FileHighScoreRepository(highScoreFile).load();
    assertEquals(20, reloadedScores.getHigh("NORMAL"));
    assertEquals(2, reloadedScores.getHigh("EXPERT"));
    assertEquals(30, new FileTotalCoinsRepository(totalCoinsFile).load().getTotal());

    // 5. No save file in the working directory was created or modified.
    assertEquals(productionFilesBefore, productionSaveFileState());
  }

  private void seedHighScores(int normal, int expert) {
    new FileHighScoreRepository(highScoreFile)
        .save(new HighScore(Map.of("NORMAL", normal, "EXPERT", expert)));
  }

  private void seedTotalCoins(int total) {
    new FileTotalCoinsRepository(totalCoinsFile).save(new TotalCoins(total));
  }

  private HighScore savedHighScores() {
    return new FileHighScoreRepository(highScoreFile).load();
  }

  private int savedTotalCoins() {
    return new FileTotalCoinsRepository(totalCoinsFile).load().getTotal();
  }

  /** The coin counter text needs a running game, so these tests skip updating it. */
  private CollectedCoinsComponent coinsWithoutHud() {
    return new CollectedCoinsComponent(new FileTotalCoinsRepository(totalCoinsFile)) {
      @Override
      protected void updateText() {
        // no HUD in tests
      }
    };
  }

  private AchievementManager achievementsInTempDir() {
    AchievementManager achievements =
        new AchievementManager(tempDir.resolve(GameConstants.ACHIEVEMENTS_FILE));
    achievements.init();
    return achievements;
  }

  /**
   * Size and last-modified time of each save file in the working directory, or "missing". Uses file
   * metadata only, so the player's saved data is never read.
   */
  private static Map<Path, String> productionSaveFileState() throws IOException {
    Map<Path, String> state = new HashMap<>();
    for (Path file : PRODUCTION_SAVE_FILES) {
      if (Files.exists(file)) {
        BasicFileAttributes attributes = Files.readAttributes(file, BasicFileAttributes.class);
        state.put(file, attributes.size() + " bytes, modified " + attributes.lastModifiedTime());
      } else {
        state.put(file, "missing");
      }
    }
    return state;
  }
}
