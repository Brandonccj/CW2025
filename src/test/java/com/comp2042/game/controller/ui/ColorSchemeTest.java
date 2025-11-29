package com.comp2042.game.controller.ui;

import com.comp2042.game.event.GameMode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ColorScheme.
 * Tests color assignment for different game modes and brick types.
 */
class ColorSchemeTest {

    @Test
    @DisplayName("Should return transparent for color code 0")
    void testTransparentColor() {
        Paint normalColor = ColorScheme.getBrickColor(0, GameMode.NORMAL);
        Paint zenColor = ColorScheme.getBrickColor(0, GameMode.ZEN);

        assertEquals(Color.TRANSPARENT, normalColor);
        assertEquals(Color.TRANSPARENT, zenColor);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7})
    @DisplayName("Should return non-transparent colors for brick codes in Normal mode")
    void testNormalModeBrickColors(int colorCode) {
        Paint color = ColorScheme.getBrickColor(colorCode, GameMode.NORMAL);

        assertNotNull(color);
        assertNotEquals(Color.TRANSPARENT, color);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7})
    @DisplayName("Should return non-transparent colors for brick codes in Zen mode")
    void testZenModeBrickColors(int colorCode) {
        Paint color = ColorScheme.getBrickColor(colorCode, GameMode.ZEN);

        assertNotNull(color);
        assertNotEquals(Color.TRANSPARENT, color);
    }

    @Test
    @DisplayName("Normal and Zen modes should have different color schemes")
    void testDifferentColorSchemes() {
        Paint normalColor = ColorScheme.getBrickColor(1, GameMode.NORMAL);
        Paint zenColor = ColorScheme.getBrickColor(1, GameMode.ZEN);

        assertNotEquals(normalColor, zenColor);
    }

    @Test
    @DisplayName("Ghost colors should be different from brick colors")
    void testGhostColorsDifferent() {
        Paint brickColor = ColorScheme.getBrickColor(1, GameMode.NORMAL);
        Paint ghostColor = ColorScheme.getGhostColor(1, GameMode.NORMAL);

        assertNotEquals(brickColor, ghostColor);
    }

    @Test
    @DisplayName("Should return white for invalid color codes in Normal mode")
    void testInvalidColorCodeNormal() {
        Paint color = ColorScheme.getBrickColor(99, GameMode.NORMAL);

        assertEquals(Color.WHITE, color);
    }

    @Test
    @DisplayName("Should return white for invalid color codes in Zen mode")
    void testInvalidColorCodeZen() {
        Paint color = ColorScheme.getBrickColor(99, GameMode.ZEN);

        assertEquals(Color.WHITE, color);
    }

    @Test
    @DisplayName("Ghost colors should return transparent for code 0")
    void testGhostTransparent() {
        Paint normalGhost = ColorScheme.getGhostColor(0, GameMode.NORMAL);
        Paint zenGhost = ColorScheme.getGhostColor(0, GameMode.ZEN);

        assertEquals(Color.TRANSPARENT, normalGhost);
        assertEquals(Color.TRANSPARENT, zenGhost);
    }

    @Test
    @DisplayName("All brick color codes should have unique colors in Normal mode")
    void testUniqueBrickColorsNormal() {
        Paint[] colors = new Paint[7];
        for (int i = 0; i < 7; i++) {
            colors[i] = ColorScheme.getBrickColor(i + 1, GameMode.NORMAL);
        }

        for (int i = 0; i < 7; i++) {
            for (int j = i + 1; j < 7; j++) {
                assertNotEquals(colors[i], colors[j],
                        "Colors " + (i + 1) + " and " + (j + 1) + " should be different");
            }
        }
    }
}