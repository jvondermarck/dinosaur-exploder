package com.dinosaur.dinosaurexploder.view;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.dinosaur.dinosaurexploder.model.Settings;
import com.dinosaur.dinosaurexploder.utils.AudioManager;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.MenuHelper;
import com.dinosaur.dinosaurexploder.utils.SettingsProvider;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Objects;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

public class SoundMenu extends FXGLMenu {

    public static final int SPACE_ZONE = 50;
    private final LanguageManager languageManager = LanguageManager.getInstance();
    private final Settings settings = SettingsProvider.loadSettings();
    private static final String LABEL_FORMAT = "%.0f%%";

    public SoundMenu() {
        super(MenuType.GAME_MENU);
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
        TextFlow titleFlow = MenuHelper.createTitleFlow(languageManager.getTranslation("sound options").toUpperCase(), getAppWidth() * 0.8);

        VBox headerZone = new VBox(25, titleFlow);
        headerZone.setAlignment(Pos.CENTER);
        return headerZone;
    }

    private Node createOptionsZone() {
        VBox options = new VBox(20);
        options.setAlignment(Pos.CENTER);
        Text backgroundTitle = MenuHelper.createSubtitle(languageManager.getTranslation("background music"),getAppWidth()*0.8,false);

        // Creates slider with the background music volume
        Slider musicVolume = setSlider("music");
        // Sets the music volume label and the music volume depending on the actual volume value
        Label volumeLabel = setSliderLabel(musicVolume, "music");

        Text sfxTitle = MenuHelper.createSubtitle(languageManager.getTranslation("game sounds"),getAppWidth()*0.8,false);
        // Creates slider for sfx volume
        Slider sfxVolume = setSlider("sfx");
        // Sets the sfx volume label and the sfx volume depending on the actual sfx volume value
        Label sfxVolumeLabel = setSliderLabel(sfxVolume, "sfx");

        options.getChildren().addAll(backgroundTitle, volumeLabel, musicVolume, sfxTitle, sfxVolumeLabel, sfxVolume);

        return options;
    }

    private Label setSliderLabel(Slider slider, String sliderType) {

        Label label = new Label(String.format(LABEL_FORMAT, ( sliderType.equals("music") ? settings.getVolume() : settings.getSfxVolume() ) * 100));
        label.setStyle("-fx-text-fill: #61C181;");
        slider
                .valueProperty()
                .addListener(
                        (observable, oldValue, newValue) -> {
                            if(sliderType.equals("music")) {
                                AudioManager.getInstance().setVolume(newValue.doubleValue());
                                //mainMenuSound.setVolume(newValue.doubleValue());
                                settings.setVolume(newValue.doubleValue());
                            } else {
                                AudioManager.getInstance().setSfxVolume(newValue.doubleValue());
                                settings.setSfxVolume(newValue.doubleValue());
                            }

                            SettingsProvider.saveSettings(settings);
                            label.setText(String.format(LABEL_FORMAT, newValue.doubleValue() * 100));
                        });
        return label;
    }

    private Slider setSlider(String sliderType) {
        // Init sfx Slider
        Slider slider = new Slider(0, 1, 1);
        slider.adjustValue( sliderType.equals("sfx") ? settings.getSfxVolume() : settings.getVolume() );
        slider.setBlockIncrement(0.01);

        slider
                .getStylesheets()
                .add(Objects.requireNonNull(getClass().getResource("/styles/styles.css")).toExternalForm());

        slider.setMaxWidth(getAppWidth() * 0.8);

        return slider;
    }

    private Node createBackButton() {
        Button backButton = MenuHelper.createStyledButton(languageManager.getTranslation("back").toUpperCase());
        backButton.setOnAction(event -> fireResume());
        return backButton;
    }
}

