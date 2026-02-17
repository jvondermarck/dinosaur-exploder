package com.dinosaur.dinosaurexploder.achievements;

/**
 * Base class for all achievements in the game.
 * Provides common functionality and contract for achievement implementations.
 */
public abstract class Achievement {

    protected final int rewardCoins;
    protected boolean completed = false;

    public Achievement(int rewardCoins) {
        this.rewardCoins = rewardCoins;
    }

    public boolean isCompleted() {
        return completed;
    }

    public int getRewardCoins() {
        return rewardCoins;
    }

    /**
     * Returns a human-readable description of the achievement.
     * Example: "Kill 10 dinosaurs", "Reach 5000 points"
     */
    public abstract String getDescription();

    /**
     * Called every frame to update achievement progress.
     *
     * @param tpf Time per frame in seconds
     */
    public abstract void update(double tpf);

    /**
     * Called when a dinosaur is killed.
     * Override this in achievements that track kills.
     */
    public void onDinosaurKilled() {
        // Default: do nothing. Override in subclasses if needed.
    }

    /**
     * Called when score changes.
     * Override this in achievements that track score.
     */
    public void onScoreChanged(int newScore) {
        // Default: do nothing. Override in subclasses if needed.
    }

    /**
     * Called when coins are collected.
     * Override this in achievements that track coins.
     */
    public void onCoinCollected(int totalCoins) {
        // Default: do nothing. Override in subclasses if needed.
    }

    /**
     * Called when a boss is defeated.
     * Override this in achievements that track boss kills.
     */
    public void onBossDefeated() {
        // Default: do nothing. Override in subclasses if needed.
    }

    /**
     * Called when the achievement is completed.
     * Shows notification to the player.
     */
    protected abstract void onComplete();
}