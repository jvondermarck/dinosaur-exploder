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

public class PlayerGreenDinoCollision implements CollisionHandlerInterface {

  private final GameActions gameActions;
  private Logger logger = Logger.getLogger(getClass().getName());

  public PlayerGreenDinoCollision(GameActions gameActions) {
    this.gameActions = gameActions;
  }

  @Override
  public void register() {
    onCollisionBegin(
        EntityType.PLAYER,
        EntityType.GREEN_DINO,
        (player, greenDino) -> {
          AudioManager.getInstance().playSound(GameConstants.PLAYER_HIT_SOUND);
          greenDino.removeFromWorld();
          logger.info("You touched a dino !");
          gameActions.damagePlayer();
        });
  }
}
