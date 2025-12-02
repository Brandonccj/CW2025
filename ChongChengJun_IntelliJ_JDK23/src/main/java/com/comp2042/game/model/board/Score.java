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

    public void add(int i) {
        score.setValue(score.getValue() + i);
    }

    public void addLines(int lines) {
        linesCleared.setValue(linesCleared.getValue() + lines);
    }

    public int getLinesCleared() {
        return linesCleared.getValue();
    }

    public void reset() {
        score.setValue(0);
        linesCleared.setValue(0);
    }
}