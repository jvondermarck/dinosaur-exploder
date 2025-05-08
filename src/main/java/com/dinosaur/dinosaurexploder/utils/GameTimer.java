package com.dinosaur.dinosaurexploder.utils;

import javafx.util.Duration;

public interface GameTimer {
    void capture();

    boolean isElapsed(Duration duration);
}
