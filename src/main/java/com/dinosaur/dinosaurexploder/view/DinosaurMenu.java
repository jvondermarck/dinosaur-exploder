
package com.dinosaur.dinosaurexploder.view;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.Scene;
import com.almasb.fxgl.ui.FontType;
import com.dinosaur.dinosaurexploder.utils.SettingsProvider;
import com.dinosaur.dinosaurexploder.constants.GameConstants;

import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.model.Settings;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import javafx.scene.control.ComboBox;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import javafx.util.Duration;
import java.io.InputStream;

import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.util.Objects;
import com.dinosaur.dinosaurexploder.utils.AudioManager;

public class DinosaurMenu extends FXGLMenu {
    private final MediaPlayer mainMenuSound;
    LanguageManager languageManager = LanguageManager.getInstance();
    private final Button startButton = new Button("Start Game");
    private final Button quitButton = new Button("Quit");
    private final Label languageLabel = new Label("Select Language:");

    private final Settings settings = SettingsProvider.loadSettings();

    public DinosaurMenu() {
        super(MenuType.MAIN_MENU);
        
        mainMenuSound = new MediaPlayer(
            new Media(Objects.requireNonNull(getClass().getResource("/assets/sounds/mainMenu.wav")).toExternalForm())
        );

        // Listen for language changes and update menu text
        languageManager.selectedLanguageProperty().addListener((observable, oldValue, newValue) -> updateTexts());

        // Read the last saved settings and load the main menu sound
        boolean muteState = settings.isMuted();
        AudioManager.getInstance().setMuted(muteState);
        mainMenuSound.setMute(muteState);
        AudioManager.getInstance().playMusic(GameConstants.BACKGROUND_SOUND);


        var bg = new Rectangle(getAppWidth(), getAppHeight(), Color.BLACK);

        var title = FXGL.getUIFactoryService().newText(GameConstants.GAME_NAME, Color.LIME, FontType.MONO, 35);

        // Adding styles to the buttons
        startButton.getStylesheets()
                .add(Objects.requireNonNull(getClass().getResource("/styles/styles.css")).toExternalForm());
        quitButton.getStylesheets()
                .add(Objects.requireNonNull(getClass().getResource("/styles/styles.css")).toExternalForm());

        // Add the language selection UI
        ComboBox<String> languageComboBox = new ComboBox<>();
        languageComboBox.getItems().addAll(languageManager.getAvailableLanguages());
        if(settings.getLanguage() == null) {
            // Default language
            languageComboBox.setValue("English");
        }else{
            languageComboBox.setValue(settings.getLanguage());
            changeLanguage(settings.getLanguage());
        }

        languageLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #61C181; -fx-font-weight: bold;");
        languageLabel.setTranslateY(5);

        // Set action on language change
        languageComboBox.setOnAction(event -> {
            changeLanguage(languageComboBox.getValue());
            updateTexts();
        });


        // Add the language selection combo box to the menu layout
        HBox languageBox = new HBox(10, languageLabel, languageComboBox);
        languageBox.setTranslateX(getAppWidth() / 2.0 - 100); // Adjust based on UI design
        languageBox.setTranslateY(600); // Adjust Y position based on layout

        // Assuming 'root' is the layout for the menu

        Slider volumeSlider = new Slider(0, 1, 1);
        volumeSlider.adjustValue(settings.getVolume());
        volumeSlider.setBlockIncrement(0.01);

        volumeSlider.getStylesheets()
                .add(Objects.requireNonNull(getClass().getResource("/styles/styles.css")).toExternalForm());

        // Sets the volume label
        Label volumeLabel = new Label(String.format("%.0f%%", settings.getVolume() * 100));
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
        AudioManager.getInstance().setVolume(newValue.doubleValue()); // <--- THIS LINE IS IMPORTANT
        mainMenuSound.setVolume(newValue.doubleValue());
        settings.setVolume(newValue.doubleValue());
        SettingsProvider.saveSettings(settings);
        volumeLabel.setText(String.format("%.0f%%", newValue.doubleValue() * 100));
        });

        try {

            // Using InputStream for efficient fetching of images
            InputStream menuImage = getClass().getClassLoader().getResourceAsStream("assets/textures/dinomenu.png");
            if (menuImage == null) {
                throw new FileNotFoundException("Resource not found: assets/textures/dinomenu.png");
            }
            InputStream muteButton = getClass().getClassLoader().getResourceAsStream("assets/textures/silent.png");
            if (muteButton == null) {
                throw new FileNotFoundException("Resource not found: assets/textures/silent.png");
            }
            InputStream soundButton = getClass().getClassLoader().getResourceAsStream("assets/textures/playing.png");
            if (soundButton == null) {
                throw new FileNotFoundException("Resource not found: assets/textures/playing.png");
            }
            InputStream backGround = getClass().getClassLoader().getResourceAsStream("assets/textures/background.png");
            if (backGround == null) {
                throw new FileNotFoundException("Resource not found: assets/textures/background.png");
            }

            Image Background = new Image(backGround);
            ImageView imageViewB = new ImageView(Background);
            imageViewB.setFitHeight(DinosaurGUI.HEIGHT);
            imageViewB.setX(0);
            imageViewB.setY(0);
            imageViewB.setPreserveRatio(true);

            // Create a TranslateTransition for horizontal scrolling
            TranslateTransition translateTransition = getTranslateTransition(imageViewB, Background);

            // Start the transition
            translateTransition.play();

            // image for dino in main menu
            Image image = new Image(menuImage);
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(250);
            imageView.setFitWidth(200);
            imageView.setX(200);
            imageView.setY(190);
            imageView.setPreserveRatio(true);

            // adding image to manually mute music
            Image mute = new Image(muteButton);

            Image audioOn = new Image(soundButton);
            ImageView imageViewPlaying = new ImageView(settings.isMuted() ? mute : audioOn);
            imageViewPlaying.setFitHeight(50);
            imageViewPlaying.setFitWidth(60);
            imageViewPlaying.setX(470);
            imageViewPlaying.setY(20);
            imageViewPlaying.setPreserveRatio(true);

            startButton.setMinSize(50, 50);
            startButton.setPrefSize(140, 60);

            quitButton.setMinSize(140, 60);

            title.setTranslateY(100);
            title.setTranslateX(getAppWidth() / 2.0 - 145);

            startButton.setTranslateY(400);
            startButton.setTranslateX(getAppWidth() / 2.0 - 50);
            // startButton.setStyle("-fx-font-size:20");

            quitButton.setTranslateY(500);
            quitButton.setTranslateX(getAppWidth() / 2.0 - 50);
            // quitButton.setStyle("-fx-font-size:20");

            BorderPane root = new BorderPane();
            root.setTop(title);
            BorderPane.setAlignment(title, Pos.CENTER);

            BorderPane volumePane = new BorderPane();
            volumePane.setLeft(volumeLabel);
            BorderPane.setAlignment(volumeLabel, Pos.CENTER);
            volumePane.setCenter(volumeSlider);
            volumeSlider.setStyle("-fx-padding: 10px;");
            volumeSlider.setTranslateY(25);
            volumeSlider.setTranslateX(10);
            volumeLabel.setTranslateX(20);
            volumeLabel.setTranslateY(20);
            volumeLabel.setStyle("-fx-text-fill: #61C181;");
            root.setTop(languageBox);
            root.setCenter(volumePane);
            root.setBottom(new BorderPane(startButton, null, quitButton, null, null));
            BorderPane.setAlignment(startButton, Pos.CENTER);
            BorderPane.setAlignment(quitButton, Pos.BOTTOM_CENTER);

            startButton.setOnAction(event -> {
                FXGL.getSceneService().pushSubScene(new ShipSelectionMenu());
            });

            imageViewPlaying.setOnMouseClicked(mouseEvent -> {
                boolean newMutedState = !AudioManager.getInstance().isMuted();
                AudioManager.getInstance().setMuted(newMutedState); // <--- THIS LINE IS IMPORTANT
                mainMenuSound.setMute(newMutedState);
                settings.setMuted(newMutedState);
                imageViewPlaying.setImage(newMutedState ? mute : audioOn);
                SettingsProvider.saveSettings(settings);
            });

            quitButton.setOnAction(event -> fireExit());

            getContentRoot().getChildren().addAll(
                    imageViewB, title, startButton, quitButton, imageView, imageViewPlaying, volumeLabel, volumeSlider, languageBox
            );
        }
        catch (FileNotFoundException e){
            System.out.println("File not found" + e.getMessage());
        }
    }

    @NotNull
    private static TranslateTransition getTranslateTransition(ImageView imageViewB, Image Background) {
        TranslateTransition translateTransition = new TranslateTransition();
        translateTransition.setNode(imageViewB);
        translateTransition.setDuration(Duration.seconds(50)); // Set the duration for one cycle
        translateTransition.setFromX(0);
        translateTransition.setToX(-Background.getWidth() + DinosaurGUI.WIDTH * 3.8); // Move to the left by the
        // width of the image
        translateTransition.setCycleCount(TranslateTransition.INDEFINITE); // Repeat indefinitely
        translateTransition.setInterpolator(Interpolator.LINEAR); // Smooth linear transition
        translateTransition.setAutoReverse(true);
        return translateTransition;
    }

    private void changeLanguage(String selectedLanguage){
        languageManager.setSelectedLanguage(selectedLanguage);
        languageManager.loadTranslations(selectedLanguage);

        settings.setLanguage(selectedLanguage);
        SettingsProvider.saveSettings(settings);
    }

    private void updateTexts() {
        startButton.setText(languageManager.getTranslation("start"));
        quitButton.setText(languageManager.getTranslation("quit"));
        languageLabel.setText(languageManager.getTranslation("language_label"));
    }

    @Override
    public void onEnteredFrom(Scene prevState) {
        super.onEnteredFrom(prevState);
        mainMenuSound.setMute(AudioManager.getInstance().isMuted()); // Optional: sync menu music with global mute
        mainMenuSound.setVolume(AudioManager.getInstance().getVolume()); // Optional: sync menu music with global volume
        }
}