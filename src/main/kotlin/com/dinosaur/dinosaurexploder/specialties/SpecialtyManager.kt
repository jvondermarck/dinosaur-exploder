/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */
package com.dinosaur.dinosaurexploder.specialties

import com.dinosaur.dinosaurexploder.specialties.effects.NullSpecialtyEffect
import com.dinosaur.dinosaurexploder.specialties.effects.bomb.MoreBombsEffect
import com.dinosaur.dinosaurexploder.specialties.effects.life.IncreasedLivesEffect
import com.dinosaur.dinosaurexploder.specialties.effects.player.ScaleHitBoxEffect

object SpecialtyManager {
    @JvmField
    val NULL_SPECIALTY: Specialty = Specialty("none", 0, 0, NullSpecialtyEffect())

    private val TANK_SPECIALTY = Specialty("more_hearts", 20, 20, IncreasedLivesEffect(2))

    private val MORE_BOMBS_SPECIALTY = Specialty("more_bombs", 30, 50, MoreBombsEffect(5))

    private val MINI_SPECIALTY = Specialty("smaller_hitbox", 40, 80, ScaleHitBoxEffect(0.5))

    private val specialities: List<Specialty> =
        java.util.List.of(TANK_SPECIALTY, MORE_BOMBS_SPECIALTY, MINI_SPECIALTY, NULL_SPECIALTY)

    @JvmStatic
    fun getAllSpecialties(): List<Specialty> {
        return specialities
    }
}
