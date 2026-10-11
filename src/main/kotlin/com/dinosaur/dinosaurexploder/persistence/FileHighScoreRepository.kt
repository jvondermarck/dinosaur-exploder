/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */
package com.dinosaur.dinosaurexploder.persistence

import com.dinosaur.dinosaurexploder.constants.GameConstants
import com.dinosaur.dinosaurexploder.model.HighScore
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.logging.Level
import java.util.logging.Logger

/** Real, file-backed implementation used in production. */
class FileHighScoreRepository : HighScoreRepository {
    override fun load(): HighScore {
        return try {
            ObjectInputStream(FileInputStream(GameConstants.HIGH_SCORE_FILE)).use { input ->
                input.readObject() as HighScore
            }
        } catch (e: IOException) {
            HighScore()
        } catch (e: ClassNotFoundException) {
            HighScore()
        }
    }

    override fun save(highScore: HighScore) {
        try {
            ObjectOutputStream(FileOutputStream(GameConstants.HIGH_SCORE_FILE)).use { out ->
                out.writeObject(highScore)
            }
        } catch (e: IOException) {
            LOGGER.log(Level.INFO, "Error saving high score: {0}", e.message)
        }
    }

    private companion object {
        private val LOGGER: Logger = Logger.getLogger(FileHighScoreRepository::class.java.name)
    }
}
