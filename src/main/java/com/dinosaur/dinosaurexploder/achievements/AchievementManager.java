package com.dinosaur.dinosaurexploder.achievements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages all achievements in the game.
 * Handles registration, initialization, and event notifications.
 */
public class AchievementManager {

    private final List<Achievement> allAchievements = new ArrayList<>();
    private final List<Achievement> activeAchievements = new ArrayList<>();

    public AchievementManager() {
        registerAchievements();
    }

    /**
     * Registers all available achievements.
     * Add new achievements here to make them available in the game.
     */
    private void registerAchievements() {
        // Kill Count Achievements
        allAchievements.add(new KillCountAchievement(10, 50));
        allAchievements.add(new KillCountAchievement(20, 100));
        allAchievements.add(new KillCountAchievement(50, 250));

        // Score Achievements
        allAchievements.add(new ScoreAchievement(5000, 75));
        allAchievements.add(new ScoreAchievement(10000, 150));

        // Coin Collection Achievements
        allAchievements.add(new CoinCollectionAchievement(100, 50));
        allAchievements.add(new CoinCollectionAchievement(500, 200));

        // Survival Time Achievements
        allAchievements.add(new SurvivalTimeAchievement(1, 50));  // 1 minute
        allAchievements.add(new SurvivalTimeAchievement(3, 150)); // 3 minutes

        // Boss Defeat Achievement
        allAchievements.add(new BossDefeatAchievement(200));
    }

    /**
     * Called once when the game starts.
     * Randomly selects achievements to be active for this game session.
     */
    public void init() {
        if (allAchievements.isEmpty()) return;

        Collections.shuffle(allAchievements);

        // Activate 2 random achievements for variety
        int numToActivate = Math.min(2, allAchievements.size());
        for (int i = 0; i < numToActivate; i++) {
            activeAchievements.add(allAchievements.get(i));
        }
    }

    /**
     * Called every frame to update time-based achievements.
     *
     * @param tpf Time per frame in seconds
     */
    public void update(double tpf) {
        for (Achievement achievement : activeAchievements) {
            if (!achievement.isCompleted()) {
                achievement.update(tpf);
            }
        }
    }

    /**
     * Called when a dinosaur is killed.
     * Notifies all active kill-based achievements.
     */
    public void notifyDinosaurKilled() {
        for (Achievement achievement : activeAchievements) {
            achievement.onDinosaurKilled();
        }
    }

    /**
     * Called when the player's score changes.
     * Notifies all active score-based achievements.
     *
     * @param newScore The current score
     */
    public void notifyScoreChanged(int newScore) {
        for (Achievement achievement : activeAchievements) {
            achievement.onScoreChanged(newScore);
        }
    }

    /**
     * Called when coins are collected.
     * Notifies all active coin-based achievements.
     *
     * @param totalCoins The total number of coins collected
     */
    public void notifyCoinCollected(int totalCoins) {
        for (Achievement achievement : activeAchievements) {
            achievement.onCoinCollected(totalCoins);
        }
    }

    /**
     * Called when a boss is defeated.
     * Notifies all active boss-defeat achievements.
     */
    public void notifyBossDefeated() {
        for (Achievement achievement : activeAchievements) {
            achievement.onBossDefeated();
        }
    }

    public List<Achievement> getActiveAchievements() {
        return activeAchievements;
    }

    public List<Achievement> getAllAchievements() {
        return allAchievements;
    }

    public Achievement getActiveAchievement() {
        if (activeAchievements.isEmpty()) {
            return null;
        }
        return activeAchievements.get(0);
    }
}