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

  AchievementCatalog with(Supplier<Achievement> factory) {
    List<Supplier<Achievement>> extendedFactories = new ArrayList<>(factories);
    extendedFactories.add(factory);
    return new AchievementCatalog(extendedFactories);
  }

  List<Achievement> createAchievements() {
    return factories.stream().map(Supplier::get).toList();
  }
}
