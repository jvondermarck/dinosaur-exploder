/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.specialties.effects;

import com.almasb.fxgl.entity.Entity;

public class NullSpecialtyEffect implements SpecialtyEffect {
  @Override
  public void applyTo(Entity entity) {
    // NOOP
  }
}
