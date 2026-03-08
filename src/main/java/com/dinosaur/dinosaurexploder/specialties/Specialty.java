/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.specialties;

import com.dinosaur.dinosaurexploder.specialties.effects.SpecialtyEffect;

public record Specialty(String name, int costInCoins, int costInHighScore, SpecialtyEffect effect) {
  // empty
}
