package com.dinosaur.dinosaurexploder.view;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.ui.FontType;
import com.dinosaur.dinosaurexploder.controller.SettingsController;
import com.dinosaur.dinosaurexploder.exception.LockedShipException;
import com.dinosaur.dinosaurexploder.model.GameConstants;
import com.dinosaur.dinosaurexploder.model.LanguageManager;
import com.dinosaur.dinosaurexploder.model.Settings;
import com.dinosaur.dinosaurexploder.utils.GameData;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.Objects;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getDialogService;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

public class ShipSelectionMenu extends FXGLMenu {
    private MediaPlayer mainMenuSound;
    LanguageManager languageManager = LanguageManager.getInstance();
    private final Settings settings = SettingsController.loadSettings();

    public ShipSelectionMenu() {
        super(MenuType.MAIN_MENU);

        // Background music
        Media media = new Media(getClass().getResource(GameConstants.MAINMENU_SOUND).toExternalForm());
        mainMenuSound = new MediaPlayer(media);
        mainMenuSound.setVolume(SettingsController.loadSettings().getVolume());
        mainMenuSound.setMute(settings.isMuted());
        mainMenuSound.play();
        mainMenuSound.setCycleCount(MediaPlayer.INDEFINITE);

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
        var title = FXGL.getUIFactoryService().newText(languageManager.getTranslation("select_ship"), Color.LIME, FontType.MONO, 35);

        // GridPane for ships
        GridPane shipGrid = new GridPane();
        shipGrid.setAlignment(Pos.CENTER);
        shipGrid.setHgap(20);
        shipGrid.setVgap(20);
        shipGrid.setPrefWidth(getAppWidth());

        // Columns and rows for the GridPane
        int columns = 4;
        double imageSize = getAppWidth() * 0.15; // 15% of the screen width

        showSelectionButton(imageSize, columns, shipGrid);

        // Back button
        var backButton = new Button(languageManager.getTranslation("back"));
        backButton.getStylesheets()
                .add(Objects.requireNonNull(getClass().getResource("/styles/styles.css")).toExternalForm());
        backButton.setMinSize(140, 60);
        backButton.setStyle("-fx-font-size: 20px;");
        backButton.setOnAction(event -> {
            mainMenuSound.stop();
            fireResume();
        });

        // Invisible spacer to push the title and ships to the top
        Rectangle spacer = new Rectangle();
        spacer.setHeight(50);
        spacer.setWidth(getAppWidth());
        spacer.setOpacity(0);

        // Vbox layout
        VBox layout = new VBox(20, spacer, title, shipGrid, backButton);
        layout.setAlignment(Pos.CENTER);
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(50); // 50px spacing between nodes

        VBox.setVgrow(shipGrid, Priority.ALWAYS);
        VBox.setVgrow(backButton, Priority.NEVER);

        // Maxing the layout to the screen size
        layout.setMaxWidth(getAppWidth());
        layout.setMaxHeight(getAppHeight());

        getContentRoot().getChildren().add(imageViewB);
        getContentRoot().getChildren().add(layout);
    }

    private void showSelectionButton(double imageSize, int columns, GridPane shipGrid) {
        // button for each ship
        for (int i = 1; i <= 8; i++) {
            Image shipImage = new Image(
                    Objects.requireNonNull(getClass().getResourceAsStream("/assets/textures/spaceship" + i + ".png")));
            ImageView shipView = new ImageView(shipImage);
            shipView.setFitHeight(imageSize);
            shipView.setFitWidth(imageSize);

            Button shipButton = new Button();
            shipButton.setGraphic(shipView);
            int finalI = i;
            shipButton.setOnAction(event -> {
                try {
                    selectShip(finalI);
                } catch (LockedShipException exception) {
                    Button btn = getUIFactoryService().newButton(languageManager.getTranslation("ok"));
                    btn.setOnAction(event1 -> showSelectionButton(imageSize, columns, shipGrid));
                    getDialogService().showBox(languageManager.getTranslation(exception.getMessage()), new VBox(), btn);
                }
            });
            shipButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 0;");

            DropShadow hoverEffect = new DropShadow(10, Color.rgb(0, 255, 0));
            shipButton.setOnMouseEntered(event -> {
                shipButton.setEffect(hoverEffect); // Shadow effect
                shipButton
                        .setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 0;");
            });

            // Delete shadow effect when mouse exits
            shipButton.setOnMouseExited(event -> {
                shipButton.setEffect(null); // Remove shadow effect
                shipButton
                        .setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 0;");
            });
            shipButton.setMaxWidth(Double.MAX_VALUE);

            int row = (i - 1) / columns;
            int col = (i - 1) % columns;
            shipGrid.add(shipButton, col, row);
        }
    }

    private void selectShip(int shipNumber) {
        // Save the selected ship in GameData
        GameData.setSelectedShip(shipNumber);
        // Selected spaceship in console
        System.out.println("Selected Spaceship: " + shipNumber);
        fireNewGame();
        mainMenuSound.stop();
    }
}
