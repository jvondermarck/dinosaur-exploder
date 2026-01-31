package com.dinosaur.dinosaurexploder.view;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.ui.FontType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.model.Settings;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.MenuHelper;
import com.dinosaur.dinosaurexploder.utils.SettingsProvider;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Objects;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;
import static com.dinosaur.dinosaurexploder.utils.LanguageManager.DEFAULT_LANGUAGE;

public class LanguageSelectionMenu extends FXGLMenu {

    public static final int SPACE_ZONE = 50;
    private final LanguageManager languageManager = LanguageManager.getInstance();
    private final Settings settings = SettingsProvider.loadSettings();

    private Text title;
    private Label languageLabel;
    private Button backButton;

    public LanguageSelectionMenu() {
        super(MenuType.MAIN_MENU);

        MenuHelper.setupSelectionMenu(
                this,
                createHeaderZone(),
                createLanguageSelector(),
                createBackButton(),
                SPACE_ZONE,
                getAppWidth(),
                getAppHeight());
    }

    private VBox createHeaderZone() {
        title =
                getUIFactoryService()
                        .newText(languageManager.getTranslation( "select_language" ).toUpperCase(), Color.LIME, FontType.MONO, GameConstants.MAIN_TITLES);
        VBox headerZone = new VBox(25, title);
        headerZone.setAlignment(Pos.CENTER);
        return headerZone;
    }

    //adapted from the languageBox implementation originally at DinosaurMenu
    private VBox createLanguageSelector() {
        ComboBox<String> languageComboBox = new ComboBox<>();
        languageLabel = new Label(languageManager.getTranslation("select language"));
        languageComboBox.getItems().addAll(languageManager.getAvailableLanguages());

        languageComboBox.setPrefWidth(ComboBox.USE_COMPUTED_SIZE);
        languageComboBox.setMinWidth(ComboBox.USE_COMPUTED_SIZE);

        languageComboBox.setValue(
                settings.getLanguage() != null ? settings.getLanguage() : DEFAULT_LANGUAGE);

        if (settings.getLanguage() != null) {
            changeLanguage(settings.getLanguage());
        }

        // Define what text is drawn, keeping orignal item value (Draws text->"Français" while item
        // value->"French"
        languageComboBox.setCellFactory(
                cb ->
                        new ListCell<>() {
                            @Override
                            protected void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                setText(empty || item == null ? null : languageManager.getNativeLanguageName(item));
                            }
                        });

        languageComboBox.setButtonCell(
                new ListCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty || item == null ? null : languageManager.getNativeLanguageName(item));
                    }
                });

        applyStylesheet(languageComboBox);
        languageComboBox.setOnAction(
                event -> {
                    changeLanguage(languageComboBox.getValue());
                    updateTexts();
                    languageComboBox.requestLayout();
                });

        languageLabel.setText(languageManager.getTranslation("language_label").toUpperCase());
        languageLabel.setStyle(
                "-fx-text-fill: #00FF00;" + "-fx-effect: dropshadow(gaussian, black, 2, 1.0, 0, 0);");
        applyStylesheet(languageLabel);

        VBox languageBox = new VBox(10, languageLabel, languageComboBox);
        languageBox.setFillWidth(true);

        //languageBox.setTranslateY(600);
        languageBox.setMaxWidth(getAppWidth()*0.8);
        languageBox.setPadding(new Insets(20));
        languageBox.setAlignment(Pos.CENTER);
        languageBox.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.8);"
                        + "-fx-background-radius: 15;"
                        + "-fx-border-color: rgba(0, 220, 0, 0.7);"
                        + "-fx-border-width: 2;"
                        + "-fx-border-radius: 15;"
                        + "-fx-effect:  dropshadow(gaussian, rgba(0, 220, 0, 0.6), 12, 0.5, 0, 0);");

        languageBox
                .layoutBoundsProperty()
                .addListener(
                        (obs, oldBounds, newBounds) -> {
                            if (newBounds.getWidth() > 0) {
                                languageBox.setTranslateX(getAppWidth() / 2.0 - newBounds.getWidth() / 2.0);
                            }
                        });

        return languageBox;
    }

    private void changeLanguage(String selectedLanguage) {
        languageManager.setSelectedLanguage(selectedLanguage);
        languageManager.loadTranslations(selectedLanguage);
        settings.setLanguage(selectedLanguage);
        SettingsProvider.saveSettings(settings);
    }

    private void updateTexts() {
        title.setText(languageManager.getTranslation("language_label"));
        languageLabel.setText(languageManager.getTranslation("language_label"));
        backButton.setText(languageManager.getTranslation("back").toUpperCase());
        SettingsProvider.saveSettings(settings);

    }

    private void applyStylesheet(javafx.scene.Parent parent) {
        parent
                .getStylesheets()
                .add(
                        Objects.requireNonNull(getClass().getResource(GameConstants.STYLESHEET_PATH))
                                .toExternalForm());
    }

    private Button createBackButton() {
        backButton = MenuHelper.createStyledButton(languageManager.getTranslation("back").toUpperCase());
        backButton.setOnAction(event -> fireResume());
        return backButton;
    }
}
