/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */
package com.dinosaur.dinosaurexploder.interfaces

/** Summary : This interface is to implement in new playerClass for clean Understanding */
interface Player {
    /** Summary : This method limit the upSide movement. */
    fun moveUp()

    /** Summary : This method limit the downSide movement. */
    fun moveDown()

    /** Summary : This method limit the rightSide movement. */
    fun moveRight()

    /** Summary : This method limit the leftSide movement. */
    fun moveLeft()

    /** Summary : This handles with the shooting from the player and spawning of the new bullet */
    fun shoot()
}
