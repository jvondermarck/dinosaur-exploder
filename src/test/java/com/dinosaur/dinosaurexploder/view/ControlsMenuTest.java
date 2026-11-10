/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.view;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.scene.SceneService;
import com.dinosaur.dinosaurexploder.model.ControlAction;
import com.dinosaur.dinosaurexploder.model.KeyBindings;
import com.dinosaur.dinosaurexploder.utils.ControlBindings;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.MenuHelper;
import com.dinosaur.dinosaurexploder.utils.SettingsProvider;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

/** Opt-in tests requiring a graphical desktop, not an FXGL game or a headless CI runner. */
@EnabledIfSystemProperty(named = "controls.ui.tests", matches = "true")
class ControlsMenuTest {
  @BeforeAll
  static void startJavaFx() {
    Platform.startup(() -> Platform.setImplicitExit(false));
  }

  @Test
  void captureValidatesKeysAndEscapeCancelsWithoutSaving() throws Exception {
    onFxThread(() -> {
      try (Fixture fixture = new Fixture()) {
        ControlsMenu menu = new ControlsMenu();
        button(menu, ControlAction.SHOOT.defaultKey().getName()).fire();
        key(menu, KeyCode.W);
        assertNotNull(button(menu, "..."));
        assertTrue(text(menu).contains(fixture.t("controls_reserved_key")));
        key(menu, ControlAction.BOMB.defaultKey());
        assertTrue(text(menu).contains(fixture.t("controls_duplicate_key")));
        key(menu, KeyCode.ESCAPE);
        assertNotNull(button(menu, ControlAction.SHOOT.defaultKey().getName()));
        verify(fixture.scenes, never()).popSubScene();
        key(menu, KeyCode.ESCAPE);
        verify(fixture.scenes).popSubScene();
        fixture.settings.verify(
            () -> SettingsProvider.saveKeyBindings(any(KeyBindings.class)), never());
      }
    });
  }

  @Test
  void applyPersistsAndReopeningShowsTheSavedKey() throws Exception {
    onFxThread(() -> {
      try (Fixture fixture = new Fixture()) {
        ControlsMenu menu = new ControlsMenu();
        button(menu, ControlAction.SHOOT.defaultKey().getName()).fire();
        key(menu, KeyCode.J);
        button(menu, fixture.t("controls_apply")).fire();
        assertEquals(KeyCode.J, fixture.saved.get().keyFor(ControlAction.SHOOT));
        assertEquals("J", fixture.input.getTriggerName(ControlAction.SHOOT.inputActionName()));
        verify(fixture.scenes).popSubScene();
        assertNotNull(button(new ControlsMenu(), "J"));
      }
    });
  }

  @Test
  void resetIsOnlySavedAfterApplyAndCancelDiscardsTheDraft() throws Exception {
    onFxThread(() -> {
      try (Fixture fixture = new Fixture()) {
        fixture.saved.set(KeyBindings.defaults().withKey(ControlAction.SHOOT, KeyCode.J));
        ControlBindings.apply(fixture.input, fixture.saved.get());
        ControlsMenu menu = new ControlsMenu();
        button(menu, fixture.t("controls_reset")).fire();
        assertEquals(KeyCode.J, fixture.saved.get().keyFor(ControlAction.SHOOT));
        button(menu, fixture.t("controls_cancel")).fire();
        assertNotNull(button(new ControlsMenu(), "J"));

        ControlsMenu reset = new ControlsMenu();
        button(reset, fixture.t("controls_reset")).fire();
        button(reset, fixture.t("controls_apply")).fire();
        for (ControlAction action : ControlAction.values()) {
          assertEquals(action.defaultKey(), fixture.saved.get().keyFor(action));
        }
      }
    });
  }

  @Test
  void aSaveErrorIsVisibleAndLeavesTheEditorOpenWithOldRuntimeKeys() throws Exception {
    onFxThread(() -> {
      try (Fixture fixture = new Fixture()) {
        fixture
            .settings
            .when(() -> SettingsProvider.saveKeyBindings(any(KeyBindings.class)))
            .thenThrow(new IOException("Cannot write settings"));
        ControlsMenu menu = new ControlsMenu();
        button(menu, ControlAction.SHOOT.defaultKey().getName()).fire();
        key(menu, KeyCode.J);
        button(menu, fixture.t("controls_apply")).fire();
        assertTrue(text(menu).contains(fixture.t("controls_save_error")));
        verify(fixture.scenes, never()).popSubScene();
        assertEquals(
            ControlAction.SHOOT.defaultKey().getName(),
            fixture.input.getTriggerName(ControlAction.SHOOT.inputActionName()));
      }
    });
  }

  @Test
  void editorLoadsAllTranslationsWithinTheGameWidth() throws Exception {
    onFxThread(() -> {
      try (Fixture fixture = new Fixture()) {
        for (String language :
            List.of(
                "English",
                "Spanish",
                "French",
                "German",
                "Italian",
                "Portuguese",
                "Russian",
                "Greek",
                "Bulgarian",
                "Japanese",
                "Chinese")) {
          LanguageManager.getInstance().setSelectedLanguage(language);
          for (String key :
              List.of(
                  "controls_press_key",
                  "controls_hint",
                  "controls_reserved_key",
                  "controls_duplicate_key",
                  "controls_reset",
                  "controls_apply",
                  "controls_cancel",
                  "controls_save_error")) {
            assertNotEquals(key, fixture.t(key), language);
            assertFalse(fixture.t(key).isBlank(), language);
          }
          ControlsMenu menu = new ControlsMenu();
          Scene scene = new Scene(menu.getRoot(), 550, 750);
          menu.getRoot().applyCss();
          menu.getRoot().layout();
          assertNotNull(button(menu, fixture.t("controls_apply")));
          assertNotNull(button(menu, fixture.t("controls_cancel")));
          assertTrue(menu.getRoot().getBoundsInLocal().getWidth() <= 550, language);
          assertEquals(550, scene.getWidth());
        }
      }
    });
  }

  private static void onFxThread(Runnable test) throws Exception {
    FutureTask<Void> task = new FutureTask<>(test, null);
    Platform.runLater(task);
    task.get(30, TimeUnit.SECONDS);
  }

  private static Button button(ControlsMenu menu, String label) {
    return nodes(menu.getRoot()).stream()
        .filter(Button.class::isInstance)
        .map(Button.class::cast)
        .filter(button -> button.getText().equalsIgnoreCase(label))
        .findFirst()
        .orElseThrow(() -> new AssertionError("Missing button: " + label));
  }

  private static List<Node> nodes(Node root) {
    java.util.ArrayList<Node> result = new java.util.ArrayList<>();
    result.add(root);
    if (root instanceof Parent parent) {
      for (Node child : parent.getChildrenUnmodifiable()) {
        result.addAll(nodes(child));
      }
    }
    // A ScrollPane's content is not attached to its skin before CSS is applied.
    if (root instanceof javafx.scene.control.ScrollPane scroll) {
      result.addAll(nodes(scroll.getContent()));
    }
    return result;
  }

  private static String text(ControlsMenu menu) {
    return nodes(menu.getRoot()).stream()
        .filter(Text.class::isInstance)
        .map(Text.class::cast)
        .map(Text::getText)
        .collect(java.util.stream.Collectors.joining("\n"));
  }

  private static void key(ControlsMenu menu, KeyCode code) {
    menu.getRoot()
        .fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", code, false, false, false, false));
  }

  private static class Fixture implements AutoCloseable {
    final Input input = new Input();
    final SceneService scenes = mock(SceneService.class);
    final AtomicReference<KeyBindings> saved = new AtomicReference<>(KeyBindings.defaults());
    final org.mockito.MockedStatic<FXGL> fxgl = mockStatic(FXGL.class);
    final org.mockito.MockedStatic<MenuHelper> helper = mockStatic(MenuHelper.class);
    final org.mockito.MockedStatic<SettingsProvider> settings = mockStatic(SettingsProvider.class);
    final String previousLanguage = LanguageManager.getInstance().selectedLanguageProperty().get();

    Fixture() {
      LanguageManager.getInstance().setSelectedLanguage("English");
      fxgl.when(FXGL::getAppWidth).thenReturn(550);
      fxgl.when(FXGL::getAppHeight).thenReturn(750);
      fxgl.when(FXGL::getInput).thenReturn(input);
      fxgl.when(FXGL::getSceneService).thenReturn(scenes);
      helper
          .when(() -> MenuHelper.createTitleFlow(anyString(), anyDouble()))
          .thenAnswer(invocation -> new TextFlow(new Text(invocation.<String>getArgument(0))));
      helper
          .when(() -> MenuHelper.createStyledButton(anyString()))
          .thenAnswer(invocation -> {
            Button button = new Button(invocation.<String>getArgument(0).toUpperCase(Locale.ROOT));
            button.setMinSize(140, 60);
            button
                .getStylesheets()
                .add(ControlsMenuTest.class.getResource("/styles/styles.css").toExternalForm());
            return button;
          });
      settings.when(SettingsProvider::loadKeyBindings).thenAnswer(invocation -> saved.get());
      settings
          .when(() -> SettingsProvider.saveKeyBindings(any(KeyBindings.class)))
          .thenAnswer(invocation -> {
            saved.set(invocation.getArgument(0));
            return null;
          });
      ControlBindings.register(
          input,
          saved.get(),
          Map.of(
              ControlAction.SHOOT,
              () -> {},
              ControlAction.BOMB,
              () -> {},
              ControlAction.SHIELD,
              () -> {}));
    }

    String t(String key) {
      return LanguageManager.getInstance().getTranslation(key);
    }

    @Override
    public void close() {
      settings.close();
      helper.close();
      fxgl.close();
      LanguageManager.getInstance().setSelectedLanguage(previousLanguage);
    }
  }
}
