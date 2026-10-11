/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.persistence;

import com.dinosaur.dinosaurexploder.model.TotalCoins;

/** Abstracts how {@link TotalCoins} is loaded from and persisted to storage. */
public interface TotalCoinsRepository {
  TotalCoins load();

  void save(TotalCoins totalCoins);
}
