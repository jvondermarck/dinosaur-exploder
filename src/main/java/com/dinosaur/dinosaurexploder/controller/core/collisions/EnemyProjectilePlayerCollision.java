/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */
package com.dinosaur.dinosaurexploder.controller.core.collisions;

import static com.almasb.fxgl.dsl.FXGL.onCollisionBegin;

import com.dinosaur.dinosaurexploder.constants.EntityType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.controller.core.GameActions;
import com.dinosaur.dinosaurexploder.utils.AudioManager;

public class EnemyProjectilePlayerCollision implements CollisionHandlerInterface {

  private final GameActions gameActions;

  public EnemyProjectilePlayerCollision(GameActions gameActions) {
    this.gameActions = gameActions;
  }

  @Override
  public void register() {
    onCollisionBegin(
        EntityType.ENEMY_PROJECTILE,
        EntityType.PLAYER,
        (projectile, player) -> {
          AudioManager.getInstance().playSound(GameConstants.PLAYER_HIT_SOUND);
          projectile.removeFromWorld();
          System.out.println("You got hit !\n");
          gameActions.damagePlayer();
        });
  }
}
