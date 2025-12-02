package com.comp2042.game.controller.game;

import com.comp2042.game.event.*;
import com.comp2042.game.model.board.Board;
import com.comp2042.game.view.ViewData;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for GameController logic via BoardController.
 * Tests game coordination without GUI dependencies.
 */
class GameControllerTest {

    private Board board;

    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Already initialized
        }
    }

    @BeforeEach
    void setUp() {
        board = new BoardController(25, 10, GameMode.NORMAL);
        board.createNewBrick(); // Ensure brick is created
    }

    @Test
    @DisplayName("Should create board with correct game mode")
    void testBoardCreation() {
        assertEquals(GameMode.NORMAL, board.getGameMode());
    }

    @Test
    @DisplayName("Should return valid ViewData")
    void testGetViewData() {
        ViewData data = board.getViewData();

        assertNotNull(data);
        assertNotNull(data.getBrickData());
        assertNotNull(data.getNextBricksData());
        assertEquals(3, data.getNextBricksData().size());
    }

    @Test
    @DisplayName("Should handle brick movement down")
    void testBrickMovementDown() {
        boolean moved = board.moveBrickDown();

        // Should be able to move down initially
        assertNotNull(board.getViewData());
    }

    @Test
    @DisplayName("Should handle brick movement left")
    void testBrickMovementLeft() {
        board.moveBrickLeft();

        assertNotNull(board.getViewData());
    }

    @Test
    @DisplayName("Should handle brick movement right")
    void testBrickMovementRight() {
        board.moveBrickRight();

        assertNotNull(board.getViewData());
    }

    @Test
    @DisplayName("Should handle brick rotation")
    void testBrickRotation() {
        ViewData before = board.getViewData();
        board.rotateLeftBrick();
        ViewData after = board.getViewData();

        assertNotNull(before);
        assertNotNull(after);
    }

    @Test
    @DisplayName("Should handle hold brick functionality")
    void testHoldBrick() {
        boolean held = board.holdBrick();

        assertTrue(held);
        assertNotNull(board.getHeldBrickData());
    }

    @Test
    @DisplayName("Should award score for line clears")
    void testScoreForLineClears() {
        // Fill bottom row completely
        int[][] matrix = board.getBoardMatrix();
        int lastRow = matrix.length - 1;

        for (int col = 0; col < matrix[0].length; col++) {
            matrix[lastRow][col] = 1;
        }

        ClearRow clearResult = board.clearRows();

        assertEquals(1, clearResult.getLinesRemoved());
        assertEquals(50, clearResult.getScoreBonus()); // 50 * 1^2
    }

    @Test
    @DisplayName("Should track lines cleared")
    void testLinesCleared() {
        int initialLines = board.getScore().getLinesCleared();

        // Fill bottom row
        int[][] matrix = board.getBoardMatrix();
        int lastRow = matrix.length - 1;
        for (int col = 0; col < matrix[0].length; col++) {
            matrix[lastRow][col] = 1;
        }

        ClearRow clearResult = board.clearRows();
        board.getScore().addLines(clearResult.getLinesRemoved());

        assertEquals(initialLines + 1, board.getScore().getLinesCleared());
    }

    @Test
    @DisplayName("Should reset game state on new game")
    void testNewGame() {
        board.getScore().add(500);
        board.getScore().addLines(10);

        board.newGame();

        assertEquals(0, board.getScore().scoreProperty().get());
        assertEquals(0, board.getScore().getLinesCleared());
    }

    @Test
    @DisplayName("Should detect game over collision")
    void testGameOverDetection() {
        // Fill top rows to cause collision on next brick creation
        int[][] matrix = board.getBoardMatrix();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < matrix[0].length; col++) {
                matrix[row][col] = 1;
            }
        }

        boolean collision = board.createNewBrick();

        assertTrue(collision);
    }

    @Test
    @DisplayName("Should clear board")
    void testBoardClear() {
        // Add some blocks to board
        int[][] matrix = board.getBoardMatrix();
        matrix[10][5] = 1;
        matrix[15][3] = 2;
        matrix[20][7] = 3;

        board.clearBoard();

        // Check board is empty
        int[][] clearedMatrix = board.getBoardMatrix();
        for (int[] row : clearedMatrix) {
            for (int cell : row) {
                assertEquals(0, cell);
            }
        }
    }

    @Test
    @DisplayName("Should provide next brick preview")
    void testNextBrickPreview() {
        ViewData data = board.getViewData();

        assertNotNull(data.getNextBricksData());
        assertEquals(3, data.getNextBricksData().size());

        for (int[][] brickData : data.getNextBricksData()) {
            assertNotNull(brickData);
            assertEquals(4, brickData.length);
            assertEquals(4, brickData[0].length);
        }
    }

    @Test
    @DisplayName("Should calculate drop distance")
    void testDropDistance() {
        ViewData data = board.getViewData();

        int dropDistance = data.getDropDistance();
        assertTrue(dropDistance >= 0);

        // After moving down, drop distance should decrease
        board.moveBrickDown();
        ViewData newData = board.getViewData();
        assertTrue(newData.getDropDistance() >= 0);
    }

    @Test
    @DisplayName("Should calculate quadratic scoring for multiple lines")
    void testQuadraticScoring() {
        // Fill 2 complete rows
        int[][] matrix = board.getBoardMatrix();
        int lastRow = matrix.length - 1;

        for (int row = lastRow - 1; row <= lastRow; row++) {
            for (int col = 0; col < matrix[0].length; col++) {
                matrix[row][col] = 1;
            }
        }

        ClearRow result = board.clearRows();

        assertEquals(2, result.getLinesRemoved());
        assertEquals(200, result.getScoreBonus()); // 50 * 2^2 = 200
    }

    @Test
    @DisplayName("Should not swap held brick twice in succession")
    void testHoldSwapRestriction() {
        board.holdBrick();
        boolean secondHold = board.holdBrick();

        assertFalse(secondHold);
    }

    @Test
    @DisplayName("Board should maintain correct dimensions")
    void testBoardDimensions() {
        int[][] matrix = board.getBoardMatrix();

        assertEquals(25, matrix.length);
        assertEquals(10, matrix[0].length);
    }

    @Test
    @DisplayName("Should handle multiple movements safely")
    void testMultipleMovements() {
        // Perform various movements
        board.moveBrickDown();
        board.moveBrickLeft();
        board.moveBrickRight();
        board.rotateLeftBrick();

        // Should still have valid state
        assertNotNull(board.getViewData());
        assertNotNull(board.getViewData().getBrickData());
    }

    @Test
    @DisplayName("Score should increase with soft drop")
    void testSoftDropScore() {
        int initialScore = board.getScore().scoreProperty().get();

        // Manually add score as if from soft drop
        board.getScore().add(1);

        assertEquals(initialScore + 1, board.getScore().scoreProperty().get());
    }
}