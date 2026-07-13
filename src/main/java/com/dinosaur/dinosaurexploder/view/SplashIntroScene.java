/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.view;

import com.almasb.fxgl.app.scene.IntroScene;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class SplashIntroScene extends IntroScene {

  private StackPane root;

  public SplashIntroScene() {
    int width = getAppWidth();
    int height = getAppHeight();

    getContentRoot().setStyle("-fx-background-color: black;");

    root = new SplashContent(width, height).getRoot();

    Font hintFont =
        Font.loadFont(
            getClass().getResourceAsStream("/assets/ui/fonts/" + GameConstants.GAME_FONT_NAME), 16);
    Font resolvedHintFont = hintFont != null ? hintFont : Font.font("Arial", FontWeight.NORMAL, 16);

    Text clickHint = new Text("CLICK ANYWHERE TO START");
    clickHint.setFont(resolvedHintFont);
    clickHint.setFill(Color.WHITE);

    root.getChildren().add(clickHint);
    StackPane.setAlignment(clickHint, Pos.BOTTOM_CENTER);
    clickHint.setTranslateY(-25);

    Timeline blink =
        new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(clickHint.opacityProperty(), 1.0)),
            new KeyFrame(Duration.seconds(0.8), new KeyValue(clickHint.opacityProperty(), 0.1)),
            new KeyFrame(Duration.seconds(1.6), new KeyValue(clickHint.opacityProperty(), 1.0)));
    blink.setCycleCount(Timeline.INDEFINITE);
    blink.play();

    // Entrance animation: fade + slight scale up
    root.setOpacity(0);
    root.setScaleX(0.95);
    root.setScaleY(0.95);
    Timeline entrance =
        new Timeline(
            new KeyFrame(
                Duration.ZERO,
                new KeyValue(root.opacityProperty(), 0),
                new KeyValue(root.scaleXProperty(), 0.95),
                new KeyValue(root.scaleYProperty(), 0.95)),
            new KeyFrame(
                Duration.seconds(0.6),
                new KeyValue(root.opacityProperty(), 1, Interpolator.EASE_OUT),
                new KeyValue(root.scaleXProperty(), 1, Interpolator.EASE_OUT),
                new KeyValue(root.scaleYProperty(), 1, Interpolator.EASE_OUT)));
    entrance.play();
    getContentRoot().getChildren().add(root);

    getContentRoot()
        .setOnMouseClicked(
            e -> {
              blink.stop();
              FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), root);
              fadeOut.setFromValue(1);
              fadeOut.setToValue(0);
              fadeOut.setOnFinished(ev -> finishIntro());
              fadeOut.play();
            });
  }

  @Override
  public void startIntro() {
    // No action needed: UI and animations are initialized in the constructor.
    // The intro waits for a mouse click, which is handled via setOnMouseClicked.
  }
}
