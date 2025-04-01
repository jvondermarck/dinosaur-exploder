package com.dinosaur.dinosaurexploder.model;

import com.almasb.fxgl.entity.component.Component;
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

    private final Image heart = new Image(GameConstants.HEART_IMAGEPATH);
    private int life = 3;

    // Declaring Lives Text
    private Text lifeText;
    // Declaring 3 Hearts
    private ImageView test1 = new ImageView(heart);
    private ImageView test2 = new ImageView(heart);
    private ImageView test3 = new ImageView(heart);

    private final LanguageManager languageManager = LanguageManager.getInstance();

    @Override
    public void onAdded() {
        // Initialize lifeText with the translated string
        lifeText = new Text(languageManager.getTranslation("lives"));

        // Style the text
        lifeText.setFill(Color.RED);
        lifeText.setFont(Font.font(GameConstants.ARCADECLASSIC_FONTNAME, 20));

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
        test1.setLayoutY(10);
        test2.setLayoutY(10);
        test3.setLayoutY(10);

        test2.setLayoutX(test1.getLayoutX() + 30);
        test3.setLayoutX(test2.getLayoutX() + 30);

        // Set the appropriate number of hearts based on `life`
        if (life == 3) {
            setEntity(test1);
            setEntity(test2);
            setEntity(test3);
        } else if (life == 2) {
            setEntity(test1);
            setEntity(test2);
        } else if (life == 1) {
            setEntity(test1);
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

    @Override
    public int getLife() {
        return life;
    }

    /**
     * Summary :
     * This method is overriding the superclass method to sets the life the current life
     */
    @Override
    public void setLife(int i) {
        life = i;
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