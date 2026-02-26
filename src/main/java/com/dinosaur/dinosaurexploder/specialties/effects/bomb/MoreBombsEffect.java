/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.specialties.effects.bomb;

import com.almasb.fxgl.entity.Entity;
import com.dinosaur.dinosaurexploder.components.BombComponent;

public record MoreBombsEffect(int extraBombs) implements BombSpecialtyEffect {
  @Override
  public void applyTo(Entity bomb) {
    if (!bomb.hasComponent(BombComponent.class)) {
      return;
    }

    BombComponent bombComponent = bomb.getComponent(BombComponent.class);

    int newMaximumBombs = bombComponent.getMaximumBombCount() + extraBombs;
    bombComponent.setMaximumBombCount(newMaximumBombs);
    bombComponent.setCurrentBombCount(newMaximumBombs);
  }
}
