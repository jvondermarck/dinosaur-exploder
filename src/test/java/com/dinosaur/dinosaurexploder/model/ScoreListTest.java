// ScoreListTest.java
package com.dinosaur.dinosaurexploder.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class ScoreListTest {

    private static final String FILE = "scores.ser";

    @BeforeEach
    void deleteStore() {
        // ensure a fresh start
        new File(FILE).delete();
    }

    @AfterEach
    void cleanup() {
        new File(FILE).delete();
    }

    @Test
    void loadOnEmptyReturnsEmptyList() {
        ScoreList list = ScoreList.load();
        assertNotNull(list.getEntries());
        assertTrue(list.getEntries().isEmpty());
    }

    @Test
    void addOneEntryThenListContainsIt() {
        ScoreList list = ScoreList.load();
        list.add(new ScoreEntry("AAA", 10));

        List<ScoreEntry> entries = list.getEntries();
        assertEquals(1, entries.size());
        assertEquals("AAA", entries.get(0).getName());
        assertEquals(10,    entries.get(0).getScore());
    }

    @Test
    void entriesAreSortedDescendingByScore() {
        ScoreList list = ScoreList.load();
        list.add(new ScoreEntry("A",  5));
        list.add(new ScoreEntry("B", 15));
        list.add(new ScoreEntry("C", 10));

        List<ScoreEntry> e = list.getEntries();
        assertEquals(3, e.size());
        assertEquals("B", e.get(0).getName());
        assertEquals("C", e.get(1).getName());
        assertEquals("A", e.get(2).getName());
    }

    @Test
    void listNeverExceedsTenEntries() {
        ScoreList list = ScoreList.load();
        // add 11 entries with increasing score
        IntStream.rangeClosed(1, 11)
                .forEach(i -> list.add(new ScoreEntry("P" + i, i)));

        List<ScoreEntry> e = list.getEntries();
        assertEquals(10, e.size());
        // the lowest (score=1) should have been dropped
        assertFalse(e.stream().anyMatch(x -> x.getScore() == 1));
        // highest should be 11 down to 2
        assertEquals(11, e.get(0).getScore());
        assertEquals(2,  e.get(9).getScore());
    }

    @Test
    void persistenceAcrossLoads() {
        // First instance: add two entries
        ScoreList first = ScoreList.load();
        first.add(new ScoreEntry("XX1", 50));
        first.add(new ScoreEntry("YY2", 75));

        // Load again from disk
        ScoreList second = ScoreList.load();
        List<ScoreEntry> e = second.getEntries();

        assertEquals(2, e.size());
        assertEquals("YY2", e.get(0).getName());
        assertEquals(75,    e.get(0).getScore());
        assertEquals("XX1", e.get(1).getName());
        assertEquals(50,    e.get(1).getScore());
    }
}
