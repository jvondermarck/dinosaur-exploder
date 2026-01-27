package com.dinosaur.dinosaurexploder.view;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;
import static com.dinosaur.dinosaurexploder.utils.LanguageManager.DEFAULT_LANGUAGE;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.Scene;
import com.almasb.fxgl.ui.FontType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.model.Settings;
import com.dinosaur.dinosaurexploder.utils.AudioManager;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.SettingsProvider;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

public class DinosaurMenu extends FXGLMenu {
  private final LanguageManager languageManager = LanguageManager.getInstance();
  private final Settings settings = SettingsProvider.loadSettings();
  private final MediaPlayer mainMenuSound;

  // UI Components
  private final Button startButton = new Button("Start Game".toUpperCase());
  private final Button achievementsButton = new Button("Achievements".toUpperCase());
  private final Button quitButton = new Button("Quit".toUpperCase());
  private final Label languageLabel = new Label("Select Language:");

  public DinosaurMenu() {
    super(MenuType.MAIN_MENU);

    mainMenuSound = createMainMenuSound();
    initializeAudioSettings();
    languageManager.selectedLanguageProperty().addListener((obs, oldVal, newVal) -> updateTexts());

    try {
      buildMenu();
    } catch (FileNotFoundException e) {
      System.err.println("File not found: " + e.getMessage());
    }
  }

  // ============ INITIALIZATION METHODS ============

  private MediaPlayer createMainMenuSound() {
    return new MediaPlayer(
        new Media(
            Objects.requireNonNull(getClass().getResource(GameConstants.MAIN_MENU_SOUND))
                .toExternalForm()));
  }

  private void initializeAudioSettings() {
    boolean muteState = settings.isMuted();
    AudioManager.getInstance().setMuted(muteState);
    mainMenuSound.setMute(muteState);
    AudioManager.getInstance().playMusic(GameConstants.BACKGROUND_SOUND);
  }

  // ============ BUILD MENU ============

  private void buildMenu() throws FileNotFoundException {
    // Create all UI components
    ImageView backgroundView = createAnimatedBackground();
    StackPane titlePane = createTitle();
    ImageView dinoImage = createDinoImage();
    ImageView muteIcon = createMuteIcon();
    StackPane creditsBadge = createCreditsBadge();
    VBox languageBox = createLanguageSelector();
    VBox volumeControls = createVolumeControls();

    // Configure buttons
    startButton.setMinSize(140, 60);
    startButton.setTranslateY(400);
    startButton.setOnAction(event -> FXGL.getSceneService().pushSubScene(new ShipSelectionMenu()));

    achievementsButton.setMinSize(140, 60);
    achievementsButton.setTranslateY(470);
    achievementsButton.setOnAction(event -> FXGL.getSceneService().pushSubScene(new AchievementsMenu()));

    quitButton.setMinSize(140, 60);
    quitButton.setTranslateY(540);
    quitButton.setOnAction(event -> fireExit());
  }

  // ============ HELPER METHODS ============

  private Image loadImage(String path) throws FileNotFoundException {
    InputStream stream = getClass().getClassLoader().getResourceAsStream(path);
    if (stream == null) {
      throw new FileNotFoundException("Resource not found: " + path);
    }
    return new Image(stream);
  }

  private void applyStylesheet(javafx.scene.Parent parent) {
    parent
        .getStylesheets()
        .add(
            Objects.requireNonNull(getClass().getResource(GameConstants.STYLESHEET_PATH))
                .toExternalForm());
  }

  private TranslateTransition createBackgroundTransition(ImageView imageView, Image background) {
    TranslateTransition transition = new TranslateTransition();
    transition.setNode(imageView);
    transition.setDuration(Duration.seconds(50));
    transition.setFromX(0);
    transition.setToX(-background.getWidth() + DinosaurGUI.WIDTH * 3.8);
    transition.setCycleCount(TranslateTransition.INDEFINITE);
    transition.setInterpolator(Interpolator.LINEAR);
    transition.setAutoReverse(true);
    return transition;
  }

  private void addComponentsToScene(
      ImageView background,
      StackPane title,
      ImageView dino,
      StackPane creditsBadge,
      ImageView mute,
      VBox language,
      VBox volumeControls) {
    getContentRoot()
        .getChildren()
        .addAll(
            background,
            title,
            startButton,
            achievementsButton,
            quitButton,
            dino,
            creditsBadge,
            mute,
            language,
            volumeControls);
  }

  private void setupButtonCentering() {
    startButton
        .layoutBoundsProperty()
        .addListener(
            (obs, oldBounds, newBounds) -> {
              if (newBounds.getWidth() > 0) {
                startButton.setTranslateX(getAppWidth() / 2.0 - newBounds.getWidth() / 2.0);
              }
            });

    achievementsButton
        .layoutBoundsProperty()
        .addListener(
            (obs, oldBounds, newBounds) -> {
              if (newBounds.getWidth() > 0) {
                achievementsButton.setTranslateX(getAppWidth() / 2.0 - newBounds.getWidth() / 2.0);
              }
            });

    quitButton
        .layoutBoundsProperty()
        .addListener(
            (obs, oldBounds, newBounds) -> {
              if (newBounds.getWidth() > 0) {
                quitButton.setTranslateX(getAppWidth() / 2.0 - newBounds.getWidth() / 2.0);
              }
            });

    javafx.application.Platform.runLater(
        () -> {
          startButton.requestLayout();
          quitButton.requestLayout();
        });
  }

  private void toggleMute(ImageView muteIcon, Image muteImg, Image audioOnImg) {
    boolean newMutedState = !AudioManager.getInstance().isMuted();
    AudioManager.getInstance().setMuted(newMutedState);
    mainMenuSound.setMute(newMutedState);
    settings.setMuted(newMutedState);
    muteIcon.setImage(newMutedState ? muteImg : audioOnImg);
    SettingsProvider.saveSettings(settings);
  }

  private void changeLanguage(String selectedLanguage) {
    languageManager.setSelectedLanguage(selectedLanguage);
    languageManager.loadTranslations(selectedLanguage);
    settings.setLanguage(selectedLanguage);
    SettingsProvider.saveSettings(settings);
  }

  private void updateTexts() {
    startButton.setText(languageManager.getTranslation("start").toUpperCase());
    achievementsButton.setText(languageManager.getTranslation("achievements").toUpperCase());
    quitButton.setText(languageManager.getTranslation("quit").toUpperCase());
    languageLabel.setText(languageManager.getTranslation("language_label").toUpperCase());
  }

  @Override
  public void onEnteredFrom(@NotNull Scene prevState) {
    super.onEnteredFrom(prevState);
    mainMenuSound.setMute(AudioManager.getInstance().isMuted());
    mainMenuSound.setVolume(AudioManager.getInstance().getVolume());
  }
}
