/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.controller.core.collisions.Ally;

import static com.almasb.fxgl.dsl.FXGL.onCollisionBegin;

import com.dinosaur.dinosaurexploder.constants.EntityType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.controller.core.GameActions;
import com.dinosaur.dinosaurexploder.controller.core.collisions.CollisionHandlerInterface;
import com.dinosaur.dinosaurexploder.utils.AudioManager;

public class AllyRedDinoCollision implements CollisionHandlerInterface {

  private final GameActions gameActions;

  public AllyRedDinoCollision(GameActions gameActions) {
    this.gameActions = gameActions;
  }

  @Override
  public void register() {
    onCollisionBegin(
        EntityType.ALLY,
        EntityType.RED_DINO,
        (ally, redDino) -> {
          // Maybe we could add a different for when the ally is damage
          AudioManager.getInstance().playSound(GameConstants.PLAYER_HIT_SOUND);
          gameActions.damageAlly();
        });
  }
}
