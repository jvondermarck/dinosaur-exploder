package com.dinosaur.dinosaurexploder.model;

import static org.junit.jupiter.api.Assertions.*;

import com.dinosaur.dinosaurexploder.components.ScoreComponent;
import org.junit.jupiter.api.Test;

class ScoreComponentTest {

  @Test
  void checkGetSetScore() {
    var scoreComponent = new ScoreComponent();

    scoreComponent.setScore(5);

    assertEquals(5, scoreComponent.getScore());
  }

  @Test
  void checkIncrementProperly() {
    var scoreComponent = new ScoreComponent();

    scoreComponent.setScore(4);

    scoreComponent.incrementScore(1);
    assertEquals(5, scoreComponent.getScore());
  }
}
