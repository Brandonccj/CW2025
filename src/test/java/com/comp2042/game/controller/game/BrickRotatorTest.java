package com.comp2042.game.controller.game;

import com.comp2042.game.model.brick.*;
import com.comp2042.game.view.NextShapeInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for BrickRotator.
 * Tests rotation state management and shape transitions.
 */
class BrickRotatorTest {

    private BrickRotator rotator;

    @BeforeEach
    void setUp() {
        rotator = new BrickRotator();
    }

    @Test
    @DisplayName("Should start at rotation state 0")
    void testInitialRotationState() {
        Brick brick = new IBrick();
        rotator.setBrick(brick);

        assertNotNull(rotator.getCurrentShape());
    }

    @Test
    @DisplayName("Should get next rotation state for IBrick")
    void testGetNextShapeIBrick() {
        Brick brick = new IBrick();
        rotator.setBrick(brick);

        NextShapeInfo nextInfo = rotator.getNextShape();

        assertNotNull(nextInfo);
        assertNotNull(nextInfo.getShape());
        assertEquals(1, nextInfo.getPosition());
    }

    @Test
    @DisplayName("Should cycle through all rotations")
    void testRotationCycle() {
        Brick brick = new JBrick(); // Has 4 rotations
        rotator.setBrick(brick);

        // Cycle through all 4 rotations
        for (int i = 0; i < 4; i++) {
            NextShapeInfo nextInfo = rotator.getNextShape();
            rotator.setCurrentShape(nextInfo.getPosition());
            assertEquals((i + 1) % 4, nextInfo.getPosition());
        }

        // After 4 rotations, should be back at position 0
        NextShapeInfo backToStart = rotator.getNextShape();
        assertEquals(1, backToStart.getPosition()); // Next after 0 is 1
    }

    @Test
    @DisplayName("Should handle OBrick single rotation state")
    void testOBrickRotation() {
        Brick brick = new OBrick();
        rotator.setBrick(brick);

        NextShapeInfo next1 = rotator.getNextShape();
        assertEquals(0, next1.getPosition()); // Should stay at 0
    }

    @Test
    @DisplayName("Should update current shape")
    void testSetCurrentShape() {
        Brick brick = new TBrick();
        rotator.setBrick(brick);

        rotator.setCurrentShape(2);
        NextShapeInfo nextInfo = rotator.getNextShape();

        assertEquals(3, nextInfo.getPosition());
    }

    @Test
    @DisplayName("Should return correct brick reference")
    void testGetBrick() {
        Brick brick = new LBrick();
        rotator.setBrick(brick);

        assertEquals(brick, rotator.getBrick());
    }

    @Test
    @DisplayName("Should reset rotation state when setting new brick")
    void testResetRotationOnNewBrick() {
        Brick brick1 = new SBrick();
        rotator.setBrick(brick1);
        rotator.setCurrentShape(1);

        Brick brick2 = new ZBrick();
        rotator.setBrick(brick2);

        NextShapeInfo nextInfo = rotator.getNextShape();
        assertEquals(1, nextInfo.getPosition()); // Should be at state 0, so next is 1
    }
}