package com.dinosaur.dinosaurexploder.model;

import java.io.Serializable;

public class ScoreEntry implements Serializable {
    private final String name;
    private final int score;

    public ScoreEntry(String name, int score) {
        this.name  = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }
}
