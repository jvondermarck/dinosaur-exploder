package com.dinosaur.dinosaurexploder;

import com.almasb.fxgl.app.GameApplication;
import com.jpro.webapi.JProApplication;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class DinosaurWebApp extends JProApplication {

  @Override
  public void start(Stage stage) {
    System.setProperty("fxgl. isBrowser", "true");
    System.setProperty("jpro.web.mode", "true");  // <--- AJOUTEZ CETTE LIGNE pour plus de sûreté

    GameApplication app = new DinosaurApp();
    var fxglRoot = GameApplication.embeddedLaunch(app);

    stage.setScene(new Scene(new StackPane(fxglRoot)));
    stage.show();  // <--- AJOUTEZ AUSSI CETTE LIGNE (bonne pratique)
  }

  public static void main(String[] args) {
    launch(args);
  }
}