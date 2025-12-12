package com.dinosaur.dinosaurexploder.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.ui.FontFactory;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.interfaces.Score;
import com.dinosaur.dinosaurexploder.model.HighScore;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import java.io.*;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.io.*;
import java.util.Set;

/** Handles the score component of the player. */
public class ScoreComponent extends Component implements Score {
    private int score = 0;
    private static HighScore highScore = new HighScore();
    private final LanguageManager languageManager = LanguageManager.getInstance();

    private Text scoreText;
    private Text highScoreText;

    @Override
    public void onAdded() {
        loadHighScore();
        createScoreUI();
        updateTexts();

        languageManager.selectedLanguageProperty().addListener((obs, oldVal, newVal) -> updateTexts());
    }

    @Override
    public void onUpdate(double tpf) {
        updateTexts(); // Could be optimized to update only when score changes
    }

    private void createScoreUI() {
        scoreText = createText();
        highScoreText = createText();

        ImageView dinoIcon = new ImageView(
                new Image(GameConstants.GREEN_DINO_IMAGE_PATH, 25, 20, false, false)
        );

        // Group scoreText and icon together
        HBox scoreBox = new HBox(5, scoreText, dinoIcon);
        scoreBox.setAlignment(Pos.CENTER_LEFT);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.add(scoreBox, 1, 0);
        gridPane.add(highScoreText, 1, 1);

        entity.getViewComponent().addChild(gridPane);
    }

    private Text createText() {
        Text text = new Text();
        Set<String> cyrLangs = Set.of("Greek","Russian");
        FontFactory basecyrFont = FXGL.getAssetLoader().loadFont("Geologica-Regular.ttf");
        Font cyr20Font = basecyrFont.newFont(GameConstants.TEXT_SIZE_GAME_DETAILS);
        FontFactory baseArcadeFont = FXGL.getAssetLoader().loadFont("arcade_classic.ttf");
        Font arcade20Font = baseArcadeFont.newFont(GameConstants.TEXT_SIZE_GAME_DETAILS);
        text.setFill(Color.YELLOW);
        if ( cyrLangs.contains(languageManager.selectedLanguageProperty().getValue()) ) {
            text.fontProperty().unbind();
            text.setFont(cyr20Font);
        } else {
            text.fontProperty().unbind();
            text.setFont(arcade20Font);
        }
        return text;
    }
  }

  private void saveHighScore() {
    try (ObjectOutputStream out =
        new ObjectOutputStream(new FileOutputStream(GameConstants.HIGH_SCORE_FILE))) {
      out.writeObject(highScore);
    } catch (IOException e) {
      System.err.println("Error saving high score: " + e.getMessage());
    }
  }

  @Override
  public int getScore() {
    return score;
  }

  @Override
  public void setScore(int score) {
    this.score = score;
  }

  @Override
  public void incrementScore(int increment) {
    score += increment;

    if (score > highScore.getHigh()) {
      highScore = new HighScore(score);
      saveHighScore();
    }
  }
}
