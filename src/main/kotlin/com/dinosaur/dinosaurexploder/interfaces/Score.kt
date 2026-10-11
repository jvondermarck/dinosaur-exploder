/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */
package com.dinosaur.dinosaurexploder.interfaces

/** Summary : This interface is to implement in new playerClass for clean Understanding */
interface Score {
    /**
     * Summary : This method runs for every frame like a continues flow , without any stop until we
     * put stop to it. Parameters : double ptf
     */
    fun onUpdate(tpf: Double)

    /** Summary : This property is the current Score (read with getScore, set with setScore) */
    var score: Int

    /** Summary : This method increment the Score to the current Score */
    fun incrementScore(i: Int)
}
