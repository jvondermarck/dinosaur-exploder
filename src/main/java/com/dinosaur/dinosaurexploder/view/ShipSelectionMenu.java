package com.dinosaur.dinosaurexploder.view;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getDialogService;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.ui.FontType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.exception.LockedShipException;
import com.dinosaur.dinosaurexploder.model.GameData;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import java.io.InputStream;
import java.util.Objects;

import com.dinosaur.dinosaurexploder.utils.MenuHelper;
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

public class ShipSelectionMenu extends FXGLMenu {

  // ============ CONSTANTS ============
  private static final int SHIP_COLUMNS = 4;
  private static final int TOTAL_SHIPS = 8;
  private static final double SHIP_SIZE_RATIO = 0.15;
  private static final int GRID_GAP = 20;
  private static final int ZONE_SPACING = 50;
  private static final String STYLESHEET_PATH = "/styles/styles.css";

  // ============ FIELDS ============
  private final LanguageManager languageManager = LanguageManager.getInstance();
  private GridPane shipGrid;

  // ============ CONSTRUCTOR ============
  public ShipSelectionMenu() {
    super(MenuType.MAIN_MENU);
    buildMenu();
  }

  // ============ MENU BUILDING ============

  private void buildMenu() {
    ImageView background = createAnimatedBackground();
    VBox headerZone = createHeaderZone();
    GridPane shipGridZone = createShipGridZone();
    Button backButton = createBackButton();

    VBox layout = new VBox(ZONE_SPACING, headerZone, shipGridZone, backButton);
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
                languageManager.getTranslation("select_ship").toUpperCase(),
                Color.LIME,
                FontType.MONO,
                GameConstants.MAIN_TITLES);

    // ✅ Wrap le Text
    TextFlow titleFlow = new TextFlow(title);
    titleFlow.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
    titleFlow.setMaxWidth(getAppWidth() * 0.8);
    titleFlow.setLineSpacing(5);

    var highScore =
        getUIFactoryService()
            .newText(
                (languageManager.getTranslation("high_score") + ": " + GameData.getHighScore())
                    .toUpperCase(),
                Color.LIME,
                FontType.MONO,
                GameConstants.TEXT_SUB_DETAILS);

    var totalCoins =
        getUIFactoryService()
            .newText(
                (languageManager.getTranslation("total_coins") + ": " + GameData.getTotalCoins())
                    .toUpperCase(),
                Color.LIME,
                FontType.MONO,
                GameConstants.TEXT_SUB_DETAILS);

    // ✅ Change l'espacement de 10 à 25 (ou plus si besoin)
    VBox headerZone = new VBox(25, titleFlow, highScore, totalCoins);
    headerZone.setAlignment(Pos.CENTER);

    return headerZone;
  }

  // ============ ZONE 2: SHIP GRID ============

  private GridPane createShipGridZone() {
    shipGrid = new GridPane();
    shipGrid.setAlignment(Pos.CENTER);
    shipGrid.setHgap(GRID_GAP);
    shipGrid.setVgap(GRID_GAP);
    shipGrid.setPrefWidth(getAppWidth());

    double shipSize = getAppWidth() * SHIP_SIZE_RATIO;
    populateShipGrid(shipSize);

    return shipGrid;
  }

  private void populateShipGrid(double shipSize) {
    for (int i = 1; i <= TOTAL_SHIPS; i++) {
      StackPane shipContainer = createShipButton(i, shipSize);
      int row = (i - 1) / SHIP_COLUMNS;
      int col = (i - 1) % SHIP_COLUMNS;
      shipGrid.add(shipContainer, col, row);
    }
  }

  private StackPane createShipButton(int shipNumber, double shipSize) {
    Image shipImage = loadShipImage(shipNumber);
    boolean isLocked = !GameData.checkUnlockedShip(shipNumber);

    ImageView shipView = createShipImageView(shipImage, shipSize, isLocked);
    ImageView lockIcon = createLockIcon(isLocked);
    Button shipButton = createClickableShipButton(shipView, shipNumber);

    StackPane container = new StackPane(shipButton, lockIcon);
    StackPane.setAlignment(lockIcon, Pos.TOP_RIGHT);

    return container;
  }

  private Image loadShipImage(int shipNumber) {
    return new Image(
        Objects.requireNonNull(
            getClass().getResourceAsStream("/assets/textures/spaceship" + shipNumber + ".png")));
  }

  private ImageView createShipImageView(Image image, double size, boolean isLocked) {
    return MenuHelper.createItemImageView(image, size, isLocked);
  }

  private ImageView createLockIcon(boolean isLocked) {
    return MenuHelper.createLockIcon(isLocked);
  }

  private Button createClickableShipButton(ImageView shipView, int shipNumber) {
    Button button = new Button();
    button.setGraphic(shipView);
    button.setStyle(
        "-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 0;");
    button.setMaxWidth(Double.MAX_VALUE);

    button.setOnAction(event -> handleShipSelection(shipNumber));
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
        .add(Objects.requireNonNull(getClass().getResource(STYLESHEET_PATH)).toExternalForm());
    backButton.setMinSize(140, 60);
    backButton.setOnAction(event -> fireResume());

    return backButton;
  }

  // ============ EVENT HANDLERS ============

  private void handleShipSelection(int shipNumber) {
    try {
      selectShip(shipNumber);
    } catch (LockedShipException exception) {
      showLockedShipDialog(exception);
    }
  }

  private void showLockedShipDialog(LockedShipException exception) {
    MenuHelper.showLockedDialog(exception.getMessage());
  }

  private void selectShip(int shipNumber) {
    GameData.setSelectedShip(shipNumber);
    System.out.println("Selected Spaceship: " + shipNumber);
    FXGL.getSceneService().pushSubScene(new WeaponSelectionMenu());
  }
}
