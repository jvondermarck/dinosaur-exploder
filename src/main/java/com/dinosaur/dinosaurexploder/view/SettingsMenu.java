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
                        languageManager.getTranslation("SETTINGS / HELP"), getAppWidth() * 0.8);

        var highScore =
                getUIFactoryService()
                        .newText(
                                (languageManager.getTranslation("high_score") + ": " + GameData.getHighScore())
                                        .toUpperCase(),
                                Color.LIME,
                                FontType.MONO,
                                GameConstants.TEXT_SUB_DETAILS);

        var totalCoins =
                getUIFactoryService()
                        .newText(
                                (languageManager.getTranslation("total_coins") + ": " + GameData.getTotalCoins())
                                        .toUpperCase(),
                                Color.LIME,
                                FontType.MONO,
                                GameConstants.TEXT_SUB_DETAILS);

        VBox headerZone = new VBox(25, titleFlow);
        headerZone.setAlignment(Pos.CENTER);
        return headerZone;

    }

    private VBox createOptionsZone() {
        VBox options = new VBox(20);
        options.setAlignment(Pos.CENTER);

        Label title = new Label("Settings (dummy)");
        Label option1 = new Label("• Sound: ON");
        Label option2 = new Label("• Music: ON");
        Label option3 = new Label("• Fullscreen: OFF");

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
        /*
        soundButton.setFont(Font.font("Monospaced", GameConstants.TEXT_SUB_DETAILS));
        soundButton.setTextFill(Color.LIME);
        soundButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        */
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
        Button btnOK = getUIFactoryService().newButton(languageManager.getTranslation("ok"));
        btnOK.setMinWidth(200);

        var highScore =
                getUIFactoryService()
                        .newText(
                                (languageManager.getTranslation("high_score") + ": " + GameData.getHighScore())
                                        .toUpperCase(),
                                Color.LIME,
                                FontType.MONO,
                                GameConstants.TEXT_SUB_DETAILS);

        var totalCoins =
                getUIFactoryService()
                        .newText(
                                (languageManager.getTranslation("total_coins") + ": " + GameData.getTotalCoins())
                                        .toUpperCase(),
                                Color.LIME,
                                FontType.MONO,
                                GameConstants.TEXT_SUB_DETAILS);

        VBox layout = new VBox(20, highScore, totalCoins);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new javafx.geometry.Insets(20));
        layout.setMinWidth(500);

        getDialogService()
                .showBox(languageManager.getTranslation("your stats").toUpperCase(), layout, btnOK);

    }


    private void createKeyDialog() {
        Button btnOK = getUIFactoryService().newButton(languageManager.getTranslation("ok"));
        btnOK.setMinWidth(200);

        var moveUpKey =
                getUIFactoryService()
                        .newText("↑ / W : " + languageManager.getTranslation("move_up"),
                                Color.LIME,
                                FontType.MONO,
                                GameConstants.TEXT_SUB_DETAILS);

        var moveDownKey =
                getUIFactoryService()
                        .newText("↓ / S :  " + languageManager.getTranslation("move_down"),
                                Color.LIME,
                                FontType.MONO,
                                GameConstants.TEXT_SUB_DETAILS);

        var moveRightKey =
                getUIFactoryService()
                        .newText("→ / D : " + languageManager.getTranslation("move_right"),
                                Color.LIME,
                                FontType.MONO,
                                GameConstants.TEXT_SUB_DETAILS);

        var moveLeftKey =
                getUIFactoryService()
                        .newText("← / A : " + languageManager.getTranslation("move_left"),
                                Color.LIME,
                                FontType.MONO,
                                GameConstants.TEXT_SUB_DETAILS);

        var B_Key =
                getUIFactoryService()
                        .newText("B: " + languageManager.getTranslation("bomb"),
                                Color.LIME,
                                FontType.MONO,
                                GameConstants.TEXT_SUB_DETAILS);

        var E_Key =
                getUIFactoryService()
                        .newText("E: " + languageManager.getTranslation("shield"),
                                Color.LIME,
                                FontType.MONO,
                                GameConstants.TEXT_SUB_DETAILS);

        var escKey =
                getUIFactoryService()
                        .newText(languageManager.getTranslation("pause_game"),
                                Color.LIME,
                                FontType.MONO,
                                GameConstants.TEXT_SUB_DETAILS);

        var spaceKey =
                getUIFactoryService()
                        .newText(languageManager.getTranslation("shoot"),
                                Color.LIME,
                                FontType.MONO,
                                GameConstants.TEXT_SUB_DETAILS);

        VBox layout = new VBox(25,moveUpKey, moveDownKey, moveLeftKey, moveRightKey, spaceKey, escKey, B_Key, E_Key);

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new javafx.geometry.Insets(25));

        getDialogService()
                .showBox(languageManager.getTranslation("key info").toUpperCase(), layout, btnOK);

    }

}

