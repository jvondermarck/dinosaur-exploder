/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.view;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.ui.FontType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.MenuHelper;
import java.util.List;
import java.util.Objects;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class DisplayMenu extends FXGLMenu {

  private static final double[] SCALES = {1.0, 1.25, 1.5, 2.0};
  // Keep the selected windowed scale while the game is running.
  // Null means FXGL chose a size smaller than our presets.
  private static Double lastWindowedScale = null;
  private final LanguageManager languageManager = LanguageManager.getInstance();
  private final Stage stage = FXGL.getPrimaryStage();
  private ToggleGroup fullscreenGroup;

  public DisplayMenu() {
    this(false);
  }

  public DisplayMenu(boolean fromPauseMenu) {
    super(MenuType.MAIN_MENU);

    VBox headerZone = createHeaderZone();
    VBox optionsZone = createOptionsZone();
    Button backButton = createBackButton();

    if (fromPauseMenu) {
      Rectangle background =
          new Rectangle(getAppWidth(), getAppHeight(), Color.color(0, 0, 0, 0.85));

      VBox layout = new VBox(50, headerZone, optionsZone, backButton);
      layout.setAlignment(Pos.CENTER);

      StackPane container = new StackPane(layout);
      container.setPrefSize(getAppWidth(), getAppHeight());
      container.setAlignment(Pos.CENTER);

      getContentRoot().getChildren().addAll(background, container);
    } else {
      MenuHelper.setupSelectionMenu(
          this, headerZone, optionsZone, backButton, 50, getAppWidth(), getAppHeight());
    }
  }

  private VBox createHeaderZone() {
    var title =
        getUIFactoryService()
            .newText(
                languageManager.getTranslation("display").toUpperCase(),
                Color.LIME,
                FontType.MONO,
                GameConstants.MAIN_TITLES);

    VBox headerZone = new VBox(25, title);
    headerZone.setAlignment(Pos.CENTER);
    return headerZone;
  }

  // Group the fullscreen and resolution settings.
  private VBox createOptionsZone() {
    VBox options = new VBox(30);
    options.setAlignment(Pos.CENTER_LEFT);
    options.setMaxWidth(getAppWidth() * 0.8);
    options.setPadding(new Insets(20));
    options.setStyle(
        "-fx-background-color: rgba(0, 0, 0, 0.8);"
            + "-fx-background-radius: 15;"
            + "-fx-border-color: rgba(0, 220, 0, 0.7);"
            + "-fx-border-width: 2;"
            + "-fx-border-radius: 15;"
            + "-fx-effect: dropshadow(gaussian, rgba(0, 220, 0, 0.6), 12, 0.5, 0, 0);");
    applyStylesheet(options);

    VBox fullscreenSection = createFullscreenSection();
    VBox resolutionSection = createResolutionSection();

    options.getChildren().addAll(fullscreenSection, resolutionSection);

    refreshControls();
    return options;
  }

  private VBox createFullscreenSection() {
    Label label =
        createSettingLabel(languageManager.getTranslation("fullscreen").toUpperCase() + ":");

    RadioButton noRadio = new RadioButton(languageManager.getTranslation("no").toUpperCase());
    RadioButton yesRadio = new RadioButton(languageManager.getTranslation("yes").toUpperCase());
    fullscreenGroup = new ToggleGroup();
    noRadio.setToggleGroup(fullscreenGroup);
    yesRadio.setToggleGroup(fullscreenGroup);

    String radioStyle =
        "-fx-font-family: 'Public Pixel';"
            + "-fx-font-size: 15px;"
            + "-fx-text-fill: white;"
            + "-fx-mark-color: #00FF00;";
    noRadio.setStyle(radioStyle);
    yesRadio.setStyle(radioStyle);

    noRadio.setOnAction(e -> changeFullscreen(false));
    yesRadio.setOnAction(e -> changeFullscreen(true));

    HBox choices = new HBox(35, noRadio, yesRadio);
    choices.setAlignment(Pos.CENTER_LEFT);
    return new VBox(12, label, choices);
  }

  private void changeFullscreen(boolean fullscreen) {
    stage.setFullScreen(fullscreen);
    Platform.runLater(this::refreshControls);
  }

  private VBox createResolutionSection() {
    Label label =
        createSettingLabel(languageManager.getTranslation("resolution").toUpperCase() + ":");
    ComboBox<Double> resolutionComboBox = new ComboBox<>();
    for (double scale : SCALES) {
      resolutionComboBox.getItems().add(scale);
    }
    resolutionComboBox.setPrefWidth(getAppWidth() * 0.68);
    resolutionComboBox.setMaxWidth(Double.MAX_VALUE);

    resolutionComboBox.setCellFactory(combo -> createResolutionOptionCell());
    resolutionComboBox.setButtonCell(createSelectedResolutionCell());
    applyStylesheet(resolutionComboBox);

    loadCurrentScale(resolutionComboBox);
    resolutionComboBox.setPromptText(
        languageManager.getTranslation("auto_screen_fit").toUpperCase());
    resolutionComboBox.setValue(null);

    // JavaFX automatically disables this control while fullscreen is active.
    resolutionComboBox.disableProperty().bind(stage.fullScreenProperty());
    resolutionComboBox.setOnAction(e -> selectResolution(resolutionComboBox));

    return new VBox(12, label, resolutionComboBox);
  }

  private ListCell<Double> createResolutionOptionCell() {
    return new ListCell<>() {
      @Override
      protected void updateItem(Double scale, boolean empty) {
        super.updateItem(scale, empty);
        if (empty || scale == null) {
          setText(null);
          setDisable(false);
          setStyle("");
          return;
        }

        setText(formatScale(scale));
        boolean available = canFit(scale);
        setDisable(!available);
        setStyle(available ? "" : "-fx-text-fill: #555F57; -fx-opacity: 0.45;");
      }
    };
  }

  private ListCell<Double> createSelectedResolutionCell() {
    return new ListCell<>() {
      @Override
      protected void updateItem(Double scale, boolean empty) {
        super.updateItem(scale, empty);
        setText(empty || scale == null ? null : formatScale(scale));
      }
    };
  }

  private void loadCurrentScale(ComboBox<Double> resolutionComboBox) {
    // Match the current window size, including FXGL's automatic downscaling.
    if (!stage.isFullScreen()) {
      rememberWindowedScale(null);
      double currentScale = stage.getScene().getWidth() / DinosaurGUI.WIDTH;
      for (double scale : SCALES) {
        if (Math.abs(currentScale - scale) < 0.02 && canFit(scale)) {
          rememberWindowedScale(scale);
          break;
        }
      }
    }

    if (lastWindowedScale == null) {
      resolutionComboBox.setPromptText(
          languageManager.getTranslation("auto_screen_fit").toUpperCase());
    } else {
      resolutionComboBox.setValue(lastWindowedScale);
    }
  }

  private void selectResolution(ComboBox<Double> resolutionComboBox) {
    Double scale = resolutionComboBox.getValue();
    if (scale == null) {
      return;
    }

    if (!canFit(scale)) {
      resolutionComboBox.setValue(lastWindowedScale);
      return;
    }

    changeWindowSize(scale);
    rememberWindowedScale(scale);
  }

  private static void rememberWindowedScale(Double scale) {
    lastWindowedScale = scale;
  }

  private Label createSettingLabel(String text) {
    Label label = new Label(text);
    label.setStyle(
        "-fx-text-fill: #00FF00;" + "-fx-effect: dropshadow(gaussian, black, 2, 1.0, 0, 0);");
    applyStylesheet(label);
    return label;
  }

  private void applyStylesheet(Parent parent) {
    parent
        .getStylesheets()
        .add(
            Objects.requireNonNull(getClass().getResource(GameConstants.STYLESHEET_PATH))
                .toExternalForm());
  }

  private Button createBackButton() {
    Button backButton =
        MenuHelper.createStyledButton(languageManager.getTranslation("back").toUpperCase());
    backButton.setOnAction(e -> fireResume());
    return backButton;
  }

  private String formatScale(double scale) {
    String factor = scale == Math.rint(scale) ? String.valueOf((int) scale) : String.valueOf(scale);
    long width = Math.round(DinosaurGUI.WIDTH * scale);
    long height = Math.round(DinosaurGUI.HEIGHT * scale);
    return "x" + factor + " (" + width + " x " + height + ")";
  }

  private Screen currentScreen() {
    List<Screen> screens =
        Screen.getScreensForRectangle(
            stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
    return screens.isEmpty() ? Screen.getPrimary() : screens.get(0);
  }

  // Prevent negative values if Stage and Scene sizes are temporarily out of sync.
  private double frameWidth() {
    return Math.max(0, stage.getWidth() - stage.getScene().getWidth());
  }

  private double frameHeight() {
    return Math.max(0, stage.getHeight() - stage.getScene().getHeight());
  }

  // Include window decorations when checking against the usable screen area.
  private boolean canFit(double scale) {
    if (stage.isFullScreen()) {
      return false;
    }

    Rectangle2D bounds = currentScreen().getVisualBounds();
    double width = Math.round(DinosaurGUI.WIDTH * scale);
    double height = Math.round(DinosaurGUI.HEIGHT * scale);
    return width + frameWidth() <= bounds.getWidth()
        && height + frameHeight() <= bounds.getHeight();
  }

  private void changeWindowSize(double scale) {
    if (!canFit(scale)) {
      return;
    }

    double width = Math.round(DinosaurGUI.WIDTH * scale);
    double height = Math.round(DinosaurGUI.HEIGHT * scale);
    // Convert the requested game display size to the outer window size.
    double outerWidth = width + frameWidth();
    double outerHeight = height + frameHeight();
    Rectangle2D bounds = currentScreen().getVisualBounds();

    stage.setWidth(outerWidth);
    stage.setHeight(outerHeight);
    stage.setX(bounds.getMinX() + (bounds.getWidth() - outerWidth) / 2);
    stage.setY(bounds.getMinY() + (bounds.getHeight() - outerHeight) / 2);
  }

  private void refreshControls() {
    fullscreenGroup.selectToggle(fullscreenGroup.getToggles().get(stage.isFullScreen() ? 1 : 0));
  }
}
