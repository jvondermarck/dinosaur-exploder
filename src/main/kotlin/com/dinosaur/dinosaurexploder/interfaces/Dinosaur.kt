/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */
package com.dinosaur.dinosaurexploder.interfaces

/** Summary : This interface is to implement in new DinosaurClass for clean Understanding */
interface Dinosaur {
    /** Summary: This property is the lives that the dino has currently (read with getLives). */
    val lives: Int

    /** Summary: This method damages the dino */
    fun damage(damage: Int)

    /**
     * Summary : This method runs for every frame like a continues flow , without any stop until we
     * put stop to it. Parameters : double ptf
     */
    fun onUpdate(ptf: Double)

    /** Summary : This handles with the shooting from the dinosaur and spawning of the new bullet */
    fun shoot()
}
