/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.components;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.dinosaur.dinosaurexploder.model.Settings;
import org.junit.jupiter.api.Test;

class AudioControlsComponentTest {

  @Test
  void volumeTypesReadTheirConfiguredSettings() {
    Settings settings = new Settings();
    settings.setVolume(0.25);
    settings.setSfxVolume(0.75);

    assertEquals(0.25, AudioControlsComponent.VolumeType.MUSIC.getVolume(settings));
    assertEquals(0.75, AudioControlsComponent.VolumeType.SFX.getVolume(settings));
  }
}
