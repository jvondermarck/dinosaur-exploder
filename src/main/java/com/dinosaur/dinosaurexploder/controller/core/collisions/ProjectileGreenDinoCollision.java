package com.dinosaur.dinosaurexploder.controller.core.collisions;

import static com.almasb.fxgl.dsl.FXGL.onCollisionBegin;
import static com.almasb.fxgl.dsl.FXGL.random;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;

import com.almasb.fxgl.entity.Entity;
import com.dinosaur.dinosaurexploder.components.LevelProgressBarComponent;
import com.dinosaur.dinosaurexploder.components.ScoreComponent;
import com.dinosaur.dinosaurexploder.constants.EntityType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.controller.core.GameActions;
import com.dinosaur.dinosaurexploder.controller.core.GameInitializer;
import com.dinosaur.dinosaurexploder.model.CollisionHandler;
import com.dinosaur.dinosaurexploder.utils.AudioManager;

public class ProjectileGreenDinoCollision implements CollisionHandlerInterface {

  private final GameActions gameActions;

  private final CollisionHandler collisionHandler;
  private final Entity score;
  private final Entity levelProgressBar;

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
            spawn("heart", greenDino.getX(), greenDino.getY());
          }
          AudioManager.getInstance().playSound(GameConstants.ENEMY_EXPLODE_SOUND);
          projectile.removeFromWorld();
          greenDino.removeFromWorld();
          if (collisionHandler.isLevelUpAfterHitDino(
              score.getComponent(ScoreComponent.class),
              levelProgressBar.getComponent(LevelProgressBarComponent.class))) {
            gameActions.showLevelMessage();
            System.out.println("Level up!");
          }
        });
  }
}
