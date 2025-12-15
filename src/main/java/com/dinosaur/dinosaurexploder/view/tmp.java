//package com.dinosaur.dinosaurexploder.view;
//
//import com.almasb.fxgl.app.scene.FXGLMenu;
//import com.almasb.fxgl.app.scene.MenuType;
//import com.almasb.fxgl.dsl.FXGL;
//import com.almasb.fxgl.scene.Scene;
//import com.almasb.fxgl.ui.FontType;
//import com.dinosaur.dinosaurexploder.model.Settings;
//import com.dinosaur.dinosaurexploder.utils.AudioManager;
//import com.dinosaur.dinosaurexploder.utils.LanguageManager;
//import com.dinosaur.dinosaurexploder.utils.SettingsProvider;
//import javafx.beans.binding.Bindings;
//import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
//import javafx.geometry.Pos;
//import javafx.scene.control.Label;
//import javafx.scene.control.Slider;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.input.KeyCode;
//import javafx.scene.layout.StackPane;
//import javafx.scene.layout.VBox;
//import javafx.scene.media.Media;
//import javafx.scene.media.MediaPlayer;
//import javafx.scene.paint.Color;
//import javafx.scene.shape.Rectangle;
//import javafx.scene.text.Text;
//import javafx.scene.control.Button;
//import static com.almasb.fxgl.dsl.FXGL.*;
//import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;
//
//import com.dinosaur.dinosaurexploder.constants.GameConstants;
//import java.io.FileNotFoundException;
//import java.io.InputStream;
//import java.util.Objects;
//
//public class PauseMenu1 extends FXGLMenu {
//    private final MediaPlayer mainMenuSound;
//    private final Settings settings = SettingsProvider.loadSettings();
//    private ImageView imageViewPlayingMenuSound;
//    private ImageView imageViewPlayingSfxSounds;
//
//
//    LanguageManager languageManager = LanguageManager.getInstance();
//    PauseButton btnBack = new PauseButton(languageManager.getTranslation("back"), this::fireResume);
//    PauseButton btnQuitGame = new PauseButton(languageManager.getTranslation("quit"), this::exit);
//    ControlButton btnControls = new ControlButton(languageManager.getTranslation("controls"));
//    ControlButton btnSound = new ControlButton(languageManager.getTranslation("sound"));
//    OptionsButton btnSoundMain = new OptionsButton(languageManager.getTranslation("sound_main"));
//    OptionsButton btnSoundSfx = new OptionsButton(languageManager.getTranslation("sound_sfx"));
//
//    // Store OptionButtons as fields so they can be updated
//    OptionsButton btnMoveUp = new OptionsButton("↑ / W : " + languageManager.getTranslation("move_up"));
//    OptionsButton btnMoveDown = new OptionsButton("↓ / S : " + languageManager.getTranslation("move_down"));
//    OptionsButton btnMoveRight = new OptionsButton("→ / D : " + languageManager.getTranslation("move_right"));
//    OptionsButton btnMoveLeft = new OptionsButton("← / A : " + languageManager.getTranslation("move_left"));
//    OptionsButton btnPauseGame = new OptionsButton(languageManager.getTranslation("pause_game"));
//    OptionsButton btnShoot = new OptionsButton(languageManager.getTranslation("shoot"));
//    OptionsButton btnBomb = new OptionsButton("B: " + languageManager.getTranslation("bomb"));
//
//    public PauseMenu() {
//        super(MenuType.GAME_MENU);
//
//        mainMenuSound = new MediaPlayer(
//                new Media(Objects.requireNonNull(getClass().getResource("/assets/sounds/mainMenu.wav")).toExternalForm())
//        );
//
//        // Read the last saved settings and load the main menu sound
//        boolean muteState = settings.isMuted();
//        AudioManager.getInstance().setMuted(muteState);
//        mainMenuSound.setMute(muteState);
//        AudioManager.getInstance().playMusic(GameConstants.BACKGROUND_SOUND);
//
//        // Init music Slider
//        Slider volumeSlider = new Slider(0, 1, 1);
//        volumeSlider.adjustValue(settings.getVolume());
//        volumeSlider.setBlockIncrement(0.01);
//
//        volumeSlider.getStylesheets()
//                .add(Objects.requireNonNull(getClass().getResource("/styles/styles.css")).toExternalForm());
//
//        // Init sfx Slider
//        Slider sfxVolumeSlider = new Slider(0, 1, 1);
//        sfxVolumeSlider.adjustValue(settings.getSfxVolume());
//        sfxVolumeSlider.setBlockIncrement(0.01);
//
//        sfxVolumeSlider.getStylesheets()
//                .add(Objects.requireNonNull(getClass().getResource("/styles/styles.css")).toExternalForm());
//
//        // Sets the music volume label
//        Label volumeLabel = new Label(String.format("%.0f%%", settings.getVolume() * 100));
//        volumeLabel.setStyle("-fx-text-fill: #61C181;");
//        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
//            AudioManager.getInstance().setVolume(newValue.doubleValue());
//            mainMenuSound.setVolume(newValue.doubleValue());
//            settings.setVolume(newValue.doubleValue());
//            SettingsProvider.saveSettings(settings);
//            volumeLabel.setText(String.format("%.0f%%", newValue.doubleValue() * 100));
//        });
//
//        // Sets the sfx volume label
//        Label sfxVolumeLabel = new Label(String.format("%.0f%%", settings.getSfxVolume() * 100));
//        sfxVolumeLabel.setStyle("-fx-text-fill: #61C181;");
//        sfxVolumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
//            AudioManager.getInstance().setSfxVolume(newValue.doubleValue());
//            settings.setSfxVolume(newValue.doubleValue());
//            SettingsProvider.saveSettings(settings);
//            sfxVolumeLabel.setText(String.format("%.0f%%", newValue.doubleValue() * 100));
//        });
//        try {
//            InputStream muteButton = getClass().getClassLoader().getResourceAsStream("assets/textures/silent.png");
//            if (muteButton == null) {
//                throw new FileNotFoundException("Resource not found: assets/textures/silent.png");
//            }
//            InputStream soundButton = getClass().getClassLoader().getResourceAsStream("assets/textures/playing.png");
//            if (soundButton == null) {
//                throw new FileNotFoundException("Resource not found: assets/textures/playing.png");
//            }
//            // adding image to manually mute music
//            Image mute = new Image(muteButton);
//
//            // Sets music on/off functionality
//            Image audioOn = new Image(soundButton);
//            imageViewPlayingMenuSound = new ImageView(settings.isMuted() ? mute : audioOn);
//            imageViewPlayingMenuSound.setFitHeight(50);
//            imageViewPlayingMenuSound.setFitWidth(60);
//            imageViewPlayingMenuSound.setX(470);
//            imageViewPlayingMenuSound.setY(20);
//            imageViewPlayingMenuSound.setPreserveRatio(true);
//            imageViewPlayingMenuSound.setOnMouseClicked(mouseEvent -> {
//                boolean newMutedState = !AudioManager.getInstance().isMuted();
//                AudioManager.getInstance().setMuted(newMutedState);
//                mainMenuSound.setMute(newMutedState);
//                settings.setMuted(newMutedState);
//                imageViewPlayingMenuSound.setImage(newMutedState ? mute : audioOn);
//                SettingsProvider.saveSettings(settings);
//            });
//
//            // Sets Sound effects on/off functionality
//            imageViewPlayingSfxSounds = new ImageView(settings.isSfxMuted() ? mute : audioOn);
//            imageViewPlayingSfxSounds.setFitHeight(50);
//            imageViewPlayingSfxSounds.setFitWidth(60);
//            imageViewPlayingSfxSounds.setX(470);
//            imageViewPlayingSfxSounds.setY(20);
//            imageViewPlayingSfxSounds.setPreserveRatio(true);
//            imageViewPlayingSfxSounds.setOnMouseClicked(mouseEvent -> {
//                boolean newMutedState = !AudioManager.getInstance().isSfxMuted();
//                AudioManager.getInstance().setSfxMuted(newMutedState);
//                settings.setSfxMuted(newMutedState);
//                imageViewPlayingSfxSounds.setImage(newMutedState ? mute : audioOn);
//                SettingsProvider.saveSettings(settings);
//            });
//        } catch (FileNotFoundException e) {
//            System.out.println("File not found" + e.getMessage());
//        }
//
//        updateTexts();
//
//        // Listen for language changes and update UI automatically
//        languageManager.selectedLanguageProperty().addListener((observable, oldValue, newValue) -> updateTexts());
//
//        btnControls.setControlAction(() -> {
//            var bg = new Rectangle(getAppWidth(), getAppHeight(), Color.color(0, 0, 0, 0.5));
//            var controlsBox = new VBox(15);
//
//            controlsBox.getChildren().addAll(
//                    new PauseButton(languageManager.getTranslation("back"), () -> {
//                        controlsBox.getChildren().removeAll(controlsBox.getChildren());
//                        removeChild(bg);
//                        btnBack.enable();
//                        btnSound.enable();
//                        btnQuitGame.enable();
//                        btnControls.enable();
//                    }),
//                    btnMoveUp,
//                    btnMoveDown,
//                    btnMoveRight,
//                    btnMoveLeft,
//                    btnPauseGame,
//                    btnShoot,
//                    btnBomb
//            );
//
//            controlsBox.setTranslateX(300);
//            controlsBox.setTranslateY(getAppWidth() / 2.0);
//
//            btnBack.disable();
//            btnSound.disable();
//            btnQuitGame.disable();
//            btnControls.disable();
//
//            getContentRoot().getChildren().addAll(
//                    bg,
//                    controlsBox);
//
//        });
//
//        btnSound.setControlAction(() -> {
//            volumeSlider.adjustValue(settings.getVolume());
//            sfxVolumeSlider.adjustValue(settings.getSfxVolume());
//
//            var bg = new Rectangle(getAppWidth(), getAppHeight(), Color.color(0, 0, 0, 0.5));
//            var controlsBox = new VBox(10);
//            PauseButton pb = new PauseButton(languageManager.getTranslation("back"), () -> {
//                controlsBox.getChildren().removeAll(controlsBox.getChildren());
//                removeChild(bg);
//                btnBack.enable();
//                btnSound.enable();
//                btnQuitGame.enable();
//                btnControls.enable();
//            });
//            pb.setPrefHeight(15);
//
//            controlsBox.getChildren().addAll(
//                    pb,
//                    btnSoundMain,
//                    volumeLabel,
//                    volumeSlider,
//                    imageViewPlayingMenuSound,
//                    btnSoundSfx,
//                    sfxVolumeLabel,
//                    sfxVolumeSlider,
//                    imageViewPlayingSfxSounds
//            );
//
//            controlsBox.setTranslateX(300);
//            controlsBox.setTranslateY(getAppWidth() / 2.0);
//
//            btnBack.disable();
//            btnSound.disable();
//            btnQuitGame.disable();
//            btnControls.disable();
//
//            getContentRoot().getChildren().addAll(
//                    bg,
//                    controlsBox);
//
//        });
//
//        var bg = new Rectangle(getAppWidth(), getAppHeight(), Color.color(0, 0, 0, 0.5));
//        var title = FXGL.getUIFactoryService().newText(GameConstants.GAME_NAME, Color.WHITE, FontType.MONO, 35);
//        var box = new VBox(15,
//                btnBack,
//                btnSound,
//                btnControls,
//                btnQuitGame);
//        var version = FXGL.getUIFactoryService().newText(GameConstants.VERSION, Color.WHITE, FontType.MONO, 20);
//
//        title.setTranslateX(getAppWidth() / 2.0 - 175);
//        title.setTranslateY(150);
//
//        box.setTranslateX(100);
//        box.setTranslateY(getAppWidth() / 2.0 + 100);
//
//        version.setTranslateX(10);
//        version.setTranslateY(getAppHeight() - 10);
//
//        getContentRoot().getChildren().addAll(
//                bg, title, version, box);
//    }
//
//    private static class OptionsButton extends StackPane {
//
//        private final String description;
//        private final Text text;
//
//        public OptionsButton(String description) {
//            this.description = description;
//
//            text = getUIFactoryService().newText(description, Color.WHITE, 14.0);
//            setAlignment(Pos.CENTER_LEFT);
//            getChildren().addAll(text);
//
//        }
//        public void setText(String newText) {
//            text.setText(newText);
//        }
//    }
//
//    private static class PauseButton extends StackPane {
//
//        private static final Color SELECTED_COLOR = Color.WHITE;
//        private static final Color NOT_SELECTED_COLOR = Color.GRAY;
//        private String name;
//        private Runnable action;
//
//        private Text text;
//        private Rectangle selector;
//
//        private boolean disable = false;
//
//        public void disable() {
//            disable = true;
//        }
//
//        public void enable() {
//            disable = false;
//        }
//
//        public PauseButton(String name, Runnable action) {
//            this.name = name;
//            this.action = action;
//
//            text = getUIFactoryService().newText(name, Color.WHITE, 24.0);
//
//            text.strokeProperty().bind(
//                    Bindings.when(focusedProperty()).then(SELECTED_COLOR).otherwise(NOT_SELECTED_COLOR));
//            text.setStrokeWidth(0.5);
//
//            setAlignment(Pos.CENTER_LEFT);
//            setFocusTraversable(true);
//
//            setOnKeyPressed(e -> {
//                if (e.getCode() == KeyCode.ENTER && !disable) {
//                    action.run();
//                }
//            });
//
//            setOnMouseClicked(event -> {
//                if (!disable) {
//                    action.run();
//                }
//            });
//
//            setOnMouseEntered(event -> text.setFill(Color.RED));
//            setOnMouseExited(event -> text.setFill(SELECTED_COLOR));
//
//            getChildren().addAll(text);
//
//        }
//
//        public void setText(String newText) {
//            text.setText(newText);
//        }
//    }
//
//    private static class ControlButton extends StackPane {
//
//        private static final Color SELECTED_COLOR = Color.WHITE;
//        private static final Color NOT_SELECTED_COLOR = Color.GRAY;
//        private String name;
//        private Runnable action;
//
//        private Text text;
//        private Rectangle selector;
//
//        private boolean disable = false;
//
//        public void disable() {
//            disable = true;
//        }
//
//        public void enable() {
//            disable = false;
//        }
//
//        public ControlButton(String name) {
//            this.name = name;
//            text = getUIFactoryService().newText(name, Color.WHITE, 24.0);
//
//        }
//
//        public void setText(String newText) {
//            text.setText(newText);
//        }
//
//        public void setControlAction(Runnable action) {
//            this.action = action;
//
//            text = getUIFactoryService().newText(name, Color.WHITE, 24.0);
//
//            text.strokeProperty().bind(
//                    Bindings.when(focusedProperty()).then(SELECTED_COLOR).otherwise(NOT_SELECTED_COLOR));
//            text.setStrokeWidth(0.5);
//
//            setAlignment(Pos.CENTER_LEFT);
//            setFocusTraversable(true);
//
//            setOnKeyPressed(e -> {
//                if (e.getCode() == KeyCode.ENTER & !disable) {
//                    action.run();
//                }
//            });
//
//            setOnMouseClicked(event -> {
//                if (!disable) {
//                    action.run();
//                }
//            });
//
//            setOnMouseEntered(event -> text.setFill(Color.RED));
//            setOnMouseExited(event -> text.setFill(SELECTED_COLOR));
//
//            getChildren().addAll(text);
//        }
//    }
//    private void updateTexts() {
//        btnBack.setText(languageManager.getTranslation("back"));
//        btnSound.setText(languageManager.getTranslation("sound"));
//        btnSoundMain.setText(languageManager.getTranslation("sound_main"));
//        btnSoundSfx.setText(languageManager.getTranslation("sound_sfx"));
//        btnQuitGame.setText(languageManager.getTranslation("quit"));
//        btnControls.setText(languageManager.getTranslation("controls"));
//        btnMoveUp.setText("↑ / W : " + languageManager.getTranslation("move_up"));
//        btnMoveDown.setText("↓ / S : " + languageManager.getTranslation("move_down"));
//        btnMoveRight.setText("→ / D : " + languageManager.getTranslation("move_right"));
//        btnMoveLeft.setText("← / A : " + languageManager.getTranslation("move_left"));
//        btnPauseGame.setText(languageManager.getTranslation("pause_game"));
//        btnShoot.setText(languageManager.getTranslation("shoot"));
//        btnBomb.setText("B: " + languageManager.getTranslation("bomb"));
//    }
//
//    public void exit() {
//        Button btnYes = getUIFactoryService().newButton(languageManager.getTranslation("yes"));
//        btnYes.setPrefWidth(200);
//        btnYes.defaultButtonProperty();
//        // action event for the yes Button
//        EventHandler<ActionEvent> backToHomeEvent = e -> getGameController().gotoMainMenu();
//
//        // when button is pressed
//        btnYes.setOnAction(backToHomeEvent);
//
//        Button btnNo = getUIFactoryService().newButton(languageManager.getTranslation("no"));
//        btnNo.setPrefWidth(200);
//
//        // action event for the no Button
//        EventHandler<ActionEvent> resumeEvent = e -> getGameController().resumeEngine();
//
//        // when button is pressed
//        btnNo.setOnAction(resumeEvent);
//
//        getDialogService().showBox(languageManager.getTranslation("quit_game"), new VBox(), btnYes, btnNo);
//    }
//}