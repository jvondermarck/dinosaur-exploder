package com.dinosaur.dinosaurexploder.view;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class SettingsMenu extends FXGLMenu {

  public static final int SPACE_ZONE = 50;
  public static final String SCORE_LABEL = "score_label";
  public static final String CONTROLS = "controls";
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

    // listener to update the texts when language is changed
    languageManager.selectedLanguageProperty().addListener((obs, oldVal, newVal) -> updateTexts());
  }

  // Builds the menu using MenuHelper
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
            .newText(
                languageManager.getTranslation("options").toUpperCase(),
                Color.LIME,
                FontType.MONO,
                GameConstants.MAIN_TITLES);

    VBox headerZone = new VBox(25, title);
    headerZone.setAlignment(Pos.CENTER);
    return headerZone;
  }

  private VBox createOptionsZone() {
    VBox options = new VBox(20);
    options.setAlignment(Pos.CENTER);

    soundButton =
        getUIFactoryService().newButton(languageManager.getTranslation("sound").toUpperCase());
    soundButton.setMinSize(getAppWidth() * 0.8, 60);
    soundButton.setWrapText(true);
    soundButton.setOnAction(e -> FXGL.getSceneService().pushSubScene(new SoundMenu()));
    statsButton =
        getUIFactoryService().newButton(languageManager.getTranslation(SCORE_LABEL).toUpperCase());
    statsButton.setMinSize(getAppWidth() * 0.8, 60);
    statsButton.setWrapText(true);
    statsButton.setOnAction(e -> createScoreDialog());
    keyButton =
        getUIFactoryService().newButton(languageManager.getTranslation(CONTROLS).toUpperCase());
    keyButton.setWrapText(true);
    keyButton.setMinSize(getAppWidth() * 0.8, 60);
    keyButton.setOnAction(e -> createControlsDialog());
    languageButton =
        getUIFactoryService().newButton(languageManager.getTranslation("language").toUpperCase());
    languageButton.setWrapText(true);
    languageButton.setMinSize(getAppWidth() * 0.8, 60);
    languageButton.setAlignment(Pos.CENTER);
    languageButton.setOnAction(
        e -> FXGL.getSceneService().pushSubScene(new LanguageSelectionMenu()));
    options.getChildren().addAll(statsButton, keyButton, soundButton, languageButton);

    return options;
  }

  private Button createBackButton() {
    backButton =
        MenuHelper.createStyledButton(languageManager.getTranslation("back").toUpperCase());
    backButton.setOnAction(event -> fireResume());
    return backButton;
  }

  // Dialog for the player's score values
  private void createScoreDialog() {
    String highScore =
        (languageManager.getTranslation("high_score") + ": " + GameData.getHighScore())
                .toUpperCase()
            + "\n";
    String totalCoins =
        (languageManager.getTranslation("total_coins") + ": " + GameData.getTotalCoins())
            .toUpperCase();
    MenuHelper.showDialog(
        languageManager.getTranslation(SCORE_LABEL).toUpperCase(), highScore + totalCoins);
  }

  // dialog for the controls
  private void createControlsDialog() {
    String moveUpKey = "↑ / W : " + languageManager.getTranslation("move_up") + "\n";
    String moveDownKey = "↓ / S :  " + languageManager.getTranslation("move_down") + "\n";
    String moveRightKey = "→ / D : " + languageManager.getTranslation("move_right") + "\n";
    String moveLeftKey = "← / A : " + languageManager.getTranslation("move_left") + "\n";
    String keyB = "B: " + languageManager.getTranslation("bomb") + "\n";
    String keyE = "E: " + languageManager.getTranslation("shield") + "\n";
    String escKey = languageManager.getTranslation("pause_game") + "\n";
    String spaceKey = languageManager.getTranslation("shoot") + "\n";

    MenuHelper.showDialog(
        languageManager.getTranslation(CONTROLS).toUpperCase(),
        moveUpKey + moveDownKey + moveRightKey + moveLeftKey + keyB + keyE + escKey + spaceKey);
  }

  // called when language is changed to update the texts
  private void updateTexts() {
    title.setText(languageManager.getTranslation("options").toUpperCase());
    soundButton.setText(languageManager.getTranslation("sound").toUpperCase());
    languageButton.setText(languageManager.getTranslation("language").toUpperCase());
    statsButton.setText(languageManager.getTranslation(SCORE_LABEL).toUpperCase());
    keyButton.setText(languageManager.getTranslation(CONTROLS).toUpperCase());
    backButton.setText(languageManager.getTranslation("back").toUpperCase());
  }
}
