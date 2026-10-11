/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */
package com.dinosaur.dinosaurexploder.specialties.effects

import com.almasb.fxgl.entity.Entity

class NullSpecialtyEffect : SpecialtyEffect {
    override fun applyTo(entity: Entity) {
        // NOOP
    }
}
