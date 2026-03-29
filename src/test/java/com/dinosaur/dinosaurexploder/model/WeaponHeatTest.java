/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.model;

import static org.junit.jupiter.api.Assertions.*;

import com.dinosaur.dinosaurexploder.components.PlayerComponent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

class WeaponHeatTest {

  // Helper to call a private no-arg method on PlayerComponent
  private void invokePrivateNoArg(PlayerComponent pc, String methodName, Class<?>... paramTypes)
      throws Exception {
    Method m = PlayerComponent.class.getDeclaredMethod(methodName, paramTypes);
    m.setAccessible(true);
    m.invoke(pc);
  }

  private void invokePrivate(PlayerComponent pc, String methodName, Object... args)
      throws Exception {
    Class<?>[] paramTypes = new Class[args.length];
    for (int i = 0; i < args.length; i++) paramTypes[i] = args[i].getClass();
    Method m = PlayerComponent.class.getDeclaredMethod(methodName, paramTypes);
    m.setAccessible(true);
    m.invoke(pc, args);
  }

  private void setWeaponHeat(PlayerComponent pc, double value) throws Exception {
    Field f = PlayerComponent.class.getDeclaredField("weaponHeat");
    f.setAccessible(true);
    f.setDouble(pc, value);
  }

  private double getWeaponHeat(PlayerComponent pc) throws Exception {
    Method m = PlayerComponent.class.getDeclaredMethod("getWeaponHeat");
    m.setAccessible(true);
    return (double) m.invoke(pc);
  }

  @Test
  void testIncreaseWeaponHeatAndMax() throws Exception {
    var pc = new PlayerComponent(new com.dinosaur.dinosaurexploder.utils.MockGameTimer());

    // Call increaseWeaponHeat 10 times -> should cap at MAX_WEAPON_HEAT (100)
    for (int i = 0; i < 10; i++) {
      invokePrivateNoArg(pc, "increaseWeaponHeat");
    }

    double heat = getWeaponHeat(pc);
    assertTrue(heat <= 100.0, "Weapon heat should not exceed 100");
    assertEquals(
        100.0,
        heat,
        0.0001,
        "Weapon heat should be exactly capped at 100 after repeated increases");
  }

  @Test
  void testCoolingOverTime() throws Exception {
    var pc = new PlayerComponent(new com.dinosaur.dinosaurexploder.utils.MockGameTimer());

    // set heat to 80
    setWeaponHeat(pc, 80.0);

    // coolWeapon expects tpf (time per frame). COOLING_RATE_PER_SECOND = 30.0
    // calling coolWeapon with tpf=1.0 should reduce heat by 30
    Method cool = PlayerComponent.class.getDeclaredMethod("coolWeapon", double.class);
    cool.setAccessible(true);
    cool.invoke(pc, 1.0);

    double heatAfter = getWeaponHeat(pc);
    assertEquals(
        50.0, heatAfter, 0.0001, "Weapon should have cooled by 30 after 1s equivalent tpf");
  }

  @Test
  void testCanShootSlowedCooldownBehavior() throws Exception {
    var pc = new PlayerComponent(new com.dinosaur.dinosaurexploder.utils.MockGameTimer());

    // set heat above slowdown threshold
    setWeaponHeat(pc, 95.0);

    // Access private canShoot method
    Method canShoot = PlayerComponent.class.getDeclaredMethod("canShoot");
    canShoot.setAccessible(true);

    // Use the MockGameTimer to control elapsed time
    Field shootTimerField = PlayerComponent.class.getDeclaredField("shootTimer");
    shootTimerField.setAccessible(true);
    com.dinosaur.dinosaurexploder.utils.MockGameTimer mockTimer =
        (com.dinosaur.dinosaurexploder.utils.MockGameTimer) shootTimerField.get(pc);

    // capture at time zero
    mockTimer.capture();

    // Immediately after capture, canShoot() should return false because not enough time elapsed
    boolean immediate = (boolean) canShoot.invoke(pc);
    assertFalse(
        immediate,
        "When weapon heat is above threshold and timer just captured, player should NOT be able to shoot immediately");

    // Advance simulated time slightly more than the slowed cooldown (0.28s)
    mockTimer.advance(javafx.util.Duration.millis(320));

    boolean afterWait = (boolean) canShoot.invoke(pc);
    assertTrue(
        afterWait,
        "After the slowed cooldown time has passed, player should be able to shoot again");
  }
}
