package com.dinosaur.dinosaurexploder.model;

// Keep necessary imports: Component, LanguageManager, Text, Color, Font, GridPane, Image, ImageView
import com.almasb.fxgl.entity.component.Component;
import com.dinosaur.dinosaurexploder.view.LanguageManager; // Keep if needed for UI text
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
// Remove imports related to File I/O and Serialization if no longer used here
// import java.io.FileInputStream;
// import java.io.FileOutputStream;
// import java.io.IOException;
// import java.io.ObjectInputStream;
// import java.io.ObjectOutputStream;

// Assuming GameConstants is still needed for paths/fonts
// import static com.dinosaur.dinosaurexploder.model.GameConstants.*;

/**
 * Summary :
 * This handles the Score display component for the Player.
 * It tracks the score for the CURRENT game session only.
 */
public class ScoreComponent extends Component implements Score { // Keep implements Score if the interface is just for get/set/increment
    private Integer score = 0; // Score for the current game
    // ---- REMOVED HighScore ----
    // public static HighScore highScore = new HighScore();

    // Keep LanguageManager if you need it for localized "Score:" text
    private final LanguageManager languageManager = LanguageManager.getInstance();

    private Text scoreText;
    // ---- REMOVED highScoreText ----
    // private Text highScoreText;

    @Override
    public void onAdded() {
        // ---- REMOVED loadHighScore() ----
        // loadHighScore();

        // Create UI elements for current score
        scoreText = new Text();
        // ---- REMOVED highScoreText creation ----
        // highScoreText = new Text();
        // Keep image if desired next to score
        Image image = new Image(GameConstants.GREENDINO_IMAGEPATH, 25, 20, false, false);
        ImageView imageView = new ImageView(image);

        scoreText.setFill(Color.GREEN);
        scoreText.setFont(Font.font(GameConstants.ARCADECLASSIC_FONTNAME, 20));
        // ---- REMOVED highScoreText setup ----
        // highScoreText.setFill(Color.GREEN);
        // highScoreText.setFont(Font.font(GameConstants.ARCADECLASSIC_FONTNAME, 20));

        // Arrange UI in a GridPane
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.add(scoreText, 1, 0);
        // ---- REMOVED highScoreText add ----
        // gridPane.add(highScoreText, 1, 1);
        gridPane.add(imageView, 2, 0); // Adjust column index if needed

        entity.getViewComponent().addChild(gridPane);

        // Initial text update
        updateTexts();

        // Listen for language changes if needed
        languageManager.selectedLanguageProperty().addListener((obs, oldVal, newVal) -> updateTexts());
    }

    @Override
    public void onUpdate(double ptf) {
        // Refresh score display every frame (or less often if preferred)
        updateTexts();
    }

    private void updateTexts() {
        // Only update the current score text
        scoreText.setText(languageManager.getTranslation("Game.3") + score); // Using "Game.3=Score: " property
        // ---- REMOVED highScoreText update ----
        // highScoreText.setText(languageManager.getTranslation("high_score") + ": " + highScore.getHigh());
    }

    // ---- REMOVED loadHighScore method ----
    /*
    private void loadHighScore() {
        // ... removed ...
    }
    */

    /**
     * Summary :
     * Returns the score for the current game session.
     */
    @Override
    public int getScore() {
        return score;
    }

    /**
     * Summary :
     * Sets the score for the current game session.
     */
    @Override
    public void setScore(int i) {
        score = i;
        // No high score logic needed here anymore
    }

    /**
     * Summary :
     * Increments the score for the current game session.
     */
    @Override
    public void incrementScore(int i) {
        score += i;
        // ---- REMOVED high score checking and saving logic ----
        /*
        if(score > highScore.getHigh()){ highScore = new HighScore(score);
        try{FileOutputStream fileOut = new FileOutputStream("highScore.ser");
       ObjectOutputStream out = new ObjectOutputStream(fileOut);
       out.writeObject(highScore);
        out.close();
        fileOut.close();} catch (IOException e){
            e.printStackTrace();
        }}
        */
    }
}