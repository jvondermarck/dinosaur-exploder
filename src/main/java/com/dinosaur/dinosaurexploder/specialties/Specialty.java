package com.dinosaur.dinosaurexploder.specialties;

import com.dinosaur.dinosaurexploder.specialties.effects.SpecialtyEffect;

public record Specialty(String name, int costInCoins, SpecialtyEffect effect) {
  // empty
}
