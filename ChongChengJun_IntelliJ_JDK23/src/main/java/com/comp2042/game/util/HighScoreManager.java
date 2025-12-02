package com.comp2042.game.util;

import java.io.*;
import java.nio.file.*;

/**
 * Utility class for managing high score persistence.
 * Handles loading and saving high scores to file system.
 */
public class HighScoreManager {
    private static final String HIGH_SCORE_FILE = "highscore.txt";

    // Private constructor to prevent instantiation
    private HighScoreManager() {
    }

    public static int loadHighScore() {
        try {
            if (Files.exists(Paths.get(HIGH_SCORE_FILE))) {
                String content = Files.readString(Paths.get(HIGH_SCORE_FILE));
                return Integer.parseInt(content.trim());
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading high score: " + e.getMessage());
        }
        return 0;
    }

    public static void saveHighScore(int score) {
        try {
            Files.writeString(Paths.get(HIGH_SCORE_FILE), String.valueOf(score));
        } catch (IOException e) {
            System.err.println("Error saving high score: " + e.getMessage());
        }
    }
}