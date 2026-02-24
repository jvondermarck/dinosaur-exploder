package com.dinosaur.dinosaurexploder.specialties.effects;

import com.almasb.fxgl.entity.Entity;
import com.dinosaur.dinosaurexploder.components.LifeComponent;

public record IncreasedLivesEffect(int extraLives) implements SpecialtyEffect {
  @Override
  public void applyTo(Entity entity) {
    if (!entity.hasComponent(LifeComponent.class)) {
      return;
    }

    LifeComponent lifeComponent = entity.getComponent(LifeComponent.class);
    lifeComponent.setMaxLives(lifeComponent.getMaxLives() + extraLives);
  }
}
