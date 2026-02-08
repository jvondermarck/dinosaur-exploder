package com.dinosaur.dinosaurexploder.components;

import static com.almasb.fxgl.dsl.FXGL.getGameTimer;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

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
import javafx.geometry.Point2D;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class PlayerComponent extends Component implements Player {

  private static final double MAX_WEAPON_HEAT = 100.0;
  private static final double HEAT_PER_SHOT = 12.0;
  private static final double COOLING_RATE_PER_SECOND =
      30.0; // these variables can be adjusted as needed
  private static final double SLOWDOWN_THRESHOLD = 90.0;
  private static final double SLOWED_SHOT_COOLDOWN_SECONDS = 0.28;
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

  // Default constructor used by the game (will create an FXGL-backed timer)
  public PlayerComponent() {
    this.shootTimer = new FXGLGameTimer();
  }

  // Test-friendly constructor to inject a mock GameTimer
  public PlayerComponent(GameTimer shootTimer) {
    this.shootTimer = shootTimer;
  }

  public void setInvincible(boolean invincible) {
    this.isInvincible = invincible;
    if (invincible) {
      entity.getViewComponent().setOpacity(0.5);
    } else {
      entity.getViewComponent().setOpacity(1.0);
    }
  }

  public boolean isInvincible() {
    return isInvincible;
  }

  @Override
  public void onAdded() {
    shootTimer.capture();
    // Load images once when component is added
    // Use resource stream for both images to ensure consistency
    shipImage =
        new Image(Objects.requireNonNull(getClass().getResourceAsStream("/" + shipImagePath)));

    projectileImage =
        new Image(Objects.requireNonNull(getClass().getResourceAsStream(weaponImagePath)));
  }

  @Override
  public void onUpdate(double tpf) {
    coolWeapon(tpf);
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
      return; // not ready
    }
    // activate
    shieldActive = true;
    shieldTimeLeft = shieldDuration;
    setInvincible(true);

    // create visual - add to entity view so it follows the player
    shieldVisual = new Circle(getEntity().getWidth() / 2 + 10);
    shieldVisual.setFill(Color.color(0.2, 0.6, 1.0, 0.15));
    shieldVisual.setStroke(Color.color(0.2, 0.8, 1.0, 0.9));
    shieldVisual.setStrokeWidth(4);
    shieldVisual.setEffect(new DropShadow(20, Color.CYAN));
    // position relative to entity center
    shieldVisual.setTranslateX(getEntity().getWidth() / 2);
    shieldVisual.setTranslateY(getEntity().getHeight() / 2);
    getEntity().getViewComponent().addChild(shieldVisual);

    // start shield timer: update every 0.1s
    shieldTimerAction =
        getGameTimer()
            .runAtInterval(
                () -> {
                  shieldTimeLeft -= 0.1;
                  if (shieldTimeLeft <= 0) {
                    // expire shield
                    shieldTimerAction.expire();
                    shieldActive = false;
                    setInvincible(false);
                    // remove visual
                    if (shieldVisual != null) {
                      getEntity().getViewComponent().removeChild(shieldVisual);
                      shieldVisual = null;
                    }
                    // start cooldown
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
      System.out.println("Out of bounds");
      return;
    }
    entity.translateY(-movementSpeed);
    spawnMovementAnimation();
  }

  /** Summary : This method is overriding the superclass method to limit the downSide movement. */
  public void moveDown() {
    if (!(entity.getY() < DinosaurGUI.HEIGHT - entity.getHeight())) {
      System.out.println("Out of bounds");
      return;
    }
    entity.translateY(movementSpeed);
    spawnMovementAnimation();
  }

  /** Summary : This method is overriding the superclass method to limit the rightSide movement. */
  public void moveRight() {
    if (!(entity.getX() < DinosaurGUI.WIDTH - entity.getWidth())) {
      System.out.println("Out of bounds");
      return;
    }
    entity.translateX(movementSpeed);
    spawnMovementAnimation();
  }

  /** Summary : This method is overriding the superclass method to limit the leftSide movement. */
  public void moveLeft() {
    if (entity.getX() < 0) {
      System.out.println("Out of bounds");
      return;
    }
    entity.translateX(-movementSpeed);
    spawnMovementAnimation();
  }

  /**
   * Summary : This method is overriding the superclass method to the shooting from the player and
   * spawning of the new bullet
   */
  public void shoot() {
    if (!canShoot()) { // Implemented cooldown based on weapon heat
      return;
    }

    AudioManager.getInstance().playSound(GameConstants.SHOOT_SOUND);
    Point2D center = entity.getCenter();
    Vec2 direction = Vec2.fromAngle(entity.getRotation() - 90);
    System.out.println("Shoot with selected weapon: " + selectedWeapon);

    spawn(
        "basicProjectile",
        new SpawnData(
                center.getX() - (projectileImage.getWidth() / 2) + 3,
                center.getY() - 25) // adjust Accordingly
            // tamaño de la nave
            .put("direction", direction.toPoint2D()));
    increaseWeaponHeat();
    shootTimer.capture();
  }

  private void spawnMovementAnimation() {
    FXGL.entityBuilder()
        .at(getEntity().getCenter().subtract(shipImage.getWidth() / 2, shipImage.getHeight() / 2))
        .view(new Texture(shipImage))
        .with(new ExpireCleanComponent(Duration.seconds(0.15)).animateOpacity())
        .buildAndAttach();
  }

  // Check if the player can shoot based on weapon heat and cooldown

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
    // clean up timer actions to prevent memory leaks
    if (shieldTimerAction != null && !shieldTimerAction.isExpired()) {
      shieldTimerAction.expire();
    }
    if (shieldCooldownAction != null && !shieldCooldownAction.isExpired()) {
      shieldCooldownAction.expire();
    }
  }

  // Getter for weapon heat for fron end feature
  public double getWeaponHeat() {
    return weaponHeat;
  }

  public double getWeaponHeatPercentage() {
    return (weaponHeat / MAX_WEAPON_HEAT) * 100.0;
  }
}
