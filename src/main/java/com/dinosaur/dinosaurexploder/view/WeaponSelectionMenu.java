package com.dinosaur.dinosaurexploder.view;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.ui.FontType;
import com.dinosaur.dinosaurexploder.exception.LockedWeaponException;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.model.Settings;
import com.dinosaur.dinosaurexploder.model.GameData;
import com.dinosaur.dinosaurexploder.utils.SettingsProvider;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import com.dinosaur.dinosaurexploder.utils.AudioManager;
import java.io.InputStream;
import java.util.Objects;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getDialogService;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

public class WeaponSelectionMenu extends FXGLMenu {
    
    LanguageManager languageManager = LanguageManager.getInstance();
    private final Settings settings = SettingsProvider.loadSettings();

    public WeaponSelectionMenu() {
        super(MenuType.MAIN_MENU);

        

        // background image
        InputStream backGround = getClass().getClassLoader().getResourceAsStream("assets/textures/background.png");
        Image Background = new Image(backGround);
        ImageView imageViewB = new ImageView(Background);
        imageViewB.setFitHeight(DinosaurGUI.HEIGHT);
        imageViewB.setX(0);
        imageViewB.setY(0);
        imageViewB.setPreserveRatio(true);

        // Background animation
        TranslateTransition translateTransition = new TranslateTransition();
        translateTransition.setNode(imageViewB);
        translateTransition.setDuration(Duration.seconds(50)); // Duración del ciclo
        translateTransition.setFromX(0);
        translateTransition.setToX(-Background.getWidth() + DinosaurGUI.WIDTH * 3.8);
        translateTransition.setCycleCount(TranslateTransition.INDEFINITE);
        translateTransition.setInterpolator(Interpolator.LINEAR);
        translateTransition.setAutoReverse(true);
        translateTransition.play();

        // Title
        var title = FXGL.getUIFactoryService().newText(languageManager.getTranslation("select_weapon"), Color.LIME,
                FontType.MONO, 35);

        // GridPane for weapons
        GridPane weaponGrid = new GridPane();
        weaponGrid.setAlignment(Pos.CENTER);
        weaponGrid.setHgap(20);
        weaponGrid.setVgap(20);
        weaponGrid.setPrefWidth(getAppWidth());

        // Columns and rows for the GridPane
        int columns = 4;
        double imageSize = getAppWidth() * 0.15; // 15% of the screen width

        showSelectionButton(imageSize, columns, weaponGrid);

        // Back button
        var backButton = new Button(languageManager.getTranslation("back"));
        backButton.getStylesheets()
                .add(Objects.requireNonNull(getClass().getResource("/styles/styles.css")).toExternalForm());
        backButton.setMinSize(140, 60);
        backButton.setStyle("-fx-font-size: 20px;");
        backButton.setOnAction(event -> {
            fireResume();
        });

        // Invisible spacer to push the title and weapons to the top
        Rectangle spacer = new Rectangle();
        spacer.setHeight(50);
        spacer.setWidth(getAppWidth());
        spacer.setOpacity(0);

        // Vbox layout
        VBox layout = new VBox(20, spacer, title, weaponGrid, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(50); // 50px spacing between nodes

        VBox.setVgrow(weaponGrid, Priority.ALWAYS);
        VBox.setVgrow(backButton, Priority.NEVER);

        // Maxing the layout to the screen size
        layout.setMaxWidth(getAppWidth());
        layout.setMaxHeight(getAppHeight());

        getContentRoot().getChildren().add(imageViewB);
        getContentRoot().getChildren().add(layout);
    }

    private void showSelectionButton(double imageSize, int columns, GridPane weaponGrid) {
        int selectedShip = GameData.getSelectedShip();
        // button for each weapon
        for (int i = 1; i <= 3; i++) {
            Image weaponImage = new Image(
                    Objects.requireNonNull(getClass().getResourceAsStream(
                            "/assets/textures/projectiles/projectile" + selectedShip + "_" + i + ".png")));
            boolean isLocked = !GameData.checkUnlockedWeapon(i);

            ImageView weaponView = new ImageView(weaponImage);
            weaponView.setRotate(-90);
            weaponView.setFitHeight(imageSize);
            weaponView.setFitWidth(imageSize);
            applyDarkFilterIfLocked(isLocked, weaponView);

            ImageView lockIcon = new ImageView(new Image(getClass().getResourceAsStream("/assets/textures/lock.png")));
            setLockProperties(lockIcon, isLocked);

            Button weaponButton = new Button();
            weaponButton.setGraphic(weaponView);

            int finalI = i;
            weaponButton.setOnAction(event -> {
                try {
                    selectWeapon(finalI);
                } catch (LockedWeaponException exception) {
                    Button btn = getUIFactoryService().newButton(languageManager.getTranslation("ok"));
                    btn.setOnAction(event1 -> showSelectionButton(imageSize, columns, weaponGrid));
                    getDialogService().showBox(languageManager.getTranslation(exception.getMessage()), new VBox(), btn);
                }
            });
            weaponButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 0;");

            DropShadow hoverEffect = new DropShadow(10, Color.rgb(0, 255, 0));
            weaponButton.setOnMouseEntered(event -> {
                weaponButton.setEffect(hoverEffect); // Shadow effect
                weaponButton
                        .setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 0;");
            });

            // Delete shadow effect when mouse exits
            weaponButton.setOnMouseExited(event -> {
                weaponButton.setEffect(null); // Remove shadow effect
                weaponButton
                        .setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 0;");
            });
            weaponButton.setMaxWidth(Double.MAX_VALUE);

            int row = (i - 1) / columns;
            int col = (i - 1) % columns;
            StackPane weaponContainer = new StackPane(weaponButton, lockIcon);
            StackPane.setAlignment(lockIcon, Pos.TOP_RIGHT);
            weaponGrid.add(weaponContainer, col, row);
        }
    }

    private void applyDarkFilterIfLocked(boolean isLocked, ImageView weaponView) {
        if (isLocked) {
            ColorAdjust grayscale = new ColorAdjust();
            grayscale.setBrightness(-0.5);
            weaponView.setEffect(grayscale);
        }
    }

    private void setLockProperties(ImageView lockIcon, boolean isLocked) {
        lockIcon.setFitWidth(30);
        lockIcon.setFitHeight(30);
        lockIcon.setMouseTransparent(true);
        lockIcon.setOpacity(0.6);
        lockIcon.setVisible(isLocked);
    }

    private void selectWeapon(int weaponNumber) {
        // Save the selected weapon in GameData
        GameData.setSelectedWeapon(weaponNumber);
        // Selected weapon in console
        System.out.println("Selected Weapon: " + weaponNumber);
        fireNewGame();
    }
}
