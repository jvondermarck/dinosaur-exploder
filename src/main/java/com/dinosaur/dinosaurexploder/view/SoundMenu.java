/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.view;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.dinosaur.dinosaurexploder.components.AudioControlsComponent;
import com.dinosaur.dinosaurexploder.components.AudioControlsComponent.VolumeType;
import com.dinosaur.dinosaurexploder.model.Settings;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.MenuHelper;
import com.dinosaur.dinosaurexploder.utils.SettingsProvider;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class SoundMenu extends FXGLMenu {

  public static final int SPACE_ZONE = 50;
  public static final String MUSIC = "music";
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
    TextFlow titleFlow =
        MenuHelper.createTitleFlow(
            languageManager.getTranslation("sound").toUpperCase(), getAppWidth() * 0.8);

    VBox headerZone = new VBox(25, titleFlow);
    headerZone.setAlignment(Pos.CENTER);
    return headerZone;
  }

  private Node createOptionsZone() {
    VBox options = new VBox(20);
    options.setAlignment(Pos.CENTER);
    Text backgroundTitle =
        MenuHelper.createSubtitle(
            languageManager.getTranslation("sound_main"), getAppWidth() * 0.8, false);
    VBox musicVolumeControl =
        AudioControlsComponent.createVolumeControl(VolumeType.MUSIC, settings, getAppWidth() * 0.8);

    Text sfxTitle =
        MenuHelper.createSubtitle(
            languageManager.getTranslation("sound_sfx"), getAppWidth() * 0.8, false);
    VBox sfxVolumeControl =
        AudioControlsComponent.createVolumeControl(VolumeType.SFX, settings, getAppWidth() * 0.8);

    options.getChildren().addAll(backgroundTitle, musicVolumeControl, sfxTitle, sfxVolumeControl);
    return options;
  }

  private Node createBackButton() {
    Button backButton =
        MenuHelper.createStyledButton(languageManager.getTranslation("back").toUpperCase());
    backButton.setOnAction(event -> fireResume());
    return backButton;
  }
}
