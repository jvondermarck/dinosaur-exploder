package com.dinosaur.dinosaurexploder.achievements;

import com.almasb.fxgl.dsl.FXGL;

public class KillCountAchievement implements Achievement {

    private final int targetKills;
    private final int rewardCoins;

    private int currentKills = 0;
    private boolean completed = false;

    public KillCountAchievement(int targetKills, int rewardCoins) {
        this.targetKills = targetKills;
        this.rewardCoins = rewardCoins;
    }

    @Override
    public String getDescription() {
        return "Kill " + targetKills + " dinosaurs";
    }

    @Override
    public boolean isCompleted() {
        return completed;
    }

    // 🔔 Called by AchievementManager
    @Override
    public void onDinosaurKilled() {
        if (completed) return;

        currentKills++;

        if (currentKills >= targetKills) {
            completed = true;
            FXGL.getNotificationService().pushNotification("Achievement Unlocked!");
            onComplete();
        }
    }


    @Override
    public void update(double tpf) {
        // Not needed for count-based achievement
    }

    @Override
    public void onComplete() {
        FXGL.getNotificationService().pushNotification("🏆 Achievement unlocked: " + getDescription());
    }

    @Override
    public int getRewardCoins() {
        return rewardCoins;
    }
}
