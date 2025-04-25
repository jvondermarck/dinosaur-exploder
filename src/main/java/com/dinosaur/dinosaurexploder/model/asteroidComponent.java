package com.dinosaur.dinosaurexploder.model;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;
/**
 * Summary :
 *      This class extends Component and Implements the Dinosaur Classes and Handles updating an asteroid
 */
public class asteroidComponent extends Component implements Dinosaur{
    double verticalSpeed = 1;
    private boolean isPaused = false;
    private boolean isMuted = false;

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    @Override
    public void onAdded(){
        //Get the current enemy speed from the level manager
        LevelManager levelManager = FXGL.<LevelManager>geto("levelManager");
        verticalSpeed = levelManager.getEnemySpeed();
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
        entity.translateY(verticalSpeed);
    }
    
    /**
     * Summary :
     *      This method controls the projectiles that come from the asteroid after it explodes
     */
    @Override
    public void shoot() {
        if(!isMuted) {
            FXGL.play(GameConstants.ENEMYSHOOT_SOUND);
        }
        Point2D center = entity.getCenter();
        Vec2 direction = Vec2.fromAngle(entity.getRotation() +90);
        spawn("basicEnemyProjectile",
                new SpawnData(center.getX() + 50 +3, center.getY())
                        .put("direction", direction.toPoint2D() )
        );
    }
}
