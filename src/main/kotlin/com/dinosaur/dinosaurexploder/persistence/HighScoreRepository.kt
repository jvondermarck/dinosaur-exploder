/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */
package com.dinosaur.dinosaurexploder.persistence

import com.dinosaur.dinosaurexploder.model.HighScore

/** Abstracts how a [HighScore] is loaded from and persisted to storage. */
interface HighScoreRepository {
    fun load(): HighScore

    fun save(highScore: HighScore)
}
