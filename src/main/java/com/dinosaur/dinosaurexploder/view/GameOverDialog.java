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
import com.dinosaur.dinosaurexploder.utils.LanguageManager;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import java.time.Duration;

public class GameOverDialog {

  private final LanguageManager languageManager;
  private final LevelManager levelManager;
  private final long sessionStartNanos;

  public GameOverDialog(
      LanguageManager languageManager, LevelManager levelManager, long sessionStartNanos) {
    this.languageManager = languageManager;
    this.levelManager = levelManager;
    this.sessionStartNanos = sessionStartNanos;
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

    long survivedSeconds = Duration.ofNanos(System.nanoTime() - sessionStartNanos).toSeconds();
    GameOverStats stats =
        new GameOverStats(
            finalScore, GameData.getHighScore(), levelManager.getCurrentLevel(), survivedSeconds);

    getSceneService().pushSubScene(new GameOverMenu(languageManager, stats));
  }
}
