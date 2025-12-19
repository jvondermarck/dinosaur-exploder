package com.dinosaur.dinosaurexploder.view;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.ui.FontType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.model.Settings;
import com.dinosaur.dinosaurexploder.utils.AudioManager;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.SettingsProvider;
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
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

public class PauseMenu extends FXGLMenu {
  private final MediaPlayer mainMenuSound;
  private final Settings settings = SettingsProvider.loadSettings();
  private ImageView imageViewPlayingMenuSound;
  private ImageView imageViewPlayingSfxSounds;

  LanguageManager languageManager = LanguageManager.getInstance();
  PauseButton btnBack = new PauseButton(languageManager.getTranslation("back"), this::fireResume);
  PauseButton btnQuitGame = new PauseButton(languageManager.getTranslation("quit"), this::exit);
  ControlButton btnControls = new ControlButton(languageManager.getTranslation("controls"));
  ControlButton btnSound = new ControlButton(languageManager.getTranslation("sound"));
  OptionsButton btnSoundMain = new OptionsButton(languageManager.getTranslation("sound_main"));
  OptionsButton btnSoundSfx = new OptionsButton(languageManager.getTranslation("sound_sfx"));

  // Options buttons (controls list)
  OptionsButton btnMoveUp =
      new OptionsButton("↑ / W : " + languageManager.getTranslation("move_up"));
  OptionsButton btnMoveDown =
      new OptionsButton("↓ / S :  " + languageManager.getTranslation("move_down"));
  OptionsButton btnMoveRight =
      new OptionsButton("→ / D : " + languageManager.getTranslation("move_right"));
  OptionsButton btnMoveLeft =
      new OptionsButton("← / A : " + languageManager.getTranslation("move_left"));
  OptionsButton btnPauseGame = new OptionsButton(languageManager.getTranslation("pause_game"));
  OptionsButton btnShoot = new OptionsButton(languageManager.getTranslation("shoot"));
  OptionsButton btnBomb = new OptionsButton("B: " + languageManager.getTranslation("bomb"));
  OptionsButton btnShield = new OptionsButton("E: " + languageManager.getTranslation("shield"));

  public PauseMenu() {
    super(MenuType.GAME_MENU);

      mainMenuSound = new MediaPlayer(
              new Media(Objects.requireNonNull(getClass().getResource("/assets/sounds/mainMenu.wav")).toExternalForm())
      );

      // Read the last saved settings and load the main menu sound
      boolean muteState = settings.isMuted();
      AudioManager.getInstance().setMuted(muteState);
      mainMenuSound.setMute(muteState);
      AudioManager.getInstance().playMusic(GameConstants.BACKGROUND_SOUND);

      // Init music Slider
      Slider volumeSlider = new Slider(0, 1, 1);
      volumeSlider.adjustValue(settings.getVolume());
      volumeSlider.setBlockIncrement(0.01);

      volumeSlider.getStylesheets()
              .add(Objects.requireNonNull(getClass().getResource("/styles/styles.css")).toExternalForm());

      // Init sfx Slider
      Slider sfxVolumeSlider = new Slider(0, 1, 1);
      sfxVolumeSlider.adjustValue(settings.getSfxVolume());
      sfxVolumeSlider.setBlockIncrement(0.01);

      sfxVolumeSlider.getStylesheets()
              .add(Objects.requireNonNull(getClass().getResource("/styles/styles.css")).toExternalForm());

      // Sets the music volume label
      Label volumeLabel = new Label(String.format("%.0f%%", settings.getVolume() * 100));
      volumeLabel.setStyle("-fx-text-fill: #61C181;");
      volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
          AudioManager.getInstance().setVolume(newValue.doubleValue());
          mainMenuSound.setVolume(newValue.doubleValue());
          settings.setVolume(newValue.doubleValue());
          SettingsProvider.saveSettings(settings);
          volumeLabel.setText(String.format("%.0f%%", newValue.doubleValue() * 100));
      });

      // Sets the sfx volume label
      Label sfxVolumeLabel = new Label(String.format("%.0f%%", settings.getSfxVolume() * 100));
      sfxVolumeLabel.setStyle("-fx-text-fill: #61C181;");
      sfxVolumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
          AudioManager.getInstance().setSfxVolume(newValue.doubleValue());
          settings.setSfxVolume(newValue.doubleValue());
          SettingsProvider.saveSettings(settings);
          sfxVolumeLabel.setText(String.format("%.0f%%", newValue.doubleValue() * 100));
      });
      try {
          InputStream muteButton = getClass().getClassLoader().getResourceAsStream("assets/textures/silent.png");
          if (muteButton == null) {
              throw new FileNotFoundException("Resource not found: assets/textures/silent.png");
          }
          InputStream soundButton = getClass().getClassLoader().getResourceAsStream("assets/textures/playing.png");
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
          imageViewPlayingMenuSound.setOnMouseClicked(mouseEvent -> {
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
          imageViewPlayingSfxSounds.setOnMouseClicked(mouseEvent -> {
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
      btnSound.setControlAction(() -> {
          volumeSlider.adjustValue(settings.getVolume());
          sfxVolumeSlider.adjustValue(settings.getSfxVolume());

          var controlsBg = new Rectangle(getAppWidth(), getAppHeight(), Color.color(0, 0, 0, 0.85));

          var controlsBox = new VBox(10);
          controlsBox.setAlignment(Pos.CENTER);
          controlsBox.setMaxWidth(getAppWidth() * 0.7);

          StackPane controlsContainer = new StackPane(controlsBox);
          controlsContainer.setPrefSize(getAppWidth(), getAppHeight());
          controlsContainer.setAlignment(Pos.CENTER);

          PauseButton btnBackFromSounds = new PauseButton(languageManager.getTranslation("back"),
                  () -> {
              getContentRoot().getChildren().removeAll(controlsBg, controlsContainer);
              btnBack.enable();
              btnSound.enable();
              btnQuitGame.enable();
              btnControls.enable();
          });

          VBox.setMargin(btnBackFromSounds, new Insets(0, 0, 40, 0));

          controlsBox.getChildren().addAll(
                  btnBackFromSounds,
                  btnSoundMain,
                  volumeLabel,
                  volumeSlider,
                  imageViewPlayingMenuSound,
                  btnSoundSfx,
                  sfxVolumeLabel,
                  sfxVolumeSlider,
                  imageViewPlayingSfxSounds
          );

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
    btnSound.setText(languageManager.getTranslation("sound"));
    btnSoundMain.setText(languageManager.getTranslation("sound_main"));
    btnSoundSfx.setText(languageManager.getTranslation("sound_sfx"));
    btnQuitGame.setText(languageManager.getTranslation("quit").toUpperCase());
    btnControls.setText(languageManager.getTranslation("controls").toUpperCase());

    btnMoveUp.setText("↑ / W : " + languageManager.getTranslation("move_up"));
    btnMoveDown.setText("↓ / S : " + languageManager.getTranslation("move_down"));
    btnMoveRight.setText("→ / D : " + languageManager.getTranslation("move_right"));
    btnMoveLeft.setText("← / A : " + languageManager.getTranslation("move_left"));
    btnPauseGame.setText(languageManager.getTranslation("pause_game"));
    btnShoot.setText(languageManager.getTranslation("shoot"));
    btnBomb.setText("B: " + languageManager.getTranslation("bomb"));
    btnShield.setText("E: " + languageManager.getTranslation("shield"));
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
