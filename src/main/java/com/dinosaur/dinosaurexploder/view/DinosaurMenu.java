package com.dinosaur.dinosaurexploder.view;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

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
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

public class DinosaurMenu extends FXGLMenu {
  private final LanguageManager languageManager = LanguageManager.getInstance();
  private final Settings settings = SettingsProvider.loadSettings();
  private final MediaPlayer mainMenuSound;

  // UI Components
  private final Button startButton = new Button("Start Game".toUpperCase());
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
    VBox languageBox = createLanguageSelector();
    Slider volumeSlider = createVolumeSlider();
    Text volumeText = createVolumeText(volumeSlider);

    // Configure buttons
    configureButtons();

    // Add all components to scene
    addComponentsToScene(
        backgroundView, titlePane, dinoImage, muteIcon, languageBox, volumeSlider, volumeText);

    // Setup button centering
    setupButtonCentering();
  }

  // ============ UI COMPONENT CREATORS ============

  private ImageView createAnimatedBackground() throws FileNotFoundException {
    Image background = loadImage(GameConstants.BACKGROUND_IMAGE_PATH);
    ImageView backgroundView = new ImageView(background);
    backgroundView.setFitHeight(DinosaurGUI.HEIGHT);
    backgroundView.setX(0);
    backgroundView.setY(0);
    backgroundView.setPreserveRatio(true);

    TranslateTransition transition = createBackgroundTransition(backgroundView, background);
    transition.play();

    return backgroundView;
  }

  private StackPane createTitle() {
    var title =
        getUIFactoryService()
            .newText(
                GameConstants.GAME_NAME.toUpperCase(),
                Color.LIME,
                FontType.MONO,
                GameConstants.MAIN_TITLES);

    StackPane titlePane = new StackPane(title);
    titlePane.setAlignment(Pos.CENTER);
    titlePane.setTranslateY(100);
    titlePane.setPrefWidth(getAppWidth());

    return titlePane;
  }

  private ImageView createDinoImage() throws FileNotFoundException {
    Image dinoImg = loadImage(GameConstants.GAME_LOGO_DINOSAUR);
    ImageView dinoView = new ImageView(dinoImg);
    dinoView.setFitHeight(250);
    dinoView.setFitWidth(200);
    dinoView.setX(200);
    dinoView.setY(190);
    dinoView.setPreserveRatio(true);

    return dinoView;
  }

  private ImageView createMuteIcon() throws FileNotFoundException {
    Image muteImg = loadImage(GameConstants.SILENT_IMAGE_PATH);
    Image audioOnImg = loadImage(GameConstants.PLAYING_IMAGE_PATH);

    ImageView muteIcon = new ImageView(settings.isMuted() ? muteImg : audioOnImg);
    muteIcon.setFitHeight(50);
    muteIcon.setFitWidth(60);
    muteIcon.setX(470);
    muteIcon.setY(20);
    muteIcon.setPreserveRatio(true);

    muteIcon.setOnMouseClicked(event -> toggleMute(muteIcon, muteImg, audioOnImg));

    return muteIcon;
  }

  private VBox createLanguageSelector() {
    ComboBox<String> languageComboBox = new ComboBox<>();
    languageComboBox.getItems().addAll(languageManager.getAvailableLanguages());

    languageComboBox.setPrefWidth(ComboBox.USE_COMPUTED_SIZE);
    languageComboBox.setMinWidth(ComboBox.USE_COMPUTED_SIZE);

    languageComboBox.setValue(settings.getLanguage() != null ? settings.getLanguage() : "English");

    if (settings.getLanguage() != null) {
      changeLanguage(settings.getLanguage());
    }

    applyStylesheet(languageComboBox);
    languageComboBox.setOnAction(
        event -> {
          changeLanguage(languageComboBox.getValue());
          updateTexts();
          languageComboBox.requestLayout();
        });

    languageLabel.setText(languageManager.getTranslation("language_label").toUpperCase());
    languageLabel.setStyle(
        "-fx-text-fill: #00FF00;" + "-fx-effect: dropshadow(gaussian, black, 2, 1.0, 0, 0);");
    applyStylesheet(languageLabel);

    VBox languageBox = new VBox(10, languageLabel, languageComboBox);
    languageBox.setFillWidth(true);
    languageBox.setAlignment(Pos.CENTER);
    languageBox.setTranslateY(620);
    languageBox.setPadding(new Insets(20));
    languageBox.setStyle(
        "-fx-background-color: rgba(0, 0, 0, 0.8);"
            + "-fx-background-radius: 15;"
            + "-fx-border-color: rgba(0, 220, 0, 0.7);"
            + "-fx-border-width: 2;"
            + "-fx-border-radius: 15;"
            + "-fx-effect:  dropshadow(gaussian, rgba(0, 220, 0, 0.6), 12, 0.5, 0, 0);");

    languageBox
        .layoutBoundsProperty()
        .addListener(
            (obs, oldBounds, newBounds) -> {
              if (newBounds.getWidth() > 0) {
                languageBox.setTranslateX(getAppWidth() / 2.0 - newBounds.getWidth() / 2.0);
              }
            });

    return languageBox;
  }

  private Slider createVolumeSlider() {
    Slider volumeSlider = new Slider(0, 1, 1);
    volumeSlider.adjustValue(settings.getVolume());
    volumeSlider.setBlockIncrement(0.01);
    volumeSlider.setTranslateY(10);
    volumeSlider.setTranslateX(75);

    applyStylesheet(volumeSlider);

    return volumeSlider;
  }

  private Text createVolumeText(Slider volumeSlider) {
    var volumeText =
        getUIFactoryService()
            .newText(
                String.format("%.0f%%", settings.getVolume() * 100),
                Color.LIME,
                GameConstants.TEXT_SIZE_GAME_INFO);

    volumeText.setTranslateX(20);
    volumeText.setTranslateY(35);

    volumeSlider
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              AudioManager.getInstance().setVolume(newVal.doubleValue());
              settings.setVolume(newVal.doubleValue());
              SettingsProvider.saveSettings(settings);
              volumeText.setText(String.format("%.0f%%", newVal.doubleValue() * 100));
            });

    return volumeText;
  }

  private void configureButtons() {
    applyStylesheet(startButton);
    applyStylesheet(quitButton);

    startButton.setMinSize(140, 60);
    startButton.setTranslateY(400);
    startButton.setOnAction(event -> FXGL.getSceneService().pushSubScene(new ShipSelectionMenu()));

    quitButton.setMinSize(140, 60);
    quitButton.setTranslateY(500);
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
      ImageView mute,
      VBox language,
      Slider volume,
      Text volumeText) {
    getContentRoot()
        .getChildren()
        .addAll(
            background, title, startButton, quitButton, dino, mute, volumeText, volume, language);
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
