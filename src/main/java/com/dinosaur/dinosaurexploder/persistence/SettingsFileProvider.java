/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.persistence;

import java.io.IOException;
import java.util.Properties;

/** Abstracts file operations for game settings. */
public interface SettingsFileProvider {

  Properties load();

  void save(Properties properties) throws IOException;

  void delete();
}
