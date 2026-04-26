/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

 package com.dinosaur.dinosaurexploder.view; 

 import com.dinosaur.dinosaurexploder.components.ScoreComponent;
 import com.dinosaur.dinosaurexploder.components.CollectedCoinsComponent;
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
 public class DebugMenuView extends VBox {
 
     public DebugMenuView(ScoreComponent scoreComponent, CollectedCoinsComponent coinsComponent) {
         super(12);
         setPadding(new Insets(16));
         setAlignment(Pos.TOP_LEFT);
         setStyle("-fx-background-color: rgba(0,0,0,0.85); -fx-border-color: yellow; -fx-border-width: 2;");
         setMaxWidth(280);
 
         Label title = new Label("🛠 DEBUG MENU [DEV ONLY]");
         title.setTextFill(Color.YELLOW);
         title.setFont(Font.font("System", FontWeight.BOLD, 14));
 
         // --- Score section ---
         Label scoreLabel = new Label("Set Score:");
         scoreLabel.setTextFill(Color.WHITE);
         TextField scoreField = new TextField();
         scoreField.setPromptText("Enter score value...");
         Button setScoreButton = new Button("Set Score");
         setScoreButton.setOnAction(e -> {
             // TODO: wire up to scoreComponent.setScore(int)
         });
 
         // --- High score section ---
         Label highScoreLabel = new Label("Set High Score:");
         highScoreLabel.setTextFill(Color.WHITE);
         TextField highScoreField = new TextField();
         highScoreField.setPromptText("Enter high score value...");
         Button setHighScoreButton = new Button("Set High Score");
         setHighScoreButton.setOnAction(e -> {
             // TODO: wire up to HighScore.setHigh(...)
         });
 
         // --- Coins section ---
         Label coinsLabel = new Label("Set Coins:");
         coinsLabel.setTextFill(Color.WHITE);
         TextField coinsField = new TextField();
         coinsField.setPromptText("Enter coin amount...");
         Button setCoinsButton = new Button("Set Coins");
         setCoinsButton.setOnAction(e -> {
            // TODO: requires CollectedCoinsComponent.setCoin(int) to be added first
         });
 
         getChildren().addAll(
             title,
             scoreLabel, scoreField, setScoreButton,
             highScoreLabel, highScoreField, setHighScoreButton,
             coinsLabel, coinsField, setCoinsButton
         );
     }
 }