package com.dinosaur.dinosaurexploder.view;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getDialogService;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.ui.FontType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.exception.LockedWeaponException;
import com.dinosaur.dinosaurexploder.model.GameData;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import java.io.InputStream;
import java.util.Objects;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

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
    ImageView background = createAnimatedBackground();
    VBox headerZone = createHeaderZone();
    GridPane weaponGridZone = createWeaponGridZone();
    Button backButton = createBackButton();

    VBox layout = new VBox(ZONE_SPACING, headerZone, weaponGridZone, backButton);
    layout.setAlignment(Pos.CENTER);

    StackPane container = new StackPane(layout);
    container.setPrefSize(getAppWidth(), getAppHeight());
    container.setAlignment(Pos.CENTER);

    getContentRoot().getChildren().addAll(background, container);
  }

  // ============ BACKGROUND ============

  private ImageView createAnimatedBackground() {
    InputStream backgroundStream =
        getClass().getClassLoader().getResourceAsStream(GameConstants.BACKGROUND_IMAGE_PATH);
    Image backgroundImage = new Image(backgroundStream);

    ImageView backgroundView = new ImageView(backgroundImage);
    backgroundView.setFitHeight(DinosaurGUI.HEIGHT);
    backgroundView.setX(0);
    backgroundView.setY(0);
    backgroundView.setPreserveRatio(true);

    TranslateTransition transition = createBackgroundAnimation(backgroundView, backgroundImage);
    transition.play();

    return backgroundView;
  }

  private TranslateTransition createBackgroundAnimation(ImageView imageView, Image background) {
    TranslateTransition transition = new TranslateTransition();
    transition.setNode(imageView);
    transition.setDuration(Duration.seconds(50));
    transition.setFromX(0);
    transition.setToX(-background.getWidth() + DinosaurGUI.WIDTH * 3.8);
    transition.setCycleCount(TranslateTransition.INDEFINITE);
    transition.setInterpolator(javafx.animation.Interpolator.LINEAR);
    transition.setAutoReverse(true);
    return transition;
  }

  // ============ ZONE 1: HEADER ============

  private VBox createHeaderZone() {
    var title =
        getUIFactoryService()
            .newText(
                languageManager.getTranslation("select_weapon").toUpperCase(),
                Color.LIME,
                FontType.MONO,
                GameConstants.MAIN_TITLES);

    // ✅ Wrap dans TextFlow pour le retour à la ligne automatique
    TextFlow titleFlow = new TextFlow(title);
    titleFlow.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
    titleFlow.setMaxWidth(getAppWidth() * 0.8);
    titleFlow.setLineSpacing(5);

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
    ImageView imageView = new ImageView(image);
    imageView.setRotate(-90);
    imageView.setFitHeight(size);
    imageView.setFitWidth(size);

    if (isLocked) {
      ColorAdjust darkFilter = new ColorAdjust();
      darkFilter.setBrightness(-0.5);
      imageView.setEffect(darkFilter);
    }

    return imageView;
  }

  private ImageView createLockIcon(boolean isLocked) {
    Image lockImage =
        new Image(
            Objects.requireNonNull(getClass().getResourceAsStream("/assets/textures/lock.png")));

    ImageView lockIcon = new ImageView(lockImage);
    lockIcon.setFitWidth(30);
    lockIcon.setFitHeight(30);
    lockIcon.setMouseTransparent(true);
    lockIcon.setOpacity(0.6);
    lockIcon.setVisible(isLocked);

    return lockIcon;
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
    Button okButton = getUIFactoryService().newButton(languageManager.getTranslation("ok"));
    okButton.setMinWidth(250);
    okButton.setPrefWidth(300);

    String fullErrorMessage = exception.getMessage();

    var textNode =
        getUIFactoryService().newText(fullErrorMessage, Color.LIME, GameConstants.TEXT_SUB_DETAILS);

    textNode.setWrappingWidth(450);
    textNode.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
    textNode.setLineSpacing(10);

    VBox content = new VBox(textNode);
    content.setAlignment(Pos.CENTER);
    content.setSpacing(25);
    content.setPadding(new javafx.geometry.Insets(20));
    content.setMinWidth(500);

    getDialogService().showBox(languageManager.getTranslation("locked"), content, okButton);
  }

  private void selectWeapon(int weaponNumber) {
    GameData.setSelectedWeapon(weaponNumber);
    System.out.println("Selected Weapon: " + weaponNumber);
    fireNewGame();
  }
}
