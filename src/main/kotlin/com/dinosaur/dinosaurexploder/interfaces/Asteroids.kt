/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */
package com.dinosaur.dinosaurexploder.interfaces

interface Asteroids {
    /** Summary: This property is the lives that the asteroids has currently (read with getLives). */
    val lives: Int

    /** Summary: This method damages the asteroids */
    fun damage(damage: Int)

    /**
     * Summary : This method runs for every frame like a continues flow , without any stop until we
     * put stop to it. Parameters : double ptf
     */
    fun onUpdate(ptf: Double)
}
