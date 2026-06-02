/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.view;

import com.dinosaur.dinosaurexploder.constants.GameConstants;
import java.util.Random;
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

  public SplashContent(int width, int height) {
    Image bgImage =
        new Image(SplashContent.class.getResourceAsStream("/assets/textures/background.png"));
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

    // --- Decorative background spaceships and projectiles ---
    Pane decPane = new Pane();
    decPane.setPrefSize(width, height);
    decPane.setMouseTransparent(true);

    Random rng = new Random(7);
    String[] shipFiles = {
      "spaceship1.png", "spaceship2.png", "spaceship3.png",
      "spaceship4.png", "spaceship5.png", "spaceship6.png",
      "spaceship7.png", "spaceship8.png"
    };
    Image[] shipImgs = new Image[shipFiles.length];
    for (int i = 0; i < shipFiles.length; i++) {
      shipImgs[i] =
          new Image(SplashContent.class.getResourceAsStream("/assets/textures/" + shipFiles[i]));
    }
    Image projImg =
        new Image(SplashContent.class.getResourceAsStream("/assets/textures/basicProjectile.png"));

    // 6 small ships scrolling upward; each fires projectiles periodically from its position
    for (int i = 0; i < 6; i++) {
      final ImageView sv = new ImageView(shipImgs[rng.nextInt(shipImgs.length)]);
      final double shipW = 28 + rng.nextInt(24);
      sv.setFitWidth(shipW);
      sv.setPreserveRatio(true);
      sv.setOpacity(0.45 + rng.nextDouble() * 0.40);
      final double sx = rng.nextDouble() * (width - 60);
      sv.setLayoutX(sx);
      sv.setLayoutY(height + 70);
      decPane.getChildren().add(sv);

      final double dur = 5.0 + rng.nextDouble() * 5.0;
      Timeline shipAnim = new Timeline(
          new KeyFrame(Duration.ZERO,
              new KeyValue(sv.layoutYProperty(), height + 70, Interpolator.LINEAR)),
          new KeyFrame(Duration.seconds(dur),
              new KeyValue(sv.layoutYProperty(), -70, Interpolator.LINEAR)));
      shipAnim.setCycleCount(Timeline.INDEFINITE);
      shipAnim.play();
      // Spread ships along their cycle so they're distributed from the first frame
      shipAnim.jumpTo(Duration.seconds(rng.nextDouble() * dur));

      // Fire a projectile upward from the ship's nose every fireRate seconds
      final double fireRate = 1.5 + rng.nextDouble() * 1.5;
      Timeline fireTimer = new Timeline(new KeyFrame(Duration.seconds(fireRate), e -> {
        double py = sv.getLayoutY() - 10;
        if (py < -20 || py > height + 20) return; // ship is off-screen, skip shot
        ImageView pv = new ImageView(projImg);
        pv.setFitWidth(7);
        pv.setFitHeight(18);
        pv.setOpacity(0.85);
        pv.setLayoutX(sx + shipW / 2.0 - 3.5);
        pv.setLayoutY(py);
        decPane.getChildren().add(pv);
        Timeline shotAnim = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(pv.layoutYProperty(), py, Interpolator.LINEAR)),
            new KeyFrame(Duration.seconds(0.9),
                new KeyValue(pv.layoutYProperty(), py - 200, Interpolator.LINEAR)));
        shotAnim.setCycleCount(1);
        shotAnim.setOnFinished(ev -> decPane.getChildren().remove(pv));
        shotAnim.play();
      }));
      fireTimer.setCycleCount(Timeline.INDEFINITE);
      fireTimer.play();
    }

    root = new StackPane();
    root.setPrefSize(width, height);
    root.getChildren().addAll(bgPane, decPane, dinoView, textBox);

    StackPane.setAlignment(dinoView, Pos.BOTTOM_CENTER);
    dinoView.setTranslateY(-120);

    StackPane.setAlignment(textBox, Pos.TOP_CENTER);
    textBox.setTranslateY(-240);

    // Bounce animation on the dinosaur
    Timeline bounce = new Timeline(
        new KeyFrame(Duration.ZERO,
            new KeyValue(dinoView.translateYProperty(), -120, Interpolator.EASE_BOTH)),
        new KeyFrame(Duration.seconds(1.0),
            new KeyValue(dinoView.translateYProperty(), -145, Interpolator.EASE_BOTH)),
        new KeyFrame(Duration.seconds(2.0),
            new KeyValue(dinoView.translateYProperty(), -120, Interpolator.EASE_BOTH)));
    bounce.setCycleCount(Timeline.INDEFINITE);
    bounce.play();

    // Color cycling on the title text
    Timeline colorCycle = new Timeline(
        new KeyFrame(Duration.ZERO,
            new KeyValue(dinosaurText.fillProperty(), Color.LIMEGREEN),
            new KeyValue(exploderText.fillProperty(), Color.LIMEGREEN)),
        new KeyFrame(Duration.seconds(2.0),
            new KeyValue(dinosaurText.fillProperty(), Color.CYAN),
            new KeyValue(exploderText.fillProperty(), Color.CYAN)),
        new KeyFrame(Duration.seconds(4.0),
            new KeyValue(dinosaurText.fillProperty(), Color.LIMEGREEN),
            new KeyValue(exploderText.fillProperty(), Color.LIMEGREEN)));
    colorCycle.setCycleCount(Timeline.INDEFINITE);
    colorCycle.play();
  }

  public StackPane getRoot() {
    return root;
  }
}
