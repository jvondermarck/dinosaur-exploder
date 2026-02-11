/*
 * SPDX-FileCopyrightText: 2026 jvondermarck
 * SPDX-License-Identifier: MIT
 */
package com.dinosaur.dinosaurexploder.controller;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGL.getAppHeight;
import static com.almasb.fxgl.dsl.FXGL.getAppWidth;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.time.TimerAction;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class CountdownAnimation {

  private TimerAction countDownAction;
  private int countDown;
  private Text countDownText;

  public CountdownAnimation(int numbersToCount) {
    countDown = numbersToCount;
  }

  public void startCountdown(Runnable onFinished) {

    countDownAction =
        getGameTimer()
            .runAtInterval(
                () -> {
                  if (countDown > 0) {
                    countDown -= 1;
                    countDownText.setText(String.valueOf(countDown));
                  } else {
                    countDownAction.expire();
                    countDownText.setVisible(false);
                  }
                  if (countDown == 1) {
                    onFinished.run();
                  }
                },
                Duration.seconds(1));

    createCountDownAnimation();
  }

  private void createCountDownAnimation() {
    countDownText = getUIFactoryService().newText(String.valueOf(countDown), Color.WHITE, 60);
    getGameScene().addUINode(countDownText);
    FXGL.animationBuilder()
        .interpolator(Interpolators.ELASTIC.EASE_OUT())
        .onCycleFinished(() -> System.out.println("Countdown: " + countDown))
        .onFinished(() -> System.out.println("Countdown animation finished"))
        .duration(Duration.seconds(1))
        .repeat(4)
        .translate(countDownText)
        .from(
            new Point2D(
                (getAppWidth() - countDownText.getLayoutBounds().getWidth()) / 2,
                getAppHeight() / 2.0 - 200))
        .to(
            new Point2D(
                (getAppWidth() - countDownText.getLayoutBounds().getWidth()) / 2,
                getAppHeight() / 2.0))
        .buildAndPlay();
  }
}
