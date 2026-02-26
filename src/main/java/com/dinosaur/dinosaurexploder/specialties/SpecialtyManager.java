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
      new Specialty("null_specialty", 0, 0, new NullSpecialtyEffect());

  private static final Specialty TANK_SPECIALTY =
      new Specialty("specialty_more_hearts", 10, 10, new IncreasedLivesEffect(2));

  private static final Specialty MORE_BOMBS_SPECIALTY =
      new Specialty("specialty_more_bombs", 10, 10, new MoreBombsEffect(5));

  private static final Specialty MINI_SPECIALTY =
      new Specialty("specialty_mini", 10, 10, new ScaleHitBoxEffect(0.5));

  private static final List<Specialty> specialities =
      List.of(NULL_SPECIALTY, TANK_SPECIALTY, MORE_BOMBS_SPECIALTY, MINI_SPECIALTY);

  public static List<Specialty> getAllSpecialties() {
    return specialities;
  }
}
