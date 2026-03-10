/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.controller.core.collisions.Player;

import static com.almasb.fxgl.dsl.FXGL.onCollisionBegin;

import com.almasb.fxgl.entity.Entity;
import com.dinosaur.dinosaurexploder.components.LifeComponent;
import com.dinosaur.dinosaurexploder.constants.EntityType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.controller.core.GameInitializer;
import com.dinosaur.dinosaurexploder.controller.core.collisions.CollisionHandlerInterface;
import com.dinosaur.dinosaurexploder.utils.AudioManager;
import java.util.logging.Logger;

public class PlayerHeartCollision implements CollisionHandlerInterface {

  private final Entity life;
  private Logger logger = Logger.getLogger(getClass().getName());

  public PlayerHeartCollision(GameInitializer gameInitializer) {
    this.life = gameInitializer.getLife();
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
          life.getComponent(LifeComponent.class).increaseLife(1);
        });
  }
}
