/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.view;

import com.almasb.fxgl.app.scene.StartupScene;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class SplashScene extends StartupScene {

  public SplashScene(int width, int height) {
    super(width, height);

    Image bgImage = new Image(getClass().getResourceAsStream("/assets/textures/background.png"));
    ImageView bgView = new ImageView(bgImage);

    bgView.setViewport(new javafx.geometry.Rectangle2D(200, 0, width, height));

    bgView.setFitWidth(width);
    bgView.setFitHeight(height);
    bgView.setPreserveRatio(false);

    Pane bgPane = new Pane(bgView);
    bgPane.setPrefSize(width, height);
    bgPane.setClip(new Rectangle(width, height));

    Image dinoImage = new Image(getClass().getResourceAsStream("/assets/textures/dinomenu.png"));
    ImageView dinoView = new ImageView(dinoImage);
    dinoView.setFitWidth(width * 0.65);
    dinoView.setPreserveRatio(true);
    Font customFont60 =
        Font.loadFont(
            getClass().getResourceAsStream("/assets/ui/fonts/" + GameConstants.GAME_FONT_NAME), 60);
    Text dinosaurText = new Text("DINOSAUR");
    dinosaurText.setFont(
        customFont60 != null ? customFont60 : Font.font("Arial", FontWeight.BOLD, 60));
    dinosaurText.setFill(Color.LIMEGREEN);

    Text exploderText = new Text("EXPLODER");
    exploderText.setFont(
        customFont60 != null ? customFont60 : Font.font("Arial", FontWeight.BOLD, 60));
    exploderText.setFill(Color.LIMEGREEN);

    VBox textBox = new VBox(4, dinosaurText, exploderText);
    textBox.setAlignment(Pos.CENTER);

    StackPane root = new StackPane();
    root.setPrefSize(width, height);
    root.getChildren().addAll(bgPane, dinoView, textBox);

    StackPane.setAlignment(dinoView, Pos.BOTTOM_CENTER);
    dinoView.setTranslateY(-120);

    StackPane.setAlignment(textBox, Pos.TOP_CENTER);
    textBox.setTranslateY(-240);

    root.setOpacity(0);
    FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.2), root);
    fadeIn.setFromValue(0);
    fadeIn.setToValue(1);
    fadeIn.play();

    getContentRoot().getChildren().add(root);
  }
}
