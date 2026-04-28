/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

 package com.dinosaur.dinosaurexploder.view;

 import com.almasb.fxgl.app.scene.FXGLMenu;
 import com.almasb.fxgl.app.scene.MenuType;
 import com.dinosaur.dinosaurexploder.components.CollectedCoinsComponent;
 import com.dinosaur.dinosaurexploder.components.ScoreComponent;

 import com.dinosaur.dinosaurexploder.constants.GameMode;
 import com.dinosaur.dinosaurexploder.achievements.AchievementManager;
 import com.dinosaur.dinosaurexploder.constants.GameConstants;
 import com.dinosaur.dinosaurexploder.model.GameData;
 import com.dinosaur.dinosaurexploder.model.HighScore;
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
 
 /**
  * Developer-only debug menu for manually overriding game state during testing.
  * This view should never be exposed to players in production builds.
  */
 public class DebugMenu extends FXGLMenu {
    
    private Label title;
    private Label scoreLabel;
    private Label highScoreLabel;
    private Label coinsLabel;
    TextField scoreField;
    TextField highScoreField;
    TextField coinsField;
    private Button setScoreButton;
    private Button setHighScoreButton;
    private Button setCoinsButton;
    private Button backButton;

    public DebugMenu  (ScoreComponent scoreComponent, CollectedCoinsComponent coinsComponent){
        super(MenuType.GAME_MENU);
        buildMenu();
    }

    public void buildMenu(){
        VBox layout = createLayout();
        createTitle();
        createSetScoreButton();
        createSetHighScoreButton();
        createSetCoinsButton();
        createBackButton();
        
        layout.getChildren().
            addAll(
            title,
            scoreLabel, scoreField, setScoreButton,
            highScoreLabel, highScoreField, setHighScoreButton,
            coinsLabel, coinsField, setCoinsButton, backButton
         );
        getContentRoot().getChildren().add(layout);
    }

    private VBox createLayout() {
        VBox layout = new VBox(12);
        layout.setPadding(new Insets(16));
        layout.setAlignment(Pos.TOP_LEFT);
        layout.setStyle("-fx-background-color: rgba(0,0,0,0.85); -fx-border-color: yellow; -fx-border-width: 2;");
        layout.setMaxWidth(280);
        return layout;
    }

    private Label createTitle(){
        title = new Label("DEBUG MENU [DEV ONLY]");
        title.setTextFill(Color.YELLOW);
        title.setFont(Font.font("System", FontWeight.BOLD, 14));
        return title;
    }

    private Button createSetScoreButton (){
        scoreLabel = new Label("Set Score:");
        scoreLabel.setTextFill(Color.WHITE);
        scoreField = new TextField();
        scoreField.setPromptText("Enter score value...");
        setScoreButton = new Button("Set Score");
        setScoreButton.setOnAction(e -> {
            // TODO: wire up to scoreComponent.setScore(int)
        });
        return setScoreButton;
    }

    private Button createSetHighScoreButton (){
        highScoreLabel = new Label("Set High Score:");
        highScoreLabel.setTextFill(Color.WHITE);
        highScoreField = new TextField();
        highScoreField.setPromptText("Enter high score value...");
        setHighScoreButton = new Button("Set High Score");
        setHighScoreButton.setOnAction(e -> {
            // TODO: wire up to HighScore.setHigh(...)

        });
        return setHighScoreButton;
    }

    private Button createSetCoinsButton(){
        coinsLabel = new Label("Set Coins:");
        coinsLabel.setTextFill(Color.WHITE);
        coinsField = new TextField();
        coinsField.setPromptText("Enter coin amount...");
        setCoinsButton = new Button("Set Coins");
        setCoinsButton.setOnAction(e -> {
             // TODO: requires CollectedCoinsComponent.setCoin(int) to be added first
        });
        return setCoinsButton;
    }

    private Button createBackButton(){
        backButton = MenuHelper.createStyledButton ("Back");
        backButton.setOnAction(event -> fireResume());
        return backButton;
    }
}
