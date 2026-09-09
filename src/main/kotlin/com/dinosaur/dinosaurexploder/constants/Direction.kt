/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */
package com.dinosaur.dinosaurexploder.constants

import com.almasb.fxgl.core.math.FXGLMath.random

enum class Direction {
    UP, DOWN, LEFT, RIGHT;

    companion object {
        @JvmStatic
        fun modeDirection(mode: GameMode): Direction {
            return if (mode == GameMode.EXPERT) randomDirection() else UP
        }

        @JvmStatic
        fun randomDirection(): Direction {
            val values = values()
            val index = random(0, values.size - 1)
            return values[index]
        }
    }
}