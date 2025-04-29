package com.dinosaur.dinosaurexploder.components;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import com.dinosaur.dinosaurexploder.interfaces.Dinosaur;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import com.dinosaur.dinosaurexploder.view.DinosaurGUI;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;

/**
 * Summary :
 *      This class extends Component and Implements the Dinosaur Classes and Handles the Shooting and Updating the Dino
 */
public class RedDinoComponent extends Component implements Dinosaur {
    double horizontalSpeed = 1.5;
    public int lives = 10;
    private LocalTimer shootTimer = FXGL.newLocalTimer();
    private boolean isPaused = false;
    private boolean isMuted = false;

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

    public double getHorizontalSpeed() {
        return horizontalSpeed;
    }

    public void setHorizontalSpeed(double horizontalSpeed) {
        this.horizontalSpeed = horizontalSpeed;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    @Override
    public void onAdded(){
        //Get the current enemy speed from the level manager
        //levelManager = FXGL.geto("levelManager");
        firstTime = true;
    }
    /**
     * Summary :
     *      This method runs for every frame like a continues flow , without any stop until we put stop to it.
     * Parameters :
     *      double ptf
     */
    @Override
    public void onUpdate(double ptf) {
        if(isPaused) return;

        if(firstTime){
            System.out.println("level: " + levelManager.getCurrentLevel());
            horizontalSpeed = levelManager.getEnemySpeed();
            lives = levelManager.getCurrentLevel()*2;
            firstTime = false;
        }

        // the Dino turns around just before he hits a site of the screen
        if(entity.getX() < 0 || entity.getX() > DinosaurGUI.WIDTH - entity.getWidth()-40) {
            horizontalSpeed *= -1;
        }
        entity.translateX(horizontalSpeed);

        //The dinosaur shoots depending on the absolute value of its own speed on the current Level

        if (shootTimer.elapsed(Duration.seconds(levelManager.getEnemySpawnRate()*1.3)))
        {
            shoot();
            shootTimer.capture();
        }
    }

    /**
     * Summary :
     *      This handles with the shooting of the dinosaur and spawning of the new bullet
     */
    @Override
    public void shoot() {
        if(!isMuted) {
            FXGL.play(GameConstants.ENEMY_SHOOT_SOUND);
        }
        Point2D center = entity.getCenter();
        Vec2 direction = Vec2.fromAngle(entity.getRotation() + 90 + random(-45,45));
        spawn("basicEnemyProjectile",
                new SpawnData(center.getX() + 50 +3, center.getY())
                        .put("direction", direction.toPoint2D() )
        );
    }

    /**
     * Summary :
     * This method damages the red dino
     *
     * @param damage
     */
    public void damage(int damage) {
        lives -= damage;
    }
}
