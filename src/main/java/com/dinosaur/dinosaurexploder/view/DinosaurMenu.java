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
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
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
  private final Button quitButton = new Button("Quit".toUpperCase());
  private final Button settingsButton = new Button("Options".toUpperCase());
  private final Label languageLabel = new Label("Select Language:");

  public DinosaurMenu() {
    super(MenuType.MAIN_MENU);

    mainMenuSound = createMainMenuSound();
    initializeAudioSettings();
    assert settings != null;
    String language = settings.getLanguage();
    languageManager.setSelectedLanguage(language);
    updateTexts();
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
    VBox volumeControls = createVolumeControls();

    // Configure buttons
    configureButtons();

    // Add all components to scene
    addComponentsToScene(
        backgroundView, titlePane, dinoImage, creditsBadge, volumeControls);

    // Setup button centering
    setupButtonCentering();
  }

  private VBox createVolumeControls() {
    Slider volumeSlider = createVolumeSlider();
    Text volumeText = createVolumeText(volumeSlider);

    VBox volumeBox = new VBox(volumeText, volumeSlider);
    volumeBox.setAlignment(Pos.CENTER_LEFT);
    volumeBox.setTranslateY(10);
    volumeBox.setTranslateX(20);

    return volumeBox;
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
    titlePane.setTranslateY(80);
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

  private StackPane createCreditsBadge() {
    // Create circular badge background
    Circle circle = new Circle(35);
    circle.setFill(Color.rgb(0, 220, 0, 0.3));
    circle.setStroke(Color.rgb(0, 255, 0));
    circle.setStrokeWidth(3);

    // Create star text
    Text creditsText = new Text("★");
    creditsText.setFont(Font.font("Arial", 40));
    creditsText.setFill(Color.LIME);
    creditsText.setEffect(new DropShadow(10, Color.rgb(0, 255, 0)));

    // Stack them together
    StackPane badge = new StackPane(circle, creditsText);
    badge.setTranslateX(90); // Position next to dino
    badge.setTranslateY(260); // Aligned with dino's middle
    badge.setStyle("-fx-cursor: hand;");

    // Add pulsing animation
    ScaleTransition pulse = new ScaleTransition(Duration.seconds(1), badge);
    pulse.setFromX(1.0);
    pulse.setFromY(1.0);
    pulse.setToX(1.15);
    pulse.setToY(1.15);
    pulse.setCycleCount(ScaleTransition.INDEFINITE);
    pulse.setAutoReverse(true);
    pulse.play();

    // Make it clickable
    badge.setOnMouseClicked(
        event -> {
          FXGL.getSceneService().pushSubScene(new CreditsMenu());
        });

    // Hover effect
    badge.setOnMouseEntered(
        event -> {
          badge.setScaleX(1.2);
          badge.setScaleY(1.2);
          badge.setCursor(javafx.scene.Cursor.HAND);
        });

    badge.setOnMouseExited(
        event -> {
          badge.setScaleX(1.0);
          badge.setScaleY(1.0);
        });

    return badge;
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

  private Slider createVolumeSlider() {
    Slider volumeSlider = new Slider(0, 1, 1);
    volumeSlider.adjustValue(settings.getVolume());
    volumeSlider.setBlockIncrement(0.01);

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
    applyStylesheet(settingsButton);

    startButton.setMinSize(140, 60);
    startButton.setTranslateY(420);
    startButton.setOnAction(event -> FXGL.getSceneService().pushSubScene(new ShipSelectionMenu()));

    settingsButton.setMinSize(140, 60);
    settingsButton.setTranslateY(500);
    settingsButton.setOnAction(event -> FXGL.getSceneService().pushSubScene(new SettingsMenu()));

    quitButton.setMinSize(140, 60);
    quitButton.setTranslateY(580);
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
      // ImageView mute,
      // VBox language,
      VBox volumeControls) {
    getContentRoot()
        .getChildren()
        .addAll(
            background,
            title,
            startButton,
            quitButton,
            settingsButton,
            dino,
            creditsBadge,
            // mute,
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

    quitButton
        .layoutBoundsProperty()
        .addListener(
            (obs, oldBounds, newBounds) -> {
              if (newBounds.getWidth() > 0) {
                quitButton.setTranslateX(getAppWidth() / 2.0 - newBounds.getWidth() / 2.0);
              }
            });

    settingsButton
        .layoutBoundsProperty()
        .addListener(
            (obs, oldBounds, newBounds) -> {
              if (newBounds.getWidth() > 0) {
                settingsButton.setTranslateX(getAppWidth() / 2.0 - newBounds.getWidth() / 2.0);
              }
            });

    javafx.application.Platform.runLater(
        () -> {
          startButton.requestLayout();
          quitButton.requestLayout();
          settingsButton.requestLayout();
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

  private void updateTexts() {
    startButton.setText(languageManager.getTranslation("start").toUpperCase());
    quitButton.setText(languageManager.getTranslation("quit").toUpperCase());
    settingsButton.setText(languageManager.getTranslation("options").toUpperCase());
    languageLabel.setText(languageManager.getTranslation("language_label").toUpperCase());
  }

  @Override
  public void onEnteredFrom(@NotNull Scene prevState) {
    super.onEnteredFrom(prevState);
    mainMenuSound.setMute(AudioManager.getInstance().isMuted());
    mainMenuSound.setVolume(AudioManager.getInstance().getVolume());
  }
}
