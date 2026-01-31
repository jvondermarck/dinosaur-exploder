package com.dinosaur.dinosaurexploder.view;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
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
import javafx.scene.text.TextFlow;

import java.util.Objects;

import static com.almasb.fxgl.dsl.FXGL.getDialogService;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;


public class SettingsMenu extends FXGLMenu {

    public static final int SPACE_ZONE = 50;
    private final LanguageManager languageManager = LanguageManager.getInstance();
    private static final String STYLESHEET_PATH = "/styles/styles.css";

    public SettingsMenu() {
        super(MenuType.MAIN_MENU);
        buildMenu();
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
        TextFlow titleFlow =
                MenuHelper.createTitleFlow(
                        languageManager.getTranslation("settings / help").toUpperCase(), getAppWidth() * 0.8);


        VBox headerZone = new VBox(25, titleFlow);
        headerZone.setAlignment(Pos.CENTER);
        return headerZone;

    }

    private VBox createOptionsZone() {
        VBox options = new VBox(20);
        options.setAlignment(Pos.CENTER);


        var soundButton = getUIFactoryService().newButton(languageManager.getTranslation("sound".toUpperCase()));
        soundButton.setWrapText(true);
        var statsButton = getUIFactoryService().newButton(languageManager.getTranslation("my stats".toUpperCase()));
        statsButton.setWrapText(true);
        statsButton.setOnAction(e -> createStatsDialog());
        var keyButton = getUIFactoryService().newButton(languageManager.getTranslation("key info".toUpperCase()));
        keyButton.setWrapText(true);
        keyButton.setOnAction(e -> createKeyDialog());
        var languageButton = getUIFactoryService().newButton(languageManager.getTranslation("language".toUpperCase()));
        languageButton.setWrapText(true);

        options.getChildren().addAll(statsButton, keyButton, soundButton, languageButton);

        return options;
    }


    private Button createBackButton() {
        Button backButton = new Button(languageManager.getTranslation("back").toUpperCase());
        backButton
                .getStylesheets()
                .add(Objects.requireNonNull(getClass().getResource(STYLESHEET_PATH)).toExternalForm());
        backButton.setMinSize(140, 60);
        backButton.setOnAction(event -> fireResume());

        return backButton;
    }


    private void createStatsDialog() {
        String highScore =  (languageManager.getTranslation("high_score") + ": " + GameData.getHighScore()).toUpperCase() + "\n";
        String totalCoins = (languageManager.getTranslation("total_coins") + ": " + GameData.getTotalCoins()).toUpperCase();
        MenuHelper.showDialog(languageManager.getTranslation("my stats").toUpperCase(),highScore + totalCoins);
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

        MenuHelper.showDialog(languageManager.getTranslation("keyboard info").toUpperCase(), moveUpKey + moveDownKey + moveRightKey + moveLeftKey + B_Key + E_Key + escKey + spaceKey);
    }

}

