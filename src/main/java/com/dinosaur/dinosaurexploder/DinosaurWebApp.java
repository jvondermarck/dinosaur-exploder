/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder;

import com.almasb.fxgl.app.GameApplication;
import com.jpro.webapi.JProApplication;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class DinosaurWebApp extends JProApplication {

  private static javafx.scene.Parent fxglRoot = null;

  @Override
  public void start(Stage stage) {
    System.setProperty("fxgl.isBrowser", "true");
    System.setProperty("javafx.media.disable", "true");
    System.setProperty("fxgl.dev.screenshot", "false");
    System.setProperty("fxgl.dev.profiler", "false");

    if (fxglRoot == null) {
      GameApplication app = new DinosaurApp();
      fxglRoot = GameApplication.embeddedLaunch(app);
    }

    stage.setScene(new Scene(new StackPane(fxglRoot)));
  }

  public static void main(String[] args) {
    launch(args);
  }
}
