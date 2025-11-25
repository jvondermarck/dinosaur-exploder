package com.dinosaur.dinosaurexploder.view;

import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.components.ScoreComponent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import com.dinosaur.dinosaurexploder.constants.GameConstants;

import static com.almasb.fxgl.dsl.FXGL.getDialogService;
import static com.almasb.fxgl.dsl.FXGL.getGameController;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

public class GameOverDialog {

    private final LanguageManager languageManager;

    public GameOverDialog(LanguageManager languageManager){
        this.languageManager = languageManager;
    }

    public void createDialog(){
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

        // Try to fetch final score from ScoreComponent in the game world
        int finalScore = 0;
        try {
            var scoreEntities = getGameWorld().getEntitiesByComponent(ScoreComponent.class);
            if (!scoreEntities.isEmpty()) {
                var scoreEntity = scoreEntities.iterator().next();
                ScoreComponent sc = scoreEntity.getComponent(ScoreComponent.class);
                finalScore = sc.getScore();
            }
        } catch (Exception ignored) {
            // If anything goes wrong, leave finalScore as 0
        }

        Text scoreText = new Text(languageManager.getTranslation("score") + ": " + finalScore);
        scoreText.setFill(Color.YELLOW);
        scoreText.setFont(Font.font(GameConstants.ARCADE_CLASSIC_FONTNAME, GameConstants.TEXT_SIZE_GAME_DETAILS));
        VBox content = new VBox(10, scoreText);
        content.setAlignment(Pos.CENTER);

        getDialogService().showBox(languageManager.getTranslation("new_game"), content, btnYes, btnNo);
    }

}
