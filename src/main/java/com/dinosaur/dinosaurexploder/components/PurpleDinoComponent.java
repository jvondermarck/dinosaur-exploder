package com.dinosaur.dinosaurexploder.components;

import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.interfaces.Dinosaur;
import com.dinosaur.dinosaurexploder.utils.AudioManager;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import javafx.geometry.Point2D;
import javafx.util.Duration;

/**
 * Summary : This class extends Component and Implements the Dinosaur Classes and Handles the
 * Shooting and Updating the Dino This purple dino moves in a zigzag pattern (diagonal movement)
 * while descending and shoots more frequently than green dinos
 */
public class PurpleDinoComponent extends Component implements Dinosaur {
  double verticalSpeed = 1.5;
  double horizontalSpeed = 1.5;
  private final LocalTimer timer = FXGL.newLocalTimer();
  private boolean isPaused = false;
  private int lives = 2;
  private int direction = 1; // 1 for right, -1 for left

  public int getLives() {
    return lives;
  }

  public void setPaused(boolean paused) {
    isPaused = paused;
  }

  @Override
  public void onAdded() {
    // Get the current enemy speed from the level manager
    LevelManager levelManager = FXGL.geto("levelManager");
    verticalSpeed = levelManager.getEnemySpeed();
    horizontalSpeed = levelManager.getEnemySpeed() * 0.8;

    // Randomly start going left or right
    direction = Math.random() < 0.5 ? 1 : -1;
  }

  /**
   * Summary : This method runs for every frame like a continues flow , without any stop until we
   * put stop to it. Parameters : double ptf
   */
  @Override
  public void onUpdate(double ptf) {
    if (isPaused) return;

    // Move down and diagonally (zigzag pattern)
    entity.translateY(verticalSpeed);
    entity.translateX(horizontalSpeed * direction);

    // Change direction when hitting screen boundaries
    if (entity.getX() <= 0 || entity.getX() >= FXGL.getAppWidth() - entity.getWidth()) {
      direction *= -1;
    }

    // The dinosaur shoots every 1 second (faster than green dino)
    if (timer.elapsed(Duration.seconds(1.0)) && entity.getPosition().getY() > 0) {
      shoot();
      timer.capture();
    }
  }

  /** Summary : This handles with the shooting of the dinosaur and spawning of the new bullet */
  @Override
  public void shoot() {

    AudioManager.getInstance().playSound(GameConstants.SHOOT_SOUND);

    Point2D center = entity.getCenter();
    Vec2 direction = Vec2.fromAngle(entity.getRotation() + 90);
    spawn(
        "basicEnemyProjectile",
        new SpawnData(center.getX(), center.getY()).put("direction", direction.toPoint2D()));
  }

  public void damage(int damage) {
    lives -= damage;
  }
}
