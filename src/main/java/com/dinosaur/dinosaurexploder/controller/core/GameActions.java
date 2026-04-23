/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.controller.core;

import static com.almasb.fxgl.dsl.FXGL.getGameScene;
import static com.almasb.fxgl.dsl.FXGL.runOnce;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;
import static javafx.util.Duration.seconds;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.dinosaur.dinosaurexploder.components.*;
import com.dinosaur.dinosaurexploder.constants.EntityType;
import com.dinosaur.dinosaurexploder.model.CollisionHandler;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import com.dinosaur.dinosaurexploder.utils.TextUtils;
import com.dinosaur.dinosaurexploder.view.DinosaurGUI;
import com.dinosaur.dinosaurexploder.view.GameOverDialog;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class GameActions {

  private final EnemySpawner enemySpawner;
  private final AsteroidsSpawner asteroidsSpawner;
  private final CollisionHandler collisionHandler;
  private final LevelManager levelManager;
  private final LanguageManager languageManager;
  private final Entity levelDisplay;
  private final Entity player;
  private final Entity life;
  private final Entity levelProgressBar;
  private final Entity bomb;
  private AllyComponent ally;
  private boolean isAllyUse = false;
  private final long sessionStartNanos;
  private boolean gameOverTriggered = false;
  private static final Logger LOGGER = Logger.getLogger(GameActions.class.getName());

  public GameActions(GameInitializer gameInitializer) {
    this.enemySpawner = gameInitializer.getEnemySpawner();
    this.asteroidsSpawner = gameInitializer.getAsteroidsSpawner();
    this.collisionHandler = gameInitializer.getCollisionHandler();
    this.levelManager = gameInitializer.getLevelManager();
    this.languageManager = gameInitializer.getLanguageManager();
    this.levelDisplay = gameInitializer.getLevelDisplay();
    this.player = gameInitializer.getPlayer();
    this.life = gameInitializer.getLife();
    this.levelProgressBar = gameInitializer.getLevelProgressBar();
    this.bomb = gameInitializer.getBomb();
    this.sessionStartNanos = System.nanoTime();
  }

  public void updateLevelDisplay() {
    Text levelText = (Text) levelDisplay.getViewComponent().getChildren().get(0);
    levelText.setText(
        languageManager.getTranslation("level").toUpperCase()
            + ": "
            + levelManager.getCurrentLevel());

    // Regenerate bombs when level changes
    if (bomb.hasComponent(BombComponent.class)) {
      bomb.getComponent(BombComponent.class)
          .checkLevelForBombRegeneration(levelManager.getCurrentLevel());
    }
  }

  /**
   * Summary : Detecting the player damage to decrease the lives and checking if the game is over
   */
  public void damagePlayer() {
    if (player == null || life == null) {

      LOGGER.log(Level.WARNING, "damagePlayer() called but player or life entity is null.");

      return;
    }

    if (player.getComponent(PlayerComponent.class).isInvincible()) {
      return;
    }
    if (gameOverTriggered) {
      return;
    }
    int lives = collisionHandler.getDamagedPlayerLife(life.getComponent(LifeComponent.class));
    var flash = new Rectangle(DinosaurGUI.WIDTH, DinosaurGUI.HEIGHT, Color.rgb(190, 10, 15, 0.5));
    getGameScene().addUINode(flash);
    runOnce(() -> getGameScene().removeUINode(flash), seconds(0.5));

    if (lives <= 0) {
      gameOverTriggered = true;
      // Added extra line of code to sync the lives counter after death
      // All hearts disappear after death
      life.getComponent(LifeComponent.class).onUpdate(lives);

      LOGGER.log(Level.INFO, "Game Over!");
      startGameOverSequence();
    } else {
      LOGGER.log(Level.INFO, "{0} lives remaining !", lives);
    }
  }

  public void damageAlly() {
    ally = collisionHandler.onAllyHit(ally);
    if (ally.getLife() == 0) {
      ally.getEntity().removeFromWorld();
      ally = null;
      player.getComponent(PlayerComponent.class).setAlly(ally);
    }
  }

  /**
   * Summary : Handles level progression when enemies are defeated and shows a message when the
   * level is changed
   */
  public void showLevelMessage() {
    // Hide the progress bar for boss levels if there are less than 2 bosses to defeat
    if (singleBoss()) {
      levelProgressBar.setVisible(false);
    }

    // Pause game elements during level transition
    pauseElement();

    // Display centered level notification
    Text levelText =
        getUIFactoryService()
            .newText(
                languageManager.getTranslation("level").toUpperCase()
                    + " "
                    + levelManager.getCurrentLevel(),
                Color.WHITE,
                24);
    levelText.setStroke(Color.BLACK);
    levelText.setStrokeWidth(1.5);
    TextUtils.centerText(levelText);
    getGameScene().addUINode(levelText);

    // Trigger bomb regeneration for level advancement
    regenerateBombe();

    // Resume gameplay after a delay
    runOnce(
        () -> {
          if (!singleBoss()) {
            levelProgressBar.setVisible(true);
          }

          getGameScene().removeUINode(levelText);
          updateLevelDisplay();

          if (levelProgressBar.hasComponent(LevelProgressBarComponent.class)) {
            levelProgressBar.getComponent(LevelProgressBarComponent.class).resetProgress();
          }

          FXGL.getGameWorld()
              .getEntitiesByType(EntityType.GREEN_DINO)
              .forEach(
                  e -> {
                    if (e.hasComponent(GreenDinoComponent.class)) {
                      e.getComponent(GreenDinoComponent.class).setPaused(false);
                    } else if (e.hasComponent(AsteroidsComponent.class)) {
                      e.getComponent(AsteroidsComponent.class).setPaused(false);
                    }
                  });

          enemySpawner.resumeEnemySpawning();
          asteroidsSpawner.resumeAsteroidsSpawning();

          player.getComponent(PlayerComponent.class).setInvincible(true);
          runOnce(
              () -> {
                if (player.isActive()) {
                  player.getComponent(PlayerComponent.class).setInvincible(false);
                }
              },
              seconds(3));
        },
        seconds(2));
  }

  public void pauseElement() {
    FXGL.getGameWorld()
        .getEntitiesByType(EntityType.GREEN_DINO)
        .forEach(
            e -> {
              if (e.hasComponent(GreenDinoComponent.class)) {
                e.getComponent(GreenDinoComponent.class).setPaused(true);
              } else if (e.hasComponent(AsteroidsComponent.class)) {
                e.getComponent(AsteroidsComponent.class).setPaused(true);
              }
            });
    enemySpawner.pauseEnemySpawning();
    asteroidsSpawner.pauseAsteroidsSpawning();
  }

  public void regenerateBombe() {
    if (bomb.hasComponent(BombComponent.class)) {
      bomb.getComponent(BombComponent.class)
          .checkLevelForBombRegeneration(levelManager.getCurrentLevel());
    }
  }

  private boolean singleBoss() {
    return levelManager.getBossesToDefeat() < 2 && levelManager.getCurrentLevel() % 5 == 0;
  }

  private void startGameOverSequence() {
    pauseElement();
    player.getComponent(PlayerComponent.class).setInvincible(true);
    player.getViewComponent().setOpacity(0);
    FXGL.spawn("explosion", player.getCenter());

    Text gameOverText =
        getUIFactoryService()
            .newText(
                languageManager.getTranslation("game_over").toUpperCase(), Color.ORANGERED, 30);
    gameOverText.setStroke(Color.BLACK);
    gameOverText.setStrokeWidth(1.5);
    TextUtils.centerText(gameOverText);
    getGameScene().addUINode(gameOverText);

    runOnce(
        () -> {
          getGameScene().removeUINode(gameOverText);
          gameOver();
        },
        Duration.seconds(1.5));
  }

  /** Summary : To detect whether the player lives are empty or not */
  public void gameOver() {
    new GameOverDialog(languageManager, levelManager, sessionStartNanos).createDialog();
  }

  public AllyComponent getAlly() {
    return ally;
  }

  public void setAlly(AllyComponent ally) {
    this.ally = ally;
  }

  public boolean isAllyUse() {
    return isAllyUse;
  }

  public void setAllyUse(boolean allyUse) {
    this.isAllyUse = allyUse;
  }
}
