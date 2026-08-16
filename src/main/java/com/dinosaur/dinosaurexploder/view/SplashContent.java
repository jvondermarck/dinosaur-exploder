/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.view;

import com.dinosaur.dinosaurexploder.constants.GameConstants;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class SplashContent {

  private final StackPane root;
  private final BorderPane overlay;

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

    Text dinosaurText = new Text("DINOSAUR");
    dinosaurText.setFont(titleFont);
    dinosaurText.setFill(Color.LIMEGREEN);

    Text exploderText = new Text("EXPLODER");
    exploderText.setFont(titleFont);
    exploderText.setFill(Color.LIMEGREEN);

    VBox textBox = new VBox(4, dinosaurText, exploderText);
    textBox.setAlignment(Pos.CENTER);

    // Overlay uses dedicated top/center/bottom regions instead of manual
    // translateY math, so the title, dinosaur, and (later) the click-hint
    // text can never overlap regardless of screen size.
    overlay = new BorderPane();
    overlay.setPrefSize(width, height);
    overlay.setPickOnBounds(false);

    BorderPane.setAlignment(textBox, Pos.CENTER);
    BorderPane.setMargin(textBox, new Insets(height * 0.06, 0, 0, 0));
    overlay.setTop(textBox);

    BorderPane.setAlignment(dinoView, Pos.CENTER);
    overlay.setCenter(dinoView);

    root = new StackPane();
    root.setPrefSize(width, height);
    root.getChildren().addAll(bgPane, overlay);
  }

  public StackPane getRoot() {
    return root;
  }

  public BorderPane getOverlay() {
    return overlay;
  }
}
