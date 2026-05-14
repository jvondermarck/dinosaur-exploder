/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.controller.core;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;
import static javafx.util.Duration.seconds;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.dinosaur.dinosaurexploder.components.*;
import com.dinosaur.dinosaurexploder.constants.EntityType;
import com.dinosaur.dinosaurexploder.constants.GameMode;
import com.dinosaur.dinosaurexploder.model.CollisionHandler;
import com.dinosaur.dinosaurexploder.model.GameData;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import com.dinosaur.dinosaurexploder.utils.TextUtils;
import com.dinosaur.dinosaurexploder.view.DinosaurGUI;
import com.dinosaur.dinosaurexploder.view.GameOverDialog;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point2D;
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
  private double emissionRate;
  private final int numParticles;
  private final double emissionRateAugmentation;

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

    this.numParticles = 4;
    if (life.getComponent(LifeComponent.class).getLife() == 5) {
      this.emissionRateAugmentation = 0.1;
      this.emissionRate = -0.1;
    } else {
      this.emissionRateAugmentation = 0.2;
      this.emissionRate = 0;
    }

    this.sessionStartNanos = System.nanoTime();
  }

  public void updateLevelDisplay() {
    Text levelText = (Text) levelDisplay.getViewComponent().getChildren().get(0);
    levelText.setText(
            languageManager.getTranslation("level").toUpperCase()
                    + ": "
                    + levelManager.getCurrentLevel());

    if (bomb.hasComponent(BombComponent.class)) {
      bomb.getComponent(BombComponent.class)
              .checkLevelForBombRegeneration(levelManager.getCurrentLevel());
    }
  }

  /**
   * Summary : Detecting the player damage to decrease the lives and checking if the game is over
   *
   * ========== MODIFIED: Added invincibility frames with difficulty scaling and visual feedback ==========
   */
  public void damagePlayer() {
    if (player == null || life == null) {
      LOGGER.log(Level.WARNING, "damagePlayer() called but player or life entity is null.");
      return;
    }

    // Check if player is already invincible
    if (player.getComponent(PlayerComponent.class).isInvincible()) {
      return;
    }
    if (gameOverTriggered) {
      return;
    }

    // Apply damage
    int lives = collisionHandler.getDamagedPlayerLife(life.getComponent(LifeComponent.class));

    // ========== ADDED: Scale invincibility duration based on difficulty ==========
    double invincibleSeconds;
    GameMode currentDifficulty = GameData.getSelectedDifficulty();
    switch (currentDifficulty) {
      case EXPERT:
        invincibleSeconds = 1.0;  // Expert mode: 1 second invincibility
        break;
      default: // NORMAL
        invincibleSeconds = 1.5;  // Normal mode: 1.5 seconds invincibility
        break;
    }
    // ========== END ADDED ==========

    // ========== ADDED: Activate invincibility frames with visual feedback ==========
    PlayerComponent playerComp = player.getComponent(PlayerComponent.class);
    playerComp.setInvincible(true);  // This will start the blinking animation
    runOnce(
            () -> {
              if (player.isActive()) {
                playerComp.setInvincible(false);  // Disable invincibility after duration
              }
            },
            seconds(invincibleSeconds));
    // ========== END ADDED ==========

    // Visual flash effect (red screen flash)
    var flash = new Rectangle(DinosaurGUI.WIDTH, DinosaurGUI.HEIGHT, Color.rgb(190, 10, 15, 0.5));
    getGameScene().addUINode(flash);
    runOnce(() -> getGameScene().removeUINode(flash), seconds(0.5));

    emissionRate += emissionRateAugmentation;
    changeFire();

    if (lives <= 0) {
      gameOverTriggered = true;
      life.getComponent(LifeComponent.class).onUpdate(lives);
      LOGGER.log(Level.INFO, "Game Over!");
      startGameOverSequence();
    } else {
      LOGGER.log(Level.INFO, "{0} lives remaining !", lives);
    }
  }
  // ========== END MODIFIED ==========

  public void damageAlly() {
    ally = collisionHandler.onAllyHit(ally);
    if (ally.getLife() == 0) {
      ally.getEntity().removeFromWorld();
      ally = null;
      player.getComponent(PlayerComponent.class).setAlly(ally);
    }
  }

  public void healPlayer() {
    int lives = collisionHandler.getHealPlayerLife(life.getComponent(LifeComponent.class));
    emissionRate -= emissionRateAugmentation;
    changeFire();

    if (lives == 3 || lives >= 4) {
      player.removeComponent(ParticleComponent.class);
    }
  }

  public void showLevelMessage() {
    if (singleBoss()) {
      levelProgressBar.setVisible(false);
    }

    pauseElement();

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

    regenerateBombe();

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

  public void changeFire() {
    double minScale = 0.5;
    double maxScale = 1.5;
    double minDuration = 0.1;
    double maxDuration = 0.6;
    double minEmitterSize = 2;
    double maxEmitterSize = 4;
    ParticleEmitter emitter = ParticleEmitters.newFireEmitter();
    emitter.setMaxEmissions(Integer.MAX_VALUE);
    emitter.setSize(minEmitterSize, maxEmitterSize);
    emitter.setStartColor(Color.color(1.0, 0.5, 0.0, 1.0));
    emitter.setEndColor(Color.color(0.8, 0.1, 0.0, 0.0));
    emitter.setScaleFunction(
            i -> new Point2D(random(minScale, maxScale), random(minScale, maxScale)));
    emitter.setSpawnPointFunction(
            i -> new Point2D(random(0, player.getWidth()), random(0, player.getHeight())));
    emitter.setExpireFunction(i -> Duration.seconds(random(minDuration, maxDuration)));

    player.removeComponent(ParticleComponent.class);
    emitter.setEmissionRate(emissionRate);
    emitter.setNumParticles(numParticles);
    ParticleComponent particles = new ParticleComponent(emitter);
    player.addComponent(particles);
  }
}