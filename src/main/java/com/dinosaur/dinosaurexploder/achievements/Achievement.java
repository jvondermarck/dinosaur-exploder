package com.dinosaur.dinosaurexploder.achievements;

public interface Achievement {
  // Short description shown to the player
  String getDescription();

  // Called every frame to check if achievement is completed
  boolean isCompleted();

  // Logic check (kills, time, survival, etc.)
  void update(double tpf);

  // What happens when achievement is completed
  void onComplete();

    // Reward for completing achievement
    int getRewardCoins();

    default void onDinosaurKilled() {
        // optional override
    }

  default void onDinosaurKilled() {
    // optional override
  }
}
