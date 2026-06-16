/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.controller.core.collisions;

import static com.almasb.fxgl.dsl.FXGL.onCollisionBegin;
import static com.almasb.fxgl.dsl.FXGL.random;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

import com.almasb.fxgl.entity.Entity;
import com.dinosaur.dinosaurexploder.components.*;
import com.dinosaur.dinosaurexploder.constants.EntityType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.controller.core.GameActions;
import com.dinosaur.dinosaurexploder.controller.core.GameInitializer;
import com.dinosaur.dinosaurexploder.model.CollisionHandler;
import com.dinosaur.dinosaurexploder.utils.AudioManager;
import java.util.logging.Logger;

public class ProjectileGreenDinoCollision implements CollisionHandlerInterface {

  private final GameActions gameActions;

  private final CollisionHandler collisionHandler;
  private final Entity score;
  private final Entity levelProgressBar;

  private Logger logger = Logger.getLogger(getClass().getName());

  public ProjectileGreenDinoCollision(GameInitializer gameInitializer, GameActions gameActions) {
    this.gameActions = gameActions;
    this.collisionHandler = gameInitializer.getCollisionHandler();
    this.score = gameInitializer.getScore();
    this.levelProgressBar = gameInitializer.getLevelProgressBar();
  }

  @Override
  public void register() {
    onCollisionBegin(
        EntityType.PROJECTILE,
        EntityType.GREEN_DINO,
        (projectile, greenDino) -> {
          spawn("explosion", greenDino.getX() - 25, greenDino.getY() - 30);
          if (random(0, 100) < 5) {
            Entity heart = spawn("heart", greenDino.getX(), greenDino.getY());
            heart
                .getComponent(Heart.class)
                .updateDirection(greenDino.getComponent(GreenDinoComponent.class).getDirection());
          }
          if (random(0, 100) < 1 && !gameActions.isAllyUse()) {
            Entity ally = spawn("allyDrop", greenDino.getX(), greenDino.getY());
            ally.getComponent(AllyDropComponent.class)
                .updateDirection(greenDino.getComponent(GreenDinoComponent.class).getDirection());
          }
          AudioManager.getInstance().playSound(GameConstants.ENEMY_EXPLODE_SOUND);
          projectile.removeFromWorld();
          greenDino.removeFromWorld();
          if (collisionHandler.isLevelUpAfterHitDino(
              score.getComponent(ScoreComponent.class),
              levelProgressBar.getComponent(LevelProgressBarComponent.class))) {
            gameActions.showLevelMessage();
            logger.info("Level up!");
          }
        });
  }
}
