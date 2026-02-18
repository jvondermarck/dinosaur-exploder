/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.dinosaur.dinosaurexploder.achievements.AchievementManager;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.interfaces.Score;
import com.dinosaur.dinosaurexploder.model.HighScore;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import java.io.*;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class ScoreComponent extends Component implements Score {
  private int score = 0;
  private static HighScore highScore = new HighScore();
  private final LanguageManager languageManager = LanguageManager.getInstance();

  private Text scoreText;
  private Text highScoreText;

  @Override
  public void onAdded() {
    loadHighScore();
    createScoreUI();
    updateTexts();

    languageManager.selectedLanguageProperty().addListener((obs, oldVal, newVal) -> updateTexts());
  }

  @Override
  public void onUpdate(double tpf) {
    updateTexts();
  }

  private void createScoreUI() {
    // ✅ Utilise FXGL UI Factory pour la police
    scoreText =
        FXGL.getUIFactoryService().newText("", Color.YELLOW, GameConstants.TEXT_SIZE_GAME_INFO);

    highScoreText =
        FXGL.getUIFactoryService().newText("", Color.YELLOW, GameConstants.TEXT_SIZE_GAME_INFO);

    ImageView dinoIcon =
        new ImageView(new Image(GameConstants.GREEN_DINO_IMAGE_PATH, 25, 20, false, false));

    HBox scoreBox = new HBox(5, scoreText, dinoIcon);
    scoreBox.setAlignment(Pos.CENTER_LEFT);

    VBox layout = new VBox(5, scoreBox, highScoreText);
    layout.setAlignment(Pos.TOP_LEFT);

    entity.getViewComponent().addChild(layout);
  }

  private void updateTexts() {
    scoreText.setText(languageManager.getTranslation("score").toUpperCase() + ": " + score);
    highScoreText.setText(
        languageManager.getTranslation("high_score").toUpperCase() + ": " + highScore.getHigh());
  }

  private void loadHighScore() {
    try (ObjectInputStream in =
        new ObjectInputStream(new FileInputStream(GameConstants.HIGH_SCORE_FILE))) {
      highScore = (HighScore) in.readObject();
    } catch (IOException | ClassNotFoundException e) {
      highScore = new HighScore();
    }
  }

  private void saveHighScore() {
    try (ObjectOutputStream out =
        new ObjectOutputStream(new FileOutputStream(GameConstants.HIGH_SCORE_FILE))) {
      out.writeObject(highScore);
    } catch (IOException e) {
      System.err.println("Error saving high score: " + e.getMessage());
    }
  }

  @Override
  public int getScore() {
    return score;
  }

  @Override
  public void setScore(int score) {
    this.score = score;
  }

  @Override
  public void incrementScore(int increment) {
    score += increment;

    if (score > highScore.getHigh()) {
      highScore = new HighScore(score);
      saveHighScore();

      // Notify achievements about score change
      AchievementManager achievementManager =
          FXGL.getWorldProperties().getValue("achievementManager");
      if (achievementManager != null) {
        achievementManager.notifyScoreChanged(score);
      }
    }
  }
}
