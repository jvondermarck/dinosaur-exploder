package com.dinosaur.dinosaurexploder.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.ui.FontFactory;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.interfaces.Life;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import java.util.List;
import java.util.Set;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Summary : This handles the life component of the Player implements the life interface and extends
 * the Component
 */
public class LifeComponent extends Component implements Life {

  private static final int MAX_LIVES = 3;
  private Image heart;
  private Image heartLost;
  private int life = MAX_LIVES;

  // Declaring Lives Text
  private Text lifeText;
  // Declaring 3 Hearts
  private ImageView heart1;
  private ImageView heart2;
  private ImageView heart3;

  private final LanguageManager languageManager = LanguageManager.getInstance();

  @Override
  public void onAdded() {
    heart = new Image(GameConstants.HEART_IMAGE_PATH);
    heart1 = new ImageView(heart);
    heart2 = new ImageView(heart);
    heart3 = new ImageView(heart);

    // Initialize lifeText with the translated string
    lifeText = new Text(languageManager.getTranslation("lives"));

    // Style the text
    Set<String> cyrLangs = Set.of("Greek", "Russian");
    FontFactory basecyrFont = FXGL.getAssetLoader().loadFont("Geologica-Regular.ttf");
    Font cyr20Font = basecyrFont.newFont(20);
    FontFactory baseArcadeFont = FXGL.getAssetLoader().loadFont("arcade_classic.ttf");
    Font arcade20Font = baseArcadeFont.newFont(20);
    lifeText.setFill(Color.RED);
    if (cyrLangs.contains(languageManager.selectedLanguageProperty().getValue())) {
      lifeText.fontProperty().unbind();
      lifeText.setFont(cyr20Font);
    } else {
      lifeText.fontProperty().unbind();
      lifeText.setFont(arcade20Font);
    }

    // Listen for language changes and update UI automatically
    languageManager.selectedLanguageProperty().addListener((obs, oldVal, newVal) -> updateTexts());

    // Initial display update
    updateLifeDisplay();
  }

  @Override
  public void onUpdate(double ptf) {
    updateLifeDisplay(); // Update hearts and text display every frame
  }

  private void updateTexts() {
    lifeText.setText(languageManager.getTranslation("lives") + ": " + life);
  }

  private void updateLifeDisplay() {
    heartLost = new Image(GameConstants.HEART_LOST_IMAGE_PATH);
    // Clear previous entities
    clearEntity();

    List<ImageView> lives = List.of(heart1, heart2, heart3);

    // Set the appropriate number of hearts based on `life`
    for (int i = MAX_LIVES; i > 0; i--) {
      ImageView currentHeart = lives.get(MAX_LIVES - i);
      if (i > life) {
        currentHeart.setImage(heartLost);
      } else {
        currentHeart.setImage(heart);
      }

      currentHeart.setLayoutY(10);
      currentHeart.setLayoutX((MAX_LIVES - i) * 30);
      setEntity(currentHeart);
    }

    // Display the lifeText component
    lifeText.setText(languageManager.getTranslation("lives") + ": " + life);
    setEntity(lifeText);
  }

  // Created two methods for shorter and cleaner code
  public void setEntity(Node j) {
    entity.getViewComponent().addChild(j);
  }

  public void clearEntity() {
    entity.getViewComponent().clearChildren();
  }

  /**
   * Summary : This method is overriding the superclass method to increase the life to the current
   * life without exceeding the maximum number of lives allowed
   */
  @Override
  public int increaseLife(int i) {
    life = Math.min(life + i, MAX_LIVES);
    return life;
  }

  /**
   * Summary : This method is overriding the superclass method to decrease the life to the current
   * life
   */
  @Override
  public int decreaseLife(int i) {
    life -= i;
    return life;
  }

  public int getLife() {
    return life;
  }
}
