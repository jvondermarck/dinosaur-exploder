package com.dinosaur.dinosaurexploder.view;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.ui.FontFactory;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.Set;

import static com.almasb.fxgl.dsl.FXGL.getDialogService;
import static com.almasb.fxgl.dsl.FXGL.getGameController;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

public class GameOverDialog {

    private final LanguageManager languageManager;

    public GameOverDialog(LanguageManager languageManager){
        this.languageManager = languageManager;
    }

    public void createDialog(){
        Set<String> cyrLangs = Set.of("Greek","Russian");

        FontFactory baseCyrFont = FXGL.getAssetLoader().loadFont("Geologica-Regular.ttf");
        Font cyr20Font = baseCyrFont.newFont(20);

        FontFactory baseArcadeFont = FXGL.getAssetLoader().loadFont("arcade_classic.ttf");
        Font arcade20Font = baseArcadeFont.newFont(20);

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

        if ( cyrLangs.contains(languageManager.selectedLanguageProperty().getValue()) ) {
            btnYes.fontProperty().unbind();
            btnYes.setFont(cyr20Font);
            btnNo.fontProperty().unbind();
            btnNo.setFont(cyr20Font);
        } else {
            btnYes.fontProperty().unbind();
            btnYes.setFont(arcade20Font);
            btnNo.fontProperty().unbind();
            btnNo.setFont(arcade20Font);
        }

        getDialogService().showBox(languageManager.getTranslation("new_game"), new VBox(), btnYes, btnNo);
    }

}
