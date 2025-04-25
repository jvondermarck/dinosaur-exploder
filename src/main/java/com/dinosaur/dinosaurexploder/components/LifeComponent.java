package com.dinosaur.dinosaurexploder.model;

import com.almasb.fxgl.entity.component.Component;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Summary :
 * This handles the life component of the Player implements the life interface and extends the Component
 */
public class LifeComponent extends Component implements Life {

    private static final int MAX_LIVES = 3;
    private final Image heart = new Image(GameConstants.HEART_IMAGE_PATH);
    private int life = MAX_LIVES;

    // Declaring Lives Text
    private Text lifeText;
    // Declaring 3 Hearts
    private final ImageView heart1 = new ImageView(heart);
    private final ImageView heart2 = new ImageView(heart);
    private final ImageView heart3 = new ImageView(heart);

    private final LanguageManager languageManager = LanguageManager.getInstance();

    @Override
    public void onAdded() {
        // Initialize lifeText with the translated string
        lifeText = new Text(languageManager.getTranslation("lives"));

        // Style the text
        lifeText.setFill(Color.RED);
        lifeText.setFont(Font.font(GameConstants.ARCADE_CLASSIC_FONTNAME, 20));

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
        // Clear previous entities
        clearEntity();

        // Adjust hearts and set them based on the current life value
        heart1.setLayoutY(10);
        heart2.setLayoutY(10);
        heart3.setLayoutY(10);

        heart2.setLayoutX(heart1.getLayoutX() + 30);
        heart3.setLayoutX(heart2.getLayoutX() + 30);

        // Set the appropriate number of hearts based on `life`
        if (life == 3) {
            setEntity(heart1);
            setEntity(heart2);
            setEntity(heart3);
        } else if (life == 2) {
            setEntity(heart1);
            setEntity(heart2);
        } else if (life == 1) {
            setEntity(heart1);
        }

        // Display the lifeText component
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
     * Summary :
     * This method is overriding the superclass method to increase the life to the current life without exceeding
     * the maximum number of lives allowed
     */
    @Override
    public int increaseLife(int i) {
        life = Math.min(life + i, MAX_LIVES);
        return life;
    }

    /**
     * Summary :
     * This method is overriding the superclass method to decrease the life to the current life
     */
    @Override
    public int decreaseLife(int i) {
        life -= i;
        return life;
    }
}