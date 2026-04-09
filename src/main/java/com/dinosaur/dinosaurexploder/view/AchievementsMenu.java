/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.view;

import static com.almasb.fxgl.dsl.FXGL.getWorldProperties;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.dinosaur.dinosaurexploder.achievements.Achievement;
import com.dinosaur.dinosaurexploder.achievements.AchievementManager;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.MenuHelper;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class AchievementsMenu extends FXGLMenu {

  private static final double CONTENT_WIDTH = 460;
  private static final double CONTENT_HEIGHT = 420;
  private static final Logger LOGGER = Logger.getLogger(AchievementsMenu.class.getName());

  private final LanguageManager languageManager = LanguageManager.getInstance();
  private final AchievementManager achievementManager = loadAchievements();

  public AchievementsMenu() {
    super(MenuType.MAIN_MENU);
    buildMenu();
  }

  private AchievementManager loadAchievements() {
    try {
      AchievementManager manager = getWorldProperties().getValue("achievementManager");
      if (manager != null) {
        return manager;
      }
    } catch (RuntimeException e) {
      LOGGER.log(Level.FINE, "Falling back to local achievement manager: {0}", e.getMessage());
    }

    AchievementManager fallbackManager = new AchievementManager();
    fallbackManager.init();
    return fallbackManager;
  }

  private void buildMenu() {
    var background = MenuHelper.createAnimatedBackground(getAppWidth(), getAppHeight());
    TextFlow title =
        MenuHelper.createTitleFlow(languageManager.getTranslation("achievements"), getAppWidth());
    Text subtitle =
        MenuHelper.createSubtitle(
            languageManager.getTranslation("achievements_subtitle"), getAppWidth() * 0.75, false);

    ScrollPane content = createContent();
    Button backButton = MenuHelper.createStyledButton(languageManager.getTranslation("back"));
    backButton.setOnAction(event -> fireResume());

    VBox layout = new VBox(20, title, subtitle, content, backButton);
    layout.setAlignment(Pos.CENTER);
    layout.setPadding(new Insets(30, 40, 30, 40));
    layout.setPrefWidth(getAppWidth());
    layout.setPrefHeight(getAppHeight());

    getContentRoot().getChildren().addAll(background, layout);
  }

  private ScrollPane createContent() {
    List<Achievement> achievements = achievementManager.getActiveAchievements();
    List<Achievement> completed = achievements.stream().filter(Achievement::isCompleted).toList();
    List<Achievement> toDo =
        achievements.stream().filter(achievement -> !achievement.isCompleted()).toList();

    VBox sections =
        new VBox(
            20,
            createSection(languageManager.getTranslation("achievements_todo"), toDo, false),
            createSection(
                languageManager.getTranslation("achievements_completed"), completed, true));
    sections.setPadding(new Insets(15));
    sections.setFillWidth(true);

    ScrollPane pane = new ScrollPane(sections);
    pane.setFitToWidth(true);
    pane.setPrefWidth(CONTENT_WIDTH);
    pane.setMaxWidth(CONTENT_WIDTH);
    pane.setPrefViewportHeight(CONTENT_HEIGHT);
    pane.setStyle(
        "-fx-background: rgba(0, 0, 0, 0.8);"
            + "-fx-background-color: rgba(0, 0, 0, 0.8);"
            + "-fx-border-color: rgba(0, 220, 0, 0.7);"
            + "-fx-border-width: 2;"
            + "-fx-border-radius: 10;"
            + "-fx-background-radius: 10;");
    pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    pane.getStylesheets()
        .add(
            Objects.requireNonNull(getClass().getResource("/styles/scrollbar.css"))
                .toExternalForm());
    return pane;
  }

  private VBox createSection(String title, List<Achievement> achievements, boolean completed) {
    Text heading = MenuHelper.createSubtitle(title, CONTENT_WIDTH - 40, false);
    VBox cards = new VBox(12);
    cards.setFillWidth(true);

    if (achievements.isEmpty()) {
      Text emptyLabel =
          getUIFactoryService()
              .newText(
                  languageManager.getTranslation("achievements_empty"),
                  Color.rgb(180, 255, 180),
                  GameConstants.TEXT_SUB_DETAILS);
      emptyLabel.setWrappingWidth(CONTENT_WIDTH - 80);
      cards.getChildren().add(emptyLabel);
    } else {
      achievements.forEach(
          achievement -> cards.getChildren().add(createAchievementCard(achievement, completed)));
    }

    VBox section = new VBox(10, heading, cards);
    section.setPadding(new Insets(10, 0, 0, 0));
    return section;
  }

  private VBox createAchievementCard(Achievement achievement, boolean completed) {
    Text description =
        getUIFactoryService()
            .newText(achievement.getDescription(), Color.LIME, GameConstants.TEXT_SUB_DETAILS);
    description.setWrappingWidth(CONTENT_WIDTH - 80);

    Text reward =
        getUIFactoryService()
            .newText(
                languageManager.getTranslation("achievement_reward")
                    + ": "
                    + achievement.getRewardCoins()
                    + " "
                    + languageManager.getTranslation("achievement_reward_coins").toLowerCase(),
                Color.rgb(180, 255, 180),
                GameConstants.TEXT_SIZE_GAME_INFO);
    reward.setWrappingWidth(CONTENT_WIDTH - 80);

    Text status =
        getUIFactoryService()
            .newText(
                completed
                    ? languageManager.getTranslation("achievements_completed")
                    : languageManager.getTranslation("achievements_todo"),
                completed ? Color.CYAN : Color.ORANGE,
                GameConstants.TEXT_SIZE_GAME_INFO);
    status.setWrappingWidth(CONTENT_WIDTH - 80);

    VBox card = new VBox(8, description, reward, status);
    card.setPadding(new Insets(14));
    card.setFillWidth(true);
    card.setStyle(
        "-fx-background-color: rgba(0, 0, 0, 0.72);"
            + "-fx-border-color: "
            + (completed ? "rgba(0, 255, 255, 0.55);" : "rgba(255, 165, 0, 0.55);")
            + "-fx-border-width: 2;"
            + "-fx-border-radius: 10;"
            + "-fx-background-radius: 10;");
    return card;
  }
}
