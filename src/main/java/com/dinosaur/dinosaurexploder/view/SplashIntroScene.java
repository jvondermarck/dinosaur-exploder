/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.view;

import com.almasb.fxgl.app.scene.IntroScene;
import com.almasb.fxgl.dsl.FXGL;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class SplashIntroScene extends IntroScene {

  private StackPane root;

  public SplashIntroScene() {
    int width = getAppWidth();
    int height = getAppHeight();

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

    Text dinosaurText = new Text("DINOSAUR");
    Text exploderText = new Text("EXPLODER");
    var fontFactory = FXGL.getAssetLoader().loadFont(GameConstants.GAME_FONT_NAME);

    dinosaurText.setFont(fontFactory.newFont(60));
    dinosaurText.setFill(Color.LIMEGREEN);
    exploderText.setFont(fontFactory.newFont(60));
    exploderText.setFill(Color.LIMEGREEN);

    VBox textBox = new VBox(4, dinosaurText, exploderText);
    textBox.setAlignment(Pos.CENTER);

    Text clickHint = new Text("CLICK ANYWHERE TO START");
    clickHint.setFont(fontFactory.newFont(16));
    clickHint.setFill(Color.WHITE);

    Timeline blink =
        new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(clickHint.opacityProperty(), 1.0)),
            new KeyFrame(Duration.seconds(0.8), new KeyValue(clickHint.opacityProperty(), 0.1)),
            new KeyFrame(Duration.seconds(1.6), new KeyValue(clickHint.opacityProperty(), 1.0)));
    blink.setCycleCount(Timeline.INDEFINITE);
    blink.play();

    root = new StackPane();
    root.setPrefSize(width, height);
    root.getChildren().addAll(bgPane, dinoView, textBox, clickHint);

    StackPane.setAlignment(dinoView, Pos.BOTTOM_CENTER);
    dinoView.setTranslateY(-120);

    StackPane.setAlignment(textBox, Pos.TOP_CENTER);
    textBox.setTranslateY(-240);

    StackPane.setAlignment(clickHint, Pos.BOTTOM_CENTER);
    clickHint.setTranslateY(-25);

    root.setOpacity(1);

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
  public void startIntro() {}
}
