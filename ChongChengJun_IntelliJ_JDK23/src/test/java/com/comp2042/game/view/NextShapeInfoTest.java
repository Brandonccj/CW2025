package com.comp2042.game.view;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for NextShapeInfo.
 * Tests rotation information encapsulation.
 */
class NextShapeInfoTest {

    @Test
    @DisplayName("Should store shape correctly")
    void testGetShape() {
        int[][] shape = {{1, 1}, {0, 0}};
        NextShapeInfo info = new NextShapeInfo(shape, 2);

        int[][] retrieved = info.getShape();

        assertEquals(1, retrieved[0][0]);
        assertEquals(1, retrieved[0][1]);
    }

    @Test
    @DisplayName("Should store position correctly")
    void testGetPosition() {
        int[][] shape = {{1, 1}};
        NextShapeInfo info = new NextShapeInfo(shape, 3);

        assertEquals(3, info.getPosition());
    }

    @Test
    @DisplayName("Should return independent copy of shape")
    void testShapeIndependence() {
        int[][] shape = {{1, 1}, {2, 2}};
        NextShapeInfo info = new NextShapeInfo(shape, 0);

        int[][] retrieved = info.getShape();
        retrieved[0][0] = 99;

        assertEquals(1, info.getShape()[0][0]);
    }

    @Test
    @DisplayName("Should handle different rotation positions")
    void testDifferentPositions() {
        int[][] shape = {{1}};

        NextShapeInfo info0 = new NextShapeInfo(shape, 0);
        NextShapeInfo info1 = new NextShapeInfo(shape, 1);
        NextShapeInfo info3 = new NextShapeInfo(shape, 3);

        assertEquals(0, info0.getPosition());
        assertEquals(1, info1.getPosition());
        assertEquals(3, info3.getPosition());
    }
}