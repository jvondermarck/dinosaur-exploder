package com.dinosaur.dinosaurexploder.components;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.interfaces.Dinosaur;
import com.dinosaur.dinosaurexploder.utils.AudioManager;
import com.dinosaur.dinosaurexploder.utils.GameTimer;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import com.dinosaur.dinosaurexploder.view.DinosaurGUI;
import javafx.geometry.Point2D;

import static com.almasb.fxgl.dsl.FXGLForKtKt.random;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;
import static java.lang.Math.atan;

/**
 * Summary :
 * This class extends Component and Implements the Dinosaur Classes
 * and Handles Updating the Dino
 * This Dino is an orange Boss who appears every ten levels.
 * He doesn't shoot but follows the player to ram him.
 * he has three times the lives than the current level.
 */
public class OrangeDinoComponent extends Component implements Dinosaur {
    double movementSpeed = 1.5;
    private int lives = 10;
    private final GameTimer gameTimer;
    private final PlayerComponent playerComponent;
    Point2D playerPosition;

    public OrangeDinoComponent(GameTimer gameTimer, PlayerComponent playerComponent) {
        this.gameTimer = gameTimer;
        this.playerComponent = playerComponent;
    }

    private boolean isPaused = false;
    

    boolean firstTime = true;
    private LevelManager levelManager;

    public void setLevelManager(LevelManager levelManager) {
        this.levelManager = levelManager;
    }


    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public double getMovementSpeed() {
        return movementSpeed;
    }

    public void setMovementSpeed(double movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    

    @Override
    public void onAdded() {
        //Get the current enemy speed from the level manager
        //levelManager = FXGL.geto("levelManager");
        firstTime = true;
    }

    /**
     * Summary :
     * This method runs for every frame like a continues flow , without any stop until we put stop to it.
     * Parameters :
     * double ptf
     */
    @Override
    public void onUpdate(double ptf) {
        playerPosition = playerComponent.getEntity().getPosition();

        if (isPaused) return;

        if (firstTime) {
            System.out.println("level: " + levelManager.getCurrentLevel());
            movementSpeed = levelManager.getEnemySpeed()/2;
            lives = levelManager.getCurrentLevel() * 3;
            firstTime = false;
        }

        if (entity.getX() < playerPosition.getX()) {
            moveRight();
        }
        if (entity.getX() > playerPosition.getX()) {
            moveLeft();
        }
        if (entity.getY() < playerPosition.getY()) {
            moveDown();
        }
        if (entity.getY() > playerPosition.getY()) {
            moveUp();
        }
        rotateDino();
    }

    /**
     * This method calculates the rotation,
     * that the dino needs to always look in the direction of the player
     * and rotates it.
     */
    void rotateDino() {
        double oppositeSide;
        double adjacentSide;

        //dino right from player
        if (entity.getX() > playerPosition.getX()) {
            oppositeSide = entity.getX()-playerPosition.getX();
        } else {
            oppositeSide = playerPosition.getX() - entity.getX();
        }
        //dino under player
        if (entity.getY() > playerPosition.getY()) {
            adjacentSide = entity.getY()-playerPosition.getY();
        } else {
            adjacentSide = playerPosition.getY() - entity.getY();
        }

        double rotation = Math.toDegrees(atan(oppositeSide / adjacentSide));

        // dino left and under from player
        if(entity.getX() < playerPosition.getX() && entity.getY() > playerPosition.getY()){
            entity.setRotation(rotation-180);
        //dino right and under player
        } else if(entity.getX() > playerPosition.getX() && entity.getY() > playerPosition.getY()){
            rotation = 90-rotation;
            entity.setRotation(rotation+90);
        //dino right and above the player
        } else if(entity.getX() > playerPosition.getX() && entity.getY() < playerPosition.getY()){
            entity.setRotation(rotation);
        //dino left and above player
        } else if(entity.getX() < playerPosition.getX() && entity.getY() < playerPosition.getY()){
            rotation = 90-rotation;
            entity.setRotation(rotation-90);
        }
    }

    /**
     * Summary :
     * This handles with the shooting of the dinosaur and spawning of the new bullet
     */
    @Override
    public void shoot() {
     
            AudioManager.getInstance().playSound(GameConstants.SHOOT_SOUND);
        
        Point2D center = entity.getCenter();
        Vec2 direction = Vec2.fromAngle(entity.getRotation() + 90 + random(-45, 45));
        spawn("basicEnemyProjectile",
                new SpawnData(center.getX() + 50 + 3, center.getY())
                        .put("direction", direction.toPoint2D())
        );
    }

    /**
     * Summary :
     * This method damages the orange dino
     *
     * @param damage
     */
    public void damage(int damage) {
        lives -= damage;
    }

    public void moveUp() {
        if (entity.getY() < 0) {
            System.out.println("Out of bounds");
            return;
        }
        entity.translateY(-movementSpeed);
    }

    /**
     * Summary :
     * This method is overriding the superclass method to limit the downSide
     * movement.
     */
    public void moveDown() {
        if (!(entity.getY() < DinosaurGUI.HEIGHT - entity.getHeight())) {
            System.out.println("Out of bounds");
            return;
        }
        entity.translateY(movementSpeed);
    }

    /**
     * Summary :
     * This method is overriding the superclass method to limit the rightSide
     * movement.
     */
    public void moveRight() {
        if (!(entity.getX() < DinosaurGUI.WIDTH - entity.getWidth())) {
            System.out.println("Out of bounds");
            return;
        }
        entity.translateX(movementSpeed);
    }

    /**
     * Summary :
     * This method is overriding the superclass method to limit the leftSide
     * movement.
     */
    public void moveLeft() {
        if (entity.getX() < 0) {
            System.out.println("Out of bounds");
            return;
        }
        entity.translateX(-movementSpeed);

    }
}

