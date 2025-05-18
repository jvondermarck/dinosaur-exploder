package com.dinosaur.dinosaurexploder.view;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.Scene;
import com.almasb.fxgl.ui.FontType;
import com.dinosaur.dinosaurexploder.model.Settings;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.SettingsProvider;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

import com.dinosaur.dinosaurexploder.constants.GameConstants;

import java.io.InputStream;

public class PauseMenu extends FXGLMenu {

    private static final String MUTE_ICON_PATH = "assets/textures/silent.png";
    private static final String SOUND_ICON_PATH = "assets/textures/playing.png";
    private static final String CSS_PATH = "/styles/styles.css";

    LanguageManager languageManager = LanguageManager.getInstance();

    BaseButton btnBack = new BaseButton(languageManager.getTranslation("back"));
    BaseButton btnQuitGame = new BaseButton(languageManager.getTranslation("quit"));
    BaseButton btnControls = new BaseButton(languageManager.getTranslation("controls"));
    BaseButton btnControlSound = new BaseButton(languageManager.getTranslation("audio"));

    // Store OptionButtons as fields so they can be updated
    OptionsButton btnMoveUp = new OptionsButton("↑ / W : " + languageManager.getTranslation("move_up"));
    OptionsButton btnMoveDown = new OptionsButton("↓ / S : " + languageManager.getTranslation("move_down"));
    OptionsButton btnMoveRight = new OptionsButton("→ / D : " + languageManager.getTranslation("move_right"));
    OptionsButton btnMoveLeft = new OptionsButton("← / A : " + languageManager.getTranslation("move_left"));
    OptionsButton btnPauseGame = new OptionsButton(languageManager.getTranslation("pause_game"));
    OptionsButton btnShoot = new OptionsButton(languageManager.getTranslation("shoot"));
    OptionsButton btnBomb = new OptionsButton("B: " + languageManager.getTranslation("bomb"));

    Settings settings;

    public PauseMenu() {
        super(MenuType.GAME_MENU);

        this.audioPlayerStart();
        updateTexts();

        // Listen for language changes and update UI automatically
        languageManager.selectedLanguageProperty().addListener((observable, oldValue, newValue) -> updateTexts());

        btnBack.setControlAction(this::fireResume);
        btnQuitGame.setControlAction(this::exit);
        btnControls.setControlAction(() -> {
            var bg = new Rectangle(getAppWidth(), getAppHeight(), Color.color(0, 0, 0, 0.5));
            var controlsBox = new VBox(15);
            BaseButton btnBackMenu = new BaseButton(languageManager.getTranslation("back"));

            btnBackMenu.setControlAction(() -> {
                controlsBox.getChildren().removeAll(controlsBox.getChildren());
                removeChild(bg);
                btnBack.enable();
                btnQuitGame.enable();
                btnControls.enable();
                btnControlSound.enable();
            });

            controlsBox.getChildren().addAll(
                    btnBackMenu,
                    btnMoveUp,
                    btnMoveDown,
                    btnMoveRight,
                    btnMoveLeft,
                    btnPauseGame,
                    btnShoot,
                    btnBomb
            );

            controlsBox.setTranslateX(300);
            controlsBox.setTranslateY(getAppWidth() / 2.0);

            btnBack.disable();
            btnQuitGame.disable();
            btnControls.disable();
            btnControlSound.disable();

            getContentRoot().getChildren().addAll(
                    bg,
                    controlsBox);

        });
        btnControlSound.setControlAction(() -> {
            var bg = new Rectangle(getAppWidth(), getAppHeight(), Color.color(0, 0, 0, 0.5));
            var controlsBox = new VBox(15);
            BaseButton btnBackMenu = new BaseButton(languageManager.getTranslation("back"));

            btnBackMenu.setControlAction(() -> {
                controlsBox.getChildren().removeAll(controlsBox.getChildren());
                removeChild(bg);
                btnBack.enable();
                btnQuitGame.enable();
                btnControls.enable();
                btnControlSound.enable();
            });

            controlsBox.getChildren().addAll(
                    btnBackMenu,
                    this.createVolumeControl()
            );

            controlsBox.setTranslateX(300);
            controlsBox.setTranslateY(getAppWidth() / 2.0);

            btnBack.disable();
            btnQuitGame.disable();
            btnControls.disable();
            btnControlSound.disable();

            getContentRoot().getChildren().addAll(
                    bg,
                    controlsBox);

        });

        var bg = new Rectangle(getAppWidth(), getAppHeight(), Color.color(0, 0, 0, 0.5));
        var title = FXGL.getUIFactoryService().newText(GameConstants.GAME_NAME, Color.WHITE, FontType.MONO, 35);
        var box = new VBox(15,
                btnBack,
                btnControls,
                btnControlSound,
                btnQuitGame);
        var version = FXGL.getUIFactoryService().newText(GameConstants.VERSION, Color.WHITE, FontType.MONO, 20);

        title.setTranslateX(getAppWidth() / 2.0 - 175);
        title.setTranslateY(150);

        box.setTranslateX(100);
        box.setTranslateY(getAppWidth() / 2.0 + 100);

        version.setTranslateX(10);
        version.setTranslateY(getAppHeight() - 10.0d);

        getContentRoot().getChildren().addAll(
                bg, title, version, box);
    }

    private static class OptionsButton extends StackPane {

        private final String description;
        private final Text text;

        public OptionsButton(String description) {
            this.description = description;

            text = getUIFactoryService().newText(description, Color.WHITE, 14.0);
            setAlignment(Pos.CENTER_LEFT);
            getChildren().addAll(text);

        }
        public void setText(String newText) {
            text.setText(newText);
        }
    }

    private static class BaseButton extends StackPane {

        private static final Color SELECTED_COLOR = Color.WHITE;
        private static final Color NOT_SELECTED_COLOR = Color.GRAY;

        private final Text text;

        private boolean disable = false;

        public void disable() {
            disable = true;
        }

        public void enable() {
            disable = false;
        }

        public BaseButton(String name) {
            text = getUIFactoryService().newText(name, Color.WHITE, 24.0);

            text.strokeProperty().bind(
                    Bindings.when(focusedProperty()).then(SELECTED_COLOR).otherwise(NOT_SELECTED_COLOR));
            text.setStrokeWidth(0.5);

            setAlignment(Pos.CENTER_LEFT);
            setFocusTraversable(true);
        }

        public void setText(String newText) {
            text.setText(newText);
        }

        public void setControlAction(Runnable action) {

            setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER & !disable) {
                    action.run();
                }
            });

            setOnMouseClicked(event -> {
                if (!disable) {
                    action.run();
                }
            });

            setOnMouseEntered(event -> text.setFill(Color.RED));
            setOnMouseExited(event -> text.setFill(SELECTED_COLOR));

            getChildren().addAll(text);
        }
    }

    private void updateTexts() {
        btnBack.setText(languageManager.getTranslation("back"));
        btnQuitGame.setText(languageManager.getTranslation("quit"));
        btnControls.setText(languageManager.getTranslation("controls"));
        btnControlSound.setText(languageManager.getTranslation("audio"));
        btnMoveUp.setText("↑ / W : " + languageManager.getTranslation("move_up"));
        btnMoveDown.setText("↓ / S : " + languageManager.getTranslation("move_down"));
        btnMoveRight.setText("→ / D : " + languageManager.getTranslation("move_right"));
        btnMoveLeft.setText("← / A : " + languageManager.getTranslation("move_left"));
        btnPauseGame.setText(languageManager.getTranslation("pause_game"));
        btnShoot.setText(languageManager.getTranslation("shoot"));
        btnBomb.setText("B: " + languageManager.getTranslation("bomb"));
    }

    public void exit() {
        Button btnYes = getUIFactoryService().newButton(languageManager.getTranslation("yes"));
        btnYes.setPrefWidth(200);
        btnYes.defaultButtonProperty();
        // action event for the yes Button
        EventHandler<ActionEvent> backToHomeEvent = e -> getGameController().gotoMainMenu();

        // when button is pressed
        btnYes.setOnAction(backToHomeEvent);

        Button btnNo = getUIFactoryService().newButton(languageManager.getTranslation("no"));
        btnNo.setPrefWidth(200);

        // action event for the no Button
        EventHandler<ActionEvent> resumeEvent = e -> getGameController().resumeEngine();

        // when button is pressed
        btnNo.setOnAction(resumeEvent);

        getDialogService().showBox(languageManager.getTranslation("quit_game"), new VBox(), btnYes, btnNo);
        FXGL.getAudioPlayer().stopAllSounds();
    }

    public VBox createVolumeControl() {
        Text title = getUIFactoryService().newText(languageManager.getTranslation("sound_effects_volume"), Color.WHITE, 15);
        ImageView muteButton = createMuteToggleButton();
        HBox volumeSlider = createVolumeSlider();

        HBox audioControls = new HBox(10, muteButton, volumeSlider);
        VBox audioContent = new VBox(10, title, audioControls);
        audioControls.setAlignment(Pos.CENTER);
        return audioContent;
    }

    private HBox createVolumeSlider() {
        Slider volumeSlider = new Slider(0, 1, settings.getVolumeVFX());
        Label volumeLabel = new Label(formatVolumeLabel(settings.getVolumeVFX()));
        volumeLabel.setStyle("-fx-text-fill: #61C181;");
        volumeSlider.setBlockIncrement(0.01);

        volumeSlider.getStylesheets().add(getClass().getResource(CSS_PATH).toExternalForm());

        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double volume = newVal.doubleValue();
            settings.setVolumeVFX(volume);
            SettingsProvider.saveSettings(settings);

            FXGL.getSettings().setGlobalMusicVolume(volume);
            FXGL.getSettings().setGlobalSoundVolume(volume);
            volumeLabel.setText(formatVolumeLabel(volume));
        });

        HBox boxSlider = new HBox(10, volumeSlider, volumeLabel);
        boxSlider.setAlignment(Pos.CENTER);

        return boxSlider;
    }

    private ImageView createMuteToggleButton() {
        Image muteImage = loadImage(MUTE_ICON_PATH);
        Image soundImage = loadImage(SOUND_ICON_PATH);

        ImageView buttonIcon = new ImageView(settings.isVfxMuted() ? muteImage : soundImage);
        buttonIcon.setFitHeight(30);
        buttonIcon.setFitWidth(40);
        buttonIcon.setPreserveRatio(true);

        buttonIcon.setOnMouseClicked(event -> {
            boolean newMuteStatus = !settings.isVfxMuted();
            settings.setVfxMuted(newMuteStatus);
            buttonIcon.setImage(newMuteStatus ? muteImage : soundImage);
            SettingsProvider.saveSettings(settings);

            FXGL.getSettings().setGlobalMusicVolume(newMuteStatus ? 0.0 : settings.getVolumeVFX());
            FXGL.getSettings().setGlobalSoundVolume(newMuteStatus ? 0.0 : settings.getVolumeVFX());
        });

        return buttonIcon;
    }

    private Image loadImage(String path) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(path);
        if (stream == null) {
            throw new RuntimeException("Resource not found: " + path);
        }
        return new Image(stream);
    }

    private String formatVolumeLabel(double volume) {
        return String.format("%.0f%%", volume * 100);
    }

    @Override
    public void onEnteredFrom(Scene prevState) {
        super.onEnteredFrom(prevState);
        audioPlayerStart();
    }

    void audioPlayerStart() {
        settings = SettingsProvider.loadSettings();
        FXGL.getSettings().setGlobalSoundVolume(settings.isVfxMuted() ? 0.0 : settings.getVolumeVFX());
        FXGL.getSettings().setGlobalMusicVolume(settings.isVfxMuted() ? 0.0 : settings.getVolumeVFX());
    }
}