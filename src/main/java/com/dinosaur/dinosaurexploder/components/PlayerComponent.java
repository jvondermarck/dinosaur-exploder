/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.components;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.time.TimerAction;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.interfaces.Player;
import com.dinosaur.dinosaurexploder.model.GameData;
import com.dinosaur.dinosaurexploder.utils.AudioManager;
import com.dinosaur.dinosaurexploder.utils.FXGLGameTimer;
import com.dinosaur.dinosaurexploder.utils.GameTimer;
import com.dinosaur.dinosaurexploder.view.DinosaurGUI;
import java.util.Objects;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class PlayerComponent extends Component implements Player {

  private static final double MAX_WEAPON_HEAT = 100.0;
  private static final double HEAT_PER_SHOT = 12.0;
  private static final double COOLING_RATE_PER_SECOND = 30.0;
  private static final double SLOWDOWN_THRESHOLD = 90.0;
  private static final double SLOWED_SHOT_COOLDOWN_SECONDS = 0.28;
  private static final String OUT_OF_BOUNDS = "Out of bounds";

  // Shield fields
  private boolean shieldActive = false;
  private double shieldTimeLeft = 0;
  private double shieldDuration = 3.0;
  private double shieldCooldownLeft = 0;
  private double shieldCooldown = 8.0;
  private TimerAction shieldTimerAction;
  private TimerAction shieldCooldownAction;
  private Circle shieldVisual;

  private final int selectedShip = GameData.getSelectedShip();
  private final int selectedWeapon = GameData.getSelectedWeapon();
  String shipImagePath = "assets/textures/spaceship" + selectedShip + ".png";
  String weaponImagePath =
          "/assets/textures/projectiles/projectile" + selectedShip + "_" + selectedWeapon + ".png";
  int movementSpeed = 8;
  private boolean isInvincible = false;
  private double weaponHeat = 0.0;
  private final GameTimer shootTimer;

  // cached Images to prevent memory load
  private Image shipImage;
  private Image projectileImage;
  private AllyComponent ally;

  private Logger logger = Logger.getLogger(getClass().getName());

  // ========== ADDED: Blinking animation for invincibility visual feedback ==========
  private AnimationTimer blinkTimer;
  private long blinkStartTime;
  private static final double BLINK_INTERVAL_MS = 100; // Blink every 0.1 seconds
  private static final double TOTAL_BLINK_DURATION_MS = 1500; // Total blink duration 1.5 seconds
  // ========== END ADDED ==========

  // Default constructor used by the game (will create an FXGL-backed timer)
  public PlayerComponent() {
    this.shootTimer = new FXGLGameTimer();
  }

  // Test-friendly constructor to inject a mock GameTimer
  public PlayerComponent(GameTimer shootTimer) {
    this.shootTimer = shootTimer;
  }

  // ========== MODIFIED: Enhanced setInvincible with blinking visual feedback ==========
  public void setInvincible(boolean invincible) {
    this.isInvincible = invincible;
    if (invincible) {
      // Start blinking effect when invincible
      startBlinking();
    } else {
      // Stop blinking and restore full opacity
      stopBlinking();
      entity.getViewComponent().setOpacity(1.0);
    }
  }
  // ========== END MODIFIED ==========

  public boolean isInvincible() {
    return isInvincible;
  }

  // ========== ADDED: Start blinking animation for invincibility feedback ==========
  /**
   * Start blinking animation for invincibility visual feedback.
   * The player ship will flash between transparent and opaque.
   */
  public void startBlinking() {
    // Stop any existing blinking animation
    if (blinkTimer != null) {
      blinkTimer.stop();
    }

    blinkStartTime = System.nanoTime();
    blinkTimer = new AnimationTimer() {
      @Override
      public void handle(long now) {
        long elapsedMs = (now - blinkStartTime) / 1_000_000;

        if (elapsedMs >= TOTAL_BLINK_DURATION_MS) {
          // Blinking finished - note: invincibility may still be active,
          // but blinking stops after 1.5s to avoid infinite flashing
          stopBlinking();
          // Don't reset opacity here - let setInvincible(false) handle it
          return;
        }

        // Calculate opacity: blink every BLINK_INTERVAL_MS
        long cycle = elapsedMs / (long)BLINK_INTERVAL_MS;
        double opacity = (cycle % 2 == 0) ? 0.4 : 1.0;
        entity.getViewComponent().setOpacity(opacity);
      }
    };
    blinkTimer.start();
  }
  // ========== END ADDED ==========

  // ========== ADDED: Stop blinking animation ==========
  /**
   * Stop the blinking animation and restore full opacity.
   */
  public void stopBlinking() {
    if (blinkTimer != null) {
      blinkTimer.stop();
      blinkTimer = null;
    }
    entity.getViewComponent().setOpacity(1.0);
  }
  // ========== END ADDED ==========

  @Override
  public void onAdded() {
    shootTimer.capture();
    shipImage =
            new Image(Objects.requireNonNull(getClass().getResourceAsStream("/" + shipImagePath)));
    projectileImage =
            new Image(Objects.requireNonNull(getClass().getResourceAsStream(weaponImagePath)));
  }

  @Override
  public void onUpdate(double tpf) {
    coolWeapon(tpf);
    if (ally != null && ally.getDuration() <= 0) {
      ally.getEntity().removeFromWorld();
      ally = null;
    }
  }

  public boolean isShieldActive() {
    return shieldActive;
  }

  public double getShieldTimeLeft() {
    return shieldTimeLeft;
  }

  public double getShieldCooldownLeft() {
    return shieldCooldownLeft;
  }

  /** Activate shield ability if ready */
  public void activateShield() {
    if (shieldActive || shieldCooldownLeft > 0) {
      return;
    }
    shieldActive = true;
    shieldTimeLeft = shieldDuration;
    setInvincible(true);

    shieldVisual = new Circle(getEntity().getWidth() / 2 + 10);
    shieldVisual.setFill(Color.color(0.2, 0.6, 1.0, 0.15));
    shieldVisual.setStroke(Color.color(0.2, 0.8, 1.0, 0.9));
    shieldVisual.setStrokeWidth(4);
    shieldVisual.setEffect(new DropShadow(20, Color.CYAN));
    shieldVisual.setTranslateX(getEntity().getWidth() / 2);
    shieldVisual.setTranslateY(getEntity().getHeight() / 2);
    getEntity().getViewComponent().addChild(shieldVisual);

    shieldTimerAction =
            getGameTimer()
                    .runAtInterval(
                            () -> {
                              shieldTimeLeft -= 0.1;
                              if (shieldTimeLeft <= 0) {
                                shieldTimerAction.expire();
                                shieldActive = false;
                                setInvincible(false);
                                if (shieldVisual != null) {
                                  getEntity().getViewComponent().removeChild(shieldVisual);
                                  shieldVisual = null;
                                }
                                shieldCooldownLeft = shieldCooldown;
                                shieldCooldownAction =
                                        getGameTimer()
                                                .runAtInterval(
                                                        () -> {
                                                          shieldCooldownLeft -= 0.1;
                                                          if (shieldCooldownLeft <= 0) {
                                                            shieldCooldownAction.expire();
                                                            shieldCooldownLeft = 0;
                                                          }
                                                        },
                                                        Duration.seconds(0.1));
                              }
                            },
                            Duration.seconds(0.1));
  }

  public void moveUp() {
    if (entity.getY() < 0) {
      logger.info(OUT_OF_BOUNDS);
      return;
    }
    entity.translateY(-movementSpeed);
    if (ally != null) {
      ally.moveUp(movementSpeed);
    }
    spawnMovementAnimation();
  }

  public void moveDown() {
    if (entity.getY() >= DinosaurGUI.HEIGHT - entity.getHeight()) {
      logger.info(OUT_OF_BOUNDS);
      return;
    }
    entity.translateY(movementSpeed);
    if (ally != null) {
      ally.moveDown(movementSpeed);
    }
    spawnMovementAnimation();
  }

  public void moveRight() {
    if (entity.getX() >= DinosaurGUI.WIDTH - entity.getWidth()) {
      logger.info(OUT_OF_BOUNDS);
      return;
    }
    entity.translateX(movementSpeed);
    if (ally != null) {
      ally.moveRight(movementSpeed);
    }
    spawnMovementAnimation();
  }

  public void moveLeft() {
    if (entity.getX() < 0) {
      logger.info(OUT_OF_BOUNDS);
      return;
    }
    entity.translateX(-movementSpeed);
    if (ally != null) {
      ally.moveLeft(movementSpeed);
    }
    spawnMovementAnimation();
  }

  public void shoot() {
    if (!canShoot()) {
      return;
    }

    AudioManager.getInstance().playSound(GameConstants.SHOOT_SOUND);
    Point2D center = entity.getCenter();
    Vec2 direction = Vec2.fromAngle(entity.getRotation() - 90);
    logger.info(() -> String.format("Shoot with selected weapon: %s", selectedWeapon));

    spawn(
            "basicProjectile",
            new SpawnData(
                    center.getX() - (projectileImage.getWidth() / 2) + 3,
                    center.getY() - 25)
                    .put("direction", direction.toPoint2D()));
    if (ally != null) {
      ally.shoot();
    }
    increaseWeaponHeat();
    shootTimer.capture();
  }

  private void spawnMovementAnimation() {
    double scaleX = entity.getScaleX();
    double scaleY = entity.getScaleY();

    Texture texture = new Texture(shipImage);
    texture.setScaleX(scaleX);
    texture.setScaleY(scaleY);
    texture.setRotate(entity.getRotation());

    FXGL.entityBuilder()
            .at(
                    entity
                            .getCenter()
                            .subtract(shipImage.getWidth() * scaleX / 2, shipImage.getHeight() * scaleY / 2))
            .view(texture)
            .with(new ExpireCleanComponent(Duration.seconds(0.15)).animateOpacity())
            .buildAndAttach();
  }

  private boolean canShoot() {
    if (weaponHeat >= SLOWDOWN_THRESHOLD) {
      return shootTimer.isElapsed(Duration.seconds(SLOWED_SHOT_COOLDOWN_SECONDS));
    }
    return true;
  }

  private void increaseWeaponHeat() {
    weaponHeat = Math.min(MAX_WEAPON_HEAT, weaponHeat + HEAT_PER_SHOT);
  }

  private void coolWeapon(double tpf) {
    if (weaponHeat <= 0) {
      weaponHeat = 0;
      return;
    }
    weaponHeat = Math.max(0.0, weaponHeat - (COOLING_RATE_PER_SECOND * tpf));
  }

  @Override
  public void onRemoved() {
    if (shieldTimerAction != null && !shieldTimerAction.isExpired()) {
      shieldTimerAction.expire();
    }
    if (shieldCooldownAction != null && !shieldCooldownAction.isExpired()) {
      shieldCooldownAction.expire();
    }
    // ========== ADDED: Clean up blink timer to prevent memory leaks ==========
    if (blinkTimer != null) {
      blinkTimer.stop();
    }
    // ========== END ADDED ==========
  }

  public double getWeaponHeat() {
    return weaponHeat;
  }

  public double getWeaponHeatPercentage() {
    return (weaponHeat / MAX_WEAPON_HEAT) * 100.0;
  }

  public AllyComponent getAlly() {
    return ally;
  }

  public void setAlly(AllyComponent ally) {
    this.ally = ally;
  }
}