package com.dinosaur.dinosaurexploder.utils;

import com.dinosaur.dinosaurexploder.model.HighScore;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class FileDataProvider implements DataProvider {
    @Override
    public HighScore getHighScore() {
        try (FileInputStream file = new FileInputStream("highScore.ser");
             ObjectInputStream in = new ObjectInputStream(file)) {
            return (HighScore) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new HighScore();
        }
    }
}
