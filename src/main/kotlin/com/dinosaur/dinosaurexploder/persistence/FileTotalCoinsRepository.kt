/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */
package com.dinosaur.dinosaurexploder.persistence

import com.dinosaur.dinosaurexploder.constants.GameConstants
import com.dinosaur.dinosaurexploder.model.TotalCoins
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.logging.Level
import java.util.logging.Logger

/** Real, file-backed implementation used in production. */
class FileTotalCoinsRepository : TotalCoinsRepository {
    override fun load(): TotalCoins {
        return try {
            ObjectInputStream(FileInputStream(GameConstants.TOTAL_COINS_FILE)).use { input ->
                input.readObject() as TotalCoins
            }
        } catch (e: IOException) {
            TotalCoins()
        } catch (e: ClassNotFoundException) {
            TotalCoins()
        }
    }

    override fun save(totalCoins: TotalCoins) {
        try {
            ObjectOutputStream(FileOutputStream(GameConstants.TOTAL_COINS_FILE)).use { out ->
                out.writeObject(totalCoins)
            }
        } catch (e: IOException) {
            LOGGER.log(Level.SEVERE, "Error saving coins: {0}", e.message)
        }
    }

    private companion object {
        private val LOGGER: Logger = Logger.getLogger(FileTotalCoinsRepository::class.java.name)
    }
}
