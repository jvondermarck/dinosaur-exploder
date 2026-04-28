/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.view;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;
import com.almasb.fxgl.ui.FontType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.dinosaur.dinosaurexploder.components.CollectedCoinsComponent;
import com.dinosaur.dinosaurexploder.components.ScoreComponent;
import com.dinosaur.dinosaurexploder.utils.MenuHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Developer-only debug menu for manually overriding game state during testing. This view should
 * never be exposed to players in production builds.
 */
public class DebugMenu extends FXGLMenu {

  private Text title;
  TextField scoreField;
  TextField highScoreField;
  TextField coinsField;
  private Button setScoreButton;
  private Button setHighScoreButton;
  private Button setCoinsButton;
  private Button backButton;
  private static final double CONTENT_SPACING = 18;

  public DebugMenu(ScoreComponent scoreComponent, CollectedCoinsComponent coinsComponent) {
    super(MenuType.GAME_MENU);
    buildMenu();
  }

  public void buildMenu() {
    VBox layout = createLayout();
    createTitle();
    createSetScoreButton();
    createSetHighScoreButton();
    createSetCoinsButton();
    createBackButton();

    layout
        .getChildren()
        .addAll(
            title,
            scoreField,
            setScoreButton,
            highScoreField,
            setHighScoreButton,
            coinsField,
            setCoinsButton,
            backButton);
    getContentRoot().getChildren().add(layout);
  }

  private VBox createLayout() {
    VBox layout = new VBox(CONTENT_SPACING);
    layout.setPadding(new Insets(20));
    layout.setAlignment(Pos.TOP_LEFT);
    layout.setStyle(
        "-fx-background-color: rgba(0,0,0,0.85); -fx-border-color: lime; -fx-border-width: 2;");
    layout.setMaxWidth(400);
    return layout;
  }

  private Text createTitle() {
    title =
    getUIFactoryService()
            .newText(
                "DEBUG MENU [DEV ONLY]",
                Color.LIME,
                FontType.MONO,
                GameConstants.TEXT_SUB_DETAILS);
    return title;
  }

  private Button createSetScoreButton() {
    scoreField = new TextField();
    scoreField.setFont (Font.font(GameConstants.GAME_FONT_NAME, 20));
    scoreField.setPromptText("ENTER SCORE VALUE...");
    setScoreButton = getUIFactoryService().newButton("SET SCORE");
    setScoreButton.setPrefWidth(1500);
    setScoreButton.setOnAction(
        e -> {
          // TODO: wire up to scoreComponent.setScore(int)
        });
    return setScoreButton;
  }

  private Button createSetHighScoreButton() {
    highScoreField = new TextField();
    highScoreField.setFont (Font.font(GameConstants.GAME_FONT_NAME, 20));
    highScoreField.setPromptText("ENTER HIGH SCORE VALUE...");
    setHighScoreButton = getUIFactoryService().newButton("SET HIGH SCORE");
    setHighScoreButton.setPrefWidth(1500);
    setHighScoreButton.setOnAction(
        e -> {
          // TODO: wire up to HighScore.setHigh(...)

        });
    return setHighScoreButton;
  }

  private Button createSetCoinsButton() {
    coinsField = new TextField();
    coinsField.setFont (Font.font(GameConstants.GAME_FONT_NAME, 20));
    coinsField.setPromptText("ENTER COIN AMOUNT...");
    setCoinsButton = getUIFactoryService().newButton("SET COINS");
    setCoinsButton.setPrefWidth(1500);
    setCoinsButton.setOnAction(
        e -> {
          // TODO: requires CollectedCoinsComponent.setCoin(int) to be added first
        });
    return setCoinsButton;
  }

  private Button createBackButton() {
    backButton = MenuHelper.createStyledButton("Back");
    backButton.setOnAction(event -> fireResume());
    return backButton;
  }
}
