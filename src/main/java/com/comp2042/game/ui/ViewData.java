package com.comp2042.game.ui;

import com.comp2042.game.control.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final List<int[][]> nextBricksData;
    private final int dropDistance;

    public ViewData(int[][] brickData, int xPosition, int yPosition, List<int[][]> nextBrickData,int dropDistance) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBricksData = nextBrickData;
        this.dropDistance = dropDistance;
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
}
