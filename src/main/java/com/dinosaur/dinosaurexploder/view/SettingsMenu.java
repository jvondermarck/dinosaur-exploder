package com.dinosaur.dinosaurexploder.view;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.ui.FontType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.model.GameData;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.MenuHelper;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Objects;

import static com.almasb.fxgl.dsl.FXGL.getDialogService;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;
import static com.dinosaur.dinosaurexploder.utils.MenuHelper.createStyledButton;


public class SettingsMenu extends FXGLMenu {

    public static final int SPACE_ZONE = 50;
    private final LanguageManager languageManager = LanguageManager.getInstance();
    private Text title;
    private Button soundButton;
    private Button statsButton;
    private Button keyButton;
    private Button languageButton;
    private Button backButton;

    public SettingsMenu() {
        super(MenuType.MAIN_MENU);

        buildMenu();
        languageManager.selectedLanguageProperty().addListener((obs, oldVal, newVal) -> updateTexts());

    }


    private void buildMenu() {
        MenuHelper.setupSelectionMenu(
                this,
                createHeaderZone(),
                createOptionsZone(),
                createBackButton(),
                SPACE_ZONE,
                getAppWidth(),
                getAppHeight());
    }

    private VBox createHeaderZone() {
        title =
                getUIFactoryService()
                        .newText(languageManager.getTranslation( "options" ).toUpperCase(), Color.LIME, FontType.MONO, GameConstants.MAIN_TITLES);

        VBox headerZone = new VBox(25, title);
        headerZone.setAlignment(Pos.CENTER);
        return headerZone;

    }

    private VBox createOptionsZone() {
        VBox options = new VBox(20);
        options.setAlignment(Pos.CENTER);

        soundButton = getUIFactoryService().newButton(languageManager.getTranslation("sound").toUpperCase());
        soundButton.setWrapText(true);
        soundButton.setOnAction(e -> FXGL.getSceneService().pushSubScene(new SoundMenu()));
        statsButton = getUIFactoryService().newButton(languageManager.getTranslation("score_label").toUpperCase());
        statsButton.setWrapText(true);
        statsButton.setOnAction(e -> createStatsDialog());
        keyButton = getUIFactoryService().newButton(languageManager.getTranslation("controls").toUpperCase());
        keyButton.setWrapText(true);
        keyButton.setOnAction(e -> createKeyDialog());
        languageButton = getUIFactoryService().newButton(languageManager.getTranslation("language_label").toUpperCase());
        languageButton.setWrapText(true);
        languageButton.setOnAction(e -> FXGL.getSceneService().pushSubScene(new LanguageSelectionMenu()));
        options.getChildren().addAll(statsButton, keyButton, soundButton, languageButton);

        return options;
    }

    private Button createBackButton() {
        backButton = MenuHelper.createStyledButton(languageManager.getTranslation("back").toUpperCase());
        backButton.setOnAction(event -> fireResume());
        return backButton;
    }

    private void createStatsDialog() {
        String highScore =  (languageManager.getTranslation("high_score") + ": " + GameData.getHighScore()).toUpperCase() + "\n";
        String totalCoins = (languageManager.getTranslation("total_coins") + ": " + GameData.getTotalCoins()).toUpperCase();
        MenuHelper.showDialog(languageManager.getTranslation("score_label").toUpperCase(),highScore + totalCoins);
    }

    private void createKeyDialog() {
        String moveUpKey = "↑ / W : " + languageManager.getTranslation("move_up") + "\n";
        String moveDownKey = "↓ / S :  " + languageManager.getTranslation("move_down")+ "\n";
        String moveRightKey = "→ / D : " + languageManager.getTranslation("move_right")+ "\n";
        String moveLeftKey = "← / A : " + languageManager.getTranslation("move_left")+ "\n";
        String B_Key = "B: " + languageManager.getTranslation("bomb")+ "\n";
        String E_Key = "E: " + languageManager.getTranslation("shield")+ "\n";
        String escKey = languageManager.getTranslation("pause_game")+ "\n";
        String spaceKey = languageManager.getTranslation("shoot")+ "\n";

        MenuHelper.showDialog(languageManager.getTranslation("controls").toUpperCase(), moveUpKey + moveDownKey + moveRightKey + moveLeftKey + B_Key + E_Key + escKey + spaceKey);
    }

    private void updateTexts() {
        title.setText(languageManager.getTranslation("options").toUpperCase());
        soundButton.setText(languageManager.getTranslation("sound").toUpperCase());
        languageButton.setText(languageManager.getTranslation("language_label").toUpperCase());
        statsButton.setText(languageManager.getTranslation("score_label").toUpperCase());
        keyButton.setText(languageManager.getTranslation("controls").toUpperCase());
        backButton.setText(languageManager.getTranslation("back").toUpperCase());
    }

}

