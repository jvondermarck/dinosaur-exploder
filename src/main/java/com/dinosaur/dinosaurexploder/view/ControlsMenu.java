/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.view;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.dinosaur.dinosaurexploder.components.GameControlsComponent;
import com.dinosaur.dinosaurexploder.components.GameControlsComponent.ControlType;
import com.dinosaur.dinosaurexploder.model.ControlAction;
import com.dinosaur.dinosaurexploder.model.KeyBindings;
import com.dinosaur.dinosaurexploder.utils.ControlBindings;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.MenuHelper;
import com.dinosaur.dinosaurexploder.utils.SettingsProvider;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/** The same editor is used above both the home settings and the pause menu. */
public class ControlsMenu extends FXGLMenu {
  private static final Logger LOGGER = Logger.getLogger(ControlsMenu.class.getName());
  private final LanguageManager languageManager = LanguageManager.getInstance();
  private final Map<ControlAction, Button> keyButtons = new EnumMap<>(ControlAction.class);
  private final Text status = new Text();
  private KeyBindings draft = SettingsProvider.loadKeyBindings();
  private ControlAction capturing;
  private static final int ZONE_SPACING = 50;

  public ControlsMenu() {
    super(MenuType.GAME_MENU);
    MenuHelper.setupSelectionMenu(
        this,
        createHeaderZone(),
        createOptionsZone(),
        createBackButton(),
        ZONE_SPACING,
        getAppWidth(),
        getAppHeight());
  }

  private Node createOptionsZone() {
    VBox content = new VBox(12);
    content.setAlignment(Pos.CENTER);
    content.setPadding(new Insets(20));

    GridPane gridPane = new GridPane(25, 2);
    gridPane.setAlignment(Pos.CENTER);
    VBox fixedControls = new VBox(3);
    fixedControls.setAlignment(Pos.CENTER);
    int rowCount = 0;
    for (ControlType type :
        new ControlType[] {
          ControlType.MOVE_UP,
          ControlType.MOVE_DOWN,
          ControlType.MOVE_LEFT,
          ControlType.MOVE_RIGHT,
          ControlType.PAUSE_GAME
        }) {
      String[] splitKey = GameControlsComponent.getControlText(type).split(":");
      Text fixedKey = new Text(splitKey[0]);
      Text fixedKeyDescr = new Text(splitKey[1]);
      fixedKey.setFill(Color.LIME);
      fixedKeyDescr.setFill(Color.LIME);
      gridPane.add(fixedKey, 0, rowCount);
      gridPane.add(fixedKeyDescr, 1, rowCount);
      rowCount++;
    }
    content.getChildren().add(gridPane);

    GridPane keyBindings = new GridPane(25, 25);
    keyBindings.setAlignment(Pos.CENTER);
    rowCount = 0;
    for (ControlAction action : ControlAction.values()) {
      Text label = new Text(GameControlsComponent.getActionLabel(action));
      label.setFill(Color.LIME);
      Button key = MenuHelper.createStyledButton(draft.keyFor(action).getName());
      key.setMinSize(130, 44);
      key.setWrapText(true);
      key.setOnAction(event -> {
        capturing = action;
        status.setFill(Color.LIME);
        status.setText(t("controls_press_key"));
        refreshKeys();
        getRoot().requestFocus();
      });
      Tooltip toolTip = new Tooltip(t("controls_reserved_key"));
      Tooltip.install(key, toolTip);

      keyButtons.put(action, key);
      HBox row = new HBox(20, label, key);
      keyBindings.add(label, 0, rowCount);
      keyBindings.add(key, 1, rowCount);
      rowCount++;
      row.setAlignment(Pos.CENTER);
    }
    content.getChildren().add(keyBindings);

    Text hint = new Text(t("controls_hint"));
    hint.setFill(Color.LIME);
    status.setFill(Color.LIME);

    getRoot().setFocusTraversable(true);
    getRoot().addEventFilter(KeyEvent.KEY_PRESSED, this::captureKey);
    return content;
  }

  private Node createBackButton() {
    Button backButton =
        MenuHelper.createStyledButton(languageManager.getTranslation("back").toUpperCase());
    backButton.setOnAction(event -> fireResume());

    Button reset = MenuHelper.createStyledButton(t("controls_reset"));
    reset.setWrapText(true);
    reset.setOnAction(event -> {
      draft = KeyBindings.defaults();
      capturing = null;
      status.setText("");
      apply();
      refreshKeys();
    });
    reset.setMaxWidth(Double.MAX_VALUE);
    backButton.setMaxWidth(Double.MAX_VALUE);

    TilePane backButtons = new TilePane();
    backButtons.setAlignment(Pos.CENTER);
    backButtons.setHgap(12.0);
    backButtons.getChildren().addAll(backButton, reset);

    return backButtons;
  }

  private VBox createHeaderZone() {
    TextFlow titleFlow =
        MenuHelper.createTitleFlow(
            languageManager.getTranslation("controls").toUpperCase(), getAppWidth() * 0.8);
    VBox headerZone = new VBox(50, titleFlow);
    headerZone.setAlignment(Pos.CENTER);
    return headerZone;
  }

  private void captureKey(KeyEvent event) {
    if (capturing == null) {
      if (event.getCode() == KeyCode.ESCAPE) {
        event.consume();
        close();
      }
      return;
    }
    event.consume();
    if (event.getCode() == KeyCode.ESCAPE) {
      capturing = null;
      status.setText("");
      refreshKeys();
      return;
    }
    try {
      if (event.isControlDown() || event.isAltDown() || event.isMetaDown() || event.isShiftDown()) {
        throw new IllegalArgumentException("controls_reserved_key");
      }
      draft = draft.withKey(capturing, event.getCode());
      capturing = null;
      status.setText("");
      apply();
    } catch (IllegalArgumentException ex) {
      status.setFill(Color.ORANGERED);
      status.setText(t(ex.getMessage()));
    }
    refreshKeys();
  }

  private void refreshKeys() {
    keyButtons.forEach((action, button) ->
        button.setText(action == capturing ? "..." : draft.keyFor(action).getName()));
  }

  private void apply() {
    try {
      ControlBindings.save(FXGL.getInput(), draft);
    } catch (IOException | IllegalArgumentException | IllegalStateException ex) {
      LOGGER.log(Level.WARNING, "Unable to apply custom controls", ex);
      status.setFill(Color.ORANGERED);
      status.setText(t("controls_save_error"));
    }
  }

  private String t(String key) {
    return languageManager.getTranslation(key);
  }

  private void close() {
    FXGL.getSceneService().popSubScene();
  }
}
