package com.dinosaur.dinosaurexploder.view;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.scene.SubScene;
import com.almasb.fxgl.ui.FontType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.model.GameData;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.MenuHelper;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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
                        languageManager.getTranslation("select_ship"), getAppWidth() * 0.8);

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

        VBox headerZone = new VBox(25, titleFlow, highScore, totalCoins);
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

        options.getChildren().addAll(title, option1, option2, option3);

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




}

