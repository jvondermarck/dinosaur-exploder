/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.view;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import com.dinosaur.dinosaurexploder.components.GameControlsComponent;
import com.dinosaur.dinosaurexploder.components.GameControlsComponent.ControlType;
import com.dinosaur.dinosaurexploder.model.ControlAction;
import com.dinosaur.dinosaurexploder.model.KeyBindings;
import com.dinosaur.dinosaurexploder.utils.ControlBindings;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.MenuHelper;
import com.dinosaur.dinosaurexploder.utils.SettingsProvider;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/** The same editor is used above both the home settings and the pause menu. */
public class ControlsMenu extends SubScene {
  private static final Logger LOGGER = Logger.getLogger(ControlsMenu.class.getName());
  private final LanguageManager languageManager = LanguageManager.getInstance();
  private final Map<ControlAction, Button> keyButtons = new EnumMap<>(ControlAction.class);
  private final Text status = new Text();
  private KeyBindings draft = SettingsProvider.loadKeyBindings();
  private ControlAction capturing;

  public ControlsMenu() {
    double width = FXGL.getAppWidth();
    double height = FXGL.getAppHeight();
    VBox content = new VBox(12);
    content.setAlignment(Pos.CENTER);
    content.setPadding(new Insets(20));
    content.getChildren().add(MenuHelper.createTitleFlow(t("controls"), width * 0.8));
    VBox fixedControls = new VBox(3);
    fixedControls.setAlignment(Pos.CENTER);
    for (ControlType type :
        new ControlType[] {
          ControlType.MOVE_UP,
          ControlType.MOVE_DOWN,
          ControlType.MOVE_LEFT,
          ControlType.MOVE_RIGHT,
          ControlType.PAUSE_GAME
        }) {
      Text fixedControl = new Text(GameControlsComponent.getControlText(type));
      fixedControl.setFill(Color.LIME);
      fixedControls.getChildren().add(fixedControl);
    }
    content.getChildren().add(fixedControls);
    for (ControlAction action : ControlAction.values()) {
      Text label = new Text(GameControlsComponent.getActionLabel(action));
      label.setFill(Color.LIME);
      Button key = MenuHelper.createStyledButton(draft.keyFor(action).getName());
      key.setMinSize(130, 44);
      key.setMaxWidth(width * 0.4);
      key.setWrapText(true);
      key.setOnAction(event -> {
        capturing = action;
        status.setFill(Color.LIME);
        status.setText(t("controls_press_key"));
        refreshKeys();
        getRoot().requestFocus();
      });
      keyButtons.put(action, key);
      HBox row = new HBox(20, label, key);
      row.setAlignment(Pos.CENTER);
      content.getChildren().add(row);
    }
    Text hint = new Text(t("controls_hint"));
    hint.setFill(Color.LIME);
    hint.setWrappingWidth(width * 0.8);
    status.setWrappingWidth(width * 0.8);
    status.setFill(Color.LIME);

    Button reset = MenuHelper.createStyledButton(t("controls_reset"));
    reset.setMinHeight(44);
    reset.setMaxWidth(width * 0.8);
    reset.setWrapText(true);
    reset.setOnAction(event -> {
      draft = KeyBindings.defaults();
      capturing = null;
      status.setText("");
      refreshKeys();
    });
    Button apply = MenuHelper.createStyledButton(t("controls_apply"));
    apply.setMinHeight(44);
    apply.setOnAction(event -> apply());
    Button cancel = MenuHelper.createStyledButton(t("controls_cancel"));
    cancel.setMinHeight(44);
    cancel.setOnAction(event -> close());
    HBox actions = new HBox(12, apply, cancel);
    actions.setAlignment(Pos.CENTER);
    content.getChildren().addAll(hint, status, reset, actions);

    ScrollPane scroll = new ScrollPane(content);
    scroll.setFitToWidth(true);
    scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scroll.setStyle("-fx-background: black; -fx-background-color: transparent;");
    scroll.setMaxSize(width, height);
    StackPane container =
        new StackPane(new Rectangle(width, height, Color.color(0, 0, 0, 0.95)), scroll);
    container.setPrefSize(width, height);
    getContentRoot().getChildren().add(container);
    getRoot().setFocusTraversable(true);
    getRoot().addEventFilter(KeyEvent.KEY_PRESSED, this::captureKey);
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
      close();
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
