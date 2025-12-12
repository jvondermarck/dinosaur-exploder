package com.dinosaur.dinosaurexploder.components;

import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.interfaces.Player;
import com.dinosaur.dinosaurexploder.model.GameData;
import com.dinosaur.dinosaurexploder.utils.AudioManager;
import com.dinosaur.dinosaurexploder.utils.FXGLGameTimer;
import com.dinosaur.dinosaurexploder.utils.GameTimer;
import com.dinosaur.dinosaurexploder.view.DinosaurGUI;
import java.util.Objects;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.util.Duration;

public class PlayerComponent extends Component implements Player {
  private static final double MAX_WEAPON_HEAT = 100.0;
  private static final double HEAT_PER_SHOT = 12.0;
  private static final double COOLING_RATE_PER_SECOND =
      30.0; // these variables can be adjusted as needed
  private static final double SLOWDOWN_THRESHOLD = 90.0;
  private static final double SLOWED_SHOT_COOLDOWN_SECONDS = 0.28;

  private final int selectedShip = GameData.getSelectedShip();
  private final int selectedWeapon = GameData.getSelectedWeapon();
  String shipImagePath = "assets/textures/spaceship" + selectedShip + ".png";
  String weaponImagePath =
      "/assets/textures/projectiles/projectile" + selectedShip + "_" + selectedWeapon + ".png";
  int movementSpeed = 8;
  private boolean isInvincible = false;
  private double weaponHeat = 0.0;
  private final GameTimer shootTimer;

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
  }

  @Override
  public void onUpdate(double tpf) {
    coolWeapon(tpf);
  }

  // entity is not initialized anywhere because it is linked in the factory
  /** Summary : This method is overriding the superclass method to limit the upSide movement. */
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
    Image projImg =
        new Image(Objects.requireNonNull(getClass().getResourceAsStream(weaponImagePath)));

    spawn(
        "basicProjectile",
        new SpawnData(
                center.getX() - (projImg.getWidth() / 2) + 3, center.getY() - 25) // Ajusta según el
            // tamaño de la nave
            .put("direction", direction.toPoint2D()));
    increaseWeaponHeat();
    shootTimer.capture();
  }

  private void spawnMovementAnimation() {
    Image spcshpImg = new Image(shipImagePath);
    FXGL.entityBuilder()
        .at(getEntity().getCenter().subtract(spcshpImg.getWidth() / 2, spcshpImg.getHeight() / 2))
        .view(new Texture(spcshpImg))
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

  // Getter for weapon heat for fron end feature
  public double getWeaponHeat() {
    return weaponHeat;
  }

  public double getWeaponHeatPercentage() {
    return (weaponHeat / MAX_WEAPON_HEAT) * 100.0;
  }
}
