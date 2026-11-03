/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder;

import com.almasb.fxgl.app.GameApplication;
import com.dinosaur.dinosaurexploder.utils.AudioManager;
import com.dinosaur.dinosaurexploder.utils.FxThread;
import com.jpro.webapi.JProApplication;
import com.jpro.webapi.WebAPI;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class DinosaurWebApp extends JProApplication {

  @Override
  public void start(Stage stage) {
    System.setProperty("fxgl.isBrowser", "true");
    AudioManager.getInstance().useBrowserAudio(WebAPI.getWebAPI(stage));

    GameApplication app =
        new DinosaurApp() {
          @Override
          protected void initGame() {
            FxThread.runAndWait(super::initGame);
          }

          @Override
          protected void initPhysics() {
            FxThread.runAndWait(super::initPhysics);
          }
        };
    var fxglRoot = GameApplication.embeddedLaunch(app);

    stage.setScene(new Scene(new StackPane(fxglRoot)));
  }

  public static void main(String[] args) {
    launch(args);
  }
}
