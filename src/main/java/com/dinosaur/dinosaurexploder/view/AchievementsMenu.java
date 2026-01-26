package com.dinosaur.dinosaurexploder.view;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.dinosaur.dinosaurexploder.achievements.Achievement;
import com.dinosaur.dinosaurexploder.achievements.AchievementManager;
import com.dinosaur.dinosaurexploder.achievements.KillCountAchievement;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.MenuHelper;
import java.io.FileNotFoundException;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

public class AchievementsMenu extends FXGLMenu {

    private final LanguageManager languageManager;
    private final AchievementManager achievementManager;

    public AchievementsMenu() {
        super(MenuType.MAIN_MENU);
        this.languageManager = LanguageManager.getInstance();
        
        // Try to get AchievementManager from world properties, otherwise create a temporary one
        AchievementManager am = FXGL.getWorldProperties().getOptional("achievementManager").map(o -> (AchievementManager)o).orElse(null);
        if (am == null) {
            am = new AchievementManager();
        }
        this.achievementManager = am;

        try {
            buildMenu();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void buildMenu() throws FileNotFoundException {
        ImageView background = MenuHelper.createAnimatedBackground(getAppWidth(), getAppHeight());
        TextFlow title = MenuHelper.createTitleFlow(languageManager.getTranslation("achievements"), getAppWidth());
        
        VBox contentBox = createAchievementsList();
        ScrollPane scrollPane = createScrollPane(contentBox);
        
        Button backButton = MenuHelper.createStyledButton(languageManager.getTranslation("back"));
        backButton.setOnAction(event -> fireResume());

        VBox mainLayout = new VBox(20, title, scrollPane, backButton);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(30, 40, 30, 40));
        mainLayout.setPrefWidth(getAppWidth());
        mainLayout.setPrefHeight(getAppHeight());

        getContentRoot().getChildren().addAll(background, mainLayout);
    }

    private ScrollPane createScrollPane(VBox content) {
        ScrollPane pane = new ScrollPane(content);
        pane.setFitToWidth(true);
        pane.setPrefWidth(480);
        pane.setMaxWidth(480);
        pane.setPrefViewportHeight(450);

        pane.setStyle(
                "-fx-background: rgba(0, 0, 0, 0.8);"
                        + "-fx-background-color: rgba(0, 0, 0, 0.8);"
                        + "-fx-border-color: rgba(0, 220, 0, 0.7);"
                        + "-fx-border-width: 2;"
                        + "-fx-border-radius: 10;"
                        + "-fx-background-radius: 10;");

        pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        return pane;
    }

    private VBox createAchievementsList() {
        VBox list = new VBox(15);
        list.setPadding(new Insets(10));
        list.setAlignment(Pos.TOP_CENTER);
        list.setStyle("-fx-background-color: transparent;");

        List<Achievement> achievements = achievementManager.getAllAchievements();
        for (Achievement achievement : achievements) {
            list.getChildren().add(createAchievementBox(achievement));
        }

        return list;
    }

    private VBox createAchievementBox(Achievement achievement) {
        String name = languageManager.getTranslation(achievement.getNameKey());
        String description = "";
        if (achievement instanceof KillCountAchievement) {
            description = ((KillCountAchievement) achievement).getDescription();
        } else {
            description = languageManager.getTranslation(achievement.getDescriptionKey());
        }
        
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-family: 'Public Pixel'; -fx-font-size: 14px; -fx-text-fill: #00FF00; -fx-font-weight: bold;");
        
        Text descText = getUIFactoryService().newText(description, Color.WHITE, 12);
        descText.setWrappingWidth(400);
        
        HBox statusBox = new HBox(10);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        
        boolean isCompleted = achievement.isCompleted();
        Label statusLabel = new Label(isCompleted ? languageManager.getTranslation("unlocked").toUpperCase() : languageManager.getTranslation("locked").toUpperCase());
        statusLabel.setStyle("-fx-font-family: 'Public Pixel'; -fx-font-size: 10px; -fx-text-fill: " + (isCompleted ? "#00FF00" : "#FF6666") + ";");
        
        Label rewardLabel = new Label(languageManager.getTranslation("reward") + ": " + achievement.getRewardCoins() + " " + languageManager.getTranslation("coin"));
        rewardLabel.setStyle("-fx-font-family: 'Public Pixel'; -fx-font-size: 10px; -fx-text-fill: #FFFF00;");
        
        VBox box = new VBox(8, nameLabel, descText, new HBox(20, statusLabel, rewardLabel));
        box.setPadding(new Insets(15));
        box.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.6);"
                        + "-fx-background-radius: 8;"
                        + "-fx-border-color: " + (isCompleted ? "rgba(0, 255, 0, 0.5)" : "rgba(255, 255, 255, 0.2)") + ";"
                        + "-fx-border-width: 1;"
                        + "-fx-border-radius: 8;");
        
        return box;
    }
}
