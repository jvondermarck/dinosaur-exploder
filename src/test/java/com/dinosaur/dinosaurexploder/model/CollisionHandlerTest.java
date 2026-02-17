/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.model;

import static org.junit.jupiter.api.Assertions.*;

import com.almasb.fxgl.entity.Entity;
import com.dinosaur.dinosaurexploder.achievements.AchievementManager;
import com.dinosaur.dinosaurexploder.components.BombComponent;
import com.dinosaur.dinosaurexploder.components.CollectedCoinsComponent;
import com.dinosaur.dinosaurexploder.components.LevelProgressBarComponent;
import com.dinosaur.dinosaurexploder.components.LifeComponent;
import com.dinosaur.dinosaurexploder.components.RedDinoComponent;
import com.dinosaur.dinosaurexploder.components.ScoreComponent;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import com.dinosaur.dinosaurexploder.utils.MockGameTimer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CollisionHandlerTest {

  public static final int LEVEL_UP_COUNT = 5;
  public static final int RED_DINO_LIVES = 10;
  public static final int PLAYER_MAX_LIVES = 3;
  public static final int MAX_BOMB_COUNT = 3;

  CollisionHandler collisionHandler;
  LevelManager levelManager;

  @BeforeEach
  void setUp() {
    levelManager = new LevelManager();

    AchievementManager achievementManager = new AchievementManager();
    achievementManager.init();

    collisionHandler = new CollisionHandler(levelManager, achievementManager);
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

    collisionHandler.handleBossDefeat(scoreComponent);

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
}
