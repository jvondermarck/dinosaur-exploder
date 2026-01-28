package com.dinosaur.dinosaurexploder.controller.core.collisions;

import static com.almasb.fxgl.dsl.FXGL.onCollisionBegin;

import com.dinosaur.dinosaurexploder.constants.EntityType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.controller.core.GameActions;
import com.dinosaur.dinosaurexploder.utils.AudioManager;

public class PlayerPurpleDinoCollision implements CollisionHandlerInterface {

  private final GameActions gameActions;

  public PlayerPurpleDinoCollision(GameActions gameActions) {
    this.gameActions = gameActions;
  }

  @Override
  public void register() {
    onCollisionBegin(
        EntityType.PLAYER,
        EntityType.PURPLE_DINO,
        (player, purpleDino) -> {
          AudioManager.getInstance().playSound(GameConstants.PLAYER_HIT_SOUND);
          purpleDino.removeFromWorld();
          System.out.println("You touched a purple dino!");
          gameActions.damagePlayer();
        });
  }
}
