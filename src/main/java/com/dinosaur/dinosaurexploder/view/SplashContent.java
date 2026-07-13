/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.view;

import com.dinosaur.dinosaurexploder.constants.GameConstants;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
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

  private static Image loadImage(String path) {
    InputStream is = SplashContent.class.getResourceAsStream(path);
    return new Image(Objects.requireNonNull(is, "Resource not found: " + path));
  }

  public SplashContent(int width, int height) {
    Image bgImage = loadImage("/assets/textures/background.png");
    double srcX = 200, srcW = width, srcH = height;

    // Canvas TRIPLE: normal (0..h) → espejo (h..2h) → normal (2h..3h)
    // Al animar 0→-height*2 el loop es perfecto: la 3ª franja = la 1ª
    Canvas bgCanvas = new Canvas(width, height * 3);
    var gc = bgCanvas.getGraphicsContext2D();

    // Franja 0..height → imagen normal
    gc.drawImage(bgImage, srcX, 0, srcW, srcH, 0, 0, width, height);

    // Franja height..height*2 → imagen espejada horizon lastalmente
    gc.drawImage(bgImage, srcX, 0, srcW, srcH, width, height, -width, height);

    // Franja height*2..height*3 → imagen normal (igual que la 1ª, cierra el bucle)
    gc.drawImage(bgImage, srcX, 0, srcW, srcH, 0, height * 2, width, height);

    // Difuminar la unión entre las franjas
    int fadeH = height / 5;
    for (int seam : new int[] {height, height * 2}) {
      gc.setFill(
          new LinearGradient(
              0,
              seam - fadeH,
              0,
              seam + fadeH,
              false,
              CycleMethod.NO_CYCLE,
              new Stop(0.0, Color.TRANSPARENT),
              new Stop(0.4, Color.color(0, 0, 0, 0.65)),
              new Stop(0.5, Color.color(0, 0, 0, 0.80)),
              new Stop(0.6, Color.color(0, 0, 0, 0.65)),
              new Stop(1.0, Color.TRANSPARENT)));
      gc.fillRect(0, seam - fadeH, width, fadeH * 2);
    }

    Pane bgPane = new Pane(bgCanvas);
    bgPane.setPrefSize(width, height);
    bgPane.setClip(new Rectangle(width, height));

    // Animar 0 → -height*2 (pasa por las 3 franjas), loop perfecto
    Timeline bgScroll =
        new Timeline(
            new KeyFrame(
                Duration.ZERO, new KeyValue(bgCanvas.translateYProperty(), 0, Interpolator.LINEAR)),
            new KeyFrame(
                Duration.seconds(16.0),
                new KeyValue(bgCanvas.translateYProperty(), -height * 2, Interpolator.LINEAR)));
    bgScroll.setCycleCount(Timeline.INDEFINITE);
    bgScroll.play();
    Image dinoImage = loadImage("/assets/textures/dinomenu.png");
    ImageView dinoView = new ImageView(dinoImage);
    dinoView.setFitWidth(width * 0.38);
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

    // --- Decorative background green dinosaurs ---
    Pane decPane = new Pane();
    decPane.setPrefSize(width, height);
    decPane.setMouseTransparent(true);

    Image greenDinoImage = loadImage("/assets/textures/greenDino.png");
    for (int i = 0; i < 5; i++) {
      final ImageView gv = new ImageView(greenDinoImage);
      final double dinoW = 36.0 + ThreadLocalRandom.current().nextInt(30);
      gv.setFitWidth(dinoW);
      gv.setPreserveRatio(true);
      gv.setOpacity(0.30 + ThreadLocalRandom.current().nextDouble() * 0.30);
      final double gx = ThreadLocalRandom.current().nextDouble() * (width - 60);
      gv.setLayoutX(gx);
      gv.setLayoutY(height + 60);
      decPane.getChildren().add(gv);

      final double dur = 6.0 + ThreadLocalRandom.current().nextDouble() * 6.0;
      Timeline dinoAnim =
          new Timeline(
              new KeyFrame(
                  Duration.ZERO,
                  new KeyValue(gv.layoutYProperty(), height + 60, Interpolator.LINEAR)),
              new KeyFrame(
                  Duration.seconds(dur),
                  new KeyValue(gv.layoutYProperty(), -80, Interpolator.LINEAR)));
      dinoAnim.setCycleCount(Timeline.INDEFINITE);
      dinoAnim.play();
      dinoAnim.jumpTo(Duration.seconds(ThreadLocalRandom.current().nextDouble() * dur));
    }

    root = new StackPane();
    root.setPrefSize(width, height);
    root.getChildren().addAll(bgPane, decPane, dinoView, textBox);

    StackPane.setAlignment(dinoView, Pos.BOTTOM_CENTER);
    dinoView.setTranslateY(-120);

    StackPane.setAlignment(textBox, Pos.TOP_CENTER);
    textBox.setTranslateY(-240);

    // Bounce animation on the dinosaur
    Timeline bounce =
        new Timeline(
            new KeyFrame(
                Duration.ZERO,
                new KeyValue(dinoView.translateYProperty(), -120, Interpolator.EASE_BOTH)),
            new KeyFrame(
                Duration.seconds(1.0),
                new KeyValue(dinoView.translateYProperty(), -145, Interpolator.EASE_BOTH)),
            new KeyFrame(
                Duration.seconds(2.0),
                new KeyValue(dinoView.translateYProperty(), -120, Interpolator.EASE_BOTH)));
    bounce.setCycleCount(Timeline.INDEFINITE);
    bounce.play();
  }

  public StackPane getRoot() {
    return root;
  }
}
