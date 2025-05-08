package com.dinosaur.dinosaurexploder.utils;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.time.LocalTimer;

import javafx.util.Duration;


public class FXGLGameTimer implements GameTimer {
    private final LocalTimer shootTimer = FXGL.newLocalTimer();

    @Override
    public void capture() {
        shootTimer.capture();
    }

    @Override
    public boolean isElapsed(Duration duration) {
        return shootTimer.elapsed(duration);
    }
}
