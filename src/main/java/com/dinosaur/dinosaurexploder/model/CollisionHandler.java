package com.dinosaur.dinosaurexploder.model;

import com.dinosaur.dinosaurexploder.components.BombComponent;
import com.dinosaur.dinosaurexploder.components.CollectedCoinsComponent;
import com.dinosaur.dinosaurexploder.components.LifeComponent;
import com.dinosaur.dinosaurexploder.components.RedDinoComponent;
import com.dinosaur.dinosaurexploder.components.ScoreComponent;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import org.jetbrains.annotations.Nullable;

public class CollisionHandler {
    private final LevelManager levelManager;

    public CollisionHandler(LevelManager levelManager) {
        this.levelManager = levelManager;
    }

    public void onProjectileHitDino(ScoreComponent scoreComponent) {
        scoreComponent.incrementScore(1);
        levelManager.incrementDefeatedEnemies();

        adjustLevel();
    }

    public void handleHitBoss(RedDinoComponent redDinoComponent) {
        redDinoComponent.damage(1);
    }

    public void handleBossDefeat(ScoreComponent scoreComponent) {
        scoreComponent.incrementScore(levelManager.getCurrentLevel());
        levelManager.nextLevel();
    }

    public int getDamagedPlayerLife(LifeComponent lifeComponent) {
        return lifeComponent.decreaseLife(1);
    }

    public void onPlayerGetCoin(CollectedCoinsComponent collectedCoinsComponent, @Nullable BombComponent bombComponent) {
        collectedCoinsComponent.incrementCoin();

        // Check for bomb regeneration when coin is collected
        if (bombComponent != null) {
            bombComponent.trackCoinForBombRegeneration();
        }
    }

    public void onPlayerGetHeart(LifeComponent lifeComponent) {
        lifeComponent.increaseLife(1);
    }

    private void adjustLevel() {
        if (levelManager.shouldAdvanceLevel()) {
            levelManager.nextLevel();
        }
    }
}
