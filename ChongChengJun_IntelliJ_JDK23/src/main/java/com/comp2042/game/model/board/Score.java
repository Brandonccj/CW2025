package com.comp2042.game.model.board;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Model class representing game score and lines cleared.
 * Uses JavaFX properties for binding to UI components.
 */
public final class Score {

    private final IntegerProperty score = new SimpleIntegerProperty(0);
    private final IntegerProperty linesCleared = new SimpleIntegerProperty(0);

    public IntegerProperty scoreProperty() {
        return score;
    }

    public IntegerProperty linesClearedProperty() {
        return linesCleared;
    }

    /**
     * Adds points to the current score.
     *
     * @param i the number of points to add
     */
    public void add(int i) {
        score.setValue(score.getValue() + i);
    }

    /**
     * Adds cleared lines to the total count.
     *
     * @param lines the number of lines to add
     */
    public void addLines(int lines) {
        linesCleared.setValue(linesCleared.getValue() + lines);
    }

    public int getLinesCleared() {
        return linesCleared.getValue();
    }

    /**
     * Resets score and lines cleared to zero.
     */
    public void reset() {
        score.setValue(0);
        linesCleared.setValue(0);
    }
}