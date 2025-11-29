package com.comp2042.game.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Test class for HighScoreManager.
 * Tests high score persistence and file operations.
 */
class HighScoreManagerTest {

    @AfterEach
    void cleanup() {
        try {
            Files.deleteIfExists(Paths.get("highscore.txt"));
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }

    @Test
    @DisplayName("Should return 0 when no high score file exists")
    void testLoadHighScoreNoFile() {
        try {
            Files.deleteIfExists(Paths.get("highscore.txt"));
        } catch (Exception e) {
            // Ignore
        }

        int score = HighScoreManager.loadHighScore();

        assertEquals(0, score);
    }

    @Test
    @DisplayName("Should save and load high score correctly")
    void testSaveAndLoadHighScore() {
        int testScore = 12345;

        HighScoreManager.saveHighScore(testScore);
        int loadedScore = HighScoreManager.loadHighScore();

        assertEquals(testScore, loadedScore);
    }

    @Test
    @DisplayName("Should overwrite existing high score")
    void testOverwriteHighScore() {
        HighScoreManager.saveHighScore(1000);
        HighScoreManager.saveHighScore(2000);

        int loadedScore = HighScoreManager.loadHighScore();

        assertEquals(2000, loadedScore);
    }

    @Test
    @DisplayName("Should handle large high scores")
    void testLargeHighScore() {
        int largeScore = 999999;

        HighScoreManager.saveHighScore(largeScore);
        int loadedScore = HighScoreManager.loadHighScore();

        assertEquals(largeScore, loadedScore);
    }

    @Test
    @DisplayName("Should handle zero high score")
    void testZeroHighScore() {
        HighScoreManager.saveHighScore(0);
        int loadedScore = HighScoreManager.loadHighScore();

        assertEquals(0, loadedScore);
    }
}