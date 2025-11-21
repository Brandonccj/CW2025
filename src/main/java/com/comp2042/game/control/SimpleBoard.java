package com.comp2042.game.control;

import com.comp2042.game.event.GameMode;
import com.comp2042.game.ui.NextShapeInfo;
import com.comp2042.game.ui.Score;
import com.comp2042.game.ui.ViewData;
import com.comp2042.game.event.ClearRow;
import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class SimpleBoard implements Board {

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;
    private Brick heldBrick = null;
    private boolean hasSwapped = false;
    private final GameMode gameMode;

    public SimpleBoard(int width, int height, GameMode mode) {
        this.width = width;
        this.height = height;
        this.gameMode = mode;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
    }
    @Override
    public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(0, 1);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }


    @Override
    public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(-1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

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

    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        currentOffset = new Point(3, 1);
        hasSwapped = false;
        return MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
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

    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

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


    @Override
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        createNewBrick();
    }

    public int dropDistance() {
        int[][] matrix = MatrixOperations.copy(currentGameMatrix);
        int[][] shape  = brickRotator.getCurrentShape();
        int dist = 0;
        while (!MatrixOperations.intersect(matrix, shape,
                (int) currentOffset.getX(),
                (int) currentOffset.getY() + dist + 1)) {
            dist++;
        }
        return dist;
    }

    @Override
    public boolean holdBrick() {
        if (hasSwapped) {
            return false;
        }

        Brick currentBrick = brickRotator.brick;

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

    @Override
    public void clearBoard() {
        currentGameMatrix = new int[width][height];
    }
}
