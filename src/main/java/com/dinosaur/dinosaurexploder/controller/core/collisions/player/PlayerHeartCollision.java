/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.controller.core.collisions.player;

import static com.almasb.fxgl.dsl.FXGL.onCollisionBegin;

import com.dinosaur.dinosaurexploder.constants.EntityType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.controller.core.GameActions;
import com.dinosaur.dinosaurexploder.controller.core.collisions.CollisionHandlerInterface;
import com.dinosaur.dinosaurexploder.utils.AudioManager;
import java.util.logging.Logger;

public class PlayerHeartCollision implements CollisionHandlerInterface {

  private final GameActions gameActions;
  private Logger logger = Logger.getLogger(getClass().getName());

  public PlayerHeartCollision(GameActions gameActions) {
    this.gameActions = gameActions;
  }

  @Override
  public void register() {
    onCollisionBegin(
        EntityType.PLAYER,
        EntityType.HEART,
        (player, heart) -> {
          AudioManager.getInstance().playSound(GameConstants.HEART_HIT_SOUND);
          heart.removeFromWorld();
          logger.info("You touched a heart!");
          gameActions.healPlayer();
        });
  }
}
