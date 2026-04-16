/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.controller.core.collisions.player;

import static com.almasb.fxgl.dsl.FXGL.onCollisionBegin;

import com.almasb.fxgl.entity.Entity;
import com.dinosaur.dinosaurexploder.components.AllyComponent;
import com.dinosaur.dinosaurexploder.components.AllyDropComponent;
import com.dinosaur.dinosaurexploder.components.PlayerComponent;
import com.dinosaur.dinosaurexploder.constants.EntityType;
import com.dinosaur.dinosaurexploder.controller.core.GameActions;
import com.dinosaur.dinosaurexploder.controller.core.collisions.CollisionHandlerInterface;

public class PlayerAllyDropCollision implements CollisionHandlerInterface {
  private GameActions gameActions;

  public PlayerAllyDropCollision(GameActions gameActions) {
    this.gameActions = gameActions;
  }

  @Override
  public void register() {
    onCollisionBegin(
        EntityType.PLAYER,
        EntityType.ALLY_DROP,
        (playerEntity, allyDrop) -> {
          PlayerComponent player = playerEntity.getComponent(PlayerComponent.class);
          allyDrop.removeFromWorld();
          if (player.getAlly() == null) {
            Entity ally =
                allyDrop
                    .getComponent(AllyDropComponent.class)
                    .spawnAlly(playerEntity.getX(), playerEntity.getY());
            player.setAlly(ally.getComponent(AllyComponent.class));
            gameActions.setAlly(ally.getComponent(AllyComponent.class));
            gameActions.setAllyUse(true);
          }
        });
  }
}
