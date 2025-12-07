package com.comp2042.game.controller.game;

import com.comp2042.game.event.ClearRow;
import com.comp2042.game.event.GameMode;
import com.comp2042.game.model.board.Board;
import com.comp2042.game.model.board.Score;
import com.comp2042.game.model.brick.Brick;
import com.comp2042.game.model.brick.BrickGenerator;
import com.comp2042.game.model.brick.RandomBrickGenerator;
import com.comp2042.game.util.MatrixOperations;
import com.comp2042.game.view.NextShapeInfo;
import com.comp2042.game.view.ViewData;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Main board controller managing the game board state and brick operations.
 * Handles brick movement, rotation, collision detection, and line clearing.
 */
public class BoardController implements Board {

    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;
    private Brick heldBrick = null;
    private boolean hasSwapped = false;
    private final GameMode gameMode;

    /**
     * Creates a new board controller with specified dimensions and game mode.
     * Initializes the board matrix, brick generator, and scoring system.
     *
     * @param width the width of the game board
     * @param height the height of the game board
     * @param mode the game mode (NORMAL or ZEN)
     */
    public BoardController(int width, int height, GameMode mode) {
        this.gameMode = mode;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
    }

    /**
     * Attempts to move the current brick down by one row.
     *
     * @return true if the move was successful, false if blocked
     */
    @Override
    public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(0, 1);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(),
                (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    /**
     * Attempts to move the current brick left by one column.
     *
     * @return true if the move was successful, false if blocked
     */
    @Override
    public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(-1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(),
                (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    /**
     * Attempts to move the current brick right by one column.
     *
     * @return true if the move was successful, false if blocked
     */
    @Override
    public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(),
                (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    /**
     * Attempts to rotate the current brick counter-clockwise.
     * Implements wall kick system to allow rotation near boundaries.
     *
     * @return true if rotation was successful, false if blocked
     */
    @Override
    public boolean rotateLeftBrick() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        NextShapeInfo nextShape = brickRotator.getNextShape();
        Point originalOffset = new Point(currentOffset);

        int[] kicks = {0, -1, 1, -2, 2, -3, 3};

        for (int dx : kicks) {
            Point kicked = new Point(originalOffset);
            kicked.translate(dx, 0);

            boolean conflict = MatrixOperations.intersect(currentMatrix,
                    nextShape.getShape(),
                    (int) kicked.getX(),
                    (int) kicked.getY());
            if (!conflict) {
                currentOffset = kicked;
                brickRotator.setCurrentShape(nextShape.getPosition());
                return true;
            }
        }

        return false;
    }

    /**
     * Creates and spawns a new brick at the top of the board.
     *
     * @return true if the new brick collides (game over), false otherwise
     */
    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        currentOffset = new Point(3, 1);
        hasSwapped = false;
        return MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(),
                (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override
    public ViewData getViewData() {
        List<Brick> upcomingBricks = brickGenerator.getNextBricks(3);
        List<int[][]> nextBricksData = new ArrayList<>();

        for (Brick brick : upcomingBricks) {
            nextBricksData.add(brick.getShapeMatrix().get(0));
        }

        return new ViewData(
                brickRotator.getCurrentShape(),
                (int) currentOffset.getX(),
                (int) currentOffset.getY(),
                nextBricksData,
                dropDistance(),
                getHeldBrickData()
        );
    }

    /**
     * Merges the current brick into the board background matrix.
     * Called when a brick can no longer move down.
     */
    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(),
                (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    /**
     * Checks for and clears any complete rows.
     *
     * @return ClearRow object containing cleared row information and score bonus
     */
    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;
    }

    @Override
    public Score getScore() {
        return score;
    }

    /**
     * Resets the board for a new game.
     * Clears the board matrix, resets score, and spawns first brick.
     */
    @Override
    public void newGame() {
        currentGameMatrix = new int[currentGameMatrix.length][currentGameMatrix[0].length];
        score.reset();
        createNewBrick();
    }

    /**
     * Attempts to hold the current brick and swap with previously held brick.
     * Can only be used once per brick placement.
     *
     * @return true if hold was successful, false if already used or collision
     */
    @Override
    public boolean holdBrick() {
        if (hasSwapped) {
            return false;
        }

        Brick currentBrick = brickRotator.getBrick();

        if (heldBrick == null) {
            heldBrick = currentBrick;
            createNewBrick();
        } else {
            Brick temp = heldBrick;
            heldBrick = currentBrick;
            brickRotator.setBrick(temp);
            currentOffset = new Point(3, 2);

            if (MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(),
                    (int) currentOffset.getX(), (int) currentOffset.getY())) {
                heldBrick = temp;
                brickRotator.setBrick(currentBrick);
                return false;
            }
        }

        hasSwapped = true;
        return true;
    }

    @Override
    public int[][] getHeldBrickData() {
        if (heldBrick == null) {
            return null;
        }
        return heldBrick.getShapeMatrix().get(0);
    }

    @Override
    public GameMode getGameMode() {
        return gameMode;
    }

    /**
     * Clears the entire game board matrix.
     * Used in Zen mode when board fills up.
     */
    @Override
    public void clearBoard() {
        currentGameMatrix = new int[currentGameMatrix.length][currentGameMatrix[0].length];
    }

    /**
     * Calculates the distance the current brick can drop before collision.
     * Used for ghost piece rendering.
     *
     * @return the number of rows the brick can drop
     */
    private int dropDistance() {
        int[][] matrix = MatrixOperations.copy(currentGameMatrix);
        int[][] shape = brickRotator.getCurrentShape();
        int dist = 0;
        while (!MatrixOperations.intersect(matrix, shape,
                (int) currentOffset.getX(),
                (int) currentOffset.getY() + dist + 1)) {
            dist++;
        }
        return dist;
    }
}