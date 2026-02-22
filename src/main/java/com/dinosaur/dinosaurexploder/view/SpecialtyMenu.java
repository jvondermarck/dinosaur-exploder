package com.dinosaur.dinosaurexploder.view;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.model.GameData;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.MenuHelper;
import java.util.Objects;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

public class SpecialtyMenu extends FXGLMenu {
  // ========= CONSTANTS ================
  private static final int GRID_GAP = 20;
  private static final int ZONE_SPACING = 50;
  private static final int SPECIALTY_COLUMNS = 4;
  private static final int TOTAL_SPECIALTIES = 8;

  // ======== FIELDS =============
  private final LanguageManager languageManager = LanguageManager.getInstance();
  private GridPane specialtyGrid;
  private Label specialtyName;
  private Label specialtyDescription;

  // ========= CONSTRUCTOR ==============
  public SpecialtyMenu() {
    super(MenuType.MAIN_MENU);
    buildMenu();
  }

  private void buildMenu() {
    MenuHelper.setupSelectionMenu(this, createHeaderZone(), createSpecialtyGridZone(), createNavigationZone(), ZONE_SPACING, getAppWidth(), getAppHeight());
  }

  // ========== ZONE 1: HEADER =============
  private VBox createHeaderZone() {
    // TODO: Add in other translations other than english
    TextFlow titleFlow = MenuHelper.createTitleFlow(languageManager.getTranslation("select_specialty"), getAppWidth() * 0.8);

    // Create the specialty description zone
    VBox specialtyInfo = new VBox(5);
    specialtyInfo.setAlignment(Pos.CENTER);
    specialtyInfo.setPadding(new Insets(20, 0, 0, 0));

    specialtyName = new Label("[Specialty Name]"); // TODO: retrieve from game data
    specialtyName.setStyle("-fx-font-weight: bold; -fx-text-fill: #ffcc00; -fx-font-size: 24px;"); // TODO: Maybe change styling
    specialtyDescription = new Label("[Specialty Short Description is Here]"); // TODO: Retrieve from game data
    specialtyDescription.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
    specialtyDescription.setWrapText(true);
    specialtyDescription.setAlignment(Pos.CENTER);
    specialtyDescription.setMaxWidth(getAppWidth() * 0.7);

    specialtyInfo.getChildren().addAll(specialtyName, specialtyDescription);

    VBox headerZone = new VBox(titleFlow, specialtyInfo);
    headerZone.setAlignment(Pos.CENTER);
    return headerZone;
  }

  // ============= ZONE 2: SPECIALTY GRID =============
  private GridPane createSpecialtyGridZone() {
    specialtyGrid = new GridPane();
    specialtyGrid.setAlignment(Pos.CENTER);
    specialtyGrid.setHgap(GRID_GAP);
    specialtyGrid.setVgap(GRID_GAP);
    specialtyGrid.setPrefWidth(getAppWidth());

    // TODO: I want text box and description
    double specialtySize = getAppWidth() * 0.8;
    populateSpecialtyGrid(specialtySize);

    return specialtyGrid;
  }

  private void populateSpecialtyGrid(double size) {
    // int selectedSpecialty = GameData.getSelectedSpecialty();
    int selectedSpecialty = 1; // TODO: Integrate with gamadata

    for (int i = 1; i <= TOTAL_SPECIALTIES; i++) {
      StackPane specialtyContainer = createSpecialtyButton(i, (size / SPECIALTY_COLUMNS) * 0.9);
      int row = (i - 1) / SPECIALTY_COLUMNS;
      int col = (i - 1) % SPECIALTY_COLUMNS;

      specialtyGrid.add(specialtyContainer, col, row);
    }
  }

  private StackPane createSpecialtyButton(int number, double size) {
    Image specialtyImage = loadSpecialtyImage();
    boolean isLocked = false; // TODO: get this from gama data

    ImageView iconView = new ImageView(loadSpecialtyImage());
    iconView.setFitWidth(size);
    iconView.setPreserveRatio(true);

    // Create the button
    Button specialtyButton = new Button();
    specialtyButton.setGraphic(iconView);
    // specialtyButton.setPrefWidth(size / 2); // TODO: Is this alright?? Should probably not be done here

    specialtyButton.setOnAction(event -> {
      if (!isLocked) {
        specialtyName.setText("Specialty #" + number);
        specialtyDescription.setText("Description of specialty #" + number);
      }
    });

    // Lock icon
    ImageView lockIcon = MenuHelper.createLockIcon(isLocked);
    lockIcon.setMouseTransparent(true); // Clicks pass through this icon

    StackPane container = new StackPane(specialtyButton, lockIcon);
    StackPane.setAlignment(lockIcon, Pos.TOP_RIGHT);
    StackPane.setMargin(lockIcon, new Insets(5));

    return container;
  }

  private Image loadSpecialtyImage() {
    // TODO: Placeholder image hearts
    String path = String.format("/assets/textures/life.png");
    return new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
  }

  private HBox createNavigationZone() {
    Button backButton = createBackButton();

    Button nextButton = new Button(languageManager.getTranslation("next").toUpperCase());
    nextButton.getStylesheets().add(Objects.requireNonNull(getClass().getResource(GameConstants.STYLESHEET_PATH)).toExternalForm());
    nextButton.setMinSize(140, 60);
    nextButton.setOnAction(event -> fireNewGame()); // TODO: Add check that a specialty is selected or one is selected by default
    // TODO: Should a button be toggable

    HBox navigationZone = new HBox(30);
    navigationZone.setPadding(new Insets(0, 40, 20, 40));
    navigationZone.setAlignment(Pos.CENTER);
    navigationZone.getChildren().setAll(backButton, nextButton);
    return navigationZone;
    
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
}
