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

public class PlayerAsteroidsCollision implements CollisionHandlerInterface {

  private final GameActions gameActions;

  public PlayerAsteroidsCollision(GameActions gameActions) {
    this.gameActions = gameActions;
  }

  @Override
  public void register() {
    onCollisionBegin(
        EntityType.PLAYER,
        EntityType.ASTEROIDS,
        (player, asteroids) -> {
          AudioManager.getInstance().playSound(GameConstants.PLAYER_HIT_SOUND);
          asteroids.removeFromWorld();
          System.out.println("You touched an asteroids !");
          gameActions.damagePlayer();
        });
  }
}
