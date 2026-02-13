/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.controller.core.collisions;

import static com.almasb.fxgl.dsl.FXGL.onCollisionBegin;

import com.almasb.fxgl.entity.Entity;
import com.dinosaur.dinosaurexploder.components.BombComponent;
import com.dinosaur.dinosaurexploder.components.CollectedCoinsComponent;
import com.dinosaur.dinosaurexploder.components.ScoreComponent;
import com.dinosaur.dinosaurexploder.constants.EntityType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.controller.core.GameInitializer;
import com.dinosaur.dinosaurexploder.model.CollisionHandler;
import com.dinosaur.dinosaurexploder.utils.AudioManager;

public class PlayerCoinCollision implements CollisionHandlerInterface {

  private final CollisionHandler collisionHandler;
  private final CollectedCoinsComponent collectedCoinsComponent;
  private final Entity bomb;
  private final Entity score;

  public PlayerCoinCollision(GameInitializer gameInitializer) {
    this.collisionHandler = gameInitializer.getCollisionHandler();
    this.collectedCoinsComponent = gameInitializer.getCollectedCoinsComponent();
    this.bomb = gameInitializer.getBomb();
    this.score = gameInitializer.getScore();
  }

  @Override
  public void register() {
    onCollisionBegin(
        EntityType.PLAYER,
        EntityType.COIN,
        (player, coin) -> {
          AudioManager.getInstance().playSound(GameConstants.COIN_GAIN);
          coin.removeFromWorld();
          System.out.println("You touched a coin!");
          BombComponent bombComponent = null;
          if (bomb.hasComponent(BombComponent.class))
            bombComponent = bomb.getComponent(BombComponent.class);
          collisionHandler.onPlayerGetCoin(
              collectedCoinsComponent, score.getComponent(ScoreComponent.class), bombComponent);
        });
  }
}
