/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.dinosaur.dinosaurexploder.components.ScoreComponent;
import com.dinosaur.dinosaurexploder.persistence.HighScoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScoreComponentTest {

  // Mocked: incrementScore() saves a new high score, and the default repository writes the
  // player's real highScore.ser.
  private final HighScoreRepository highScoreRepository = mock(HighScoreRepository.class);

  @BeforeEach
  void setUp() {
    when(highScoreRepository.load()).thenReturn(new HighScore());
  }

  @Test
  void checkGetSetScore() {
    var scoreComponent = new ScoreComponent(highScoreRepository);
    scoreComponent.setScore(5);
    assertEquals(5, scoreComponent.getScore());
  }

  @Test
  void checkIncrementProperly() {
    var scoreComponent = new ScoreComponent(highScoreRepository);
    scoreComponent.setScore(4);
    scoreComponent.incrementScore(1);
    assertEquals(5, scoreComponent.getScore());
  }
}
