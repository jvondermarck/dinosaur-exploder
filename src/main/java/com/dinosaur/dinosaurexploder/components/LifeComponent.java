/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.components;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

import com.almasb.fxgl.entity.component.Component;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.interfaces.Life;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * Summary : This handles the life component of the Player implements the life interface and extends
 * the Component
 */
public class LifeComponent extends Component implements Life {
  private static final int DEFAULT_MAX_LIVES = 3;
  private static final int DEFAULT_LIVES = 3;

  private Image heart;
  private Image heartLost;

  private int maxLives = DEFAULT_MAX_LIVES;
  private int currentLives = DEFAULT_LIVES;

  // Declaring Lives Text
  private Text lifeText;
  private List<ImageView> hearts = new ArrayList<>();

  private final LanguageManager languageManager = LanguageManager.getInstance();

  @Override
  public void onAdded() {
    heart = new Image(GameConstants.HEART_IMAGE_PATH);
    heartLost = new Image(GameConstants.HEART_LOST_IMAGE_PATH);

    lifeText =
        getUIFactoryService()
            .newText(
                languageManager.getTranslation(GameConstants.LIVES).toUpperCase(),
                Color.RED,
                GameConstants.TEXT_SIZE_GAME_INFO);

    setMaxLives(DEFAULT_MAX_LIVES);
    currentLives = maxLives;

    // Listen for language changes and update UI automatically
    languageManager.selectedLanguageProperty().addListener((obs, oldVal, newVal) -> updateTexts());

    // Initial display update
    updateLifeDisplay();
  }

  @Override
  public void onUpdate(double ptf) {
    updateLifeDisplay(); // Update hearts and text display every frame
  }

  private void updateTexts() {
    lifeText.setText(
        languageManager.getTranslation(GameConstants.LIVES).toUpperCase() + ": " + currentLives);
  }

  private void updateLifeDisplay() {
    // Clear previous entities
    clearEntity();

    // Set the appropriate number of hearts based on `life`
    for (int i = maxLives; i > 0; i--) {
      ImageView currentHeart = hearts.get(maxLives - i);
      if (i > currentLives) {
        currentHeart.setImage(heartLost);
      } else {
        currentHeart.setImage(heart);
      }

      currentHeart.setLayoutY(10);
      currentHeart.setLayoutX((maxLives - i) * 30.0);
      setEntity(currentHeart);
    }

    // Display the lifeText component
    lifeText.setText(
        languageManager.getTranslation(GameConstants.LIVES).toUpperCase() + ": " + currentLives);
    setEntity(lifeText);
  }

  // Created two methods for shorter and cleaner code
  public void setEntity(Node j) {
    entity.getViewComponent().addChild(j);
  }

  public void clearEntity() {
    entity.getViewComponent().clearChildren();
  }

  public int getMaxLives() {
    return maxLives;
  }

  public void setMaxLives(int maxLives) {
    this.maxLives = maxLives;

    hearts.clear();
    for (int i = 0; i < maxLives; i++) {
      hearts.add(new ImageView(heart));
    }

    updateLifeDisplay();
  }

  public void setCurrentLives(int currentLives) {
    this.currentLives = currentLives;

    updateLifeDisplay();
  }

  /**
   * Summary : This method is overriding the superclass method to increase the life to the current
   * life without exceeding the maximum number of lives allowed
   */
  @Override
  public int increaseLife(int i) {
    currentLives = Math.min(currentLives + i, maxLives);
    return currentLives;
  }

  /**
   * Summary : This method is overriding the superclass method to decrease the life to the current
   * life
   */
  @Override
  public int decreaseLife(int i) {
    currentLives -= i;
    return currentLives;
  }

  public int getLife() {
    return currentLives;
  }
}
