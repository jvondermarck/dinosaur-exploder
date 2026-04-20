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
import java.util.logging.Level;
import java.util.logging.Logger;
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
  private Logger logger = Logger.getLogger(getClass().getName());

  LanguageManager languageManager = LanguageManager.getInstance();
  PauseButton btnBack = new PauseButton(languageManager.getTranslation("back"), this::fireResume);
  PauseButton btnQuitGame = new PauseButton(languageManager.getTranslation("quit"), this::exit);
  ControlButton btnControls = new ControlButton(languageManager.getTranslation("controls"));
  ControlButton btnSound = new ControlButton(languageManager.getTranslation("sound"));
  ControlButton btnAchievements = new ControlButton(languageManager.getTranslation("achievements"));
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
      logger.log(Level.INFO, "File not found {0}", e.getMessage());
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
                    btnAchievements.enable();
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
          btnAchievements.disable();

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
                    btnAchievements.enable();
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
          btnAchievements.disable();

          getContentRoot().getChildren().addAll(controlsBg, controlsContainer);
        });

    // --- MISE EN PAGE DU MENU PRINCIPAL ---
    btnAchievements.setControlAction(
        () -> {
          com.dinosaur.dinosaurexploder.achievements.AchievementManager achievementManager =
              new com.dinosaur.dinosaurexploder.achievements.AchievementManager();
          java.util.List<com.dinosaur.dinosaurexploder.achievements.Achievement> achievements =
              achievementManager.loadAchievement();
          if (achievements.isEmpty()) achievements = achievementManager.getAllAchievements();

          // Palette matching styles.css
          Color cGreen = Color.web("#00dc00");
          Color cCyan  = Color.web("#00c8ff");
          Color cLime  = Color.web("#7DD858");
          Color cGold  = Color.GOLD;
          Color cLocked = Color.web("#4a4a4a");

          // ── Overlay background ─────────────────────────────────────────────
          var achBg = new Rectangle(getAppWidth(), getAppHeight(), Color.color(0, 0, 0, 0.93));

          // ── Title + double-glow (green outer, cyan inner) ──────────────────
          var titleText =
              FXGL.getUIFactoryService()
                  .newText(
                      languageManager.getTranslation("achievements").toUpperCase(),
                      cGreen,
                      com.almasb.fxgl.ui.FontType.MONO,
                      GameConstants.MAIN_TITLES);
          javafx.scene.effect.DropShadow tg1 = new javafx.scene.effect.DropShadow(18, cGreen);
          tg1.setSpread(0.35);
          javafx.scene.effect.DropShadow tg2 = new javafx.scene.effect.DropShadow(32, cCyan);
          tg2.setSpread(0.10);
          tg1.setInput(tg2);
          titleText.setEffect(tg1);

          javafx.animation.FadeTransition titlePulse =
              new javafx.animation.FadeTransition(javafx.util.Duration.seconds(2.4), titleText);
          titlePulse.setFromValue(0.82);
          titlePulse.setToValue(1.0);
          titlePulse.setCycleCount(javafx.animation.FadeTransition.INDEFINITE);
          titlePulse.setAutoReverse(true);
          titlePulse.play();

          javafx.scene.text.TextFlow titleFlow = new javafx.scene.text.TextFlow(titleText);
          titleFlow.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
          titleFlow.setMaxWidth(getAppWidth() * 0.8);

          // ── Separator line ─────────────────────────────────────────────────
          javafx.scene.shape.Rectangle sep =
              new javafx.scene.shape.Rectangle(getAppWidth() * 0.70, 2);
          sep.setFill(
              new javafx.scene.paint.LinearGradient(
                  0, 0, 1, 0, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                  new javafx.scene.paint.Stop(0, Color.TRANSPARENT),
                  new javafx.scene.paint.Stop(0.2, cCyan),
                  new javafx.scene.paint.Stop(0.8, cGreen),
                  new javafx.scene.paint.Stop(1, Color.TRANSPARENT)));
          sep.setEffect(new javafx.scene.effect.DropShadow(6, cCyan));

          // ── Progress bar ───────────────────────────────────────────────────
          long unlocked =
              achievements.stream()
                  .filter(com.dinosaur.dinosaurexploder.achievements.Achievement::isCompleted)
                  .count();
          int total = achievements.size();
          double barW = getAppWidth() * 0.68;
          double fillRatio = (total == 0) ? 0 : (double) unlocked / total;

          javafx.scene.shape.Rectangle track =
              new javafx.scene.shape.Rectangle(barW, 13, Color.web("#111111"));
          track.setArcWidth(13); track.setArcHeight(13);
          track.setStroke(Color.web("#333333")); track.setStrokeWidth(1);

          javafx.scene.shape.Rectangle fill =
              new javafx.scene.shape.Rectangle(Math.max(barW * fillRatio, 0), 13);
          fill.setArcWidth(13); fill.setArcHeight(13);
          fill.setFill(new javafx.scene.paint.LinearGradient(
              0, 0, 1, 0, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
              new javafx.scene.paint.Stop(0.0, cGreen),
              new javafx.scene.paint.Stop(0.5, cLime),
              new javafx.scene.paint.Stop(1.0, cCyan)));
          javafx.scene.effect.DropShadow fillGlow =
              new javafx.scene.effect.DropShadow(10, cGreen);
          fillGlow.setSpread(0.25);
          fill.setEffect(fillGlow);

          StackPane fillWrap = new StackPane(fill);
          fillWrap.setMaxWidth(barW);
          fillWrap.setAlignment(Pos.CENTER_LEFT);

          StackPane progressBar = new StackPane(track, fillWrap);
          progressBar.setMaxWidth(barW);
          progressBar.setAlignment(Pos.CENTER_LEFT);

          var summaryText =
              FXGL.getUIFactoryService()
                  .newText(
                      unlocked + " / " + total + "  unlocked",
                      cLime,
                      com.almasb.fxgl.ui.FontType.MONO,
                      GameConstants.TEXT_SIZE_GAME_INFO);

          VBox headerBox = new VBox(5, titleFlow, sep, summaryText, progressBar);
          headerBox.setAlignment(Pos.CENTER);

          // ── Cards list ─────────────────────────────────────────────────────
          VBox achBox = new VBox(9);
          achBox.setAlignment(Pos.CENTER);
          achBox.setPadding(new Insets(8, 14, 8, 14));

          double cardW = getAppWidth() * 0.80;
          double cardH = 64;

          // Load textures once
          java.io.InputStream coinIs =
              getClass().getClassLoader().getResourceAsStream("assets/textures/coin.png");
          java.io.InputStream lockIs =
              getClass().getClassLoader().getResourceAsStream("assets/textures/lock.png");
          java.io.InputStream starIs =
              getClass().getClassLoader().getResourceAsStream("assets/textures/star_badge.png");
          javafx.scene.image.Image coinImg = coinIs != null ? new javafx.scene.image.Image(coinIs) : null;
          javafx.scene.image.Image lockImg = lockIs != null ? new javafx.scene.image.Image(lockIs) : null;
          javafx.scene.image.Image starImg = starIs != null ? new javafx.scene.image.Image(starIs) : null;

          // Helper: save coins to totalCoins.ser
          java.util.function.Consumer<Integer> addCoins = amount -> {
            com.dinosaur.dinosaurexploder.model.TotalCoins tc =
                new com.dinosaur.dinosaurexploder.model.TotalCoins();
            try (java.io.ObjectInputStream ois = new java.io.ObjectInputStream(
                new java.io.FileInputStream(GameConstants.TOTAL_COINS_FILE))) {
              tc = (com.dinosaur.dinosaurexploder.model.TotalCoins) ois.readObject();
            } catch (Exception ignored) {}
            tc.setTotal(tc.getTotal() + amount);
            try (java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(
                new java.io.FileOutputStream(GameConstants.TOTAL_COINS_FILE))) {
              oos.writeObject(tc);
            } catch (Exception ignored) {}
          };

          for (com.dinosaur.dinosaurexploder.achievements.Achievement ach : achievements) {
            boolean done = ach.isCompleted();
            boolean claimed = ach.isClaimed();

            // Card background with gradient + double border glow
            javafx.scene.shape.Rectangle cardBg =
                new javafx.scene.shape.Rectangle(cardW, cardH);
            cardBg.setArcWidth(6); cardBg.setArcHeight(6);
            if (claimed) {
              // Gold tint for claimed
              cardBg.setFill(new javafx.scene.paint.LinearGradient(
                  0, 0, 1, 0, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                  new javafx.scene.paint.Stop(0.0, Color.web("#1a1200", 0.97)),
                  new javafx.scene.paint.Stop(1.0, Color.web("#0d0800", 0.97))));
              cardBg.setStroke(cGold);
              cardBg.setStrokeWidth(1.5);
            } else if (done) {
              cardBg.setFill(new javafx.scene.paint.LinearGradient(
                  0, 0, 1, 0, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                  new javafx.scene.paint.Stop(0.0, Color.web("#0a1a0a", 0.97)),
                  new javafx.scene.paint.Stop(0.6, Color.web("#001500", 0.97)),
                  new javafx.scene.paint.Stop(1.0, Color.web("#001020", 0.97))));
              cardBg.setStroke(new javafx.scene.paint.LinearGradient(
                  0, 0, 1, 0, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                  new javafx.scene.paint.Stop(0, cGreen),
                  new javafx.scene.paint.Stop(1, cCyan)));
              cardBg.setStrokeWidth(1.5);
              javafx.scene.effect.DropShadow cg1 =
                  new javafx.scene.effect.DropShadow(14, cGreen);
              cg1.setSpread(0.12);
              cardBg.setEffect(cg1);
            } else {
              cardBg.setFill(Color.web("#0d0d0d", 0.92));
              cardBg.setStroke(Color.web("#2a2a2a")); cardBg.setStrokeWidth(1);
            }

            // Corner pixel accents
            javafx.scene.shape.Rectangle tlAcc = new javafx.scene.shape.Rectangle(5, 5);
            tlAcc.setFill(claimed ? cGold : done ? cCyan : Color.web("#2a2a2a"));
            if (done || claimed) tlAcc.setEffect(new javafx.scene.effect.Glow(0.8));
            tlAcc.setTranslateX(-cardW / 2.0 + 4); tlAcc.setTranslateY(-cardH / 2.0 + 4);

            javafx.scene.shape.Rectangle brAcc = new javafx.scene.shape.Rectangle(5, 5);
            brAcc.setFill(claimed ? cGold : done ? cCyan : Color.web("#2a2a2a"));
            if (done || claimed) brAcc.setEffect(new javafx.scene.effect.Glow(0.8));
            brAcc.setTranslateX(cardW / 2.0 - 4); brAcc.setTranslateY(cardH / 2.0 - 4);

            // ── Badge ─────────────────────────────────────────────────────
            // Outer ring (larger, subtle glow)
            double badgeR = 24;
            javafx.scene.shape.Circle outerRing = new javafx.scene.shape.Circle(badgeR);
            outerRing.setFill(Color.TRANSPARENT);
            if (claimed) {
              outerRing.setStroke(cGold);
              outerRing.setStrokeWidth(2);
              javafx.scene.effect.DropShadow or = new javafx.scene.effect.DropShadow(14, cGold);
              or.setSpread(0.25); outerRing.setEffect(or);
            } else if (done) {
              outerRing.setStroke(cCyan);
              outerRing.setStrokeWidth(1.5);
              javafx.scene.effect.DropShadow or = new javafx.scene.effect.DropShadow(12, cCyan);
              or.setSpread(0.18); outerRing.setEffect(or);
            } else {
              outerRing.setStroke(Color.web("#2a2a2a"));
              outerRing.setStrokeWidth(1);
            }

            // Inner filled circle
            javafx.scene.shape.Circle badgeCircle = new javafx.scene.shape.Circle(19);
            if (claimed) {
              badgeCircle.setFill(new javafx.scene.paint.LinearGradient(0, 0, 1, 1, true,
                  javafx.scene.paint.CycleMethod.NO_CYCLE,
                  new javafx.scene.paint.Stop(0, Color.web("#2a1a00", 0.97)),
                  new javafx.scene.paint.Stop(1, Color.web("#0d0800", 0.97))));
              badgeCircle.setStroke(cGold); badgeCircle.setStrokeWidth(1.2);
            } else if (done) {
              badgeCircle.setFill(new javafx.scene.paint.LinearGradient(0, 0, 1, 1, true,
                  javafx.scene.paint.CycleMethod.NO_CYCLE,
                  new javafx.scene.paint.Stop(0, Color.web("#003d00", 0.97)),
                  new javafx.scene.paint.Stop(1, Color.web("#001520", 0.97))));
              badgeCircle.setStroke(cGreen); badgeCircle.setStrokeWidth(1.2);
              javafx.scene.effect.DropShadow bcg = new javafx.scene.effect.DropShadow(10, cGreen);
              bcg.setSpread(0.22); badgeCircle.setEffect(bcg);
            } else {
              badgeCircle.setFill(Color.web("#111111", 0.95));
              badgeCircle.setStroke(Color.web("#333333")); badgeCircle.setStrokeWidth(1);
            }

            // Icon inside badge
            javafx.scene.Node badgeIcon;
            if (claimed) {
              // Gold checkmark / coin for claimed
              if (coinImg != null) {
                javafx.scene.image.ImageView civ2 = new javafx.scene.image.ImageView(coinImg);
                civ2.setFitWidth(22); civ2.setFitHeight(22); civ2.setPreserveRatio(true);
                javafx.scene.effect.DropShadow cg2 = new javafx.scene.effect.DropShadow(10, cGold);
                cg2.setSpread(0.4); civ2.setEffect(cg2);
                badgeIcon = civ2;
              } else {
                javafx.scene.text.Text t = FXGL.getUIFactoryService().newText("✓", cGold, 20);
                t.setEffect(new javafx.scene.effect.DropShadow(8, cGold)); badgeIcon = t;
              }
            } else if (done && starImg != null) {
              javafx.scene.image.ImageView siv = new javafx.scene.image.ImageView(starImg);
              siv.setFitWidth(24); siv.setFitHeight(24); siv.setPreserveRatio(true);
              javafx.scene.effect.DropShadow sg = new javafx.scene.effect.DropShadow(10, cGreen);
              sg.setSpread(0.3);
              javafx.scene.effect.Glow gl = new javafx.scene.effect.Glow(1.0);
              gl.setInput(sg);
              siv.setEffect(gl);
              badgeIcon = siv;
            } else if (!done && lockImg != null) {
              javafx.scene.image.ImageView liv = new javafx.scene.image.ImageView(lockImg);
              liv.setFitWidth(18); liv.setFitHeight(18); liv.setPreserveRatio(true);
              javafx.scene.effect.ColorAdjust ca = new javafx.scene.effect.ColorAdjust();
              ca.setSaturation(-1); ca.setBrightness(-0.3); liv.setEffect(ca);
              badgeIcon = liv;
            } else {
              badgeIcon = FXGL.getUIFactoryService()
                  .newText(claimed ? "✓" : done ? "★" : "?",
                      claimed ? cGold : done ? cGreen : cLocked, 18);
            }

            // Pulsing animation on claimable badges
            StackPane badgeBox = new StackPane(outerRing, badgeCircle, badgeIcon);
            badgeBox.setMinWidth(58); badgeBox.setAlignment(Pos.CENTER);
            if (done && !claimed) {
              javafx.animation.ScaleTransition badgePulse =
                  new javafx.animation.ScaleTransition(javafx.util.Duration.seconds(1.4), outerRing);
              badgePulse.setFromX(1.0); badgePulse.setToX(1.18);
              badgePulse.setFromY(1.0); badgePulse.setToY(1.18);
              badgePulse.setCycleCount(javafx.animation.ScaleTransition.INDEFINITE);
              badgePulse.setAutoReverse(true); badgePulse.play();
            }

            // Description
            var descText =
                FXGL.getUIFactoryService()
                    .newText(ach.getDescription(),
                        claimed ? cGold : done ? cLime : cLocked,
                        com.almasb.fxgl.ui.FontType.MONO,
                        GameConstants.TEXT_SIZE_GAME_INFO);
            descText.setWrappingWidth(cardW * 0.50);
            if (done && !claimed) {
              javafx.scene.effect.DropShadow dg = new javafx.scene.effect.DropShadow(5, cGreen);
              dg.setSpread(0.10); descText.setEffect(dg);
            } else if (claimed) {
              javafx.scene.effect.DropShadow dg = new javafx.scene.effect.DropShadow(5, cGold);
              dg.setSpread(0.10); descText.setEffect(dg);
            }

            // Coin icon + amount (right side)
            javafx.scene.Node coinIcon;
            if (coinImg != null) {
              javafx.scene.image.ImageView civ = new javafx.scene.image.ImageView(coinImg);
              civ.setFitWidth(16); civ.setFitHeight(16); civ.setPreserveRatio(true);
              if (!done) {
                javafx.scene.effect.ColorAdjust ca = new javafx.scene.effect.ColorAdjust();
                ca.setSaturation(-1); ca.setBrightness(-0.4); civ.setEffect(ca);
              }
              coinIcon = civ;
            } else {
              coinIcon = FXGL.getUIFactoryService()
                  .newText("C", done ? cGold : cLocked, 12);
            }

            // Right-side pill: "CLAIMED" label or coin reward
            javafx.scene.layout.HBox coinPill;
            if (claimed) {
              var claimedLabel = FXGL.getUIFactoryService()
                  .newText("CLAIMED", cGold, com.almasb.fxgl.ui.FontType.MONO,
                      GameConstants.TEXT_SIZE_GAME_INFO);
              javafx.scene.effect.DropShadow gl = new javafx.scene.effect.DropShadow(6, cGold);
              gl.setSpread(0.2); claimedLabel.setEffect(gl);
              coinPill = new javafx.scene.layout.HBox(claimedLabel);
            } else {
              var coinAmt = FXGL.getUIFactoryService()
                  .newText("+" + ach.getRewardCoins(),
                      done ? cGold : cLocked,
                      com.almasb.fxgl.ui.FontType.MONO,
                      GameConstants.TEXT_SIZE_GAME_INFO);
              if (done) {
                javafx.scene.effect.DropShadow gg = new javafx.scene.effect.DropShadow(6, cGold);
                gg.setSpread(0.15); coinAmt.setEffect(gg);
              }
              coinPill = new javafx.scene.layout.HBox(5, coinIcon, coinAmt);
            }
            coinPill.setAlignment(Pos.CENTER);
            coinPill.setPadding(new Insets(4, 10, 4, 8));
            coinPill.setStyle(done
                ? "-fx-background-color: linear-gradient(to bottom,#1a1200,#0d0a00);"
                    + " -fx-background-radius:4; -fx-border-radius:4;"
                    + " -fx-border-color: rgba(218,165,32,0.75),rgba(0,200,255,0.35);"
                    + " -fx-border-width:1.5;"
                    + " -fx-effect:dropshadow(gaussian,rgba(218,165,32,0.5),8,0.3,0,0);"
                : "-fx-background-color:#111111; -fx-background-radius:4;"
                    + " -fx-border-radius:4; -fx-border-color:#2a2a2a; -fx-border-width:1;");

            StackPane coinBox = new StackPane(coinPill);
            coinBox.setMinWidth(88); coinBox.setAlignment(Pos.CENTER_RIGHT);

            javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
            javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            javafx.scene.layout.HBox inner =
                new javafx.scene.layout.HBox(8, badgeBox, descText, spacer, coinBox);
            inner.setAlignment(Pos.CENTER_LEFT);
            inner.setPadding(new Insets(0, 12, 0, 6));
            inner.setMaxWidth(cardW - 4);

            StackPane card = new StackPane(cardBg, tlAcc, brAcc, inner);
            card.setMaxWidth(cardW); card.setAlignment(Pos.CENTER_LEFT);

            if (done && !claimed) {
              // Claimable: add "click to claim" hint + click handler
              var hint = FXGL.getUIFactoryService()
                  .newText("▶ CLICK TO CLAIM", cCyan, com.almasb.fxgl.ui.FontType.MONO, 9);
              javafx.animation.FadeTransition ft =
                  new javafx.animation.FadeTransition(javafx.util.Duration.seconds(0.9), hint);
              ft.setFromValue(0.3); ft.setToValue(1.0);
              ft.setCycleCount(javafx.animation.FadeTransition.INDEFINITE);
              ft.setAutoReverse(true); ft.play();
              StackPane.setAlignment(hint, Pos.BOTTOM_CENTER);
              card.getChildren().add(hint);

              card.setCursor(javafx.scene.Cursor.HAND);
              card.setOnMouseEntered(e -> {
                javafx.animation.ScaleTransition st =
                    new javafx.animation.ScaleTransition(javafx.util.Duration.millis(110), card);
                st.setToX(1.022); st.setToY(1.022); st.play();
              });
              card.setOnMouseExited(e -> {
                javafx.animation.ScaleTransition st =
                    new javafx.animation.ScaleTransition(javafx.util.Duration.millis(110), card);
                st.setToX(1.0); st.setToY(1.0); st.play();
              });
              final java.util.List<com.dinosaur.dinosaurexploder.achievements.Achievement> achRef = achievements;
              card.setOnMouseClicked(e -> {
                int earned = ach.claim();
                if (earned > 0) {
                  addCoins.accept(earned);
                  achievementManager.saveAchievement(achRef);
                  // Visual feedback: tint card gold
                  cardBg.setFill(new javafx.scene.paint.LinearGradient(
                      0, 0, 1, 0, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                      new javafx.scene.paint.Stop(0.0, Color.web("#1a1200", 0.97)),
                      new javafx.scene.paint.Stop(1.0, Color.web("#0d0800", 0.97))));
                  cardBg.setStroke(cGold); cardBg.setStrokeWidth(1.5);
                  tlAcc.setFill(cGold); brAcc.setFill(cGold);
                  descText.setFill(cGold);
                  card.getChildren().removeIf(n -> n instanceof javafx.scene.text.Text
                      && ((javafx.scene.text.Text) n).getText().contains("CLICK TO CLAIM"));
                  card.setOnMouseClicked(null);
                  card.setCursor(javafx.scene.Cursor.DEFAULT);
                }
              });
            } else if (claimed) {
              card.setOnMouseEntered(e -> {
                javafx.animation.ScaleTransition st =
                    new javafx.animation.ScaleTransition(javafx.util.Duration.millis(110), card);
                st.setToX(1.022); st.setToY(1.022); st.play();
              });
              card.setOnMouseExited(e -> {
                javafx.animation.ScaleTransition st =
                    new javafx.animation.ScaleTransition(javafx.util.Duration.millis(110), card);
                st.setToX(1.0); st.setToY(1.0); st.play();
              });
            }
            achBox.getChildren().add(card);
          }

          javafx.scene.control.ScrollPane achScroll =
              new javafx.scene.control.ScrollPane(achBox);
          achScroll.setFitToWidth(true);
          achScroll.setPrefWidth(getAppWidth() * 0.86);
          achScroll.setPrefHeight(getAppHeight() * 0.56);
          achScroll.setStyle(
              "-fx-background: transparent; -fx-background-color: transparent;"
                  + " -fx-border-color: transparent;");
          achScroll.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
          achScroll.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED);
          String scrollbarCss = java.util.Objects.requireNonNull(
              getClass().getResource("/styles/scrollbar.css")).toExternalForm();
          achScroll.getStylesheets().add(scrollbarCss);

          // Declare achContainer early so the lambda can capture it
          VBox overlayContent = new VBox(12);
          overlayContent.setAlignment(Pos.CENTER);
          overlayContent.setMaxWidth(getAppWidth() * 0.90);

          StackPane achContainer = new StackPane(overlayContent);
          achContainer.setPrefSize(getAppWidth(), getAppHeight());
          achContainer.setAlignment(Pos.CENTER);

          // ── Back button ────────────────────────────────────────────────────
          javafx.scene.control.Button btnBackFromAch =
              new javafx.scene.control.Button(
                  languageManager.getTranslation("back").toUpperCase());
          btnBackFromAch
              .getStylesheets()
              .add(
                  java.util.Objects.requireNonNull(
                          getClass().getResource(GameConstants.STYLESHEET_PATH))
                      .toExternalForm());
          btnBackFromAch.setMinSize(140, 50);
          btnBackFromAch.setOnAction(
              e -> {
                getContentRoot().getChildren().removeAll(achBg, achContainer);
                btnBack.enable();
                btnSound.enable();
                btnControls.enable();
                btnAchievements.enable();
                btnQuitGame.enable();
              });

          overlayContent.getChildren().addAll(btnBackFromAch, headerBox, achScroll);

          btnBack.disable();
          btnSound.disable();
          btnControls.disable();
          btnAchievements.disable();
          btnQuitGame.disable();

          getContentRoot().getChildren().addAll(achBg, achContainer);
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
    var box = new VBox(15, btnBack, btnSound, btnControls, btnAchievements, btnQuitGame);
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
    btnAchievements.setText(languageManager.getTranslation("achievements").toUpperCase());

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
