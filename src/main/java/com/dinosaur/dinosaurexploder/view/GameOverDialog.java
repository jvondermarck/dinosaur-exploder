package com.dinosaur.dinosaurexploder.view;

import static com.almasb.fxgl.dsl.FXGL.getDialogService;
import static com.almasb.fxgl.dsl.FXGL.getGameController;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class GameOverDialog {

  private final LanguageManager languageManager;

  public GameOverDialog(LanguageManager languageManager) {
    this.languageManager = languageManager;
  }

  public void createDialog() {
    Button btnYes = getUIFactoryService().newButton(languageManager.getTranslation("yes"));
    btnYes.setPrefWidth(200);
    btnYes.defaultButtonProperty();
    // action event for the yes Button
    EventHandler<ActionEvent> startNewGameEvent = e -> getGameController().startNewGame();

    // when button is pressed
    btnYes.setOnAction(startNewGameEvent);

    Button btnNo = getUIFactoryService().newButton(languageManager.getTranslation("no"));
    btnNo.setPrefWidth(200);

    // action event for the no Button
    EventHandler<ActionEvent> backToMenuEvent = e -> getGameController().gotoMainMenu();

    // when button is pressed
    btnNo.setOnAction(backToMenuEvent);

    getDialogService()
        .showBox(languageManager.getTranslation("new_game"), new VBox(), btnYes, btnNo);
  }
}
