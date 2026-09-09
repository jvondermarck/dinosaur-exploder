/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.view;

import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGL.getSceneService;

import com.dinosaur.dinosaurexploder.components.ScoreComponent;
import com.dinosaur.dinosaurexploder.model.GameData;
import com.dinosaur.dinosaurexploder.model.GameOverStats;
import com.dinosaur.dinosaurexploder.utils.GameTimer;
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import javafx.util.Duration;

public class GameOverDialog {

  private final LanguageManager languageManager;
  private final LevelManager levelManager;
  private final GameTimer sessionTimer;

  public GameOverDialog(
      LanguageManager languageManager, LevelManager levelManager, GameTimer sessionTimer) {
    this.languageManager = languageManager;
    this.levelManager = levelManager;
    this.sessionTimer = sessionTimer;
  }

  public void createDialog() {
    int finalScore = 0;
    try {
      var scoreEntities = getGameWorld().getEntitiesByComponent(ScoreComponent.class);
      if (!scoreEntities.isEmpty()) {
        ScoreComponent sc = scoreEntities.get(0).getComponent(ScoreComponent.class);
        finalScore = sc.getScore();
      }
    } catch (Exception ignored) {
    }

    long survivedSeconds = 0;
    while (sessionTimer.isElapsed(Duration.seconds(survivedSeconds + 1))) {
      survivedSeconds++;
    }
    GameOverStats stats =
        new GameOverStats(
            finalScore, GameData.getHighScore(), levelManager.getCurrentLevel(), survivedSeconds);

    getSceneService().pushSubScene(new GameOverMenu(languageManager, stats));
  }
}
