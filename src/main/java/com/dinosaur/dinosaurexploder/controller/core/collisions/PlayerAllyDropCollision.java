/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.controller.core.collisions;

import static com.almasb.fxgl.dsl.FXGL.onCollisionBegin;

import com.almasb.fxgl.entity.Entity;
import com.dinosaur.dinosaurexploder.components.AllyComponent;
import com.dinosaur.dinosaurexploder.components.AllyDropComponent;
import com.dinosaur.dinosaurexploder.components.PlayerComponent;
import com.dinosaur.dinosaurexploder.constants.EntityType;
import com.dinosaur.dinosaurexploder.controller.core.GameInitializer;

public class PlayerAllyDropCollision implements CollisionHandlerInterface {

  private final Entity ally;

  public PlayerAllyDropCollision(GameInitializer gameInitializer) {
    this.ally = gameInitializer.getLife();
  }

  @Override
  public void register() {
    onCollisionBegin(
        EntityType.PLAYER,
        EntityType.ALLY_DROP,
        (playerEntity, allyDrop) -> {
          ally.removeFromWorld();
          Entity ally =
              allyDrop
                  .getComponent(AllyDropComponent.class)
                  .spawnAlly(playerEntity.getX(), playerEntity.getY());
          PlayerComponent player = playerEntity.getComponent(PlayerComponent.class);
          player.setAlly(ally.getComponent(AllyComponent.class));
        });
  }
}
