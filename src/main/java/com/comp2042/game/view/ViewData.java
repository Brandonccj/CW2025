package com.comp2042.game.view;

import com.comp2042.game.util.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable data transfer object containing all visual state for rendering.
 * Holds current brick data, position, next bricks preview, and held brick information.
 */

public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final List<int[][]> nextBricksData;
    private final int dropDistance;
    private final int[][] heldBrickData;

    public ViewData(int[][] brickData, int xPosition, int yPosition, List<int[][]> nextBrickData,int dropDistance,int[][] heldBrickData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBricksData = nextBrickData;
        this.dropDistance = dropDistance;
        this.heldBrickData = heldBrickData;
    }

    public int getDropDistance() {
        return dropDistance;
    }

    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public List<int[][]> getNextBricksData() {
        List<int[][]> copy = new ArrayList<>();
        for (int[][] brick : nextBricksData) {
            copy.add(MatrixOperations.copy(brick));
        }
        return copy;
    }

    public int[][] getHeldBrickData() {
        return heldBrickData != null ? MatrixOperations.copy(heldBrickData) : null;
    }
}
