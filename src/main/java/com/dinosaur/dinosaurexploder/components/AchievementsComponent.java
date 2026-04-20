/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.components;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;
import static com.almasb.fxgl.dsl.FXGL.getWorldProperties;

import com.dinosaur.dinosaurexploder.achievements.Achievement;
import com.dinosaur.dinosaurexploder.achievements.AchievementManager;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.MenuHelper;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * Reusable component for displaying the achievements list.
 * Eliminates duplication between AchievementsMenu and PauseMenu.
 */
public class AchievementsComponent {

  private static final double CONTENT_WIDTH = 440;
  private static final double CONTENT_HEIGHT = 400;
  private static final double CONTENT_SPACING = 18;
  private static final Insets SECTIONS_PADDING = new Insets(12);
  private static final Insets CARD_PADDING = new Insets(14);
  private static final double HORIZONTAL_SCROLL_LOCK = 0.0;
  private static final Logger LOGGER = Logger.getLogger(AchievementsComponent.class.getName());

  private AchievementsComponent() {
    // Utility class – not instantiable
  }

  /**
   * Loads the AchievementManager from world properties, falling back to a local instance
   * if the game world is not available (e.g. from the main menu).
   */
  public static AchievementManager loadAchievements() {
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

  /**
   * Creates and returns the scrollable achievements content pane.
   * Can be embedded in any menu (AchievementsMenu, PauseMenu overlay, etc.).
   */
  public static ScrollPane createContent(AchievementManager achievementManager) {
    LanguageManager languageManager = LanguageManager.getInstance();
    List<Achievement> achievements = achievementManager.getActiveAchievements();
    List<Achievement> completed = achievements.stream().filter(Achievement::isCompleted).toList();
    List<Achievement> toDo =
        achievements.stream().filter(achievement -> !achievement.isCompleted()).toList();

    VBox sections =
        new VBox(
            CONTENT_SPACING,
            createSection(languageManager.getTranslation("achievements_todo"), toDo, false),
            createSection(
                languageManager.getTranslation("achievements_completed"), completed, true));
    sections.setPadding(SECTIONS_PADDING);
    sections.setFillWidth(true);
    sections.setMinWidth(0);

    ScrollPane pane = new ScrollPane(sections);
    pane.setFitToWidth(true);
    pane.setPannable(false);
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
    pane.viewportBoundsProperty()
        .addListener((obs, oldBounds, newBounds) -> updateContentWidth(pane, sections, newBounds));
    pane.hvalueProperty()
        .addListener(
            (obs, oldValue, newValue) -> {
              if (Double.compare(newValue.doubleValue(), HORIZONTAL_SCROLL_LOCK) != 0) {
                pane.setHvalue(HORIZONTAL_SCROLL_LOCK);
              }
            });
    pane.getStylesheets()
        .add(
            Objects.requireNonNull(
                    AchievementsComponent.class.getResource("/styles/scrollbar.css"))
                .toExternalForm());
    return pane;
  }

  private static void updateContentWidth(ScrollPane pane, VBox sections, Bounds viewportBounds) {
    double viewportWidth = Math.max(0, viewportBounds.getWidth());
    sections.setPrefWidth(viewportWidth);
    sections.setMaxWidth(viewportWidth);
    pane.setHvalue(HORIZONTAL_SCROLL_LOCK);
  }

  private static VBox createSection(
      String title, List<Achievement> achievements, boolean completed) {
    LanguageManager languageManager = LanguageManager.getInstance();
    Text heading = MenuHelper.createSubtitle(title, CONTENT_WIDTH - 80, false);
    VBox cards = new VBox(12);
    cards.setFillWidth(true);
    cards.setMaxWidth(Double.MAX_VALUE);

    if (achievements.isEmpty()) {
      Text emptyLabel =
          getUIFactoryService()
              .newText(
                  languageManager.getTranslation("achievements_empty"),
                  Color.rgb(180, 255, 180),
                  GameConstants.TEXT_SUB_DETAILS);
      emptyLabel.wrappingWidthProperty().bind(Bindings.max(0, cards.widthProperty().subtract(12)));
      cards.getChildren().add(emptyLabel);
    } else {
      achievements.forEach(
          achievement -> cards.getChildren().add(createAchievementCard(achievement, completed)));
    }

    VBox section = new VBox(10, heading, cards);
    section.setFillWidth(true);
    section.setMaxWidth(Double.MAX_VALUE);
    section.setPadding(new Insets(10, 0, 0, 0));
    heading.wrappingWidthProperty().bind(Bindings.max(0, section.widthProperty().subtract(8)));
    return section;
  }

  private static VBox createAchievementCard(Achievement achievement, boolean completed) {
    LanguageManager languageManager = LanguageManager.getInstance();
    Text description =
        getUIFactoryService()
            .newText(achievement.getDescription(), Color.LIME, GameConstants.TEXT_SUB_DETAILS);

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

    Text status =
        getUIFactoryService()
            .newText(
                completed
                    ? languageManager.getTranslation("achievements_completed")
                    : languageManager.getTranslation("achievements_todo"),
                completed ? Color.CYAN : Color.ORANGE,
                GameConstants.TEXT_SIZE_GAME_INFO);

    VBox card = new VBox(8, description, reward, status);
    card.setPadding(CARD_PADDING);
    card.setFillWidth(true);
    card.setMaxWidth(Double.MAX_VALUE);
    description.wrappingWidthProperty().bind(Bindings.max(0, card.widthProperty().subtract(42)));
    reward.wrappingWidthProperty().bind(Bindings.max(0, card.widthProperty().subtract(42)));
    status.wrappingWidthProperty().bind(Bindings.max(0, card.widthProperty().subtract(42)));
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
