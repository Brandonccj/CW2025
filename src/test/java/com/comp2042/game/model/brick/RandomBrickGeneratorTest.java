package com.comp2042.game.model.brick;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Test class for RandomBrickGenerator.
 * Tests the bag system for fair piece distribution.
 */
class RandomBrickGeneratorTest {

    private RandomBrickGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new RandomBrickGenerator();
    }

    @Test
    @DisplayName("Should generate non-null bricks")
    void testGenerateNonNullBricks() {
        for (int i = 0; i < 20; i++) {
            assertNotNull(generator.getBrick());
        }
    }

    @Test
    @DisplayName("Should generate all 7 brick types in first 14 pieces")
    void testBagSystemContainsAllPieces() {
        Set<Class<?>> brickTypes = new HashSet<>();

        // Get 14 bricks (2 full bags)
        for (int i = 0; i < 14; i++) {
            Brick brick = generator.getBrick();
            brickTypes.add(brick.getClass());
        }

        // Should have all 7 types
        assertEquals(7, brickTypes.size());
    }

    @Test
    @DisplayName("getNextBricks should return requested count")
    void testGetNextBricksCount() {
        List<Brick> nextBricks = generator.getNextBricks(5);

        assertEquals(5, nextBricks.size());
    }

    @Test
    @DisplayName("getNextBricks should not modify generator state")
    void testGetNextBricksDoesNotConsumeQueue() {
        Brick firstBrick = generator.getBrick();
        generator = new RandomBrickGenerator(); // Reset

        List<Brick> preview = generator.getNextBricks(3);
        Brick actualNext = generator.getBrick();

        // The first previewed brick should be the actual next brick
        assertEquals(preview.get(0).getClass(), actualNext.getClass());
    }

    @Test
    @DisplayName("Should handle large preview requests")
    void testLargePreviewRequest() {
        List<Brick> preview = generator.getNextBricks(20);

        assertEquals(20, preview.size());
        assertNotNull(preview.get(19));
    }

    @Test
    @DisplayName("Generator should refill bag automatically")
    void testBagRefill() {
        // Consume first bag (7 pieces)
        for (int i = 0; i < 7; i++) {
            generator.getBrick();
        }

        // Should still generate pieces from second bag
        for (int i = 0; i < 7; i++) {
            assertNotNull(generator.getBrick());
        }
    }
}