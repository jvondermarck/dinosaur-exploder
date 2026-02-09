package com.dinosaur.dinosaurexploder.controller;

import com.dinosaur.dinosaurexploder.achievements.AchievementManager;
import com.dinosaur.dinosaurexploder.controller.core.CollisionRegistry;
import com.dinosaur.dinosaurexploder.controller.core.GameActions;
import com.dinosaur.dinosaurexploder.controller.core.GameInitializer;
import com.dinosaur.dinosaurexploder.controller.core.collisions.*;

/**
 * Summary : The Factory handles the Dinosaur , player controls and collision detection of all
 * entities in the game
 */
public class DinosaurController {
  private final GameInitializer gameInitializer;
  private final CollisionRegistry collisionRegistry;
  private GameActions gameActions;

  public DinosaurController() {
    gameInitializer = new GameInitializer();
    collisionRegistry = new CollisionRegistry();
  }

  public void initGame(AchievementManager achievementManager) {
    gameInitializer.setAchievementManager(achievementManager);
    gameInitializer.initGame();
    gameActions = new GameActions(gameInitializer);
    gameActions.updateLevelDisplay();
  }

  public void initInput() {
    gameInitializer.initInput();
  }

  public void initPhysics() {
    if (gameActions == null) {
      throw new IllegalStateException(
          "GameActions must be initialized before initializing physics.");
    }

    // Add all collisions
    collisionRegistry.addCollision(new EnemyProjectilePlayerCollision(gameActions));
    collisionRegistry.addCollision(new PlayerCoinCollision(gameInitializer));
    collisionRegistry.addCollision(new PlayerGreenDinoCollision(gameActions));
    collisionRegistry.addCollision(new PlayerHeartCollision(gameInitializer));
    collisionRegistry.addCollision(new PlayerOrangeDinoCollision(gameActions));
    collisionRegistry.addCollision(new PlayerRedDinoCollision(gameActions));
    collisionRegistry.addCollision(new ProjectileEnemyProjectileCollision());
    collisionRegistry.addCollision(new ProjectileGreenDinoCollision(gameInitializer, gameActions));
    collisionRegistry.addCollision(new ProjectileOrangeDinoCollision(gameInitializer, gameActions));
    collisionRegistry.addCollision(new ProjectileRedDinoCollision(gameInitializer, gameActions));

    collisionRegistry.registerAll();
  }
}
