package com.dinosaur.dinosaurexploder.components;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

import com.almasb.fxgl.entity.component.Component;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.interfaces.Life;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
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
    heartLost = new Image(GameConstants.HEART_LOST_IMAGE_PATH);
    heart1 = new ImageView(heart);
    heart2 = new ImageView(heart);
    heart3 = new ImageView(heart);

    // Initialize lifeText with the translated string
    lifeText =
        getUIFactoryService()
            .newText(
                languageManager.getTranslation(GameConstants.LIVES).toUpperCase(),
                Color.RED,
                GameConstants.TEXT_SIZE_GAME_INFO);

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
    lifeText.setText(
        languageManager.getTranslation(GameConstants.LIVES).toUpperCase() + ": " + life);
  }

  private void updateLifeDisplay() {
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
      currentHeart.setLayoutX((MAX_LIVES - i) * 30.0);
      setEntity(currentHeart);
    }

    // Display the lifeText component
    lifeText.setText(
        languageManager.getTranslation(GameConstants.LIVES).toUpperCase() + ": " + life);
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
