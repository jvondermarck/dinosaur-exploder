package com.dinosaur.dinosaurexploder.components;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.almasb.fxgl.entity.component.Component;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.interfaces.Score;
import com.dinosaur.dinosaurexploder.model.HighScore;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
/**
 * Summary :
 *      This handles the Score component of the Player implements the Score interface and extends the Component
 */
public class ScoreComponent extends Component  implements Score {
    private static final String HIGH_SCORE_FILE = "highScore.ser";

    private int score = 0;
    public static HighScore highScore = new HighScore();
    private final LanguageManager languageManager = LanguageManager.getInstance();

    private Text scoreText;
    private Text highScoreText;

    @Override
    public void onAdded() {
        loadHighScore(); // Deserialize once when the component is added

        // Create UI elements
        scoreText = new Text();
        highScoreText = new Text();
        Image image = new Image(GameConstants.GREEN_DINO_IMAGE_PATH, 25, 20, false, false);
        ImageView imageView = new ImageView(image);

        scoreText.setFill(Color.GREEN);
        scoreText.setFont(Font.font(GameConstants.ARCADE_CLASSIC_FONTNAME, 20));
        highScoreText.setFill(Color.GREEN);
        highScoreText.setFont(Font.font(GameConstants.ARCADE_CLASSIC_FONTNAME, 20));

        // Arrange UI in a GridPane
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.add(scoreText, 1, 0);
        gridPane.add(highScoreText, 1, 1);
        gridPane.add(imageView, 2, 0);

        entity.getViewComponent().addChild(gridPane);

        // Initial text update
        updateTexts();

        // Listen for language changes
        languageManager.selectedLanguageProperty().addListener((obs, oldVal, newVal) -> updateTexts());
    }

    @Override
    public void onUpdate(double ptf) {
        updateTexts(); // Refresh score display every frame
    }

    private void updateTexts() {
        scoreText.setText(languageManager.getTranslation("score") + ": " + score);
        highScoreText.setText(languageManager.getTranslation("high_score") + ": " + highScore.getHigh());
    }

    private void loadHighScore() {
        try (FileInputStream file = new FileInputStream("highScore.ser");
             ObjectInputStream in = new ObjectInputStream(file)) {
            highScore = (HighScore) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            highScore = new HighScore();
        }
    }
    /**
     * Summary :
     *      This method is overriding the superclass method to return the Score to the current Score
     */
    @Override
    public int getScore() {
        return score;
    }
    /**
     * Summary :
     *      This method is overriding the superclass method to set the Score to the current Score
     */
    @Override
    public void setScore(int i) {
        score = i;
    }
    /**
     * Summary :
     *      This method is overriding the superclass method to increment the Score to the current Score
     */
    @Override
    public void incrementScore(int i){
        score += i;
        if(score > highScore.getHigh()) {
            highScore = new HighScore(score);
        }

        try{
            FileOutputStream fileOut = new FileOutputStream(HIGH_SCORE_FILE);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(highScore);
            out.close();
            fileOut.close();
        } catch (IOException e){
            System.err.println("Error saving highScore.ser file: " + e.getMessage());
        }
    }
}
