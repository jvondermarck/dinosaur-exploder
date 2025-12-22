package com.dinosaur.dinosaurexploder.constants;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class EntityTypeTest {

  @Test
  public void entityTest() {
    EntityType[] values = EntityType.values();
    assertEquals(16, values.length, "EntityType count should match");

    assertArrayEquals(
        new EntityType[] {
          EntityType.PLAYER,
          EntityType.GREEN_DINO,
          EntityType.RED_DINO,
          EntityType.ORANGE_DINO,
          EntityType.PROJECTILE,
          EntityType.ENEMY_PROJECTILE,
          EntityType.SCORE,
          EntityType.LIFE,
          EntityType.BOMB,
          EntityType.COIN,
          EntityType.HEART,
          EntityType.LEVEL,
          EntityType.HEALTHBAR,
          EntityType.LEVEL_PROGRESS_BAR,
          EntityType.WEAPON_HEAT,
          EntityType.SHIELD
        },
        values);
  }
}
