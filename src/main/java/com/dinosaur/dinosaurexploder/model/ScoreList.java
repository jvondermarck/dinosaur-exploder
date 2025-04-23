package com.dinosaur.dinosaurexploder.model;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ScoreList implements Serializable {
    private static final String FILE_NAME = "scores.ser";
    private final List<ScoreEntry> entries = new ArrayList<>();

    /** Load existing list or return empty */
    public static ScoreList load() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (ScoreList) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ScoreList();
        }
    }

    /** Persist to disk */
    public void save() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Add new entry, keep top-10 sorted descending */
    public void add(ScoreEntry e) {
        entries.add(e);
        entries.sort(Comparator.comparingInt(ScoreEntry::getScore).reversed());
        if (entries.size() > 10) {
            entries.subList(10, entries.size()).clear();
        }
        save();
    }

    public List<ScoreEntry> getEntries() {
        return entries;
    }
}
