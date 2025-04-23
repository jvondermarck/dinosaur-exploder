// ScoreEntryTest.java
package com.dinosaur.dinosaurexploder.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScoreEntryTest {

    @Test
    void gettersReturnConstructorValues() {
        ScoreEntry e = new ScoreEntry("ABC", 123);

        assertEquals("ABC", e.getName());
        assertEquals(123,  e.getScore());
    }

    @Test
    void nameIsImmutable() {
        ScoreEntry e = new ScoreEntry("xyz", 5);
        // verify that getName() does not reflect any outside mutation
        String n = e.getName();
        n = "ZZZ";
        assertEquals("xyz", e.getName());
    }
}
