package com.dinosaur.dinosaurexploder.view;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.model.GameData;
import com.dinosaur.dinosaurexploder.specialties.Specialty;
import com.dinosaur.dinosaurexploder.specialties.SpecialtyManager;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.MenuHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;

/// The class responsible for creating and managing the SpecialtyMenu
public class SpecialtyMenu extends FXGLMenu {
  /// A convenient record class that contains all the data needed to display a specialty
  public record SpecialtyViewData(String nameKey, String descriptionKey, String iconPath, boolean isLocked) {}
  // ========= CONSTANTS ================
  private static final int GRID_GAP = 20;
  private static final int ZONE_SPACING = 50;
  private static final int SPECIALTY_COLUMNS = 4;

  // ======== FIELDS =============
  private final LanguageManager languageManager = LanguageManager.getInstance();
  private GridPane specialtyGrid;
  private Label specialtyName;
  private Label specialtyDescription;
  private List<SpecialtyViewData> specialtyViewData;

  // ========= CONSTRUCTOR ==============
  public SpecialtyMenu() {
    super(MenuType.MAIN_MENU);
    specialtyViewData = createSpecialtyViewData(SpecialtyManager.getAllSpecialties());
    buildMenu();
  }

  /**
   * Creates a list of SpecialtyViewData from a list of Specialty
   *
   * @param specialties a list of all the specialties in the game
   *
   * @return a list of SpecialtyViewData
   * @see SpecialtyManager and Specialty
   */
  private List<SpecialtyViewData> createSpecialtyViewData(List<Specialty> specialties) {
    List<SpecialtyViewData> viewData = new ArrayList<>();
    for (Specialty specialty : specialties) {
      String nameKey = String.format("specialty_%s", specialty.name().toLowerCase());
      String descriptionKey = String.format("%s_description", nameKey);
      String iconPath;
      // TODO: Add rest of icon assets
      switch(specialty.name().toLowerCase()) {
        case "tank":
          iconPath = "more_hearts.png";
          break;
        default:
          iconPath = "more_hearts.png";
          break;
      }

      // TODO: Add highscore cost
      boolean isLocked = GameData.getTotalCoins() > specialty.costInCoins();
      viewData.add(new SpecialtyViewData(nameKey,descriptionKey, iconPath, isLocked));
    }
    return viewData;
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
    specialtyName.setStyle("-fx-font-weight: bold; -fx-text-fill: #ffcc00; -fx-font-size: 24px;"); 
    specialtyDescription = new Label("[Specialty Short Description is Here]"); 
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

    double zone_width = getAppWidth() * 0.8;

    for (int i = 0; i < specialtyViewData.size(); i++) {
      StackPane specialtyContainer = createSpecialtyButton(specialtyViewData.get(i), (zone_width/ SPECIALTY_COLUMNS) * 0.9);
      int row = i / SPECIALTY_COLUMNS;
      int col = i % SPECIALTY_COLUMNS;

      specialtyGrid.add(specialtyContainer, col, row);
    }

    return specialtyGrid;
  }

  private StackPane createSpecialtyButton(SpecialtyViewData specialty, double size) {
    ImageView iconView = new ImageView(createImage(specialty.iconPath()));
    iconView.setFitWidth(size);
    iconView.setPreserveRatio(true);

    // Create the button
    Button specialtyButton = new Button();
    specialtyButton.setGraphic(iconView);
    specialtyButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

    // Hover Effect
    DropShadow hoverEffect = new DropShadow(10, Color.rgb(0, 255, 0));
    specialtyButton.setOnMouseEntered(event -> specialtyButton.setEffect(hoverEffect));
    specialtyButton.setOnMouseExited(event -> specialtyButton.setEffect(null));

    specialtyButton.setOnAction(event -> {
        specialtyName.setText(languageManager.getTranslation(specialty.nameKey()));
        specialtyDescription.setText(languageManager.getTranslation(specialty.descriptionKey()));
    });

    // Lock icon
    ImageView lockIcon = MenuHelper.createLockIcon(specialty.isLocked());
    lockIcon.setMouseTransparent(true); // Clicks pass through this icon

    StackPane container = new StackPane(specialtyButton, lockIcon);
    StackPane.setAlignment(lockIcon, Pos.TOP_RIGHT);
    StackPane.setMargin(lockIcon, new Insets(5));

    return container;
  }

  /**
   * Create an Image object from an given iconPath
   *
   * @param iconPath the name of the image plus extension (assumes that the asset is in /assets/textures/)
   * @return the created Image object
   */
  private Image createImage(String iconPath) {
    String asset_path = String.format("/assets/textures/" + iconPath);
    return new Image(Objects.requireNonNull(getClass().getResourceAsStream(asset_path)));
  }

  /// Create a zone with a back and next button
  private HBox createNavigationZone() {
    Button backButton = createButton("back", () -> fireResume());
    Button nextButton = createButton("next", () -> fireNewGame());

    HBox navigationZone = new HBox(30);
    navigationZone.setPadding(new Insets(0, 40, 20, 40));
    navigationZone.setAlignment(Pos.CENTER);
    navigationZone.getChildren().setAll(backButton, nextButton);
    return navigationZone;
    
  }

  /**
   * Helper method that creates a basic green button.
   *
   * @param translationKey the key used for the button text
   * @param callback a lambda function that runs when the button is clicked
   *
   * @return the created Button
  */
  private Button createButton(String translationKey, Runnable callback) {
    Button button = new Button(languageManager.getTranslation(translationKey).toUpperCase());

    button.getStylesheets().add(Objects.requireNonNull(getClass().getResource(GameConstants.STYLESHEET_PATH)).toExternalForm());
    button.setMinSize(140, 60);

    button.setOnAction(event -> callback.run());

    return button;
  }
}
