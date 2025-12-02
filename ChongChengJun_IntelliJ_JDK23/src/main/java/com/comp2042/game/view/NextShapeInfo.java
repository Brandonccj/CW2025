package com.comp2042.game.view;

import com.comp2042.game.util.MatrixOperations;

/**
 * Data class containing information about the next brick shape after rotation.
 * Holds the rotated shape matrix and the rotation position index.
 */

public final class NextShapeInfo {

    private final int[][] shape;
    private final int position;

    public NextShapeInfo(final int[][] shape, final int position) {
        this.shape = shape;
        this.position = position;
    }

    public int[][] getShape() {
        return MatrixOperations.copy(shape);
    }

    public int getPosition() {
        return position;
    }
}
