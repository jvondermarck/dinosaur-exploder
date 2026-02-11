/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */
package com.dinosaur.dinosaurexploder.view;

import static com.almasb.fxgl.dsl.FXGL.getDialogService;
import static com.almasb.fxgl.dsl.FXGL.getGameController;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

import com.dinosaur.dinosaurexploder.components.ScoreComponent;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class GameOverDialog {

  private final LanguageManager languageManager;

  public GameOverDialog(LanguageManager languageManager) {
    this.languageManager = languageManager;
  }

  public void createDialog() {
    Button btnYes = getUIFactoryService().newButton(languageManager.getTranslation("yes"));
    btnYes.setMinWidth(200);
    btnYes.setOnAction(e -> getGameController().startNewGame());

    Button btnNo = getUIFactoryService().newButton(languageManager.getTranslation("no"));
    btnNo.setMinWidth(200);
    btnNo.setOnAction(e -> getGameController().gotoMainMenu());

    // Get score
    int finalScore = 0;
    try {
      var scoreEntities = getGameWorld().getEntitiesByComponent(ScoreComponent.class);
      if (!scoreEntities.isEmpty()) {
        ScoreComponent sc = scoreEntities.get(0).getComponent(ScoreComponent.class);
        finalScore = sc.getScore();
      }
    } catch (Exception ignored) {
    }

    // Display question text
    var questionText =
        getUIFactoryService()
            .newText(
                languageManager.getTranslation("new_game"),
                Color.WHITE,
                GameConstants.TEXT_SUB_DETAILS);
    questionText.setWrappingWidth(450);
    questionText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
    questionText.setLineSpacing(8);

    // Display text score
    Text scoreText =
        getUIFactoryService()
            .newText(
                languageManager.getTranslation("score") + ": " + finalScore,
                Color.YELLOW,
                GameConstants.TEXT_SUB_DETAILS);

    VBox box = new VBox(20, questionText, scoreText);
    box.setAlignment(Pos.CENTER);
    box.setPadding(new javafx.geometry.Insets(20));
    box.setMinWidth(500);

    getDialogService()
        .showBox(languageManager.getTranslation("game_over").toUpperCase(), box, btnYes, btnNo);
  }
}
