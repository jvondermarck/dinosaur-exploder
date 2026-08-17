/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.view;

import com.almasb.fxgl.app.scene.StartupScene;
import javafx.animation.FadeTransition;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class SplashScene extends StartupScene {

  public SplashScene(int width, int height) {
    super(width, height);

    getContentRoot().setStyle("-fx-background-color: black;");

    StackPane root = new SplashContent(width, height).getRoot();
    root.setOpacity(0);

    FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.2), root);
    fadeIn.setFromValue(0);
    fadeIn.setToValue(1);
    fadeIn.play();

    getContentRoot().getChildren().add(root);
  }
}
