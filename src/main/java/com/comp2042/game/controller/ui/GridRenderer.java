package com.comp2042.game.controller.ui;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.function.Function;

/**
 * Handles rendering of preview and hold grids.
 */
public class GridRenderer {

    private static final int BRICK_SIZE = 20;

    private final GridPane nextBrickGrid;
    private final GridPane heldBrickGrid;

    public GridRenderer(GridPane nextBrickGrid, GridPane heldBrickGrid) {
        this.nextBrickGrid = nextBrickGrid;
        this.heldBrickGrid = heldBrickGrid;
    }

    public void initPreviewGrid() {
        nextBrickGrid.getChildren().clear();
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 5; col++) {
                Rectangle rec = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rec.setFill(Color.TRANSPARENT);
                rec.setArcWidth(9);
                rec.setArcHeight(9);
                nextBrickGrid.add(rec, col, row);
            }
        }
    }

    public void initHoldGrid() {
        heldBrickGrid.getChildren().clear();
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                Rectangle rec = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rec.setFill(Color.TRANSPARENT);
                rec.setArcWidth(9);
                rec.setArcHeight(9);
                heldBrickGrid.add(rec, c, r);
            }
        }
    }

    public void updatePreviewGrid(List<int[][]> nextBricks, Function<Integer, Paint> colorMapper) {
        for (javafx.scene.Node node : nextBrickGrid.getChildren()) {
            ((Rectangle) node).setFill(Color.TRANSPARENT);
        }

        for (int brickIndex = 0; brickIndex < Math.min(3, nextBricks.size()); brickIndex++) {
            int[][] brickData = nextBricks.get(brickIndex);

            int minRow = 4, maxRow = -1, minCol = 4, maxCol = -1;
            for (int r = 0; r < 4; r++) {
                for (int c = 0; c < 4; c++) {
                    if (brickData[r][c] != 0) {
                        minRow = Math.min(minRow, r);
                        maxRow = Math.max(maxRow, r);
                        minCol = Math.min(minCol, c);
                        maxCol = Math.max(maxCol, c);
                    }
                }
            }

            if (maxRow < 0) continue;

            int width = maxCol - minCol + 1;
            int height = maxRow - minRow + 1;
            int colOffset = (5 - width) / 2;
            int rowOffset = (4 - height) / 2;

            for (int r = minRow; r <= maxRow; r++) {
                for (int c = minCol; c <= maxCol; c++) {
                    if (brickData[r][c] != 0) {
                        int displayRow = brickIndex * 5 + rowOffset + (r - minRow);
                        int displayCol = colOffset + (c - minCol);

                        Rectangle cell = getRectangleAt(nextBrickGrid, displayCol, displayRow);
                        if (cell != null) {
                            cell.setFill(colorMapper.apply(brickData[r][c]));
                        }
                    }
                }
            }
        }
    }

    public void updateHoldGrid(int[][] heldBrick, Function<Integer, Paint> colorMapper) {
        for (int i = 0; i < heldBrickGrid.getChildren().size(); i++) {
            Rectangle cell = (Rectangle) heldBrickGrid.getChildren().get(i);
            cell.setFill(Color.TRANSPARENT);
        }

        if (heldBrick != null) {
            int offsetX = (4 - heldBrick[0].length) / 2;
            int offsetY = (4 - heldBrick.length) / 2;

            for (int r = 0; r < heldBrick.length; r++) {
                for (int c = 0; c < heldBrick[r].length; c++) {
                    if (heldBrick[r][c] != 0) {
                        int gridRow = r + offsetY;
                        int gridCol = c + offsetX;
                        int gridIndex = gridRow * 4 + gridCol;

                        if (gridIndex < heldBrickGrid.getChildren().size()) {
                            Rectangle cell = (Rectangle) heldBrickGrid.getChildren().get(gridIndex);
                            cell.setFill(colorMapper.apply(heldBrick[r][c]));
                        }
                    }
                }
            }
        }
    }

    private Rectangle getRectangleAt(GridPane grid, int col, int row) {
        for (javafx.scene.Node node : grid.getChildren()) {
            Integer nodeCol = GridPane.getColumnIndex(node);
            Integer nodeRow = GridPane.getRowIndex(node);
            if (nodeCol != null && nodeRow != null && nodeCol == col && nodeRow == row) {
                return (Rectangle) node;
            }
        }
        return null;
    }
}