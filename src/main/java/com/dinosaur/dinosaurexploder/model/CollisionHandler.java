package com.dinosaur.dinosaurexploder.model;

import com.dinosaur.dinosaurexploder.components.BombComponent;
import com.dinosaur.dinosaurexploder.components.CollectedCoinsComponent;
import com.dinosaur.dinosaurexploder.components.LevelProgressBarComponent;
import com.dinosaur.dinosaurexploder.components.LifeComponent;
import com.dinosaur.dinosaurexploder.components.ScoreComponent;
import com.dinosaur.dinosaurexploder.interfaces.Dinosaur;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import org.jetbrains.annotations.Nullable;
import com.dinosaur.dinosaurexploder.achievements.AchievementManager;


public class CollisionHandler {
    private final LevelManager levelManager;
    private final AchievementManager achievementManager;

    public CollisionHandler(LevelManager levelManager,
                            AchievementManager achievementManager) {
        this.levelManager = levelManager;
        this.achievementManager = achievementManager;
    }


    public boolean isLevelUpAfterHitDino(
            ScoreComponent scoreComponent, LevelProgressBarComponent levelProgressBarComponent) {
        scoreComponent.incrementScore(1);
        levelManager.incrementDefeatedEnemies();
        achievementManager.notifyDinosaurKilled();
        levelProgressBarComponent.updateProgress();

        return adjustLevel();
    }

    public void handleHitBoss(Dinosaur dinoComponent) {
        dinoComponent.damage(1);
    }

    public void handleBossDefeat(ScoreComponent scoreComponent) {
        scoreComponent.incrementScore(levelManager.getCurrentLevel());
        levelManager.nextLevel();
    }

    public int getDamagedPlayerLife(LifeComponent lifeComponent) {
        return lifeComponent.decreaseLife(1);
    }

    public void onPlayerGetCoin(
            CollectedCoinsComponent collectedCoinsComponent,
            ScoreComponent scoreComponent,
            @Nullable BombComponent bombComponent) {
        collectedCoinsComponent.incrementCoin();

        scoreComponent.incrementScore(2);
        // Check for bomb regeneration when coin is collected
        if (bombComponent != null) {
            bombComponent.trackCoinForBombRegeneration();
        }
    }

  public void onPlayerGetHeart(LifeComponent lifeComponent) {
    lifeComponent.increaseLife(1);
  }

    private boolean adjustLevel() {
        if (levelManager.shouldAdvanceLevel()) {
            levelManager.nextLevel();
            return true;
        }
        return false;
    }
}
