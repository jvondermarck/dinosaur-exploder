/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.specialties;

import com.dinosaur.dinosaurexploder.specialties.effects.NullSpecialtyEffect;
import com.dinosaur.dinosaurexploder.specialties.effects.bomb.MoreBombsEffect;
import com.dinosaur.dinosaurexploder.specialties.effects.life.IncreasedLivesEffect;
import com.dinosaur.dinosaurexploder.specialties.effects.player.ScaleHitBoxEffect;
import java.util.List;

public class SpecialtyManager {
  public static final Specialty NULL_SPECIALTY =
      new Specialty("none", 0, 0, new NullSpecialtyEffect());

  private static final Specialty TANK_SPECIALTY =
      new Specialty("more_hearts", 10, 10, new IncreasedLivesEffect(2));

  private static final Specialty MORE_BOMBS_SPECIALTY =
      new Specialty("more_bombs", 10, 10, new MoreBombsEffect(5));

  private static final Specialty MINI_SPECIALTY =
      new Specialty("smaller_hitbox", 10, 10, new ScaleHitBoxEffect(0.5));

  private static final List<Specialty> specialities =
      List.of(TANK_SPECIALTY, MORE_BOMBS_SPECIALTY, MINI_SPECIALTY, NULL_SPECIALTY);

  public static List<Specialty> getAllSpecialties() {
    return specialities;
  }
}
