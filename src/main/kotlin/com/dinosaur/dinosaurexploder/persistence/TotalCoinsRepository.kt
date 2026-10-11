/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */
package com.dinosaur.dinosaurexploder.persistence

import com.dinosaur.dinosaurexploder.model.TotalCoins

/** Abstracts how [TotalCoins] is loaded from and persisted to storage. */
interface TotalCoinsRepository {
    fun load(): TotalCoins

    fun save(totalCoins: TotalCoins)
}
