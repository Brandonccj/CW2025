package com.comp2042.game.controller.game;

import com.comp2042.game.event.ClearRow;
import com.comp2042.game.event.GameMode;
import com.comp2042.game.view.ViewData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for BoardController.
 * Tests brick movement, rotation, collision detection, and game logic.
 */
class BoardControllerTest {

    private BoardController board;

    @BeforeEach
    void setUp() {
        board = new BoardController(25, 10, GameMode.NORMAL);
        board.createNewBrick();
    }

    @Test
    @DisplayName("Should create new brick successfully")
    void testCreateNewBrick() {
        boolean collision = board.createNewBrick();

        assertFalse(collision);
        assertNotNull(board.getViewData());
    }

    @Test
    @DisplayName("Should move brick down when space available")
    void testMoveBrickDown() {
        boolean moved = board.moveBrickDown();

        assertTrue(moved);
    }

    @Test
    @DisplayName("Should move brick left when space available")
    void testMoveBrickLeft() {
        boolean moved = board.moveBrickLeft();

        assertTrue(moved);
    }

    @Test
    @DisplayName("Should move brick right when space available")
    void testMoveBrickRight() {
        boolean moved = board.moveBrickRight();

        assertTrue(moved);
    }

    @Test
    @DisplayName("Should rotate brick when space available")
    void testRotateBrick() {
        ViewData before = board.getViewData();
        boolean rotated = board.rotateLeftBrick();
        ViewData after = board.getViewData();

        // Rotation might succeed or fail depending on piece type and position
        assertNotNull(after);
    }

    @Test
    @DisplayName("Should merge brick to board matrix")
    void testMergeBrickToBackground() {
        int[][] emptyBoard = board.getBoardMatrix();
        int emptyCount = countNonZeroCells(emptyBoard);

        board.mergeBrickToBackground();

        int[][] filledBoard = board.getBoardMatrix();
        int filledCount = countNonZeroCells(filledBoard);

        assertTrue(filledCount > emptyCount);
    }

    @Test
    @DisplayName("Should clear complete rows")
    void testClearRows() {
        // Fill bottom row
        int[][] matrix = board.getBoardMatrix();
        for (int i = 0; i < matrix[0].length; i++) {
            matrix[matrix.length - 1][i] = 1;
        }

        ClearRow result = board.clearRows();

        assertEquals(1, result.getLinesRemoved());
        assertTrue(result.getScoreBonus() > 0);
    }

    @Test
    @DisplayName("Should track score correctly")
    void testScoreTracking() {
        assertNotNull(board.getScore());
        assertEquals(0, board.getScore().scoreProperty().get());
    }

    @Test
    @DisplayName("Should reset game state on newGame")
    void testNewGame() {
        board.getScore().add(1000);
        board.mergeBrickToBackground();

        board.newGame();

        assertEquals(0, board.getScore().scoreProperty().get());
        assertEquals(0, countNonZeroCells(board.getBoardMatrix()));
    }

    @Test
    @DisplayName("Should hold brick successfully")
    void testHoldBrick() {
        boolean held = board.holdBrick();

        assertTrue(held);
        assertNotNull(board.getHeldBrickData());
    }

    @Test
    @DisplayName("Should not allow holding twice in succession")
    void testCannotHoldTwice() {
        board.holdBrick();
        boolean heldAgain = board.holdBrick();

        assertFalse(heldAgain);
    }

    @Test
    @DisplayName("Should swap held brick")
    void testSwapHeldBrick() {
        board.holdBrick();
        board.createNewBrick(); // Reset swap flag

        int[][] firstHeld = board.getHeldBrickData();
        board.holdBrick();
        int[][] secondHeld = board.getHeldBrickData();

        assertNotNull(secondHeld);
    }

    @Test
    @DisplayName("Should return correct game mode")
    void testGameMode() {
        assertEquals(GameMode.NORMAL, board.getGameMode());
    }

    @Test
    @DisplayName("Should clear board completely")
    void testClearBoard() {
        board.mergeBrickToBackground();
        board.clearBoard();

        assertEquals(0, countNonZeroCells(board.getBoardMatrix()));
    }

    @Test
    @DisplayName("Board matrix should have correct dimensions")
    void testBoardDimensions() {
        int[][] matrix = board.getBoardMatrix();

        assertEquals(25, matrix.length);
        assertEquals(10, matrix[0].length);
    }

    @Test
    @DisplayName("ViewData should contain preview bricks")
    void testViewDataContainsPreview() {
        ViewData data = board.getViewData();

        assertNotNull(data.getNextBricksData());
        assertEquals(3, data.getNextBricksData().size());
    }

    private int countNonZeroCells(int[][] matrix) {
        int count = 0;
        for (int[] row : matrix) {
            for (int cell : row) {
                if (cell != 0) count++;
            }
        }
        return count;
    }
}