/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.specialties.effects.life;

import com.almasb.fxgl.entity.Entity;
import com.dinosaur.dinosaurexploder.components.LifeComponent;

public record IncreasedLivesEffect(int extraLives) implements LifeSpecialtyEffect {
  @Override
  public void applyTo(Entity entity) {
    if (!entity.hasComponent(LifeComponent.class)) {
      return;
    }

    LifeComponent lifeComponent = entity.getComponent(LifeComponent.class);

    int newMaximumLives = lifeComponent.getMaxLives() + extraLives;
    lifeComponent.setMaxLives(newMaximumLives);
    lifeComponent.setCurrentLives(newMaximumLives);
  }
}
