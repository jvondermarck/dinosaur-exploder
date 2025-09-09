package com.dinosaur.dinosaurexploder.controller.core.collisions;

import com.dinosaur.dinosaurexploder.constants.EntityType;
import com.dinosaur.dinosaurexploder.constants.GameConstants;
import com.dinosaur.dinosaurexploder.controller.core.GameActions;
import com.dinosaur.dinosaurexploder.utils.AudioManager;

import static com.almasb.fxgl.dsl.FXGL.onCollisionBegin;

public class EnemyProjectilePlayerCollision implements CollisionHandlerInterface{

    private  final GameActions gameActions;

    public EnemyProjectilePlayerCollision(GameActions gameActions) {
        this.gameActions = gameActions;
    }

    @Override
    public void register() {
        onCollisionBegin(EntityType.ENEMY_PROJECTILE, EntityType.PLAYER, (projectile, player) -> {
            AudioManager.getInstance().playSound(GameConstants.PLAYER_HIT_SOUND);
            projectile.removeFromWorld();
            System.out.println("You got hit !\n");
            gameActions.damagePlayer();
        });
    }
}
