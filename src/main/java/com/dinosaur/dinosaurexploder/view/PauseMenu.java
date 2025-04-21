package com.dinosaur.dinosaurexploder.view;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.ui.FontType;
import com.dinosaur.dinosaurexploder.model.LanguageManager;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

import com.dinosaur.dinosaurexploder.model.GameConstants;

public class PauseMenu extends FXGLMenu {
    LanguageManager languageManager = LanguageManager.getInstance();
    PauseButton btnBack = new PauseButton(languageManager.getTranslation("back"), () -> fireResume());
    PauseButton btnQuitGame = new PauseButton(languageManager.getTranslation("quit"), () -> exit());
    ControlButton btnControls = new ControlButton(languageManager.getTranslation("controls"));

    // Store OptionButtons as fields so they can be updated
    OptionsButton btnMoveUp = new OptionsButton("↑ / W : " + languageManager.getTranslation("move_up"));
    OptionsButton btnMoveDown = new OptionsButton("↓ / S : " + languageManager.getTranslation("move_down"));
    OptionsButton btnMoveRight = new OptionsButton("→ / D : " + languageManager.getTranslation("move_right"));
    OptionsButton btnMoveLeft = new OptionsButton("← / A : " + languageManager.getTranslation("move_left"));
    OptionsButton btnPauseGame = new OptionsButton(languageManager.getTranslation("pause_game"));
    OptionsButton btnShoot = new OptionsButton(languageManager.getTranslation("shoot"));
    OptionsButton btnBomb = new OptionsButton("B: " + languageManager.getTranslation("bomb"));

    public PauseMenu() {
        super(MenuType.GAME_MENU);

        updateTexts();

        // Listen for language changes and update UI automatically
        languageManager.selectedLanguageProperty().addListener((observable, oldValue, newValue) -> {
            updateTexts();
        });

        btnControls.setControlAction(() -> {
            var bg = new Rectangle(getAppWidth(), getAppHeight(), Color.color(0, 0, 0, 0.5));
            var controlsBox = new VBox(15);

            controlsBox.getChildren().addAll(
                    new PauseButton(languageManager.getTranslation("back"), () -> {
                        controlsBox.getChildren().removeAll(controlsBox.getChildren());
                        removeChild(bg);
                        btnBack.enable();
                        btnQuitGame.enable();
                        btnControls.enable();
                    }),
                    btnMoveUp,
                    btnMoveDown,
                    btnMoveRight,
                    btnMoveLeft,
                    btnPauseGame,
                    btnShoot,
                    btnBomb
            );

            controlsBox.setTranslateX(300);
            controlsBox.setTranslateY(getAppWidth() / 2);

            btnBack.disable();
            btnQuitGame.disable();
            btnControls.disable();

            getContentRoot().getChildren().addAll(
                    bg,
                    controlsBox);

        });

        var bg = new Rectangle(getAppWidth(), getAppHeight(), Color.color(0, 0, 0, 0.5));
        var title = FXGL.getUIFactoryService().newText(GameConstants.GAME_NAME, Color.WHITE, FontType.MONO, 35);
        var box = new VBox(15,
                btnBack,
                btnControls,
                btnQuitGame);
        var version = FXGL.getUIFactoryService().newText("v1.2.0", Color.WHITE, FontType.MONO, 20);

        title.setTranslateX(getAppWidth() / 2 - 175);
        title.setTranslateY(150);

        box.setTranslateX(100);
        box.setTranslateY(getAppWidth() / 2 + 100);

        version.setTranslateX(10);
        version.setTranslateY(getAppHeight() - 10);

        getContentRoot().getChildren().addAll(
                bg, title, version, box);
    }

    private static class OptionsButton extends StackPane {

        private String description;
        private Text text;

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

    private static class PauseButton extends StackPane {

        private static final Color SELECTED_COLOR = Color.WHITE;
        private static final Color NOT_SELECTED_COLOR = Color.GRAY;
        private String name;
        private Runnable action;

        private Text text;
        private Rectangle selector;

        private boolean disable = false;

        public void disable() {
            disable = true;
        }

        public void enable() {
            disable = false;
        }

        public PauseButton(String name, Runnable action) {
            this.name = name;
            this.action = action;

            text = getUIFactoryService().newText(name, Color.WHITE, 24.0);

            text.strokeProperty().bind(
                    Bindings.when(focusedProperty()).then(SELECTED_COLOR).otherwise(NOT_SELECTED_COLOR));
            text.setStrokeWidth(0.5);

            setAlignment(Pos.CENTER_LEFT);
            setFocusTraversable(true);

            setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER && !disable) {
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

        public void setText(String newText) {
            text.setText(newText);
        }
    }

    private static class ControlButton extends StackPane {

        private static final Color SELECTED_COLOR = Color.WHITE;
        private static final Color NOT_SELECTED_COLOR = Color.GRAY;
        private String name;
        private Runnable action;

        private Text text;
        private Rectangle selector;

        private boolean disable = false;

        public void disable() {
            disable = true;
        }

        public void enable() {
            disable = false;
        }

        public ControlButton(String name) {
            this.name = name;
            text = getUIFactoryService().newText(name, Color.WHITE, 24.0);

        }

        public void setText(String newText) {
            text.setText(newText);
        }

        public void setControlAction(Runnable action) {
            this.action = action;

            text = getUIFactoryService().newText(name, Color.WHITE, 24.0);

            text.strokeProperty().bind(
                    Bindings.when(focusedProperty()).then(SELECTED_COLOR).otherwise(NOT_SELECTED_COLOR));
            text.setStrokeWidth(0.5);

            setAlignment(Pos.CENTER_LEFT);
            setFocusTraversable(true);

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
    }
}