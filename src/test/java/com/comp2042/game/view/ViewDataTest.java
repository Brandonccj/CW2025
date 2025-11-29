package com.comp2042.game.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for ViewData.
 * Tests immutability and data encapsulation.
 */
class ViewDataTest {

    private ViewData viewData;
    private int[][] brickData;
    private List<int[][]> nextBricks;
    private int[][] heldBrick;

    @BeforeEach
    void setUp() {
        brickData = new int[][]{{1, 1}, {1, 1}};
        nextBricks = new ArrayList<>();
        nextBricks.add(new int[][]{{2, 2}, {0, 0}});
        nextBricks.add(new int[][]{{3, 3}, {3, 0}});
        heldBrick = new int[][]{{4, 4}, {0, 0}};

        viewData = new ViewData(brickData, 5, 10, nextBricks, 3, heldBrick);
    }

    @Test
    @DisplayName("Should return correct x position")
    void testGetXPosition() {
        assertEquals(5, viewData.getxPosition());
    }

    @Test
    @DisplayName("Should return correct y position")
    void testGetYPosition() {
        assertEquals(10, viewData.getyPosition());
    }

    @Test
    @DisplayName("Should return correct drop distance")
    void testGetDropDistance() {
        assertEquals(3, viewData.getDropDistance());
    }

    @Test
    @DisplayName("Should return independent copy of brick data")
    void testBrickDataIndependence() {
        int[][] retrieved = viewData.getBrickData();
        retrieved[0][0] = 99;

        assertEquals(1, viewData.getBrickData()[0][0]);
    }

    @Test
    @DisplayName("Should return independent copy of next bricks data")
    void testNextBricksDataIndependence() {
        List<int[][]> retrieved = viewData.getNextBricksData();
        retrieved.get(0)[0][0] = 99;

        assertEquals(2, viewData.getNextBricksData().get(0)[0][0]);
    }

    @Test
    @DisplayName("Should return independent copy of held brick data")
    void testHeldBrickDataIndependence() {
        int[][] retrieved = viewData.getHeldBrickData();
        retrieved[0][0] = 99;

        assertEquals(4, viewData.getHeldBrickData()[0][0]);
    }

    @Test
    @DisplayName("Should handle null held brick")
    void testNullHeldBrick() {
        ViewData noHeld = new ViewData(brickData, 5, 10, nextBricks, 3, null);

        assertNull(noHeld.getHeldBrickData());
    }

    @Test
    @DisplayName("Should return correct number of next bricks")
    void testNextBricksCount() {
        List<int[][]> retrieved = viewData.getNextBricksData();

        assertEquals(2, retrieved.size());
    }

    @Test
    @DisplayName("Should preserve brick dimensions")
    void testBrickDimensions() {
        int[][] retrieved = viewData.getBrickData();

        assertEquals(2, retrieved.length);
        assertEquals(2, retrieved[0].length);
    }
}