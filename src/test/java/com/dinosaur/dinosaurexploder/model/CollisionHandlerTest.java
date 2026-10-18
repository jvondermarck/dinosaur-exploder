/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.almasb.fxgl.entity.Entity;
import com.dinosaur.dinosaurexploder.achievements.AchievementEvent;
import com.dinosaur.dinosaurexploder.achievements.AchievementManager;
import com.dinosaur.dinosaurexploder.components.AllyComponent;
import com.dinosaur.dinosaurexploder.components.BombComponent;
import com.dinosaur.dinosaurexploder.components.CollectedCoinsComponent;
import com.dinosaur.dinosaurexploder.components.LevelProgressBarComponent;
import com.dinosaur.dinosaurexploder.components.LifeComponent;
import com.dinosaur.dinosaurexploder.components.RedDinoComponent;
import com.dinosaur.dinosaurexploder.components.ScoreComponent;
import com.dinosaur.dinosaurexploder.interfaces.Asteroids;
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

  AchievementManager achievementManager;
  CollisionHandler collisionHandler;
  LevelManager levelManager;

  @BeforeEach
  void setUp() {
    levelManager = new LevelManager();

    achievementManager = mock(AchievementManager.class);

    collisionHandler = new CollisionHandler(levelManager, achievementManager);
  }

  @Test
  void isLevelUpAfterHitDino_returnsFalseUntilFifthHit() {
    ScoreComponent scoreComponent = mock(ScoreComponent.class);
    Rectangle rect = new Rectangle(0, 8, Color.LIMEGREEN);
    LevelProgressBarComponent levelProgressBarComponent =
        new LevelProgressBarComponent(rect, levelManager) {
          @Override
          public void updateProgress() {}
        };

    for (int i = 0; i < LEVEL_UP_COUNT - 1; i++) {
      boolean levelUp =
          collisionHandler.isLevelUpAfterHitDino(scoreComponent, levelProgressBarComponent);

      assertFalse(levelUp);
      assertEquals(1, levelManager.getCurrentLevel());
    }

    boolean levelUp =
        collisionHandler.isLevelUpAfterHitDino(scoreComponent, levelProgressBarComponent);

    assertTrue(levelUp);
    assertEquals(2, levelManager.getCurrentLevel());

    verify(scoreComponent, times(LEVEL_UP_COUNT)).incrementScore(1);
    verify(achievementManager, times(LEVEL_UP_COUNT)).dispatch(AchievementEvent.dinosaurKilled());
  }

  @Test
  void projectileHitDino_thenScoreIncrease() {
    ScoreComponent scoreComponent = mock(ScoreComponent.class);
    Rectangle rect = new Rectangle(0, 8, Color.LIMEGREEN);
    LevelProgressBarComponent levelProgressBarComponent =
        new LevelProgressBarComponent(rect, levelManager) {
          @Override
          public void updateProgress() {}
        };

    collisionHandler.isLevelUpAfterHitDino(scoreComponent, levelProgressBarComponent);

    verify(scoreComponent).incrementScore(1);
  }

  @Test
  void projectileHitBoss_thenBossDamage() {
    RedDinoComponent redDinoComponent = new RedDinoComponent(new MockGameTimer());

    collisionHandler.handleHitBoss(redDinoComponent);

    assertEquals(redDinoComponent.getLives(), RED_DINO_LIVES - 1);
  }

  @Test
  void handleHitAsteroids_dealsOneDamage() {
    Asteroids asteroids = mock(Asteroids.class);

    collisionHandler.handleHitAsteroids(asteroids);

    verify(asteroids).damage(1);
  }

  @Test
  void projectileKillBoss_givesScoreBasedOnCurrentLevel() {
    ScoreComponent scoreComponent = mock(ScoreComponent.class);
    Rectangle rect = new Rectangle(0, 8, Color.LIMEGREEN);
    LevelProgressBarComponent levelProgressBarComponent =
        new LevelProgressBarComponent(rect, levelManager) {
          @Override
          public void updateProgress() {}
        };

    assertTrue(
        collisionHandler.isLevelUpAfterBossDefeat(scoreComponent, levelProgressBarComponent));

    verify(scoreComponent).incrementScore(1);
    assertEquals(2, levelManager.getCurrentLevel());

    assertTrue(
        collisionHandler.isLevelUpAfterBossDefeat(scoreComponent, levelProgressBarComponent));

    verify(scoreComponent).incrementScore(2);
    assertEquals(3, levelManager.getCurrentLevel());

    verify(achievementManager, times(2)).dispatch(AchievementEvent.bossDefeated());
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
    CollectedCoinsComponent collectedCoinsComponent = mock(CollectedCoinsComponent.class);
    ScoreComponent scoreComponent = mock(ScoreComponent.class);

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
      collisionHandler.onPlayerGetCoin(collectedCoinsComponent, scoreComponent, bombComponent);
    }

    // then
    assertEquals(MAX_BOMB_COUNT, bombComponent.getBombCount());
    verify(collectedCoinsComponent, times(15)).incrementCoin();
    verify(scoreComponent, times(15)).incrementScore(2);
  }

  @Test
  void playerGetCoin_withNullBombComponent_increasesCoinAndScore() {
    // given
    CollectedCoinsComponent collectedCoinsComponent = mock(CollectedCoinsComponent.class);
    ScoreComponent scoreComponent = mock(ScoreComponent.class);

    when(collectedCoinsComponent.getCoin()).thenReturn(1);

    // when
    collisionHandler.onPlayerGetCoin(collectedCoinsComponent, scoreComponent, null);

    // then
    verify(collectedCoinsComponent).incrementCoin();
    verify(scoreComponent).incrementScore(2);
    verify(achievementManager).dispatch(AchievementEvent.coinCollected(1));
  }

  @Test
  void playGetHeart_thenIncreaseLife() {
    LifeComponent lifeComponent = new LifeComponent();

    lifeComponent.decreaseLife(1);
    collisionHandler.onPlayerGetHeart(lifeComponent);

    assertEquals(PLAYER_MAX_LIVES, lifeComponent.getLife());
  }

  @Test
  void getHealPlayerLife_atFullHealth_staysAtMaximum() {
    LifeComponent lifeComponent = new LifeComponent();

    int playerLife = collisionHandler.getHealPlayerLife(lifeComponent);

    assertEquals(PLAYER_MAX_LIVES, playerLife);
  }
}
