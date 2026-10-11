/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */
package com.dinosaur.dinosaurexploder.specialties.effects

import com.almasb.fxgl.entity.Entity

interface SpecialtyEffect {
    fun applyTo(entity: Entity)
}
