package com.comp2042.game.controller.ui;

import com.comp2042.game.event.GameMode;
import com.comp2042.game.view.ViewData;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import java.util.function.Function;

/**
 * Handles rendering of bricks, shadows, and game board background.
 */
public class BrickRenderer {

    private static final int BRICK_SIZE = 20;

    private final GridPane gamePanel;
    private Rectangle[][] displayMatrix;
    private Rectangle[][] rectangles;
    private Rectangle[][] shadowRectangles;

    /**
     * Creates a new brick renderer for the specified game panel.
     * Initializes shadow rectangle arrays for ghost piece rendering.
     *
     * @param gamePanel the grid pane to render bricks on
     */
    public BrickRenderer(GridPane gamePanel) {
        this.gamePanel = gamePanel;

        // Initialize shadow rectangles
        shadowRectangles = new Rectangle[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                shadowRectangles[i][j] = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                shadowRectangles[i][j].setFill(Color.TRANSPARENT);
                shadowRectangles[i][j].setArcWidth(6);
                shadowRectangles[i][j].setArcHeight(6);
                shadowRectangles[i][j].setOpacity(0.35);
            }
        }
    }

    /**
     * Initializes the display matrix for the game board background.
     * Creates rectangle objects for each cell of the visible board area.
     *
     * @param boardMatrix the board matrix to create display for
     */
    public void initializeDisplayMatrix(int[][] boardMatrix) {
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }
    }

    /**
     * Initializes rectangle objects for rendering the current brick.
     *
     * @param brick the brick view data
     * @param colorMapper function to map color codes to Paint objects
     */
    public void initializeBrickRectangles(ViewData brick, Function<Integer, Paint> colorMapper) {
        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(colorMapper.apply(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
            }
        }
    }

    /**
     * Updates the brick rendering with new position and color.
     * Also updates the ghost/shadow piece position.
     *
     * @param brick the updated brick view data
     * @param colorMapper function to map brick color codes
     * @param ghostColorMapper function to map ghost/shadow color codes
     * @param gameMode the current game mode
     */
    public void refreshBrick(ViewData brick, Function<Integer, Paint> colorMapper,
                             Function<Integer, Paint> ghostColorMapper, GameMode gameMode) {
        // Clear old brick
        for (Rectangle[] row : rectangles) {
            for (Rectangle r : row) {
                gamePanel.getChildren().remove(r);
                r.setFill(Color.TRANSPARENT);
            }
        }

        // Draw new brick
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                if (brick.getBrickData()[i][j] != 0) {
                    int gridX = brick.getxPosition() + j;
                    int gridY = brick.getyPosition() + i;
                    if (gridY >= 2 && gridY < 25) {
                        rectangles[i][j].setFill(colorMapper.apply(brick.getBrickData()[i][j]));
                        rectangles[i][j].setArcWidth(6);
                        rectangles[i][j].setArcHeight(6);
                        gamePanel.add(rectangles[i][j], gridX, gridY - 2);
                    }
                }
            }
        }

        updateShadow(brick, ghostColorMapper);
    }

    /**
     * Updates the ghost/shadow piece rendering showing drop position.
     *
     * @param brick the current brick view data
     * @param ghostColorMapper function to map ghost color codes
     */
    private void updateShadow(ViewData brick, Function<Integer, Paint> ghostColorMapper) {
        // Clear old shadow
        for (int i = 0; i < shadowRectangles.length; i++) {
            for (int j = 0; j < shadowRectangles[i].length; j++) {
                gamePanel.getChildren().remove(shadowRectangles[i][j]);
                shadowRectangles[i][j].setFill(Color.TRANSPARENT);
            }
        }

        int shadowYPosition = brick.getyPosition() + brick.getDropDistance();

        if (shadowYPosition != brick.getyPosition()) {
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    if (brick.getBrickData()[i][j] != 0) {
                        int gridX = brick.getxPosition() + j;
                        int gridY = shadowYPosition + i;
                        if (gridY >= 2 && gridY < 25) {
                            shadowRectangles[i][j].setFill(ghostColorMapper.apply(brick.getBrickData()[i][j]));
                            shadowRectangles[i][j].setArcHeight(6);
                            shadowRectangles[i][j].setArcWidth(6);
                            shadowRectangles[i][j].setOpacity(0.35);
                            shadowRectangles[i][j].setStroke(Color.WHITE);
                            shadowRectangles[i][j].setStrokeWidth(1);
                            shadowRectangles[i][j].setStrokeType(StrokeType.INSIDE);
                            gamePanel.add(shadowRectangles[i][j], gridX, gridY - 2);
                        }
                    }
                }
            }
        }
    }

    /**
     * Refreshes the entire game board background display.
     * Updates all cells with current board state.
     *
     * @param board the updated board matrix
     * @param colorMapper function to map color codes to Paint objects
     */
    public void refreshGameBackground(int[][] board, Function<Integer, Paint> colorMapper) {
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                displayMatrix[i][j].setFill(colorMapper.apply(board[i][j]));
                displayMatrix[i][j].setArcHeight(9);
                displayMatrix[i][j].setArcWidth(9);
            }
        }
    }
}