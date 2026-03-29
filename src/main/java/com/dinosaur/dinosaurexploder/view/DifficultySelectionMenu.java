/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.view;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.constants.GameMode;
import com.dinosaur.dinosaurexploder.model.GameData;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.MenuHelper;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

public class DifficultySelectionMenu extends FXGLMenu {

  // ============ CONSTANTS ============
  private static final int ZONE_SPACING = 50;

  // ============ FIELDS ============
  private final LanguageManager languageManager = LanguageManager.getInstance();
  Logger logger = Logger.getLogger(DifficultySelectionMenu.class.getName());

  // ============ CONSTRUCTOR ============
  public DifficultySelectionMenu() {
    super(MenuType.MAIN_MENU);
    buildMenu();
  }

  // ============ MENU BUILDING ============
  private void buildMenu() {
    MenuHelper.setupSelectionMenu(
        this,
        createHeaderZone(),
        createDifficultyZone(),
        createBackButton(),
        ZONE_SPACING,
        getAppWidth(),
        getAppHeight());
  }

  // ============ ZONE 1: HEADER ============
  private VBox createHeaderZone() {
    TextFlow titleFlow =
        MenuHelper.createTitleFlow(
            languageManager.getTranslation("select_difficulty"), getAppWidth() * 0.8);

    VBox headerZone = new VBox(titleFlow);
    headerZone.setAlignment(Pos.CENTER);
    return headerZone;
  }

  // ============ ZONE 2: DIFFICULTY SELECTION ============

  private VBox createDifficultyZone() {
    VBox options = new VBox(20);
    options.setAlignment(Pos.CENTER);

    Button normalButton =
        getUIFactoryService()
            .newButton(languageManager.getTranslation("normal_mode").toUpperCase());
    normalButton.setMinSize(getAppWidth() * 0.8, 60);
    normalButton.setWrapText(true);
    normalButton.setOnAction(e -> selectDifficulty(GameMode.NORMAL));
    Button expertButton =
        getUIFactoryService()
            .newButton(languageManager.getTranslation("expert_mode").toUpperCase());
    expertButton.setMinSize(getAppWidth() * 0.8, 60);
    expertButton.setWrapText(true);
    expertButton.setOnAction(e -> selectDifficulty(GameMode.EXPERT));
    options.getChildren().addAll(normalButton, expertButton);

    return options;
  }

  // ============ ZONE 3: BACK BUTTON ============

  private Button createBackButton() {
    Button backButton = new Button(languageManager.getTranslation("back").toUpperCase());
    backButton
        .getStylesheets()
        .add(
            Objects.requireNonNull(getClass().getResource(GameConstants.STYLESHEET_PATH))
                .toExternalForm());
    backButton.setMinSize(140, 60);
    backButton.setOnAction(event -> fireResume());

    return backButton;
  }

  // ============ EVENT HANDLERS ============

  private void selectDifficulty(GameMode gameMode) {
    GameData.setSelectedDifficulty(gameMode);
    logger.log(Level.INFO, "Selected Difficulty: {0}", gameMode);
    fireNewGame();
  }
}
