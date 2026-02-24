package com.dinosaur.dinosaurexploder.specialties;

import com.dinosaur.dinosaurexploder.specialties.effects.IncreasedLivesEffect;
import com.dinosaur.dinosaurexploder.specialties.effects.ScaleHitBoxEffect;

import java.util.List;

public class SpecialtyManager {
  private final static Specialty TANK_SPECIALTY =
          new Specialty("Tank", 10, new IncreasedLivesEffect(2));

  private final static Specialty MINI_SPECIALTY =
          new Specialty("Mini", 5, new ScaleHitBoxEffect(0.5));

  private final static List<Specialty> specialities = List.of(
          TANK_SPECIALTY
  );

  public List<Specialty> getAllSpecialties() {
    return specialities;
  }
}