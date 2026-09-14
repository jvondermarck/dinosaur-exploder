/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.model;

import static org.junit.jupiter.api.Assertions.*;

import com.almasb.fxgl.entity.Entity;
import com.dinosaur.dinosaurexploder.achievements.Achievement;
import com.dinosaur.dinosaurexploder.achievements.AchievementManager;
import com.dinosaur.dinosaurexploder.components.*;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import com.dinosaur.dinosaurexploder.utils.MockGameTimer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CollisionHandlerTest {

  public static final int LEVEL_UP_COUNT = 5;
  public static final int RED_DINO_LIVES = 10;
  public static final int PLAYER_MAX_LIVES = 3;
  public static final int MAX_BOMB_COUNT = 3;

  // ScoreComponent and CollectedCoinsComponent read/write these real, relative save files
  // (see GameConstants.HIGH_SCORE_FILE / TOTAL_COINS_FILE). Several tests below trigger score and
  // coin increments, which persist to disk as a side effect. The fields and hooks below make sure
  // any real player save data on disk is backed up before, and restored after, each test - and
  // that the whole class leaves the real save files byte-for-byte unchanged (see #478).
  private static final Path HIGH_SCORE_PATH = Path.of(GameConstants.HIGH_SCORE_FILE);
  private static final Path TOTAL_COINS_PATH = Path.of(GameConstants.TOTAL_COINS_FILE);

  private static byte[] originalHighScoreBytes;
  private static byte[] originalTotalCoinsBytes;

  private byte[] highScoreBackup;
  private byte[] totalCoinsBackup;

  private List<Achievement> currentAchievement = new ArrayList<>();
  AchievementManager achievementManager = new AchievementManager();
  CollisionHandler collisionHandler;
  LevelManager levelManager;

  @BeforeAll
  static void backUpRealSaveFilesOnce() throws IOException {
    originalHighScoreBytes = readAllBytesOrNull(HIGH_SCORE_PATH);
    originalTotalCoinsBytes = readAllBytesOrNull(TOTAL_COINS_PATH);
  }

  @AfterAll
  static void verifyRealSaveFilesUntouched() throws IOException {
    assertArrayEquals(
        originalHighScoreBytes,
        readAllBytesOrNull(HIGH_SCORE_PATH),
        "CollisionHandlerTest must not permanently modify " + GameConstants.HIGH_SCORE_FILE);
    assertArrayEquals(
        originalTotalCoinsBytes,
        readAllBytesOrNull(TOTAL_COINS_PATH),
        "CollisionHandlerTest must not permanently modify " + GameConstants.TOTAL_COINS_FILE);
  }

  private static byte[] readAllBytesOrNull(Path path) throws IOException {
    return Files.exists(path) ? Files.readAllBytes(path) : null;
  }

  private static void restoreFile(Path path, byte[] backup) throws IOException {
    if (backup == null) {
      Files.deleteIfExists(path);
    } else {
      Files.write(path, backup);
    }
  }

  @BeforeEach
  void setUp() throws IOException {
    highScoreBackup = readAllBytesOrNull(HIGH_SCORE_PATH);
    totalCoinsBackup = readAllBytesOrNull(TOTAL_COINS_PATH);

    levelManager = new LevelManager();
    List<Achievement> emptyList = new ArrayList<>();
    currentAchievement = achievementManager.loadAchievement();
    achievementManager.saveAchievement(emptyList);
    achievementManager.init();

    collisionHandler = new CollisionHandler(levelManager, achievementManager);
  }

  @AfterEach
  void restoreSaveFiles() throws IOException {
    restoreFile(HIGH_SCORE_PATH, highScoreBackup);
    restoreFile(TOTAL_COINS_PATH, totalCoinsBackup);
  }

  @Test
  void projectileHitDino_thenLevelUp() {
    ScoreComponent scoreComponent = new ScoreComponent();
    Rectangle rect = new Rectangle(0, 8, Color.LIMEGREEN);
    LevelProgressBarComponent levelProgressBarComponent =
        new LevelProgressBarComponent(rect, levelManager) {
          @Override
          public void updateProgress() {}
        };

    for (int i = 0; i < LEVEL_UP_COUNT; i++)
      collisionHandler.isLevelUpAfterHitDino(scoreComponent, levelProgressBarComponent);

    assertEquals(2, levelManager.getCurrentLevel());
  }

  @Test
  void projectileHitDino_thenScoreIncrease() {
    ScoreComponent scoreComponent = new ScoreComponent();
    Rectangle rect = new Rectangle(0, 8, Color.LIMEGREEN);
    LevelProgressBarComponent levelProgressBarComponent =
        new LevelProgressBarComponent(rect, levelManager) {
          @Override
          public void updateProgress() {}
        };

    collisionHandler.isLevelUpAfterHitDino(scoreComponent, levelProgressBarComponent);

    assertEquals(1, scoreComponent.getScore());
  }

  @Test
  void projectileHitBoss_thenBossDamage() {
    RedDinoComponent redDinoComponent = new RedDinoComponent(new MockGameTimer());

    collisionHandler.handleHitBoss(redDinoComponent);

    assertEquals(redDinoComponent.getLives(), RED_DINO_LIVES - 1);
  }

  @Test
  void projectileKillBoss_thenGetScoreAndLevel() {
    ScoreComponent scoreComponent = new ScoreComponent();
    Rectangle rect = new Rectangle(0, 8, Color.LIMEGREEN);
    LevelProgressBarComponent levelProgressBarComponent =
        new LevelProgressBarComponent(rect, levelManager) {
          @Override
          public void updateProgress() {}
        };

    assertTrue(
        collisionHandler.isLevelUpAfterBossDefeat(scoreComponent, levelProgressBarComponent));

    assertEquals(1, scoreComponent.getScore());
    assertEquals(2, levelManager.getCurrentLevel());
  }

  @Test
  void damagePlayerLife() {
    LifeComponent lifeComponent = new LifeComponent();

    int playerLife = collisionHandler.getDamagedPlayerLife(lifeComponent);

    assertEquals(playerLife, PLAYER_MAX_LIVES - 1);
  }

  @Test
  void damageAllyLife() {
    AllyComponent ally = new AllyComponent();
    AllyComponent ally2 = collisionHandler.onAllyHit(ally);

    assertEquals(1, ally2.getLife());
  }

  @Test
  void playerGetCoin_thenFillBomb() {
    // given
    CollectedCoinsComponent collectedCoinsComponent =
        new CollectedCoinsComponent() {
          @Override
          protected void updateText() {
            // do nothing
          }
        };

    BombComponent bombComponent =
        new BombComponent() {
          @Override
          protected void updateBombUI() {
            // do nothing
          }

          @Override
          protected void spawnBombBullets(Entity player) {
            // do nothing
          }
        };
    Entity dummyPlayer = Mockito.mock(Entity.class);

    // when
    bombComponent.useBomb(dummyPlayer);
    assertEquals(bombComponent.getBombCount(), MAX_BOMB_COUNT - 1);

    for (int i = 0; i < 15; i++) {
      collisionHandler.onPlayerGetCoin(
          collectedCoinsComponent, new ScoreComponent(), bombComponent);
    }

    // then
    assertEquals(MAX_BOMB_COUNT, bombComponent.getBombCount());
  }

  @Test
  void playGetHeart_thenIncreaseLife() {
    LifeComponent lifeComponent = new LifeComponent();

    lifeComponent.decreaseLife(1);
    collisionHandler.onPlayerGetHeart(lifeComponent);

    assertEquals(PLAYER_MAX_LIVES, lifeComponent.getLife());
  }

  @AfterEach
  void setAchievementBack() {
    achievementManager.saveAchievement(currentAchievement);
  }

  @Test
  void savingHighScoreAndCoins_doesNotPermanentlyModifyRealSaveFiles() throws IOException {
    // given: the real save files as they were before this test touched anything
    byte[] highScoreBefore = readAllBytesOrNull(HIGH_SCORE_PATH);
    byte[] totalCoinsBefore = readAllBytesOrNull(TOTAL_COINS_PATH);

    // when: production code paths that persist to disk are exercised, same as other tests in
    // this class implicitly do via ScoreComponent#incrementScore and
    // CollectedCoinsComponent#incrementCoin
    ScoreComponent scoreComponent = new ScoreComponent();
    scoreComponent.setHighScore(Integer.MAX_VALUE);

    CollectedCoinsComponent collectedCoinsComponent =
        new CollectedCoinsComponent() {
          @Override
          protected void updateText() {
            // do nothing
          }
        };
    collectedCoinsComponent.setCoin(Integer.MAX_VALUE);

    // then: writes really did happen on disk (otherwise this test would prove nothing)
    assertTrue(Files.exists(HIGH_SCORE_PATH));
    assertTrue(Files.exists(TOTAL_COINS_PATH));

    // and: once restored (as @AfterEach also does for every test), the files are back to
    // byte-for-byte what they were before, i.e. no real player data was lost
    restoreFile(HIGH_SCORE_PATH, highScoreBefore);
    restoreFile(TOTAL_COINS_PATH, totalCoinsBefore);

    assertArrayEquals(highScoreBefore, readAllBytesOrNull(HIGH_SCORE_PATH));
    assertArrayEquals(totalCoinsBefore, readAllBytesOrNull(TOTAL_COINS_PATH));
  }
}
