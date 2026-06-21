/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.view;

import com.dinosaur.dinosaurexploder.constants.GameConstants;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class SplashContent {

  private final StackPane root;

  public SplashContent(int width, int height) {
    Image bgImage =
        new Image(SplashContent.class.getResourceAsStream("/assets/textures/background.png"));
    ImageView bgView = new ImageView(bgImage);
    bgView.setViewport(new Rectangle2D(200, 0, width, height));
    bgView.setFitWidth(width);
    bgView.setFitHeight(height);
    bgView.setPreserveRatio(false);

    Pane bgPane = new Pane(bgView);
    bgPane.setPrefSize(width, height);
    bgPane.setClip(new Rectangle(width, height));

    Image dinoImage =
        new Image(SplashContent.class.getResourceAsStream("/assets/textures/dinomenu.png"));
    ImageView dinoView = new ImageView(dinoImage);
    dinoView.setFitWidth(width * 0.65);
    dinoView.setPreserveRatio(true);

    Font customFont =
        Font.loadFont(
            SplashContent.class.getResourceAsStream(
                "/assets/ui/fonts/" + GameConstants.GAME_FONT_NAME),
            60);
    Font titleFont = customFont != null ? customFont : Font.font("Arial", FontWeight.BOLD, 60);

    // 1. Create a retro arcade gradient (Orange fading to Red vertically)
    LinearGradient arcadeGradient =
        new LinearGradient(
            0,
            0,
            0,
            1,
            true,
            CycleMethod.NO_CYCLE,
            new Stop(0, Color.ORANGE),
            new Stop(1, Color.CRIMSON));

    // 2. Create a high-contrast dark drop shadow effect
    DropShadow textShadow = new DropShadow();
    textShadow.setRadius(6.0);
    textShadow.setOffsetX(4.0);
    textShadow.setOffsetY(4.0);
    textShadow.setColor(Color.BLACK);

    Text dinosaurText = new Text("DINOSAUR");
    dinosaurText.setFont(titleFont);
    dinosaurText.setFill(arcadeGradient);
    dinosaurText.setEffect(textShadow);
    // Optimization caching lines
    dinosaurText.setCache(true);
    dinosaurText.setCacheHint(javafx.scene.CacheHint.SPEED);

    Text exploderText = new Text("EXPLODER");
    exploderText.setFont(titleFont);
    exploderText.setFill(arcadeGradient);
    exploderText.setEffect(textShadow);
    exploderText.setCache(true);
    exploderText.setCacheHint(javafx.scene.CacheHint.SPEED);

    VBox textBox = new VBox(12, dinosaurText, exploderText);
    textBox.setAlignment(Pos.CENTER);

    // 3. Add a flashing "PRESS SPACE TO START" prompt
    Font promptFont =
        Font.loadFont(
            SplashContent.class.getResourceAsStream(
                "/assets/ui/fonts/" + GameConstants.GAME_FONT_NAME),
            24);
    if (promptFont == null) {
      promptFont = Font.font("Arial", FontWeight.BOLD, 24);
    }
    
    Text startPrompt = new Text("PRESS SPACE TO START");
    startPrompt.setFont(promptFont);
    startPrompt.setFill(Color.GOLD);
    startPrompt.setEffect(textShadow);

    // Create the arcade blinking animation loop
    FadeTransition fade = new FadeTransition(Duration.seconds(0.6), startPrompt);
    fade.setFromValue(1.0);
    fade.setToValue(0.1);
    fade.setCycleCount(Animation.INDEFINITE);
    fade.setAutoReverse(true);
    fade.play();

    root = new StackPane();
    root.setPrefSize(width, height);
    root.getChildren().addAll(bgPane, dinoView, textBox, startPrompt);

    // Original developer positions
    StackPane.setAlignment(dinoView, Pos.BOTTOM_CENTER);
    dinoView.setTranslateY(-120);

    StackPane.setAlignment(textBox, Pos.TOP_CENTER);
    textBox.setTranslateY(-240);
    
    // Position the blinking text neatly in the empty lower middle section
    StackPane.setAlignment(startPrompt, Pos.BOTTOM_CENTER);
    startPrompt.setTranslateY(-40);
  } // <--- FIXED: Added the missing closing brace for the constructor block here!

  public StackPane getRoot() {
    return root;
  }
}