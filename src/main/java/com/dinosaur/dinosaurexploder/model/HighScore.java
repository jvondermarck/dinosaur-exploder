package com.dinosaur.dinosaurexploder.model;

import java.io.*;

public class HighScore implements Serializable {
    private final Integer high;

    public HighScore() {
        this.high = 0;
    }

    public HighScore(Integer x) {
        this.high = x;
    }

    public Integer getHigh() {
        return this.high;
    }
}
