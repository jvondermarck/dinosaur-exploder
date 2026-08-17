/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.view;

import com.dinosaur.dinosaurexploder.constants.GameConstants;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
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

public class SplashContent {

  private final StackPane root;

  public SplashContent(int width, int height) {
    Image bgImage =
        new Image(SplashContent.class.getResourceAsStream("/assets/textures/background2.png"));
    ImageView bgView = new ImageView(bgImage);
    bgView.setViewport(new Rectangle2D(200, 0, width, height));
    bgView.setFitWidth(width);
    bgView.setFitHeight(height);
    bgView.setPreserveRatio(false);

    DoubleProperty scrollX = new SimpleDoubleProperty(200);
    scrollX.addListener(
        (obs, oldVal, newVal) ->
            bgView.setViewport(new Rectangle2D(newVal.doubleValue(), 0, width, height)));

    double maxScroll = Math.max(200, bgImage.getWidth() - width);

    Timeline bgScroll =
        new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(scrollX, 200)),
            new KeyFrame(Duration.seconds(130), new KeyValue(scrollX, maxScroll)));
    bgScroll.setCycleCount(Timeline.INDEFINITE);
    bgScroll.setAutoReverse(true);
    bgScroll.play();

    Pane bgPane = new Pane(bgView);
    bgPane.setPrefSize(width, height);
    bgPane.setClip(new Rectangle(width, height));

    Image planetImage =
        new Image(SplashContent.class.getResourceAsStream("/assets/textures/splash_planet.png"));
    ImageView planetView = new ImageView(planetImage);
    planetView.setFitWidth(width * 0.40);
    planetView.setPreserveRatio(true);

    RotateTransition planetRotate = new RotateTransition(Duration.seconds(70), planetView);
    planetRotate.setByAngle(360);
    planetRotate.setCycleCount(RotateTransition.INDEFINITE);
    planetRotate.setInterpolator(javafx.animation.Interpolator.LINEAR);
    planetRotate.play();

    Image shipImage =
        new Image(SplashContent.class.getResourceAsStream("/assets/textures/spaceship2.png"));
    ImageView shipView = new ImageView(shipImage);
    shipView.setFitWidth(width * 0.12);
    shipView.setPreserveRatio(true);

    double startX = width / 2.0 + 60;
    double startY = height / 2.0 + 60;

    double leftX = -width * 0.35;
    double leftY = 0;

    double upX = -width * 0.35;
    double upY = -height * 0.25;

    double exitX = width / 2.0 + 60;
    double exitY = -height / 2.0 - 60;

    shipView.setTranslateX(startX);
    shipView.setTranslateY(startY);
    shipView.setRotate(-45);

    Timeline shipFly =
        new Timeline(
            new KeyFrame(
                Duration.ZERO,
                new KeyValue(shipView.translateXProperty(), startX),
                new KeyValue(shipView.translateYProperty(), startY),
                new KeyValue(shipView.rotateProperty(), -45)),
            new KeyFrame(
                Duration.seconds(7),
                new KeyValue(shipView.translateXProperty(), leftX),
                new KeyValue(shipView.translateYProperty(), leftY),
                new KeyValue(shipView.rotateProperty(), -45)),
            new KeyFrame(Duration.seconds(8), new KeyValue(shipView.rotateProperty(), 0)),
            new KeyFrame(
                Duration.seconds(11),
                new KeyValue(shipView.translateXProperty(), upX),
                new KeyValue(shipView.translateYProperty(), upY),
                new KeyValue(shipView.rotateProperty(), 0)),
            new KeyFrame(Duration.seconds(12), new KeyValue(shipView.rotateProperty(), 45)),
            new KeyFrame(
                Duration.seconds(16),
                new KeyValue(shipView.translateXProperty(), exitX),
                new KeyValue(shipView.translateYProperty(), exitY),
                new KeyValue(shipView.rotateProperty(), 45)));
    shipFly.setCycleCount(Timeline.INDEFINITE);
    shipFly.play();

    Image ship2Image =
        new Image(SplashContent.class.getResourceAsStream("/assets/textures/spaceship3.png"));
    ImageView ship2View = new ImageView(ship2Image);
    ship2View.setFitWidth(width * 0.06);
    ship2View.setPreserveRatio(true);
    ship2View.setOpacity(0.5);

    double ship2StartX = -width / 2.0 - 60;
    double ship2StartY = height / 2.0 + 60;

    double ship2EndX = width / 2.0 + 60;
    double ship2EndY = height * 0.3;

    ship2View.setTranslateX(ship2StartX);
    ship2View.setTranslateY(ship2StartY);
    ship2View.setRotate(80);

    Timeline ship2Fly =
        new Timeline(
            new KeyFrame(
                Duration.ZERO,
                new KeyValue(ship2View.translateXProperty(), ship2StartX),
                new KeyValue(ship2View.translateYProperty(), ship2StartY),
                new KeyValue(ship2View.rotateProperty(), 80)),
            new KeyFrame(
                Duration.seconds(20),
                new KeyValue(ship2View.translateXProperty(), ship2EndX),
                new KeyValue(ship2View.translateYProperty(), ship2EndY),
                new KeyValue(ship2View.rotateProperty(), 80)));
    ship2Fly.setCycleCount(Timeline.INDEFINITE);
    ship2Fly.setDelay(Duration.seconds(8));
    ship2Fly.play();

    Image ship3Image =
        new Image(SplashContent.class.getResourceAsStream("/assets/textures/spaceship4.png"));
    ImageView ship3View = new ImageView(ship3Image);
    ship3View.setFitWidth(width * 0.06);
    ship3View.setPreserveRatio(true);
    ship3View.setOpacity(0.5);

    double ship3StartX = -width / 2.0 - 60;
    double ship3StartY = -height / 2.0 - 60;

    double ship3EndX = width / 2.0 + 60;
    double ship3EndY = height / 2.0 + 60;

    ship3View.setTranslateX(ship3StartX);
    ship3View.setTranslateY(ship3StartY);
    ship3View.setRotate(135);

    Timeline ship3Fly =
        new Timeline(
            new KeyFrame(
                Duration.ZERO,
                new KeyValue(ship3View.translateXProperty(), ship3StartX),
                new KeyValue(ship3View.translateYProperty(), ship3StartY),
                new KeyValue(ship3View.rotateProperty(), 135)),
            new KeyFrame(
                Duration.seconds(20),
                new KeyValue(ship3View.translateXProperty(), ship3EndX),
                new KeyValue(ship3View.translateYProperty(), ship3EndY),
                new KeyValue(ship3View.rotateProperty(), 135)));
    ship3Fly.setCycleCount(Timeline.INDEFINITE);
    ship3Fly.play();

    Image ship4Image =
        new Image(SplashContent.class.getResourceAsStream("/assets/textures/spaceship7.png"));
    ImageView ship4View = new ImageView(ship4Image);
    ship4View.setFitWidth(width * 0.06);
    ship4View.setPreserveRatio(true);
    ship4View.setOpacity(0.5);

    double ship4StartX = width / 2.0 + 60;
    double ship4StartY = 0;

    double ship4EndX = -width / 2.0 - 60;
    double ship4EndY = 0;

    ship4View.setTranslateX(ship4StartX);
    ship4View.setTranslateY(ship4StartY);
    ship4View.setRotate(-90);

    Timeline ship4Fly =
        new Timeline(
            new KeyFrame(
                Duration.ZERO,
                new KeyValue(ship4View.translateXProperty(), ship4StartX),
                new KeyValue(ship4View.translateYProperty(), ship4StartY),
                new KeyValue(ship4View.rotateProperty(), -90)),
            new KeyFrame(
                Duration.seconds(20),
                new KeyValue(ship4View.translateXProperty(), ship4EndX),
                new KeyValue(ship4View.translateYProperty(), ship4EndY),
                new KeyValue(ship4View.rotateProperty(), -90)));
    ship4Fly.setCycleCount(Timeline.INDEFINITE);
    ship4Fly.setDelay(Duration.seconds(5));
    ship4Fly.play();

    Image dinoImage =
        new Image(SplashContent.class.getResourceAsStream("/assets/textures/dinomenu.png"));
    ImageView dinoView = new ImageView(dinoImage);
    dinoView.setFitWidth(width * 0.65);
    dinoView.setPreserveRatio(true);

    dinoView.setTranslateY(140);
    TranslateTransition hoverY = new TranslateTransition(Duration.seconds(2.2), dinoView);
    hoverY.setByY(18);
    hoverY.setCycleCount(TranslateTransition.INDEFINITE);
    hoverY.setAutoReverse(true);
    hoverY.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);
    hoverY.play();

    TranslateTransition hoverX = new TranslateTransition(Duration.seconds(3.5), dinoView);
    hoverX.setByX(10);
    hoverX.setCycleCount(TranslateTransition.INDEFINITE);
    hoverX.setAutoReverse(true);
    hoverX.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);
    hoverX.play();

    ScaleTransition breathe = new ScaleTransition(Duration.seconds(3), dinoView);
    breathe.setFromX(1.0);
    breathe.setFromY(1.0);
    breathe.setToX(0.92);
    breathe.setToY(0.92);
    breathe.setCycleCount(ScaleTransition.INDEFINITE);
    breathe.setAutoReverse(true);
    breathe.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);
    breathe.play();

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

    root = new StackPane();
    root.setPrefSize(width, height);
    root.getChildren()
        .addAll(bgPane, ship4View, ship3View, ship2View, shipView, planetView, dinoView, textBox);

    StackPane.setAlignment(planetView, Pos.CENTER_RIGHT);
    planetView.setTranslateX(60);
    planetView.setTranslateY(-40);

    StackPane.setAlignment(dinoView, Pos.CENTER);

    StackPane.setAlignment(textBox, Pos.TOP_CENTER);
    textBox.setTranslateY(-240);
  }

  public StackPane getRoot() {
    return root;
  }
}
