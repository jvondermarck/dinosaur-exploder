/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.view;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.ui.FontType;
import com.dinosaur.dinosaurexploder.components.AudioControlsComponent;
import com.dinosaur.dinosaurexploder.components.AudioControlsComponent.VolumeType;
import com.dinosaur.dinosaurexploder.components.GameControlsComponent;
import com.dinosaur.dinosaurexploder.components.GameControlsComponent.ControlType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.model.Settings;
import com.dinosaur.dinosaurexploder.utils.AudioManager;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.SettingsProvider;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class PauseMenu extends FXGLMenu {
  private final MediaPlayer mainMenuSound;
  private final Settings settings = SettingsProvider.loadSettings();
  private ImageView imageViewPlayingMenuSound;
  private ImageView imageViewPlayingSfxSounds;
  private static final String LABEL_FORMAT = "%.0f%%";

  LanguageManager languageManager = LanguageManager.getInstance();
  PauseButton btnBack = new PauseButton(languageManager.getTranslation("back"), this::fireResume);
  PauseButton btnQuitGame = new PauseButton(languageManager.getTranslation("quit"), this::exit);
  ControlButton btnControls = new ControlButton(languageManager.getTranslation("controls"));
  ControlButton btnSound = new ControlButton(languageManager.getTranslation("sound"));
  OptionsButton btnSoundMain = new OptionsButton(languageManager.getTranslation("sound_main"));
  OptionsButton btnSoundSfx = new OptionsButton(languageManager.getTranslation("sound_sfx"));

  // Control buttons using GameControlsComponent
  OptionsButton btnMoveUp =
      new OptionsButton(GameControlsComponent.getControlText(ControlType.MOVE_UP));
  OptionsButton btnMoveDown =
      new OptionsButton(GameControlsComponent.getControlText(ControlType.MOVE_DOWN));
  OptionsButton btnMoveRight =
      new OptionsButton(GameControlsComponent.getControlText(ControlType.MOVE_RIGHT));
  OptionsButton btnMoveLeft =
      new OptionsButton(GameControlsComponent.getControlText(ControlType.MOVE_LEFT));
  OptionsButton btnPauseGame =
      new OptionsButton(GameControlsComponent.getControlText(ControlType.PAUSE_GAME));
  OptionsButton btnShoot =
      new OptionsButton(GameControlsComponent.getControlText(ControlType.SHOOT));
  OptionsButton btnBomb = new OptionsButton(GameControlsComponent.getControlText(ControlType.BOMB));
  OptionsButton btnShield =
      new OptionsButton(GameControlsComponent.getControlText(ControlType.SHIELD));

  public PauseMenu() {
    super(MenuType.GAME_MENU);

    mainMenuSound =
        new MediaPlayer(
            new Media(
                Objects.requireNonNull(getClass().getResource("/assets/sounds/mainMenu.wav"))
                    .toExternalForm()));

    // Read the last saved settings and load the main menu sound
    boolean muteState = settings.isMuted();
    AudioManager.getInstance().setMuted(muteState);
    mainMenuSound.setMute(muteState);
    AudioManager.getInstance().playMusic(GameConstants.BACKGROUND_SOUND);

    VBox musicVolumeControl =
        AudioControlsComponent.createVolumeControl(VolumeType.MUSIC, settings);
    VBox sfxVolumeControl = AudioControlsComponent.createVolumeControl(VolumeType.SFX, settings);

    Label volumeLabel = (Label) musicVolumeControl.getChildren().get(0);
    Slider volumeSlider = (Slider) musicVolumeControl.getChildren().get(1);

    Label sfxVolumeLabel = (Label) sfxVolumeControl.getChildren().get(0);
    Slider sfxVolumeSlider = (Slider) sfxVolumeControl.getChildren().get(1);

    volumeSlider
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              mainMenuSound.setVolume(newValue.doubleValue());
            });

    try {
      InputStream muteButton =
          getClass().getClassLoader().getResourceAsStream("assets/textures/silent.png");
      if (muteButton == null) {
        throw new FileNotFoundException("Resource not found: assets/textures/silent.png");
      }
      InputStream soundButton =
          getClass().getClassLoader().getResourceAsStream("assets/textures/playing.png");
      if (soundButton == null) {
        throw new FileNotFoundException("Resource not found: assets/textures/playing.png");
      }
      // adding image to manually mute music
      Image mute = new Image(muteButton);

      // Sets music on/off functionality
      Image audioOn = new Image(soundButton);
      imageViewPlayingMenuSound = new ImageView(settings.isMuted() ? mute : audioOn);
      imageViewPlayingMenuSound.setFitHeight(50);
      imageViewPlayingMenuSound.setFitWidth(60);
      imageViewPlayingMenuSound.setX(470);
      imageViewPlayingMenuSound.setY(20);
      imageViewPlayingMenuSound.setPreserveRatio(true);
      imageViewPlayingMenuSound.setOnMouseClicked(
          mouseEvent -> {
            boolean newMutedState = !AudioManager.getInstance().isMuted();
            AudioManager.getInstance().setMuted(newMutedState);
            mainMenuSound.setMute(newMutedState);
            settings.setMuted(newMutedState);
            imageViewPlayingMenuSound.setImage(newMutedState ? mute : audioOn);
            SettingsProvider.saveSettings(settings);
          });

      // Sets Sound effects on/off functionality
      imageViewPlayingSfxSounds = new ImageView(settings.isSfxMuted() ? mute : audioOn);
      imageViewPlayingSfxSounds.setFitHeight(50);
      imageViewPlayingSfxSounds.setFitWidth(60);
      imageViewPlayingSfxSounds.setX(470);
      imageViewPlayingSfxSounds.setY(20);
      imageViewPlayingSfxSounds.setPreserveRatio(true);
      imageViewPlayingSfxSounds.setOnMouseClicked(
          mouseEvent -> {
            boolean newMutedState = !AudioManager.getInstance().isSfxMuted();
            AudioManager.getInstance().setSfxMuted(newMutedState);
            settings.setSfxMuted(newMutedState);
            imageViewPlayingSfxSounds.setImage(newMutedState ? mute : audioOn);
            SettingsProvider.saveSettings(settings);
          });
    } catch (FileNotFoundException e) {
      System.out.println("File not found" + e.getMessage());
    }

    updateTexts();

    // Auto-update UI when language changes
    languageManager
        .selectedLanguageProperty()
        .addListener((obs, oldValue, newValue) -> updateTexts());

    // Controls button action → Opens controls overlay
    btnControls.setControlAction(
        () -> {
          // 1. Fond sombre pour le sous-menu
          var controlsBg = new Rectangle(getAppWidth(), getAppHeight(), Color.color(0, 0, 0, 0.85));

          // 2. VBox pour aligner les touches verticalement
          var controlsBox = new VBox(10);
          controlsBox.setAlignment(Pos.CENTER);
          controlsBox.setMaxWidth(getAppWidth() * 0.7);

          // 3. Conteneur global pour centrer la box (C'est lui qu'on affichera/supprimera)
          StackPane controlsContainer = new StackPane(controlsBox);
          controlsContainer.setPrefSize(getAppWidth(), getAppHeight());
          controlsContainer.setAlignment(Pos.CENTER);

          // 4. Bouton de retour spécifique au menu contrôles
          PauseButton btnBackFromControls =
              new PauseButton(
                  languageManager.getTranslation("back"),
                  () -> {
                    // ✅ On retire les deux éléments que l'on a ajouté au Root
                    getContentRoot().getChildren().removeAll(controlsBg, controlsContainer);

                    // Réactive le menu principal
                    btnBack.enable();
                    btnQuitGame.enable();
                    btnControls.enable();
                  });

          VBox.setMargin(btnBackFromControls, new Insets(0, 0, 40, 0));

          // 5. Ajout des boutons et textes dans la boîte
          controlsBox
              .getChildren()
              .addAll(
                  btnBackFromControls,
                  btnMoveUp,
                  btnMoveDown,
                  btnMoveRight,
                  btnMoveLeft,
                  btnPauseGame,
                  btnShoot,
                  btnBomb,
                  btnShield);

          // Désactivation temporaire des boutons en arrière-plan
          btnBack.disable();
          btnQuitGame.disable();
          btnControls.disable();

          // ✅ Ajout à l'affichage (le container est par-dessus le reste)
          getContentRoot().getChildren().addAll(controlsBg, controlsContainer);
        });

    // Adjust sound menu
    btnSound.setControlAction(
        () -> {
          Settings freshSettings = SettingsProvider.loadSettings();
          volumeSlider.adjustValue(freshSettings.getVolume());
          sfxVolumeSlider.adjustValue(freshSettings.getSfxVolume());

          var controlsBg = new Rectangle(getAppWidth(), getAppHeight(), Color.color(0, 0, 0, 0.85));

          var controlsBox = new VBox(10);
          controlsBox.setAlignment(Pos.CENTER);
          controlsBox.setMaxWidth(getAppWidth() * 0.7);

          StackPane controlsContainer = new StackPane(controlsBox);
          controlsContainer.setPrefSize(getAppWidth(), getAppHeight());
          controlsContainer.setAlignment(Pos.CENTER);

          PauseButton btnBackFromSounds =
              new PauseButton(
                  languageManager.getTranslation("back"),
                  () -> {
                    getContentRoot().getChildren().removeAll(controlsBg, controlsContainer);
                    btnBack.enable();
                    btnSound.enable();
                    btnQuitGame.enable();
                    btnControls.enable();
                  });

          VBox.setMargin(btnBackFromSounds, new Insets(0, 0, 40, 0));

          controlsBox
              .getChildren()
              .addAll(
                  btnBackFromSounds,
                  btnSoundMain,
                  volumeLabel,
                  volumeSlider,
                  imageViewPlayingMenuSound,
                  btnSoundSfx,
                  sfxVolumeLabel,
                  sfxVolumeSlider,
                  imageViewPlayingSfxSounds);

          btnBack.disable();
          btnSound.disable();
          btnQuitGame.disable();
          btnControls.disable();

          getContentRoot().getChildren().addAll(controlsBg, controlsContainer);
        });

    // --- MISE EN PAGE DU MENU PRINCIPAL ---

    // Background principal
    var bg = new Rectangle(getAppWidth(), getAppHeight(), Color.color(0, 0, 0, 0.8));

    // Titre centré
    var title =
        FXGL.getUIFactoryService()
            .newText(
                GameConstants.GAME_NAME, Color.WHITE, FontType.MONO, GameConstants.MAIN_TITLES);

    javafx.scene.text.TextFlow titleFlow = new javafx.scene.text.TextFlow(title);
    titleFlow.setTextAlignment(TextAlignment.CENTER);
    titleFlow.setMaxWidth(getAppWidth());

    StackPane titleContainer = new StackPane(titleFlow);
    titleContainer.setPrefWidth(getAppWidth());
    titleContainer.setTranslateY(100);

    // Menu principal (les 3 boutons)
    var box = new VBox(15, btnBack, btnSound, btnControls, btnQuitGame);
    box.setAlignment(Pos.CENTER);

    StackPane buttonContainer = new StackPane(box);
    buttonContainer.setPrefWidth(getAppWidth());
    buttonContainer.setTranslateY(getAppHeight() / 2.0 - 50);

    // Version
    var version =
        FXGL.getUIFactoryService()
            .newText(
                GameConstants.VERSION, Color.WHITE, FontType.MONO, GameConstants.TEXT_SUB_DETAILS);
    version.setTranslateX(10);
    version.setTranslateY(getAppHeight() - 10.0);

    // Assemblage final
    getContentRoot().getChildren().addAll(bg, titleContainer, version, buttonContainer);
  }

  // ------------------- BUTTON CLASSES -----------------------

  private static class OptionsButton extends StackPane {
    private final Text text;

    public OptionsButton(String desc) {
      text = getUIFactoryService().newText(desc, Color.WHITE, GameConstants.TEXT_SUB_DETAILS);

      // ✅ Wrapping avec largeur réduite
      text.setWrappingWidth(350);
      text.setTextAlignment(TextAlignment.CENTER);

      setAlignment(Pos.CENTER); // ✅ Centré
      getChildren().add(text);
    }

    public void setText(String newText) {
      text.setText(newText);
    }

    public Text getTextNode() {
      return text;
    }
  }

  private static class PauseButton extends StackPane {
    private static final Color SELECTED = Color.WHITE;
    private static final Color NOT_SELECTED = Color.GRAY;

    private Runnable action;
    private boolean disabled = false;
    private final Text text;

    public PauseButton(String name, Runnable action) {
      this.action = action;
      text = getUIFactoryService().newText(name, Color.WHITE, GameConstants.TEXT_SUB_DETAILS);

      text.strokeProperty()
          .bind(Bindings.when(focusedProperty()).then(SELECTED).otherwise(NOT_SELECTED));
      text.setStrokeWidth(0.5);

      setAlignment(Pos.CENTER); // ✅ Centré au lieu de CENTER_LEFT
      setFocusTraversable(true);

      setOnKeyPressed(
          e -> {
            if (e.getCode() == KeyCode.ENTER && !disabled) action.run();
          });

      setOnMouseClicked(
          e -> {
            if (!disabled) action.run();
          });

      setOnMouseEntered(e -> text.setFill(Color.RED));
      setOnMouseExited(e -> text.setFill(SELECTED));

      getChildren().add(text);
    }

    public void disable() {
      disabled = true;
    }

    public void enable() {
      disabled = false;
    }

    public void setText(String newText) {
      text.setText(newText);
    }

    public Text getTextNode() {
      return text;
    }
  }

  private static class ControlButton extends PauseButton {
    private Runnable action;

    public ControlButton(String name) {
      super(name, null);
    }

    public void setControlAction(Runnable action) {
      this.action = action;
      setOnMouseClicked(
          e -> {
            if (!super.disabled) action.run();
          });
    }
  }

  // ------------------- UPDATE TEXT FOR LANG CHANGE -----------------------

  private void updateTexts() {
    btnBack.setText(languageManager.getTranslation("back").toUpperCase());
    btnSound.setText(languageManager.getTranslation("sound").toUpperCase());
    btnSoundMain.setText(languageManager.getTranslation("sound_main"));
    btnSoundSfx.setText(languageManager.getTranslation("sound_sfx"));
    btnQuitGame.setText(languageManager.getTranslation("quit").toUpperCase());
    btnControls.setText(languageManager.getTranslation("controls").toUpperCase());

    // Update control buttons using GameControlsComponent
    btnMoveUp.setText(GameControlsComponent.getControlText(ControlType.MOVE_UP));
    btnMoveDown.setText(GameControlsComponent.getControlText(ControlType.MOVE_DOWN));
    btnMoveRight.setText(GameControlsComponent.getControlText(ControlType.MOVE_RIGHT));
    btnMoveLeft.setText(GameControlsComponent.getControlText(ControlType.MOVE_LEFT));
    btnPauseGame.setText(GameControlsComponent.getControlText(ControlType.PAUSE_GAME));
    btnShoot.setText(GameControlsComponent.getControlText(ControlType.SHOOT));
    btnBomb.setText(GameControlsComponent.getControlText(ControlType.BOMB));
    btnShield.setText(GameControlsComponent.getControlText(ControlType.SHIELD));
  }

  // ------------------- QUIT POPUP -----------------------

  public void exit() {
    Button btnYes = getUIFactoryService().newButton(languageManager.getTranslation("yes"));
    btnYes.setPrefWidth(200);
    btnYes.setOnAction(e -> getGameController().gotoMainMenu());

    Button btnNo = getUIFactoryService().newButton(languageManager.getTranslation("no"));
    btnNo.setPrefWidth(200);
    btnNo.setOnAction(e -> getGameController().resumeEngine());

    getDialogService()
        .showBox(languageManager.getTranslation("quit_game"), new VBox(), btnYes, btnNo);
  }
}
