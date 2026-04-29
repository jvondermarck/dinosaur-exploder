/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.view;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

import com.dinosaur.dinosaurexploder.utils.LanguageManager;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.ui.FontType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.model.GameData;
import com.dinosaur.dinosaurexploder.model.HighScore;
import com.dinosaur.dinosaurexploder.model.TotalCoins;
import com.dinosaur.dinosaurexploder.utils.MenuHelper;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Developer-only debug menu for manually overriding game state during testing. Provides controls to
 * set high score and total coins directly via save files. This view should never be exposed to
 * players in production builds.
 *
 * <p>Enabled by passing -DdebugMenu=true at launch.
 */



public class DebugMenu extends FXGLMenu {

  private Text title;
  TextField highScoreField;
  TextField coinsField;
  private Button setHighScoreButton;
  private Button setCoinsButton;
  private Button backButton;
  private static final double CONTENT_SPACING = 18;
  private static final double BASE_WIDTH = 800.0;
  private static final double MENU_WIDTH_RATIO = 0.75;
  private static final double MAX_MENU_WIDTH = 720;
  private static final double MIN_MENU_WIDTH = 500;
  private static final Logger logger = Logger.getLogger(DebugMenu.class.getName());

  // Helper funtion for translating into other languages 
  private String t(String key) {
    return LanguageManager.getInstance().getTranslation(key);
  }

  private double menuWidth() {
  return Math.max(MIN_MENU_WIDTH, Math.min(getAppWidth() * MENU_WIDTH_RATIO, MAX_MENU_WIDTH));
  }

  private double scale() {
    return Math.max(1, Math.min(getAppWidth() / BASE_WIDTH, 1.15));
  }

  public DebugMenu() {
    super(MenuType.GAME_MENU);
    buildMenu();
  }

  public void buildMenu() {
    VBox layout = createLayout();
    createTitle();
    createSetHighScoreButton();
    createSetCoinsButton();
    createBackButton();

    layout
        .getChildren()
        .addAll(title, highScoreField, setHighScoreButton, coinsField, setCoinsButton, backButton);
    getContentRoot().getChildren().add(layout);
  }

  private VBox createLayout() {
    VBox layout = new VBox(CONTENT_SPACING);
    layout.setPadding(new Insets(20));
    layout.setAlignment(Pos.TOP_LEFT);
    layout.setStyle(
        "-fx-background-color: rgba(0,0,0,0.85); -fx-border-color: lime; -fx-border-width: 2;");
    layout.setMaxWidth(menuWidth());
    layout.setPrefWidth(menuWidth());
    layout.setScaleX(scale());
    layout.setScaleY(scale());
    return layout;
  }

  private Text createTitle() {
  title =
      getUIFactoryService()
          .newText(
              t("debug_menu_title"), Color.LIME, FontType.MONO, GameConstants.TEXT_SUB_DETAILS);
  title.setWrappingWidth(menuWidth() - 40);
  return title;
  }

  /**
   * Creates the high score input field and button. Reads and overwrites the high score save file
   * for the current difficulty mode.
   */
  private Button createSetHighScoreButton() {
    highScoreField = new TextField();
    highScoreField.setFont(Font.font(GameConstants.GAME_FONT_NAME, 20));
    highScoreField.setPromptText(t("debug_high_score_prompt"));
    setHighScoreButton = getUIFactoryService().newButton(t("debug_set_high_score"));
    setHighScoreButton.setPrefWidth(menuWidth() - 40);
    setHighScoreButton.setWrapText(true);
    setHighScoreButton.setOnAction(
        e -> {
          try {
            int value = Integer.parseInt(highScoreField.getText().trim());
            HighScore hs = loadHighScore();
            hs.setHigh(GameData.getSelectedDifficulty().name(), value);
            saveHighScore(hs);
            highScoreField.clear();
            highScoreField.setPromptText(t("debug_high_score_prompt") + value);
          } catch (NumberFormatException ex) {
            highScoreField.clear();
            highScoreField.setPromptText(t("prompt_error"));
          }
        });
    return setHighScoreButton;
  }

  /**
   * Creates the coin amount input field and button. Reads and overwrites the total coins save file.
   */
  private Button createSetCoinsButton() {
    coinsField = new TextField();
    coinsField.setFont(Font.font(GameConstants.GAME_FONT_NAME, 20));
    coinsField.setPromptText(t("debug_coins_prompt"));
    setCoinsButton = getUIFactoryService().newButton(t("debug_set_coins"));
    setCoinsButton.setPrefWidth(menuWidth() - 40);
    setCoinsButton.setWrapText(true);
    setCoinsButton.setOnAction(
        e -> {
          try {
            int value = Integer.parseInt(coinsField.getText().trim());
            TotalCoins tc = loadTotalCoins();
            tc.setTotal(value);
            saveTotalCoins(tc);
            coinsField.clear();
            coinsField.setPromptText(t("debug_coins_prompt") + value);
          } catch (NumberFormatException ex) {
            coinsField.clear();
            coinsField.setPromptText(t("prompt_error"));
          }
        });
    return setCoinsButton;
  }

  private Button createBackButton() {
    backButton = MenuHelper.createStyledButton(t("debug_back"));
    backButton.setOnAction(event -> fireResume());
    return backButton;
  }

  /** Loads the high score from the save file. */
  private HighScore loadHighScore() {
    try (ObjectInputStream in =
        new ObjectInputStream(new FileInputStream(GameConstants.HIGH_SCORE_FILE))) {
      return (HighScore) in.readObject();
    } catch (IOException | ClassNotFoundException e) {
      return new HighScore();
    }
  }

  /** Persists the given HighScore object to disk. */
  private void saveHighScore(HighScore hs) {
    try (ObjectOutputStream out =
        new ObjectOutputStream(new FileOutputStream(GameConstants.HIGH_SCORE_FILE))) {
      out.writeObject(hs);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Error saving high score: {0}", e.getMessage());
    }
  }

  private TotalCoins loadTotalCoins() {
    try (ObjectInputStream in =
        new ObjectInputStream(new FileInputStream(GameConstants.TOTAL_COINS_FILE))) {
      return (TotalCoins) in.readObject();
    } catch (IOException | ClassNotFoundException e) {
      return new TotalCoins();
    }
  }

  private void saveTotalCoins(TotalCoins tc) {
    try (ObjectOutputStream out =
        new ObjectOutputStream(new FileOutputStream(GameConstants.TOTAL_COINS_FILE))) {
      out.writeObject(tc);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Error saving coins: {0}", e.getMessage());
    }
  }
}
