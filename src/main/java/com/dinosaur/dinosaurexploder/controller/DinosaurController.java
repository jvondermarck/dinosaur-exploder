package com.dinosaur.dinosaurexploder.controller;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.dinosaur.dinosaurexploder.model.*;
import com.dinosaur.dinosaurexploder.utils.GameData;
import com.dinosaur.dinosaurexploder.view.DinosaurGUI;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.Optional;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;
import static javafx.util.Duration.seconds;

/**
 * Summary :
 * The Factory handles the Dinosaur , player controls and collision detection of all entities in the game
 */
public class DinosaurController {
    private Entity player;
    private Entity score;
    private Entity life;
    private Entity bomb;
    private int lives = 3;

    private ScoreList scoreList; // Add a field for the high score list

    /**
     * Summary :
     * Detecting the player damage to decrease the lives and checking if the game is over
     */
    public void damagePlayer() {
        lives = life.getComponent(LifeComponent.class).decreaseLife(1);
        var flash = new Rectangle(DinosaurGUI.WIDTH, DinosaurGUI.HEIGHT, Color.rgb(190, 10, 15, 0.5));
        getGameScene().addUINode(flash);
        runOnce(() -> getGameScene().removeUINode(flash), seconds(0.5));

        if (lives <= 0) {
            // Sync the lives counter visually after death
            life.getComponent(LifeComponent.class).onUpdate(0); // Show 0 lives
            System.out.println("Game Over!");
            gameOver(); // Trigger the game over sequence
        } else {
            System.out.printf("%d lives remaining ! ", lives);
        }
    }

    /**
     * Summary :
     * To move the space shuttle in forward , backward , right , left directions
     */
    public void initInput() {
        onKey(KeyCode.UP, () -> player.getComponent(PlayerComponent.class).moveUp());
        onKey(KeyCode.DOWN, () -> player.getComponent(PlayerComponent.class).moveDown());
        onKey(KeyCode.LEFT, () -> player.getComponent(PlayerComponent.class).moveLeft());
        onKey(KeyCode.RIGHT, () -> player.getComponent(PlayerComponent.class).moveRight());
        onKeyDown(KeyCode.SPACE, () -> player.getComponent(PlayerComponent.class).shoot());

        onKey(KeyCode.W, () -> player.getComponent(PlayerComponent.class).moveUp());
        onKey(KeyCode.S, () -> player.getComponent(PlayerComponent.class).moveDown());
        onKey(KeyCode.A, () -> player.getComponent(PlayerComponent.class).moveLeft());
        onKey(KeyCode.D, () -> player.getComponent(PlayerComponent.class).moveRight());

        onKeyDown(KeyCode.B, () -> {
            if (bomb != null && bomb.hasComponent(BombComponent.class)) {
                bomb.getComponent(BombComponent.class).useBomb(player);
            }
        });
    }

    public void initGame() {
        // Load the high scores when the game initializes
        scoreList = ScoreList.load();

        getGameWorld().addEntityFactory(new GameEntityFactory());
        spawn("background", 0, 0);

        player = spawn("player", getAppCenter().getX() - 45, getAppHeight() - 200);

        FXGL.play(GameConstants.BACKGROUND_SOUND);

        run(() -> {
            if (random(0, 2) < 2)
                spawn("greenDino", random(0, getAppWidth() - 80), -50);
        }, seconds(0.75));


        score = spawn("Score", getAppCenter().getX() - 270, getAppCenter().getY() - 320);
        life = spawn("Life", getAppCenter().getX() - 260, getAppCenter().getY() - 250);
        bomb = spawn("Bomb", getAppCenter().getX() - 260, getAppCenter().getY() - 180);

        if (bomb != null && !bomb.hasComponent(BombComponent.class)) {
            bomb.addComponent(new BombComponent());
        }
    }

    /**
     * Summary :
     * Triggers the game over sequence by getting the score and starting the name prompt.
     */
    public void gameOver() {
        // Get the final score from ScoreComponent (which correctly has getScore())
        int finalScore = score.getComponent(ScoreComponent.class).getScore();
        System.out.println("Final Score: " + finalScore); // Debugging line

        // Start the process of prompting the player for their name
        promptForName(finalScore);
    }

    /**
     * Prompts the player for their 3-letter initials using a dialog box.
     * Validates the input and re-prompts if invalid.
     * If valid, saves the score and shows the scoreboard.
     *
     * @param finalScore The score to save.
     */
    private void promptForName(int finalScore) {
        getDialogService().showInputBox(
                getLocalizationService().getLocalizedString("dialog.enter_name"),
                // This lambda function (callback) is executed when the player submits input
                name -> {
                    // Trim whitespace from the input
                    String trimmedName = name.trim();

                    // Validate the input inside the callback
                    if (trimmedName.matches("^[a-zA-Z]{3}$")) {
                        // --- Input is VALID ---
                        System.out.println("Name entered: " + trimmedName); // Debugging line

                        // Create a new score entry (convert name to uppercase for consistency)
                        ScoreEntry newEntry = new ScoreEntry(trimmedName.toUpperCase(), finalScore);

                        // Add the entry to the list (this handles sorting, limiting, and saving)
                        scoreList.add(newEntry);
                        System.out.println("Score added to list."); // Debugging line


                        // Display the high score board (which will then call askPlayAgain)
                        displayScoreboard();

                    } else {
                        // --- Input is INVALID ---
                        System.out.println("Invalid name entered: " + name); // Debugging line

                        // Show an error message and then re-prompt by calling this method again
                        // The () -> promptForName(finalScore) means: after the user clicks OK on the error message,
                        // call promptForName again.
                        getDialogService().showMessageBox(
                                "Input must be exactly 3 letters. Please try again.",
                                () -> promptForName(finalScore) // Re-prompt
                        );
                    }
                }
        );
    }
    /**
     * Displays the formatted high score board in a dialog.
     * (Keep this method exactly as it was in the previous answer)
     */
    private void displayScoreboard() {
        StringBuilder scoreText = new StringBuilder();
        scoreText.append(getLocalizationService().getLocalizedString("dialog.scoreboard")).append("\n\n");

        List<ScoreEntry> entries = scoreList.getEntries();
        int numEntries = entries.size();

        for (int i = 0; i < numEntries; i++) {
            ScoreEntry entry = entries.get(i);
            scoreText.append(String.format("%2d. %-3s %5d\n", i + 1, entry.getName(), entry.getScore()));
        }

        for (int i = numEntries; i < 10; i++) {
            scoreText.append(String.format("%2d. ...   ...\n", i + 1));
        }
        System.out.println("Displaying Scoreboard:\n" + scoreText); // Debugging line


        getDialogService().showMessageBox(scoreText.toString(), this::askPlayAgain);
    }

    /**
     * Asks the player if they want to play again after viewing the scoreboard.
     * (Keep this method exactly as it was in the previous answer)
     */
    private void askPlayAgain() {
        getDialogService().showConfirmationBox(getLocalizationService().getLocalizedString("Game.2"), yes -> {
            if (yes) {
                getGameController().startNewGame();
            } else {
                getGameController().gotoMainMenu();
            }
        });
    }

    /**
     * Summary :
     * Detect the collision between the game entities.
     */
    public void initPhysics() {
        // Collision: Player's projectile hits a green dino
        onCollisionBegin(EntityType.PROJECTILE, EntityType.GREENDINO, (projectile, greendino) -> {
            // Spawn explosion visual/sound
            spawn("explosion", greendino.getX() - 25, greendino.getY() - 30);
            FXGL.play(GameConstants.ENEMY_EXPLODE_SOUND); // Assuming GameConstants has this

            // Remove projectile and dino
            projectile.removeFromWorld();
            greendino.removeFromWorld();

            // Increment score (Ensure ScoreComponent exists on 'score' entity)
            if (score != null && score.hasComponent(ScoreComponent.class)) {
                score.getComponent(ScoreComponent.class).incrementScore(1);
            }
        });

        // Collision: Enemy projectile hits the player
        onCollisionBegin(EntityType.ENEMYPROJECTILE, EntityType.PLAYER, (projectile, playerEntity) -> { // Changed 'player' to 'playerEntity' to avoid naming conflict with the field 'player'
            FXGL.play(GameConstants.PLAYER_HIT_SOUND); // Assuming GameConstants has this
            projectile.removeFromWorld();
            System.out.println("You got hit !\n");
            damagePlayer(); // Call your existing method to handle damage
        });

        // Collision: Player touches a green dino
        onCollisionBegin(EntityType.PLAYER, EntityType.GREENDINO, (playerEntity, greendino) -> { // Changed 'player' to 'playerEntity'
            FXGL.play(GameConstants.PLAYER_HIT_SOUND); // Assuming GameConstants has this
            greendino.removeFromWorld();
            System.out.println("You touched a dino !");
            damagePlayer(); // Call your existing method to handle damage
        });

        // Add other collision handlers if needed...
    }

    /*
    private static class PlayerComponent extends com.almasb.fxgl.entity.component.Component {
        public void moveUp() { System.out.println("Move Up"); }
        public void moveDown() { System.out.println("Move Down"); }
        public void moveLeft() { System.out.println("Move Left"); }
        public void moveRight() { System.out.println("Move Right"); }
        public void shoot() { System.out.println("Shoot"); }
    }

    private static class ScoreComponent extends com.almasb.fxgl.entity.component.Component {
        private int scoreValue = 0;
        public void incrementScore(int amount) { scoreValue += amount; System.out.println("Score: " + scoreValue); }
        public int getScore() { return scoreValue; } // *** IMPORTANT: Add this method ***
    }

    private static class LifeComponent extends com.almasb.fxgl.entity.component.Component {
        private int lifeValue = 3;
        public int decreaseLife(int amount) { lifeValue -= amount; return lifeValue; }
        public void onUpdate(int currentLives) { System.out.println("Lives display updated to: " + currentLives); } // Method to visually update lives
    }

    private static class BombComponent extends com.almasb.fxgl.entity.component.Component {
         public void useBomb(Entity player) { System.out.println("Bomb Used!"); }
    }

    private static class GameEntityFactory implements com.almasb.fxgl.entity.EntityFactory {
         // Factory methods would be here...
         @com.almasb.fxgl.dsl.Spawns("player")
         public Entity newPlayer(com.almasb.fxgl.entity.SpawnData data) { return new Entity(); }
         // etc.
    }
    */
}