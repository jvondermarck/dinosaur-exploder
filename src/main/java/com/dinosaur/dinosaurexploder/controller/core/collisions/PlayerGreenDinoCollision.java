package com.dinosaur.dinosaurexploder.controller.core.collisions;

import com.dinosaur.dinosaurexploder.constants.EntityType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.controller.core.GameActions;
import com.dinosaur.dinosaurexploder.utils.AudioManager;

import static com.almasb.fxgl.dsl.FXGL.onCollisionBegin;

public class PlayerGreenDinoCollision implements CollisionHandlerInterface{

    private final GameActions gameActions;

    public PlayerGreenDinoCollision(GameActions gameActions) {
        this.gameActions = gameActions;
    }

    @Override
    public void register() {
        onCollisionBegin(EntityType.PLAYER, EntityType.GREEN_DINO, (player, greenDino) -> {
            AudioManager.getInstance().playSound(GameConstants.PLAYER_HIT_SOUND);
            greenDino.removeFromWorld();
            System.out.println("You touched a dino !");
            gameActions.damagePlayer();
        });
    }
}
