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
import java.util.logging.Logger;

public class PlayerRedDinoCollision implements CollisionHandlerInterface {

  private final GameActions gameActions;
  private Logger logger = Logger.getLogger(getClass().getName());

  public PlayerRedDinoCollision(GameActions gameActions) {
    this.gameActions = gameActions;
  }

  @Override
  public void register() {
    onCollisionBegin(
        EntityType.PLAYER,
        EntityType.RED_DINO,
        (player, redDino) -> {
          AudioManager.getInstance().playSound(GameConstants.PLAYER_HIT_SOUND);
          logger.info("You touched a red dino !");
          gameActions.damagePlayer();
        });
  }
}
