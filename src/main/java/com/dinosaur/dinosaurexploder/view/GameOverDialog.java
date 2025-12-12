package com.dinosaur.dinosaurexploder.view;

import static com.almasb.fxgl.dsl.FXGL.getDialogService;
import static com.almasb.fxgl.dsl.FXGL.getGameController;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.ui.FontFactory;
import com.dinosaur.dinosaurexploder.components.ScoreComponent;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class GameOverDialog {

    private final LanguageManager languageManager;

    public GameOverDialog(LanguageManager languageManager) {
        this.languageManager = languageManager;
    }

    public void createDialog() {

        // Languages requiring Cyrillic font
        Set<String> cyrLangs = Set.of("Greek", "Russian");

        FontFactory baseCyrFont = FXGL.getAssetLoader().loadFont("Geologica-Regular.ttf");
        Font cyr20Font = baseCyrFont.newFont(20);

        FontFactory baseArcadeFont = FXGL.getAssetLoader().loadFont("arcade_classic.ttf");
        Font arcade20Font = baseArcadeFont.newFont(20);

        // YES BUTTON
        Button btnYes = getUIFactoryService().newButton(languageManager.getTranslation("yes"));
        btnYes.setPrefWidth(200);
        btnYes.setOnAction(e -> getGameController().startNewGame());

        // NO BUTTON
        Button btnNo = getUIFactoryService().newButton(languageManager.getTranslation("no"));
        btnNo.setPrefWidth(200);
        btnNo.setOnAction(e -> getGameController().gotoMainMenu());

        // ---- FONT SELECTION ----
        if (cyrLangs.contains(languageManager.selectedLanguageProperty().getValue())) {
            btnYes.setFont(cyr20Font);
            btnNo.setFont(cyr20Font);
        } else {
            btnYes.setFont(arcade20Font);
            btnNo.setFont(arcade20Font);
        }

        // ---- GET FINAL SCORE ----
        int finalScore = 0;
        try {
            var scoreEntities = getGameWorld().getEntitiesByComponent(ScoreComponent.class);
            if (!scoreEntities.isEmpty()) {
                ScoreComponent sc = scoreEntities.get(0).getComponent(ScoreComponent.class);
                finalScore = sc.getScore();
            }
        } catch (Exception ignored) {}

        Text scoreText = new Text(languageManager.getTranslation("score") + ": " + finalScore);
        scoreText.setFill(Color.YELLOW);
        scoreText.setFont(Font.font(GameConstants.ARCADE_CLASSIC_FONTNAME, GameConstants.TEXT_SIZE_GAME_DETAILS));

        VBox box = new VBox(10, scoreText);
        box.setAlignment(Pos.CENTER);

        getDialogService().showBox(languageManager.getTranslation("new_game"), box, btnYes, btnNo);
    }
}
