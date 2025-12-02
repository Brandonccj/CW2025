package com.comp2042.game.model.brick;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Test class for all Brick implementations.
 * Tests shape matrices, rotation states, and immutability.
 */
class BrickTest {

    @Test
    @DisplayName("IBrick should have 2 rotation states")
    void testIBrickRotations() {
        Brick brick = new IBrick();
        List<int[][]> shapes = brick.getShapeMatrix();

        assertEquals(2, shapes.size());
        assertEquals(4, shapes.get(0).length);
        assertEquals(4, shapes.get(0)[0].length);
    }

    @Test
    @DisplayName("JBrick should have 4 rotation states")
    void testJBrickRotations() {
        Brick brick = new JBrick();
        List<int[][]> shapes = brick.getShapeMatrix();

        assertEquals(4, shapes.size());
    }

    @Test
    @DisplayName("LBrick should have 4 rotation states")
    void testLBrickRotations() {
        Brick brick = new LBrick();
        List<int[][]> shapes = brick.getShapeMatrix();

        assertEquals(4, shapes.size());
    }

    @Test
    @DisplayName("OBrick should have 1 rotation state")
    void testOBrickRotations() {
        Brick brick = new OBrick();
        List<int[][]> shapes = brick.getShapeMatrix();

        assertEquals(1, shapes.size());
    }

    @Test
    @DisplayName("SBrick should have 2 rotation states")
    void testSBrickRotations() {
        Brick brick = new SBrick();
        List<int[][]> shapes = brick.getShapeMatrix();

        assertEquals(2, shapes.size());
    }

    @Test
    @DisplayName("TBrick should have 4 rotation states")
    void testTBrickRotations() {
        Brick brick = new TBrick();
        List<int[][]> shapes = brick.getShapeMatrix();

        assertEquals(4, shapes.size());
    }

    @Test
    @DisplayName("ZBrick should have 2 rotation states")
    void testZBrickRotations() {
        Brick brick = new ZBrick();
        List<int[][]> shapes = brick.getShapeMatrix();

        assertEquals(2, shapes.size());
    }

    @Test
    @DisplayName("Each brick should have unique color code")
    void testUniqueBrickColors() {
        Brick iBrick = new IBrick();
        Brick jBrick = new JBrick();
        Brick lBrick = new LBrick();
        Brick oBrick = new OBrick();

        int iColor = getNonZeroValue(iBrick.getShapeMatrix().get(0));
        int jColor = getNonZeroValue(jBrick.getShapeMatrix().get(0));
        int lColor = getNonZeroValue(lBrick.getShapeMatrix().get(0));
        int oColor = getNonZeroValue(oBrick.getShapeMatrix().get(0));

        assertNotEquals(iColor, jColor);
        assertNotEquals(iColor, lColor);
        assertNotEquals(jColor, lColor);
        assertNotEquals(oColor, iColor);
    }

    @Test
    @DisplayName("Brick matrices should be 4x4")
    void testBrickMatrixSize() {
        Brick[] bricks = {
                new IBrick(), new JBrick(), new LBrick(), new OBrick(),
                new SBrick(), new TBrick(), new ZBrick()
        };

        for (Brick brick : bricks) {
            for (int[][] shape : brick.getShapeMatrix()) {
                assertEquals(4, shape.length);
                assertEquals(4, shape[0].length);
            }
        }
    }

    @Test
    @DisplayName("getShapeMatrix should return independent copy")
    void testShapeMatrixIndependence() {
        Brick brick = new IBrick();
        List<int[][]> shapes1 = brick.getShapeMatrix();
        List<int[][]> shapes2 = brick.getShapeMatrix();

        shapes1.get(0)[0][0] = 99;

        assertNotEquals(99, shapes2.get(0)[0][0]);
    }

    private int getNonZeroValue(int[][] matrix) {
        for (int[] row : matrix) {
            for (int val : row) {
                if (val != 0) return val;
            }
        }
        return 0;
    }
}