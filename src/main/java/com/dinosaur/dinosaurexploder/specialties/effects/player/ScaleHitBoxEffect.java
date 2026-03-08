/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.specialties.effects.player;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.BoundingBoxComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.dinosaur.dinosaurexploder.specialties.effects.SpecialtyEffect;

public record ScaleHitBoxEffect(double scale) implements SpecialtyEffect {
  @Override
  public void applyTo(Entity entity) {
    // Resize the ship visually
    entity.setScaleX(scale);
    entity.setScaleY(scale);

    // Resize the hitbox
    BoundingBoxComponent bbox = entity.getBoundingBoxComponent();

    double currentWidth = bbox.getWidth();
    double currentHeight = bbox.getHeight();

    bbox.clearHitBoxes();
    bbox.addHitBox(new HitBox(BoundingShape.box(currentWidth * scale, currentHeight * scale)));
  }
}
