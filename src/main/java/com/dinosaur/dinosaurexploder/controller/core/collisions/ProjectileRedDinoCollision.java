/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.controller.core.collisions;

import static com.almasb.fxgl.dsl.FXGL.onCollisionBegin;
import static com.almasb.fxgl.dsl.FXGL.random;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

import com.almasb.fxgl.entity.Entity;
import com.dinosaur.dinosaurexploder.components.CoinComponent;
import com.dinosaur.dinosaurexploder.components.Heart;
import com.dinosaur.dinosaurexploder.components.LevelProgressBarComponent;
import com.dinosaur.dinosaurexploder.components.RedDinoComponent;
import com.dinosaur.dinosaurexploder.components.ScoreComponent;
import com.dinosaur.dinosaurexploder.constants.EntityType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.controller.BossSpawner;
import com.dinosaur.dinosaurexploder.controller.core.GameActions;
import com.dinosaur.dinosaurexploder.controller.core.GameInitializer;
import com.dinosaur.dinosaurexploder.model.CollisionHandler;
import com.dinosaur.dinosaurexploder.utils.AudioManager;
import com.dinosaur.dinosaurexploder.utils.LevelManager;

public class ProjectileRedDinoCollision implements CollisionHandlerInterface {

  private final GameActions gameActions;
  private final CollisionHandler collisionHandler;
  private final BossSpawner bossSpawner;
  private final LevelManager levelManager;
  private final Entity score;
  private final Entity levelProgressBar;

  public ProjectileRedDinoCollision(GameInitializer gameInitializer, GameActions gameActions) {
    this.gameActions = gameActions;
    this.collisionHandler = gameInitializer.getCollisionHandler();
    this.bossSpawner = gameInitializer.getBossSpawner();
    this.levelManager = gameInitializer.getLevelManager();
    this.score = gameInitializer.getScore();
    this.levelProgressBar = gameInitializer.getLevelProgressBar();
  }

  @Override
  public void register() {
    onCollisionBegin(
        EntityType.PROJECTILE,
        EntityType.RED_DINO,
        (projectile, redDino) -> {
          spawn("explosion", redDino.getX() - 25, redDino.getY() - 30);
          projectile.removeFromWorld();
          AudioManager.getInstance().playSound(GameConstants.ENEMY_EXPLODE_SOUND);
          collisionHandler.handleHitBoss(redDino.getComponent(RedDinoComponent.class));

          if (redDino.getComponent(RedDinoComponent.class).getLives() == 0) {
            // if the boss is defeated it drops 100% a heart
            Entity heart = spawn("heart", redDino.getX(), redDino.getY());
            heart
                .getComponent(Heart.class)
                .updateDirection(redDino.getComponent(RedDinoComponent.class).getDirection());
            // if the boss dino is defeated it drops as many coins as the current level
            for (int i = 0; i < levelManager.getCurrentLevel(); i++) {
              Entity coin =
                  spawn("coin", redDino.getX() + random(-25, 25), redDino.getY() + random(-25, 25));
              coin.getComponent(CoinComponent.class)
                  .updateDirection(redDino.getComponent(RedDinoComponent.class).getDirection());
            }
            bossSpawner.removeBossEntity(redDino);

            if (collisionHandler.isLevelUpAfterBossDefeat(
                score.getComponent(ScoreComponent.class),
                levelProgressBar.getComponent(LevelProgressBarComponent.class))) {
              gameActions.showLevelMessage();
              System.out.println("Level up!");
            }
          } else {
            redDino.getComponent(RedDinoComponent.class).getHealthBar().updateBar();
          }
        });
  }
}
