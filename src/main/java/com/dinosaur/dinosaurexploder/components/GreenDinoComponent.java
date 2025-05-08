package com.dinosaur.dinosaurexploder.components;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.interfaces.Dinosaur;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;
/**
 * Summary :
 *      This class extends Component and Implements the Dinosaur Classes and Handles the Shooting and Updating the Dino
 */
public class GreenDinoComponent extends Component implements Dinosaur {
    double verticalSpeed = 1.5;
    private final LocalTimer timer = FXGL.newLocalTimer();
    private boolean isPaused = false;
    private boolean isMuted = false;
    private LevelManager levelManager;
    private int lastLevel = -1;

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    public void setLevelManager(LevelManager levelManager) {
        this.levelManager = levelManager;
        this.verticalSpeed = levelManager.getEnemySpeed();
        this.lastLevel = levelManager.getCurrentLevel();
    }

    @Override
    public void onAdded(){
        // Get the current enemy speed from the level manager if not already set
        if (levelManager == null) {
            levelManager = FXGL.geto("levelManager");
        }
        verticalSpeed = levelManager.getEnemySpeed();
        lastLevel = levelManager.getCurrentLevel();
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
        // Update speed if level has changed
        if (levelManager != null && levelManager.getCurrentLevel() != lastLevel) {
            verticalSpeed = levelManager.getEnemySpeed();
            lastLevel = levelManager.getCurrentLevel();
        }
        entity.translateY(verticalSpeed);

        //The dinosaur shoots every 2 seconds
        if (timer.elapsed(Duration.seconds(1.5)) && entity.getPosition().getY() > 0)
        {
            shoot();
            timer.capture();
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
        Vec2 direction = Vec2.fromAngle(entity.getRotation() +90);
        spawn("basicEnemyProjectile",
                new SpawnData(center.getX() + 50 +3, center.getY())
                        .put("direction", direction.toPoint2D() )
        );
    }
}
