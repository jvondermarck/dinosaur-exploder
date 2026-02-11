package com.dinosaur.dinosaurexploder.controller;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.spawn;
import static javafx.util.Duration.seconds;

import com.almasb.fxgl.time.TimerAction;

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
                double x = random(0, getAppWidth() - 80);
                spawn("coin", x, 0);
              }
            },
            seconds(duration));
  }
}
