package com.comp2042.game.util;

import com.comp2042.game.event.ClearRow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for MatrixOperations utility methods.
 * Tests collision detection, matrix operations, and line clearing logic.
 */
class MatrixOperationsTest {

    @Test
    @DisplayName("Should detect intersection when brick overlaps filled cells")
    void testIntersectWithFilledCells() {
        int[][] matrix = {
                {0, 0, 0},
                {0, 1, 0},
                {0, 0, 0}
        };
        int[][] brick = {
                {1, 1},
                {0, 0}
        };

        assertTrue(MatrixOperations.intersect(matrix, brick, 0, 1));
    }

    @Test
    @DisplayName("Should not detect intersection when brick has empty space")
    void testNoIntersectWithEmptySpace() {
        int[][] matrix = {
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0}
        };
        int[][] brick = {
                {1, 1},
                {0, 0}
        };

        assertFalse(MatrixOperations.intersect(matrix, brick, 0, 0));
    }

    @Test
    @DisplayName("Should detect intersection when brick goes out of bounds horizontally")
    void testIntersectOutOfBoundsHorizontal() {
        int[][] matrix = {
                {0, 0, 0},
                {0, 0, 0}
        };
        int[][] brick = {
                {1, 1},
                {0, 0}
        };

        assertTrue(MatrixOperations.intersect(matrix, brick, 2, 0));
    }

    @Test
    @DisplayName("Should detect intersection when brick goes out of bounds vertically")
    void testIntersectOutOfBoundsVertical() {
        int[][] matrix = {
                {0, 0},
                {0, 0}
        };
        int[][] brick = {
                {1, 0},
                {1, 0}
        };

        assertTrue(MatrixOperations.intersect(matrix, brick, 0, 2));
    }

    @Test
    @DisplayName("Should create independent copy of matrix")
    void testCopyCreatesIndependentMatrix() {
        int[][] original = {
                {1, 2, 3},
                {4, 5, 6}
        };

        int[][] copy = MatrixOperations.copy(original);
        copy[0][0] = 99;

        assertEquals(1, original[0][0]);
        assertEquals(99, copy[0][0]);
    }

    @Test
    @DisplayName("Should merge brick into matrix correctly")
    void testMergeBrickIntoMatrix() {
        int[][] matrix = {
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {1, 0, 0, 1}
        };
        int[][] brick = {
                {2, 2},
                {0, 0}
        };

        int[][] result = MatrixOperations.merge(matrix, brick, 1, 1);

        assertEquals(2, result[1][1]);
        assertEquals(2, result[1][2]);
        assertEquals(1, result[2][0]);
    }

    @Test
    @DisplayName("Should clear single complete row")
    void testClearSingleRow() {
        int[][] matrix = {
                {0, 0, 0},
                {1, 1, 1},
                {0, 1, 0}
        };

        ClearRow result = MatrixOperations.checkRemoving(matrix);

        assertEquals(1, result.getLinesRemoved());
        assertEquals(50, result.getScoreBonus()); // 50 * 1^2
        assertEquals(0, result.getNewMatrix()[2][0]);
        assertEquals(1, result.getNewMatrix()[2][1]);
    }

    @Test
    @DisplayName("Should clear multiple complete rows")
    void testClearMultipleRows() {
        int[][] matrix = {
                {0, 0, 0},
                {1, 1, 1},
                {2, 2, 2}
        };

        ClearRow result = MatrixOperations.checkRemoving(matrix);

        assertEquals(2, result.getLinesRemoved());
        assertEquals(200, result.getScoreBonus()); // 50 * 2^2
    }

    @Test
    @DisplayName("Should not clear incomplete rows")
    void testNoRowsCleared() {
        int[][] matrix = {
                {0, 1, 0},
                {1, 0, 1},
                {0, 1, 0}
        };

        ClearRow result = MatrixOperations.checkRemoving(matrix);

        assertEquals(0, result.getLinesRemoved());
        assertEquals(0, result.getScoreBonus());
    }

    @Test
    @DisplayName("Should create deep copy of list")
    void testDeepCopyList() {
        List<int[][]> original = new ArrayList<>();
        original.add(new int[][]{{1, 2}, {3, 4}});
        original.add(new int[][]{{5, 6}, {7, 8}});

        List<int[][]> copy = MatrixOperations.deepCopyList(original);
        copy.get(0)[0][0] = 99;

        assertEquals(1, original.get(0)[0][0]);
        assertEquals(99, copy.get(0)[0][0]);
        assertEquals(2, copy.size());
    }

    @Test
    @DisplayName("Should calculate correct score for quad clear")
    void testQuadClearScoring() {
        int[][] matrix = {
                {1, 1, 1},
                {2, 2, 2},
                {3, 3, 3},
                {4, 4, 4}
        };

        ClearRow result = MatrixOperations.checkRemoving(matrix);

        assertEquals(4, result.getLinesRemoved());
        assertEquals(800, result.getScoreBonus()); // 50 * 4^2 = 800
    }
}