/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */
package com.dinosaur.dinosaurexploder.interfaces

/** Summary : This interface is to implement in new playerClass for clean Understanding */
interface Life {
    /**
     * Summary : This method runs for every frame like a continues flow , without any stop until we
     * put stop to it. Parameters : double ptf
     */
    fun onUpdate(tpf: Double)

    /** Summary : This method increases the life to the current life */
    fun increaseLife(i: Int): Int

    /** Summary : This method sets the life to the current life */
    fun decreaseLife(i: Int): Int
}
