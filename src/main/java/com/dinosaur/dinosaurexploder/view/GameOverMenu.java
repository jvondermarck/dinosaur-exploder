/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.view;

import static com.almasb.fxgl.dsl.FXGL.getGameController;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.model.GameOverStats;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.MenuHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class GameOverMenu extends FXGLMenu {

  private final LanguageManager languageManager;
  private final GameOverStats stats;

  public GameOverMenu(LanguageManager languageManager, GameOverStats stats) {
    super(MenuType.GAME_MENU);
    this.languageManager = languageManager;
    this.stats = stats;
    buildMenu();
  }

  private void buildMenu() {
    ImageView background = MenuHelper.createAnimatedBackground(getAppWidth(), getAppHeight());
    Rectangle overlay = new Rectangle(getAppWidth(), getAppHeight(), Color.color(0, 0, 0, 0.82));

    String gameOverRaw = languageManager.getTranslation("game_over")
        .replaceAll("[.!?\\s]+$", "");
    Text gameOverText =
        com.almasb.fxgl.dsl.FXGL.getUIFactoryService()
            .newText(gameOverRaw.toUpperCase(), Color.RED, 50);

    HBox titleRow = new HBox(gameOverText);
    titleRow.setAlignment(Pos.CENTER);
    titleRow.setPrefWidth(getAppWidth());

    Text subtitle =
        com.almasb.fxgl.dsl.FXGL.getUIFactoryService()
            .newText(
                languageManager.getTranslation("new_game"), Color.WHITE, GameConstants.TEXT_SUB_DETAILS);
    subtitle.setWrappingWidth(getAppWidth() * 0.75);
    subtitle.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

    VBox statsCard = createStatsCard();
    HBox actions = new HBox(20, createRestartButton(), createQuitButton());
    actions.setAlignment(Pos.CENTER);

    VBox inner = new VBox(24, subtitle, statsCard, actions);
    inner.setAlignment(Pos.CENTER);
    inner.setPadding(new Insets(0, 40, 30, 40));
    inner.setMaxWidth(getAppWidth() * 0.8);

    VBox layout = new VBox(24, titleRow, inner);
    layout.setAlignment(Pos.CENTER);
    layout.setPadding(new Insets(30, 0, 0, 0));

    StackPane container = new StackPane(layout);
    container.setPrefSize(getAppWidth(), getAppHeight());
    container.setAlignment(Pos.CENTER);

    getContentRoot().getChildren().addAll(background, overlay, container);
  }

  private VBox createStatsCard() {
    VBox statsCard =
        new VBox(
            12,
            createStatLine(
                languageManager.getTranslation("score"), String.valueOf(stats.finalScore())),
            createStatLine(
                languageManager.getTranslation("high_score"), String.valueOf(stats.highScore())),
            createStatLine(
                languageManager.getTranslation("level"), String.valueOf(stats.levelReached())),
            createStatLine(
                languageManager.getTranslation("time_played"), stats.formatElapsedTime()));
    statsCard.setAlignment(Pos.CENTER_LEFT);
    statsCard.setPadding(new Insets(24));
    statsCard.setMaxWidth(430);
    statsCard.setStyle(
        "-fx-background-color: rgba(0, 0, 0, 0.78);"
            + "-fx-border-color: rgba(255, 255, 255, 0.7);"
            + "-fx-border-width: 2;"
            + "-fx-border-radius: 12;"
            + "-fx-background-radius: 12;");
    return statsCard;
  }

  private HBox createStatLine(String label, String value) {
    Text labelText =
        com.almasb
            .fxgl
            .dsl
            .FXGL
            .getUIFactoryService()
            .newText(label.toUpperCase() + ":", Color.WHITE, GameConstants.TEXT_SUB_DETAILS);
    Text valueText =
        com.almasb
            .fxgl
            .dsl
            .FXGL
            .getUIFactoryService()
            .newText(value, Color.WHITE, GameConstants.TEXT_SUB_DETAILS);

    HBox row = new HBox(12, labelText, valueText);
    row.setAlignment(Pos.CENTER_LEFT);
    return row;
  }

  private Button createRestartButton() {
    Button restartButton = MenuHelper.createStyledButton(languageManager.getTranslation("start"));
    restartButton.setOnAction(event -> getGameController().startNewGame());
    return restartButton;
  }

  private Button createQuitButton() {
    Button quitButton = MenuHelper.createStyledButton(languageManager.getTranslation("quit_game"));
    quitButton.setStyle(
        "-fx-font-family: 'Public Pixel';"
            + "-fx-font-size: 15px;"
            + "-fx-background-color: linear-gradient(to bottom, #2a0000, #1a0000),"
            + "linear-gradient(to bottom, rgba(220, 0, 0, 0.9), rgba(160, 0, 0, 0.6));"
            + "-fx-background-radius: 37;"
            + "-fx-border-color: rgba(220, 0, 0, 1), rgba(180, 0, 0, 0.8);"
            + "-fx-border-width: 2;"
            + "-fx-border-radius: 37;"
            + "-fx-text-fill: white;"
            + "-fx-effect: dropshadow(gaussian, rgba(220, 0, 0, 0.7), 10, 0.5, 0, 0);"
            + "-fx-padding: 5 15 5 15;");
    quitButton.setOnMouseEntered(
        e ->
            quitButton.setStyle(
                "-fx-font-family: 'Public Pixel';"
                    + "-fx-font-size: 15px;"
                    + "-fx-background-color: linear-gradient(to bottom, #1a0000, #2a0000),"
                    + "linear-gradient(to bottom, rgba(255, 0, 0, 0.9), rgba(200, 0, 0, 0.7));"
                    + "-fx-background-radius: 37;"
                    + "-fx-border-color: rgba(255, 0, 0, 1), rgba(200, 0, 0, 0.8);"
                    + "-fx-border-width: 2;"
                    + "-fx-border-radius: 37;"
                    + "-fx-text-fill: white;"
                    + "-fx-effect: dropshadow(gaussian, rgba(255, 0, 0, 0.9), 15, 0.6, 0, 0);"
                    + "-fx-padding: 5 15 5 15;"));
    quitButton.setOnMouseExited(
        e ->
            quitButton.setStyle(
                "-fx-font-family: 'Public Pixel';"
                    + "-fx-font-size: 15px;"
                    + "-fx-background-color: linear-gradient(to bottom, #2a0000, #1a0000),"
                    + "linear-gradient(to bottom, rgba(220, 0, 0, 0.9), rgba(160, 0, 0, 0.6));"
                    + "-fx-background-radius: 37;"
                    + "-fx-border-color: rgba(220, 0, 0, 1), rgba(180, 0, 0, 0.8);"
                    + "-fx-border-width: 2;"
                    + "-fx-border-radius: 37;"
                    + "-fx-text-fill: white;"
                    + "-fx-effect: dropshadow(gaussian, rgba(220, 0, 0, 0.7), 10, 0.5, 0, 0);"
                    + "-fx-padding: 5 15 5 15;"));
    quitButton.setOnAction(event -> getGameController().gotoMainMenu());
    return quitButton;
  }
}
