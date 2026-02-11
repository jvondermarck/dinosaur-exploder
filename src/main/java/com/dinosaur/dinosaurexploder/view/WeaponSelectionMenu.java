/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */
package com.dinosaur.dinosaurexploder.view;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.exception.LockedWeaponException;
import com.dinosaur.dinosaurexploder.model.GameData;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.MenuHelper;
import java.util.Objects;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;

public class WeaponSelectionMenu extends FXGLMenu {

  // ============ CONSTANTS ============
  private static final int WEAPON_COLUMNS = 4;
  private static final int TOTAL_WEAPONS = 3;
  private static final double WEAPON_SIZE_RATIO = 0.15;
  private static final int GRID_GAP = 20;
  private static final int ZONE_SPACING = 50;

  // ============ FIELDS ============
  private final LanguageManager languageManager = LanguageManager.getInstance();
  private GridPane weaponGrid;

  // ============ CONSTRUCTOR ============
  public WeaponSelectionMenu() {
    super(MenuType.MAIN_MENU);
    buildMenu();
  }

  // ============ MENU BUILDING ============
  private void buildMenu() {
    MenuHelper.setupSelectionMenu(
        this,
        createHeaderZone(),
        createWeaponGridZone(),
        createBackButton(),
        ZONE_SPACING,
        getAppWidth(),
        getAppHeight());
  }

  // ============ ZONE 1: HEADER ============
  private VBox createHeaderZone() {
    // ✅ Utilisation du Helper
    TextFlow titleFlow =
        MenuHelper.createTitleFlow(
            languageManager.getTranslation("select_weapon"), getAppWidth() * 0.8);

    VBox headerZone = new VBox(titleFlow);
    headerZone.setAlignment(Pos.CENTER);
    return headerZone;
  }

  // ============ ZONE 2: WEAPON GRID ============

  private GridPane createWeaponGridZone() {
    weaponGrid = new GridPane();
    weaponGrid.setAlignment(Pos.CENTER);
    weaponGrid.setHgap(GRID_GAP);
    weaponGrid.setVgap(GRID_GAP);
    weaponGrid.setPrefWidth(getAppWidth());

    double weaponSize = getAppWidth() * WEAPON_SIZE_RATIO;
    populateWeaponGrid(weaponSize);

    return weaponGrid;
  }

  @SuppressWarnings("ConstantConditions")
  private void populateWeaponGrid(double weaponSize) {
    int selectedShip = GameData.getSelectedShip();

    for (int i = 1; i <= TOTAL_WEAPONS; i++) {
      StackPane weaponContainer = createWeaponButton(i, selectedShip, weaponSize);
      int row = (i - 1) / WEAPON_COLUMNS;
      int col = (i - 1) % WEAPON_COLUMNS;
      weaponGrid.add(weaponContainer, col, row);
    }
  }

  private StackPane createWeaponButton(int weaponNumber, int shipNumber, double weaponSize) {
    Image weaponImage = loadWeaponImage(shipNumber, weaponNumber);
    boolean isLocked = !GameData.checkUnlockedWeapon(weaponNumber);

    ImageView weaponView = createWeaponImageView(weaponImage, weaponSize, isLocked);
    ImageView lockIcon = createLockIcon(isLocked);
    Button weaponButton = createClickableWeaponButton(weaponView, weaponNumber);

    StackPane container = new StackPane(weaponButton, lockIcon);
    StackPane.setAlignment(lockIcon, Pos.TOP_RIGHT);

    return container;
  }

  private Image loadWeaponImage(int shipNumber, int weaponNumber) {
    String path =
        String.format("/assets/textures/projectiles/projectile%d_%d.png", shipNumber, weaponNumber);
    return new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
  }

  private ImageView createWeaponImageView(Image image, double size, boolean isLocked) {
    ImageView view = MenuHelper.createItemImageView(image, size, isLocked);
    view.setRotate(-90);
    return view;
  }

  private ImageView createLockIcon(boolean isLocked) {
    return MenuHelper.createLockIcon(isLocked);
  }

  private Button createClickableWeaponButton(ImageView weaponView, int weaponNumber) {
    Button button = new Button();
    button.setGraphic(weaponView);
    button.setStyle(
        "-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 0;");
    button.setMaxWidth(Double.MAX_VALUE);

    button.setOnAction(event -> handleWeaponSelection(weaponNumber));
    setupHoverEffect(button);

    return button;
  }

  private void setupHoverEffect(Button button) {
    DropShadow hoverEffect = new DropShadow(10, Color.rgb(0, 255, 0));

    button.setOnMouseEntered(event -> button.setEffect(hoverEffect));
    button.setOnMouseExited(event -> button.setEffect(null));
  }

  // ============ ZONE 3: BACK BUTTON ============

  private Button createBackButton() {
    Button backButton = new Button(languageManager.getTranslation("back").toUpperCase());
    backButton
        .getStylesheets()
        .add(
            Objects.requireNonNull(getClass().getResource(GameConstants.STYLESHEET_PATH))
                .toExternalForm());
    backButton.setMinSize(140, 60);
    backButton.setOnAction(event -> fireResume());

    return backButton;
  }

  // ============ EVENT HANDLERS ============

  private void handleWeaponSelection(int weaponNumber) {
    try {
      selectWeapon(weaponNumber);
    } catch (LockedWeaponException exception) {
      showLockedWeaponDialog(exception);
    }
  }

  private void showLockedWeaponDialog(LockedWeaponException exception) {
    MenuHelper.showDialog(languageManager.getTranslation("locked"), exception.getMessage());
  }

  private void selectWeapon(int weaponNumber) {
    GameData.setSelectedWeapon(weaponNumber);
    System.out.println("Selected Weapon: " + weaponNumber);
    fireNewGame();
  }
}
