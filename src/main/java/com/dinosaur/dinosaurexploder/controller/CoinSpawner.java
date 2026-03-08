/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */

package com.dinosaur.dinosaurexploder.controller;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;
import static javafx.util.Duration.seconds;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.time.TimerAction;
import com.dinosaur.dinosaurexploder.components.CoinComponent;
import com.dinosaur.dinosaurexploder.constants.Direction;
import com.dinosaur.dinosaurexploder.model.GameData;

public class CoinSpawner {

  private final int percentChanceForCoinDrop;
  private final double duration;
  private TimerAction timerAction;

  public CoinSpawner(int percentChanceForCoinDrop, double duration) {
    this.percentChanceForCoinDrop = percentChanceForCoinDrop;
    this.duration = duration;
  }

  public void startSpawning() {
    if (timerAction != null) {
      timerAction.expire();
    }

    timerAction =
        run(
            () -> {
              if (random(0, 100) < percentChanceForCoinDrop) {
                // direction is up for normal mode, random for expert mode
                Direction direction = Direction.modeDirection(GameData.getSelectedDifficulty());
                Entity coin =
                    switch (direction) {
                      case DOWN ->
                          spawn("coin", random(0, getAppWidth() - 80), getAppHeight() - 80);
                      case LEFT -> spawn("coin", 0, random(0, getAppHeight() - 80));
                      case RIGHT ->
                          spawn("coin", getAppWidth() - 80, random(0, getAppHeight() - 80));
                      default -> spawn("coin", random(0, getAppWidth() - 80), 0);
                    };
                // Apply direction to component
                coin.getComponent(CoinComponent.class).updateDirection(direction);
              }
            },
            seconds(duration));
  }
}
