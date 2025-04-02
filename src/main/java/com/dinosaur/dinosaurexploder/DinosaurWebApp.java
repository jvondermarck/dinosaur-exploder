package com.dinosaur.dinosaurexploder;

import com.almasb.fxgl.app.GameApplication;
import com.jpro.webapi.JProApplication;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

public class DinosaurWebApp extends JProApplication {

    @Override
    public void start(Stage stage) {
        System.setProperty("fxgl.isBrowser", "true");

        GameApplication app = new DinosaurApp();
        var fxglRoot = GameApplication.embeddedLaunch(app);

        stage.setScene(new Scene(new StackPane(fxglRoot)));
    }

    public static void main(String[] args) {
        launch(args);
    }
}