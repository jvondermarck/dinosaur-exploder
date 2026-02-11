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

public class PlayerOrangeDinoCollision implements CollisionHandlerInterface {

  private final GameActions gameActions;

  public PlayerOrangeDinoCollision(GameActions gameActions) {
    this.gameActions = gameActions;
  }

  @Override
  public void register() {
    onCollisionBegin(
        EntityType.PLAYER,
        EntityType.ORANGE_DINO,
        (player, orangeDino) -> {
          AudioManager.getInstance().playSound(GameConstants.PLAYER_HIT_SOUND);
          System.out.println("You touched a orange dino !");
          gameActions.damagePlayer();
        });
  }
}
