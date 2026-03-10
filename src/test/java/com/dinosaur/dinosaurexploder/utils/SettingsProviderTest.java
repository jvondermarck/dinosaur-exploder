/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.utils;

import com.dinosaur.dinosaurexploder.model.Settings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SettingsProviderTest {

  @Test
  public void testLoadSettings() {
    Settings settings = SettingsProvider.loadSettings();
    Assertions.assertNotNull(
        settings, "Settings should always be available. At least the default settings");
  }
}
