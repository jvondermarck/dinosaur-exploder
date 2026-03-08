/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.components;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.interfaces.Bomb;
import com.dinosaur.dinosaurexploder.model.GameData;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class BombComponent extends Component implements Bomb {
  private static final int DEFAULT_MAXIMUM_BOMB_COUNT = 3;
  private static final int DEFAULT_BOMB_COUNT = 3;

  private int currentBombCount = DEFAULT_BOMB_COUNT;
  private int maximumBombCount = DEFAULT_MAXIMUM_BOMB_COUNT;

  private Image spaceshipImage;
  private final int selectedShip;

  // Tracking variables for regeneration
  private int lastLevel = 1;
  private int coinCounter = 0;
  private static final int COINS_NEEDED_FOR_BOMB =
      15; // Number of coins needed to regenerate a bomb

  public BombComponent() {
    // Selected spaceship from GameData
    this.selectedShip = GameData.getSelectedShip();
  }

  private Image bombImage;
  private HBox bombContainer;
  private List<ImageView> bombImages;

  private Text bombText;

  // Logging
  Logger logger = Logger.getLogger(BombComponent.class.getName());

  private final LanguageManager languageManager = LanguageManager.getInstance();

  @Override
  public void onAdded() {
    bombImages = new ArrayList<>();
    bombImage = new Image(GameConstants.BOMB_IMAGE_PATH);
    bombText = getUIFactoryService().newText("", Color.ORANGE, GameConstants.TEXT_SIZE_GAME_INFO);

    // Listen for language changes
    languageManager.selectedLanguageProperty().addListener((obs, oldVal, newVal) -> updateTexts());

    // Initial bomb UI setup
    Node bombUI = createBombUI();
    entity.getViewComponent().addChild(bombUI);

    currentBombCount = DEFAULT_BOMB_COUNT;
    setMaximumBombCount(DEFAULT_MAXIMUM_BOMB_COUNT);
  }

  /**
   * This method creates the UI for displaying the bomb count and bomb images.
   *
   * @return Node - The created bomb UI node
   */
  private Node createBombUI() {
    bombContainer = new HBox(5);
    bombContainer.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

    VBox container = new VBox(5, bombText, bombContainer);
    container.setAlignment(javafx.geometry.Pos.TOP_LEFT);

    initializeBombImages();

    return container;
  }

  @Override
  public void onUpdate(double tpf) {
    updateBombUI(); // Update the bomb UI every frame based on bombCount
  }

  private void updateTexts() {
    bombText.setText(
        languageManager.getTranslation(GameConstants.BOMBS_LEFT).toUpperCase()
            + ": "
            + currentBombCount);
  }

  /** Updates the bomb UI based on the current bomb count. */
  protected void updateBombUI() {
    for (int i = 0; i < maximumBombCount; i++) {
      if (currentBombCount <= i) {
        System.out.println("Removing bomb image: " + i);
      }
      bombImages.get(i).setVisible(currentBombCount > i);
    }

    // Update bomb text with the remaining bombs
    bombText.setText(
        languageManager.getTranslation(GameConstants.BOMBS_LEFT).toUpperCase()
            + ": "
            + currentBombCount);
  }

  private void initializeBombImages() {
    for (ImageView bombImageView : bombImages) {
      bombContainer.getChildren().remove(bombImageView);
    }

    bombImages.clear();

    for (int i = 0; i < maximumBombCount; i++) {
      ImageView bombImageView = new ImageView(bombImage);
      bombContainer.getChildren().add(bombImageView);

      bombImages.add(bombImageView);
    }
  }

  /** Summary: This method returns the current number of bombs. */
  @Override
  public int getBombCount() {
    return currentBombCount;
  }

  /**
   * Summary : This method is used to launch a row of bullets as a bomb. Parameters : Entity player
   * - The player entity using the bomb
   */
  @Override
  public void useBomb(Entity player) {
    if (getBombCount() > 0) {
      currentBombCount--;
      updateBombUI();
      spawnBombBullets(player);
    } else {
      logger.info("No bombs left!");
    }
  }

  /**
   * Summary : This method spawns a row of bullets from the player's position. Parameters : Entity
   * player - The player entity from which to spawn the bullets
   */
  protected void spawnBombBullets(Entity player) {
    Point2D center = player.getCenter();
    Image projImg = new Image(GameConstants.BASE_PROJECTILE_IMAGE_PATH);

    if (selectedShip != 0) {
      String shipImagePath = "/assets/textures/spaceship" + selectedShip + ".png";
      logger.log(Level.INFO, "Selected spaceship: {0}", selectedShip);
      this.spaceshipImage = new Image(shipImagePath);
    }

    for (int i = -5; i <= 5; i++) {
      double angle = player.getRotation() - 90 + i * 10;
      Vec2 direction = Vec2.fromAngle(angle);
      spawn(
          "basicProjectile",
          new SpawnData(
                  center.getX() - (projImg.getWidth() / 2) + 3,
                  center.getY() - spaceshipImage.getHeight() / 2)
              .put("direction", direction.toPoint2D()));
    }
    logger.log(Level.INFO, "Bomb used! {0} bombs left!", getBombCount());
  }

  /**
   * Regenerates bombs when a new level is reached. Call this method when the player advances to a
   * new level.
   *
   * @param currentLevel The current level of the game
   */
  public void checkLevelForBombRegeneration(int currentLevel) {
    if (currentLevel > lastLevel) {
      // Player has advanced to a new level, regenerate one bomb
      regenerateBomb();
      lastLevel = currentLevel;
      logger.log(Level.INFO, "Level up! Regenerated a bomb. Current bombs: {0}", currentBombCount);
    }
  }

  /**
   * Tracks coin collection and regenerates bombs when enough coins are collected. Call this method
   * whenever the player collects a coin.
   */
  public void trackCoinForBombRegeneration() {
    coinCounter++;
    if (coinCounter >= COINS_NEEDED_FOR_BOMB) {
      // Player has collected enough coins, regenerate one bomb
      regenerateBomb();
      coinCounter = 0; // Reset counter
      logger.log(
          Level.INFO,
          "Collected {0} coins! Regenerated a bomb. Current bombs: {1}",
          new Object[] {COINS_NEEDED_FOR_BOMB, currentBombCount});
    }
  }

  /** Regenerates the specified number of bombs, not exceeding the maximum. */
  private void regenerateBomb() {
    currentBombCount = Math.min(currentBombCount + 1, maximumBombCount);
    updateBombUI();
  }

  public int getMaximumBombCount() {
    return maximumBombCount;
  }

  public void setMaximumBombCount(int maximumBombCount) {
    this.maximumBombCount = maximumBombCount;
    initializeBombImages();
    updateBombUI();
  }

  public int getCurrentBombCount() {
    return currentBombCount;
  }

  public void setCurrentBombCount(int currentBombCount) {
    this.currentBombCount = currentBombCount;
    updateBombUI();
  }
}
