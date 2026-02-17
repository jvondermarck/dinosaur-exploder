/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.controller.core.collisions;

import static com.almasb.fxgl.dsl.FXGL.onCollisionBegin;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

import com.dinosaur.dinosaurexploder.constants.EntityType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.utils.AudioManager;

public class ProjectileEnemyProjectileCollision implements CollisionHandlerInterface {
  @Override
  public void register() {
    onCollisionBegin(
        EntityType.PROJECTILE,
        EntityType.ENEMY_PROJECTILE,
        (projectile, enemyProjectile) -> {
          spawn("explosion", enemyProjectile.getX() - 25, enemyProjectile.getY() - 30);
          AudioManager.getInstance().playSound(GameConstants.ENEMY_EXPLODE_SOUND);
          projectile.removeFromWorld();
          enemyProjectile.removeFromWorld();
        });
  }
}
