/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.components;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.almasb.fxgl.dsl.EntityBuilder;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.FXGLForKtKt;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.time.Timer;
import com.almasb.fxgl.time.TimerAction;
import com.dinosaur.dinosaurexploder.utils.AudioManager;
import com.dinosaur.dinosaurexploder.utils.MockGameTimer;
import com.dinosaur.dinosaurexploder.view.DinosaurGUI;
import java.lang.reflect.Field;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.util.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

class PlayerComponentTest {

  private PlayerComponent player;
  private Entity playerEntity;
  private MockGameTimer gameTimer;
  private static final int PLAYER_STARTING_COORDS = 100;

  private static final double WEAPON_HEAT_PER_SHOT = 12.0;
  private static final double SLOWDOWN_THRESHOLD = 90.0;
  private static final double SLOWED_SHOT_COOLDOWN_MILLIS = 280;

  @BeforeEach
  void setUp() {
    gameTimer = new MockGameTimer();
    player = new PlayerComponent(gameTimer);

    playerEntity = new Entity();
    playerEntity.setPosition(PLAYER_STARTING_COORDS, PLAYER_STARTING_COORDS);
    playerEntity.addComponent(player);
  }

  private void stubMovementAnimation(MockedStatic<FXGL> fxgl) {
    EntityBuilder animationBuilder = mock(EntityBuilder.class);
    when(animationBuilder.at(any(Point2D.class))).thenReturn(animationBuilder);
    when(animationBuilder.view(any(Node.class))).thenReturn(animationBuilder);
    when(animationBuilder.with(any(ExpireCleanComponent.class))).thenReturn(animationBuilder);

    fxgl.when(FXGL::entityBuilder).thenReturn(animationBuilder);
  }

  private AllyComponent attachMockAlly() {
    AllyComponent ally = mock(AllyComponent.class);
    player.setAlly(ally);
    return ally;
  }

  /* ******************************************************
   *   MOVEMENT TESTS
   ******************************************************** */

  /** Verifies that moving up decreases Y by PlayerComponent.movementSpeed without changing X. */
  @Test
  @DisplayName("Moving up should decrease Y by PlayerComponent.movementSpeed and leave X unchanged")
  void shouldMoveUpByMovementSpeed() {
    try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class)) {
      stubMovementAnimation(fxgl);

      player.moveUp();

      assertEquals(PLAYER_STARTING_COORDS, playerEntity.getX(), 0.001);
      assertEquals(PLAYER_STARTING_COORDS - player.movementSpeed, playerEntity.getY(), 0.001);
    }
  }

  /** Verifies that moving down increases Y by PlayerComponent.movementSpeed without changing X. */
  @Test
  @DisplayName(
      "Moving down should increase Y by PlayerComponent.movementSpeed and leave X unchanged")
  void shouldMoveDownByMovementSpeed() {
    try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class)) {
      stubMovementAnimation(fxgl);

      player.moveDown();

      assertEquals(PLAYER_STARTING_COORDS, playerEntity.getX(), 0.001);
      assertEquals(PLAYER_STARTING_COORDS + player.movementSpeed, playerEntity.getY(), 0.001);
    }
  }

  /** Verifies that moving right increases X by PlayerComponent.movementSpeed without changing Y. */
  @Test
  @DisplayName(
      "Moving right should increase X by PlayerComponent.movementSpeed and leave Y unchanged")
  void shouldMoveRightByMovementSpeed() {

    try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class)) {
      stubMovementAnimation(fxgl);

      player.moveRight();

      assertEquals(PLAYER_STARTING_COORDS, playerEntity.getY(), 0.001);
      assertEquals(PLAYER_STARTING_COORDS + player.movementSpeed, playerEntity.getX(), 0.001);
    }
  }

  /** Verifies that moving left decreases X by PlayerComponent.movementSpeed without changing Y. */
  @Test
  @DisplayName(
      "Moving left should decrease X by PlayerComponent.movementSpeed and leave Y unchanged")
  void shouldMoveLeftByMovementSpeed() {
    try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class)) {
      stubMovementAnimation(fxgl);

      player.moveLeft();

      assertEquals(PLAYER_STARTING_COORDS, playerEntity.getY(), 0.001);
      assertEquals(PLAYER_STARTING_COORDS - player.movementSpeed, playerEntity.getX(), 0.001);
    }
  }

  /** Verifies that moving up also affects an ally. */
  @Test
  @DisplayName("Moving up should instruct allies to move as well")
  void shouldMoveAllyUpByMovementSpeed() {
    try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class)) {
      stubMovementAnimation(fxgl);

      AllyComponent ally = attachMockAlly();
      player.moveUp();

      verify(ally).moveUp(player.movementSpeed);
    }
  }

  /** Verifies that moving down also affects allies. */
  @Test
  @DisplayName("Moving down should instruct allies to move as well")
  void shouldMoveAllyDownByMovementSpeed() {
    try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class)) {
      stubMovementAnimation(fxgl);

      AllyComponent ally = attachMockAlly();
      player.moveDown();

      verify(ally).moveDown(player.movementSpeed);
    }
  }

  /** Verifies that moving right also affects allies. */
  @Test
  @DisplayName("Moving right should instruct allies to move as well")
  void shouldMoveAllyRightByMovementSpeed() {
    try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class)) {
      stubMovementAnimation(fxgl);

      AllyComponent ally = attachMockAlly();
      player.moveRight();

      verify(ally).moveRight(player.movementSpeed);
    }
  }

  /** Verifies that moving left also affects allies. */
  @Test
  @DisplayName("Moving left should instruct allies to move as well")
  void shouldMoveAllyLeftByMovementSpeed() {
    try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class)) {
      stubMovementAnimation(fxgl);

      AllyComponent ally = attachMockAlly();
      player.moveLeft();

      verify(ally).moveLeft(player.movementSpeed);
    }
  }

  /** Verifies that moving left to 0 X coordinate is possible. */
  @Test
  @DisplayName("Moving left to 0 X should work")
  void shouldBeAbleToMoveToZeroX() {
    try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class)) {
      stubMovementAnimation(fxgl);

      playerEntity.setX(player.movementSpeed);
      player.moveLeft();

      assertEquals(0, playerEntity.getX(), 0.001);
    }
  }

  /** Verifies that moving up to 0 Y coordinate is possible. */
  @Test
  @DisplayName("Moving up to 0 Y should work")
  void shouldBeAbleToMoveToZeroY() {
    try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class)) {
      stubMovementAnimation(fxgl);

      playerEntity.setY(player.movementSpeed);
      player.moveUp();

      assertEquals(0, playerEntity.getY(), 0.001);
    }
  }

  /** Verifies that moving down to max Y coordinate is possible. */
  @Test
  @DisplayName("Moving down to max Y should work")
  void shouldBeAbleToMoveToMaxY() {
    try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class)) {
      stubMovementAnimation(fxgl);

      playerEntity.setY(DinosaurGUI.HEIGHT - player.movementSpeed);
      player.moveDown();

      assertEquals(DinosaurGUI.HEIGHT, playerEntity.getY(), 0.001);
    }
  }

  /** Verifies that moving right to max X coordinate is possible. */
  @Test
  @DisplayName("Moving right to max X should work")
  void shouldBeAbleToMoveToMaxX() {
    try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class)) {
      stubMovementAnimation(fxgl);

      playerEntity.setX(DinosaurGUI.WIDTH - player.movementSpeed);
      player.moveRight();

      assertEquals(DinosaurGUI.WIDTH, playerEntity.getX(), 0.001);
    }
  }

  /** Verifies that moving past max X coordinate is not possible. */
  @Test
  @DisplayName("Moving past max X should not work")
  void shouldNotBeAbleToMovePastMaxX() {
    try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class)) {
      stubMovementAnimation(fxgl);

      playerEntity.setX(DinosaurGUI.WIDTH - player.movementSpeed + 1);
      player.moveRight();

      assertEquals(DinosaurGUI.WIDTH, playerEntity.getX(), 0.001);
    }
  }

  /** Verifies that moving past max Y coordinate is not possible. */
  @Test
  @DisplayName("Moving past max Y should not work")
  void shouldNotBeAbleToMovePastMaxY() {
    try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class)) {
      stubMovementAnimation(fxgl);

      playerEntity.setY(DinosaurGUI.HEIGHT - player.movementSpeed + 1);
      player.moveDown();

      assertEquals(DinosaurGUI.HEIGHT, playerEntity.getY(), 0.001);
    }
  }

  /** Verifies that moving below 0 X coordinate is not possible. */
  @Test
  @DisplayName("Moving below zero X should not work")
  void shouldNotBeAbleToMoveBelowZeroX() {
    try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class)) {
      stubMovementAnimation(fxgl);

      playerEntity.setX(player.movementSpeed - 1);
      player.moveLeft();

      assertEquals(0, playerEntity.getX(), 0.001);
    }
  }

  /** Verifies that moving below 0 Y coordinate is not possible. */
  @Test
  @DisplayName("Moving below zero Y should not work")
  void shouldNotBeAbleToMoveBelowZeroY() {
    try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class)) {
      stubMovementAnimation(fxgl);

      playerEntity.setY(player.movementSpeed - 1);
      player.moveUp();

      assertEquals(0, playerEntity.getY(), 0.001);
    }
  }

  /** Verifies that attempting to move past max X coordinates does not move the ally. */
  @Test
  @DisplayName("Trying to move past max X should not affect ally")
  void allyShouldNotBeMovedPastMaxX() {
    try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class)) {
      stubMovementAnimation(fxgl);

      AllyComponent ally = attachMockAlly();

      playerEntity.setX(DinosaurGUI.WIDTH);
      player.moveRight();

      verify(ally, never()).moveRight(player.movementSpeed);
    }
  }

  /** Verifies that attempting to move past max Y coordinates does not move the ally. */
  @Test
  @DisplayName("Trying to move past max Y should not affect ally")
  void allyShouldNotBeMovedPastMaxY() {
    try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class)) {
      stubMovementAnimation(fxgl);

      AllyComponent ally = attachMockAlly();

      playerEntity.setY(DinosaurGUI.HEIGHT);
      player.moveDown();

      verify(ally, never()).moveDown(player.movementSpeed);
    }
  }

  /** Verifies that attempting to move below 0 Y does not move the ally. */
  @Test
  @DisplayName("Trying to move below 0 Y should not affect ally")
  void allyShouldNotBeMovedBelowZeroY() {
    try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class)) {
      stubMovementAnimation(fxgl);

      AllyComponent ally = attachMockAlly();

      playerEntity.setY(0);
      player.moveUp();

      verify(ally, never()).moveUp(player.movementSpeed);
    }
  }

  /** Verifies that attempting to move below 0 X does not move the ally. */
  @Test
  @DisplayName("Trying to move below 0 X should not affect ally")
  void allyShouldNotBeMovedBelowZeroX() {
    try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class)) {
      stubMovementAnimation(fxgl);

      AllyComponent ally = attachMockAlly();

      playerEntity.setX(0);
      player.moveLeft();

      verify(ally, never()).moveLeft(player.movementSpeed);
    }
  }

  /* ******************************************************
   *   SHOOTING TESTS
   ******************************************************** */

  private void stubShootingAudio(MockedStatic<AudioManager> audio) {
    AudioManager audioManager = mock(AudioManager.class);
    audio.when(AudioManager::getInstance).thenReturn(audioManager);
  }

  private void setWeaponHeat(double heat) throws Exception {
    Field field = PlayerComponent.class.getDeclaredField("weaponHeat");
    field.setAccessible(true);
    field.setDouble(player, heat);
  }

  /** Verifies that shooting from zero heat spawns a projectile and increases heat by 12. */
  @Test
  @DisplayName("Shooting from zero heat should spawn a projectile and increase heat by 12")
  void shouldSpawnProjectileAndIncreaseHeatWhenWeaponIsCold() {
    try (MockedStatic<AudioManager> audio = mockStatic(AudioManager.class);
        MockedStatic<FXGLForKtKt> fxgl = mockStatic(FXGLForKtKt.class)) {

      stubShootingAudio(audio);

      player.shoot();

      fxgl.verify(() -> FXGLForKtKt.spawn(eq("basicProjectile"), any(SpawnData.class)));
      assertEquals(WEAPON_HEAT_PER_SHOT, player.getWeaponHeat(), 0.001);
    }
  }

  /** Verifies that shooting at 90 heat just before cooldown expires has no shooting effects. */
  @Test
  @DisplayName("Shooting at 90 heat just before cooldown expires should be blocked")
  void shouldBlockShootingAtHeatThresholdBeforeCooldown() throws Exception {
    setWeaponHeat(SLOWDOWN_THRESHOLD);
    gameTimer.advance(Duration.millis(279));

    try (MockedStatic<AudioManager> audio = mockStatic(AudioManager.class);
        MockedStatic<FXGLForKtKt> fxgl = mockStatic(FXGLForKtKt.class)) {

      player.shoot();

      fxgl.verifyNoInteractions();
      audio.verifyNoInteractions();
      assertEquals(SLOWDOWN_THRESHOLD, player.getWeaponHeat(), 0.001);
    }
  }

  /** Verifies that shooting at 89 heat should not be affected by a cooldown. */
  @Test
  @DisplayName("Shooting at 89 heat should be allowed without waiting for a cooldown")
  void shouldAllowShootingAtHeat89WithoutCooldown() throws Exception {
    setWeaponHeat(SLOWDOWN_THRESHOLD - 1);

    try (MockedStatic<AudioManager> audio = mockStatic(AudioManager.class);
        MockedStatic<FXGLForKtKt> fxgl = mockStatic(FXGLForKtKt.class)) {

      stubShootingAudio(audio);

      player.shoot();

      fxgl.verify(() -> FXGLForKtKt.spawn(eq("basicProjectile"), any(SpawnData.class)));
      assertEquals(100.0, player.getWeaponHeat(), 0.001);
    }
  }

  /** Verifies that shooting at 90 heat is allowed once the 0.28-second cooldown expires. */
  @Test
  @DisplayName("Shooting at 90 heat after 280 ms should spawn a projectile and cap heat at 100")
  void shouldAllowShootingAtHeatThresholdWhenCooldownExpires() throws Exception {

    setWeaponHeat(SLOWDOWN_THRESHOLD);
    gameTimer.advance(Duration.millis(SLOWED_SHOT_COOLDOWN_MILLIS));

    try (MockedStatic<AudioManager> audio = mockStatic(AudioManager.class);
        MockedStatic<FXGLForKtKt> fxgl = mockStatic(FXGLForKtKt.class)) {

      stubShootingAudio(audio);

      player.shoot();

      fxgl.verify(() -> FXGLForKtKt.spawn(eq("basicProjectile"), any(SpawnData.class)));
      assertEquals(100.0, player.getWeaponHeat(), 0.001);
    }
  }

  /**
   * Verifies that a successful shot with heat over threshold resets the cooldown before another
   * shot is allowed.
   */
  @Test
  @DisplayName("A successful hot shot should restart the 280 ms cooldown")
  void shouldResetCooldownAfterSuccessfulHotShot() throws Exception {
    setWeaponHeat(SLOWDOWN_THRESHOLD);
    gameTimer.advance(Duration.millis(SLOWED_SHOT_COOLDOWN_MILLIS));

    try (MockedStatic<AudioManager> audio = mockStatic(AudioManager.class);
        MockedStatic<FXGLForKtKt> fxgl = mockStatic(FXGLForKtKt.class)) {

      stubShootingAudio(audio);

      player.shoot();

      fxgl.verify(() -> FXGLForKtKt.spawn(eq("basicProjectile"), any(SpawnData.class)), times(1));

      // should be blocked by cooldown timer
      player.shoot();

      fxgl.verify(() -> FXGLForKtKt.spawn(eq("basicProjectile"), any(SpawnData.class)), times(1));

      gameTimer.advance(Duration.millis(SLOWED_SHOT_COOLDOWN_MILLIS));

      // should be allowed since cooldown is done
      player.shoot();

      fxgl.verify(() -> FXGLForKtKt.spawn(eq("basicProjectile"), any(SpawnData.class)), times(2));
    }
  }

  /** Verifies that an allowed player shot also instructs the ally to shoot. */
  @Test
  @DisplayName("An allowed player shot should also make the ally shoot")
  void shouldMakeAllyShootWhenPlayerShoots() {
    AllyComponent ally = attachMockAlly();

    try (MockedStatic<AudioManager> audio = mockStatic(AudioManager.class);
        MockedStatic<FXGLForKtKt> fxgl = mockStatic(FXGLForKtKt.class)) {

      stubShootingAudio(audio);

      player.shoot();

      fxgl.verify(() -> FXGLForKtKt.spawn(eq("basicProjectile"), any(SpawnData.class)));
      verify(ally).shoot();
    }
  }

  /** Verifies that a blocked player shot does not instruct the ally to shoot. */
  @Test
  @DisplayName("A player shot blocked by cooldown should not make the ally shoot")
  void shouldNotMakeAllyShootWhenPlayerShotIsBlocked() throws Exception {
    AllyComponent ally = attachMockAlly();
    setWeaponHeat(SLOWDOWN_THRESHOLD);
    gameTimer.advance(Duration.millis(SLOWED_SHOT_COOLDOWN_MILLIS - 1));

    try (MockedStatic<FXGLForKtKt> fxgl = mockStatic(FXGLForKtKt.class)) {

      player.shoot();

      fxgl.verifyNoInteractions();
      verify(ally, never()).shoot();
    }
  }

  /* ******************************************************
   *   SHIELD TESTS
   ******************************************************** */

  private PlayerComponent createPlayerWithBlinkingDisabled() {
    PlayerComponent shieldPlayer = spy(player);
    doNothing().when(shieldPlayer).startBlinking();
    return shieldPlayer;
  }

  /** Verifies that activating a ready shield grants three seconds of invincibility. */
  @Test
  @DisplayName("Activating a ready shield should grant invincibility for three seconds")
  void shouldActivateShieldWhenReady() {
    // use spy to suppress blinking
    PlayerComponent shieldPlayer = createPlayerWithBlinkingDisabled();

    Timer timer = mock(Timer.class);

    try (MockedStatic<FXGLForKtKt> fxgl = mockStatic(FXGLForKtKt.class)) {
      fxgl.when(FXGLForKtKt::getGameTimer).thenReturn(timer);

      shieldPlayer.activateShield();

      assertTrue(shieldPlayer.isShieldActive());
      assertTrue(shieldPlayer.isInvincible());
      assertEquals(3.0, shieldPlayer.getShieldTimeLeft(), 0.001);
      assertEquals(0.0, shieldPlayer.getShieldCooldownLeft(), 0.001);
    }
  }

  /** Verifies that reactivating an active shield does not reset its remaining duration. */
  @Test
  @DisplayName("Reactivating an active shield should preserve its remaining duration")
  void shouldNotRestartShieldWhileActive() throws Exception {
    PlayerComponent shieldPlayer = createPlayerWithBlinkingDisabled();
    Timer timer = mock(Timer.class);

    try (MockedStatic<FXGLForKtKt> fxgl = mockStatic(FXGLForKtKt.class)) {
      fxgl.when(FXGLForKtKt::getGameTimer).thenReturn(timer);

      shieldPlayer.activateShield();

      Field remainingTime = PlayerComponent.class.getDeclaredField("shieldTimeLeft");
      remainingTime.setAccessible(true);
      remainingTime.setDouble(shieldPlayer, 2.9);

      shieldPlayer.activateShield();

      assertEquals(2.9, shieldPlayer.getShieldTimeLeft(), 0.001);
    }
  }

  /** Verifies that shield expiry ends protection and starts the eight-second cooldown. */
  @Test
  @DisplayName("Shield expiry should end invincibility and start an eight-second cooldown")
  void shouldEndProtectionAndStartCooldownWhenShieldExpires() {
    PlayerComponent shieldPlayer = createPlayerWithBlinkingDisabled();

    Timer timer = mock(Timer.class);
    TimerAction shieldAction = mock(TimerAction.class);
    when(timer.runAtInterval(any(Runnable.class), eq(Duration.seconds(0.1))))
        .thenReturn(shieldAction);

    ArgumentCaptor<Runnable> callback = ArgumentCaptor.forClass(Runnable.class);

    try (MockedStatic<FXGLForKtKt> fxgl = mockStatic(FXGLForKtKt.class)) {
      fxgl.when(FXGLForKtKt::getGameTimer).thenReturn(timer);

      shieldPlayer.activateShield();

      verify(timer).runAtInterval(callback.capture(), eq(Duration.seconds(0.1)));
      Runnable shieldTick = callback.getValue();

      for (int i = 0; i < 30; i++) {
        shieldTick.run();
      }

      assertFalse(shieldPlayer.isShieldActive());
      assertFalse(shieldPlayer.isInvincible());
      assertEquals(8.0, shieldPlayer.getShieldCooldownLeft(), 0.001);
    }
  }

  /** Verifies that the shield cannot activate while its cooldown remains. */
  @Test
  @DisplayName("Shield activation during cooldown should leave the player unprotected")
  void shouldBlockShieldActivationDuringCooldown() throws Exception {
    PlayerComponent shieldPlayer = createPlayerWithBlinkingDisabled();

    Field cooldown = PlayerComponent.class.getDeclaredField("shieldCooldownLeft");
    cooldown.setAccessible(true);
    cooldown.setDouble(shieldPlayer, 0.1);

    shieldPlayer.activateShield();

    assertFalse(shieldPlayer.isShieldActive());
    assertFalse(shieldPlayer.isInvincible());
  }

  /* ******************************************************
   *   ALLY TESTS
   ******************************************************** */

  /** Verifies that an ally with remaining duration is retained. */
  @Test
  @DisplayName("An ally with remaining duration should remain attached")
  void shouldRetainAllyWithRemainingDuration() {
    AllyComponent ally = attachMockAlly();
    Entity allyEntity = mock(Entity.class);
    when(ally.getEntity()).thenReturn(allyEntity);
    when(ally.getDuration()).thenReturn(1.0);

    player.onUpdate(0.0);

    assertSame(ally, player.getAlly());
    verify(allyEntity, never()).removeFromWorld();
  }

  /** Verifies that an ally is removed when its duration reaches zero. */
  @Test
  @DisplayName("An ally with zero duration should be removed")
  void shouldRemoveAllyWithZeroDuration() {
    AllyComponent ally = attachMockAlly();
    Entity allyEntity = mock(Entity.class);
    when(ally.getEntity()).thenReturn(allyEntity);
    when(ally.getDuration()).thenReturn(0.0);

    player.onUpdate(0.0);

    verify(allyEntity).removeFromWorld();
    assertNull(player.getAlly());
  }
}
