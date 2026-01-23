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
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class GameActions {

    private final EnemySpawner enemySpawner;
    private final CollisionHandler collisionHandler;
    private final LevelManager levelManager;
    private final LanguageManager languageManager;
    private final Entity levelDisplay;
    private final Entity player;
    private final Entity life;
    private final Entity levelProgressBar;
    private final Entity bomb;

    public GameActions(GameInitializer gameInitializer) {
        this.enemySpawner = gameInitializer.getEnemySpawner();
        this.collisionHandler = gameInitializer.getCollisionHandler();
        this.levelManager = gameInitializer.getLevelManager();
        this.languageManager = gameInitializer.getLanguageManager();
        this.levelDisplay = gameInitializer.getLevelDisplay();
        this.player = gameInitializer.getPlayer();
        this.life = gameInitializer.getLife();
        this.levelProgressBar = gameInitializer.getLevelProgressBar();
        this.bomb = gameInitializer.getBomb();
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
            System.err.println("damagePlayer() called but player or life entity is null.");
            return;
        }

        if (player.getComponent(PlayerComponent.class).isInvincible()) {
            return;
        }
        int lives = collisionHandler.getDamagedPlayerLife(life.getComponent(LifeComponent.class));
        var flash = new Rectangle(DinosaurGUI.WIDTH, DinosaurGUI.HEIGHT, Color.rgb(190, 10, 15, 0.5));
        getGameScene().addUINode(flash);
        runOnce(() -> getGameScene().removeUINode(flash), seconds(0.5));

        if (lives <= 0) {
            // Added extra line of code to sync the lives counter after death
            // All hearts disappear after death
            life.getComponent(LifeComponent.class).onUpdate(lives);
            System.out.println("Game Over!");
            gameOver();
        } else {
            System.out.printf("%d lives remaining ! ", lives);
        }
    }

    /**
     * Summary : Handles level progression when enemies are defeated and shows a message when the
     * level is changed
     */
    public void showLevelMessage() {
        // Hide the progress bar for boss levels
        if (levelManager.getCurrentLevel() % 5 == 0) {
            levelProgressBar.setVisible(false);
        }

        // Pause game elements during level transition
        FXGL.getGameWorld()
                .getEntitiesByType(EntityType.GREEN_DINO)
                .forEach(
                        e -> {
                            if (e.hasComponent(GreenDinoComponent.class)) {
                                e.getComponent(GreenDinoComponent.class).setPaused(true);
                            }
                        });

        enemySpawner.pauseEnemySpawning();

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
        if (bomb.hasComponent(BombComponent.class)) {
            bomb.getComponent(BombComponent.class)
                    .checkLevelForBombRegeneration(levelManager.getCurrentLevel());
        }

        // Resume gameplay after a delay
        runOnce(
                () -> {
                    if (levelManager.getCurrentLevel() % 5 != 0) {
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
                                        }
                                    });

                    enemySpawner.resumeEnemySpawning();

                    player.getComponent(PlayerComponent.class).setInvincible(true);
                    runOnce(
                            () -> {
                                if (player != null && player.isActive()) {
                                    player.getComponent(PlayerComponent.class).setInvincible(false);
                                }
                            },
                            seconds(3));
                },
                seconds(2));
    }

    /**
     * Summary : To detect whether the player lives are empty or not
     */
    public void gameOver() {
        new GameOverDialog(languageManager).createDialog();
    }
}
