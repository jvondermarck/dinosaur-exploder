package com.dinosaur.dinosaurexploder.components;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.interfaces.Bomb;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.model.GameData;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

public class BombComponent extends Component implements Bomb {
    private int bombCount = 3;
    private int maxBombCount = 3;
    private Image spcshpImg;
    private int selectedShip;

    // Tracking variables for regeneration
    private int lastLevel = 1;
    private int coinCounter = 0;
    private static final int COINS_NEEDED_FOR_BOMB = 15; // Number of coins needed to regenerate a bomb

    public BombComponent() {
        // Selected spaceship from GameData
        this.selectedShip = GameData.getSelectedShip();

        //  Set the image of SelectedShip using the spaceship number

    }

    // Declaring 3 Bomb
    ImageView bomb1;
    ImageView bomb2;
    ImageView bomb3;
    // Declaring Bomb Text
    private Text bombText;


    private Node bombUI;

    private final LanguageManager languageManager = LanguageManager.getInstance();

    @Override
    public void onAdded() {
        Image bomb = new Image(GameConstants.BOMB_IMAGE_PATH);
        bomb1 = new ImageView(bomb);
        bomb2 = new ImageView(bomb);
        bomb3 = new ImageView(bomb);

        // Initialize bombText with the translated string
        bombText = new Text(languageManager.getTranslation("bombs_left") + ": " + bombCount);

        // Style the text
        bombText.setFill(Color.ORANGE);
        bombText.setFont(Font.font(GameConstants.GAME_FONTNAME, 20));
        bombText.setLayoutX(0);
        bombText.setLayoutY(0);

        // Listen for language changes and update UI automatically
        languageManager.selectedLanguageProperty().addListener((obs, oldVal, newVal) -> updateTexts());

        // Initial bomb UI setup
        bombUI = createBombUI();
        entity.getViewComponent().addChild(bombUI);
    }

    @Override
    public void onUpdate(double tpf) {
        updateBombUI(); // Update the bomb UI every frame based on bombCount
    }

    private void updateTexts() {
        bombText.setText(languageManager.getTranslation("bombs_left") + ": " + bombCount);
    }

    /**
     * This method creates the UI for displaying the bomb count and bomb images.
     *
     * @return Node - The created bomb UI node
     */
    private Node createBombUI() {
        var container = new Pane();

        bomb1.setLayoutY(10);
        bomb2.setLayoutY(10);
        bomb3.setLayoutY(10);
        bomb2.setLayoutX(30);
        bomb3.setLayoutX(60);

        container.getChildren().addAll(bomb1, bomb2, bomb3);
        container.getChildren().add(bombText);

        return container;
    }

    /**
     * Updates the bomb UI based on the current bomb count.
     */
    protected void updateBombUI() {
        bomb1.setVisible(bombCount >= 1);
        bomb2.setVisible(bombCount >= 2);
        bomb3.setVisible(bombCount >= 3);
        // Update bomb text with the remaining bombs
        bombText.setText(languageManager.getTranslation("bombs_left") + ": " + bombCount);
    }

    /**
     * Summary:
     * This method returns the current number of bombs.
     */
    @Override
    public int getBombCount() {
        return bombCount;
    }

    /**
     * Summary :
     * This method is used to launch a row of bullets as a bomb.
     * Parameters :
     * Entity player - The player entity using the bomb
     */
    @Override
    public void useBomb(Entity player) {
        if (getBombCount() > 0) {
            bombCount--;
            updateBombUI();
            spawnBombBullets(player);
        } else {
            System.out.println("No bombs left!");
        }
    }

    /**
     * Summary :
     * This method spawns a row of bullets from the player's position.
     * Parameters :
     * Entity player - The player entity from which to spawn the bullets
     */
    protected void spawnBombBullets(Entity player) {
        Point2D center = player.getCenter();
        Image projImg = new Image(GameConstants.BASE_PROJECTILE_IMAGE_PATH);

        if (selectedShip != 0) {
            String shipImagePath = "/assets/textures/spaceship" + selectedShip + ".png";
            System.out.println("Selected spaceship: " + selectedShip);
            this.spcshpImg = new Image(shipImagePath);
        }

        for (int i = -5; i <= 5; i++) {
            double angle = entity.getRotation() - 90 + i * 10;
            Vec2 direction = Vec2.fromAngle(angle);
            spawn("basicProjectile", new SpawnData(center.getX() - (projImg.getWidth() / 2) + 3, center.getY() - spcshpImg.getHeight() / 2)
                    .put("direction", direction.toPoint2D()));
        }
        System.out.println("Bomb used! " + getBombCount() + " bombs left!");
    }

    /**
     * Regenerates bombs when a new level is reached.
     * Call this method when the player advances to a new level.
     *
     * @param currentLevel The current level of the game
     */
    public void checkLevelForBombRegeneration(int currentLevel) {
        if (currentLevel > lastLevel) {
            // Player has advanced to a new level, regenerate one bomb
            regenerateBomb(1);
            lastLevel = currentLevel;
            System.out.println("Level up! Regenerated a bomb. Current bombs: " + bombCount);
        }
    }

    /**
     * Tracks coin collection and regenerates bombs when enough coins are collected.
     * Call this method whenever the player collects a coin.
     */
    public void trackCoinForBombRegeneration() {
        coinCounter++;
        if (coinCounter >= COINS_NEEDED_FOR_BOMB) {
            // Player has collected enough coins, regenerate one bomb
            regenerateBomb(1);
            coinCounter = 0; // Reset counter
            System.out.println("Collected " + COINS_NEEDED_FOR_BOMB + " coins! Regenerated a bomb. Current bombs: " + bombCount);
        }
    }

    /**
     * Regenerates the specified number of bombs, not exceeding the maximum.
     *
     * @param count The number of bombs to regenerate
     */
    private void regenerateBomb(int count) {
        bombCount = Math.min(bombCount + count, maxBombCount);
        updateBombUI();
    }

    public int getCoinCounter() {
        return coinCounter;
    }
}
