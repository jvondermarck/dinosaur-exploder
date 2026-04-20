/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.achievements;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

final class AchievementCatalog {

  private final List<Supplier<Achievement>> factories;

  private AchievementCatalog(List<Supplier<Achievement>> factories) {
    this.factories = List.copyOf(factories);
  }

  @SafeVarargs
  static AchievementCatalog of(Supplier<Achievement>... factories) {
    return new AchievementCatalog(List.of(factories));
  }

  static AchievementCatalog defaults() {
    return of(
        () -> new KillCountAchievement(10, 50),
        () -> new KillCountAchievement(20, 100),
        () -> new KillCountAchievement(50, 250),
        () -> new ScoreAchievement(5000, 75),
        () -> new ScoreAchievement(10000, 150),
        () -> new CoinCollectionAchievement(100, 50),
        () -> new CoinCollectionAchievement(500, 200),
        () -> new SurvivalTimeAchievement(1, 50),
        () -> new SurvivalTimeAchievement(3, 150),
        () -> new BossDefeatAchievement(200));
  }

  AchievementCatalog with(Supplier<Achievement> factory) {
    List<Supplier<Achievement>> extendedFactories = new ArrayList<>(factories);
    extendedFactories.add(factory);
    return new AchievementCatalog(extendedFactories);
  }

  List<Achievement> createAchievements() {
    return factories.stream().map(Supplier::get).toList();
  }
}
