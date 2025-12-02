package com.comp2042.game.model.board;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Score model.
 * Tests score tracking, lines cleared, and reset functionality.
 */
class ScoreTest {

    private Score score;

    @BeforeEach
    void setUp() {
        score = new Score();
    }

    @Test
    @DisplayName("Should initialize with zero score")
    void testInitialScore() {
        assertEquals(0, score.scoreProperty().get());
    }

    @Test
    @DisplayName("Should initialize with zero lines cleared")
    void testInitialLinesCleared() {
        assertEquals(0, score.getLinesCleared());
    }

    @Test
    @DisplayName("Should add score correctly")
    void testAddScore() {
        score.add(100);
        assertEquals(100, score.scoreProperty().get());

        score.add(50);
        assertEquals(150, score.scoreProperty().get());
    }

    @Test
    @DisplayName("Should add lines cleared correctly")
    void testAddLines() {
        score.addLines(1);
        assertEquals(1, score.getLinesCleared());

        score.addLines(3);
        assertEquals(4, score.getLinesCleared());
    }

    @Test
    @DisplayName("Should reset score and lines to zero")
    void testReset() {
        score.add(500);
        score.addLines(10);

        score.reset();

        assertEquals(0, score.scoreProperty().get());
        assertEquals(0, score.getLinesCleared());
    }

    @Test
    @DisplayName("Should handle multiple score additions")
    void testMultipleScoreAdditions() {
        score.add(10);
        score.add(20);
        score.add(30);
        score.add(40);

        assertEquals(100, score.scoreProperty().get());
    }

    @Test
    @DisplayName("Score property should be observable")
    void testScorePropertyIsObservable() {
        final int[] observedValue = {0};
        score.scoreProperty().addListener((obs, oldVal, newVal) -> {
            observedValue[0] = newVal.intValue();
        });

        score.add(250);

        assertEquals(250, observedValue[0]);
    }
}