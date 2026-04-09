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
import javafx.scene.text.TextFlow;

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

    TextFlow title =
        MenuHelper.createTitleFlow(languageManager.getTranslation("game_over"), getAppWidth());
    Text subtitle =
        MenuHelper.createSubtitle(
            languageManager.getTranslation("new_game"), getAppWidth() * 0.75, false);

    VBox statsCard = createStatsCard();
    HBox actions = new HBox(20, createRestartButton(), createQuitButton());
    actions.setAlignment(Pos.CENTER);

    VBox layout = new VBox(24, title, subtitle, statsCard, actions);
    layout.setAlignment(Pos.CENTER);
    layout.setPadding(new Insets(30, 40, 30, 40));
    layout.setMaxWidth(getAppWidth() * 0.8);

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
            + "-fx-border-color: rgba(0, 255, 120, 0.7);"
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
            .newText(label.toUpperCase() + ":", Color.LIGHTGREEN, GameConstants.TEXT_SUB_DETAILS);
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
    quitButton.setOnAction(event -> getGameController().gotoMainMenu());
    return quitButton;
  }
}
