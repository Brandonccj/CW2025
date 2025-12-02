package com.comp2042.game.controller.ui;

import com.comp2042.game.event.GameMode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Manages color schemes for different game modes.
 * Provides brick colors and ghost/shadow colors based on the current game mode.
 */
public class ColorScheme {

    public static Paint getBrickColor(int colorCode, GameMode mode) {
        if (mode == GameMode.ZEN) {
            return getZenBrickColor(colorCode);
        } else {
            return getNormalBrickColor(colorCode);
        }
    }

    public static Paint getGhostColor(int colorCode, GameMode mode) {
        if (mode == GameMode.ZEN) {
            return getZenGhostColor(colorCode);
        } else {
            return getNormalGhostColor(colorCode);
        }
    }

    private static Paint getZenBrickColor(int colorCode) {
        switch (colorCode) {
            case 0: return Color.TRANSPARENT;
            case 1: return Color.rgb(77, 208, 225);
            case 2: return Color.rgb(100, 181, 246);
            case 3: return Color.rgb(38, 198, 218);
            case 4: return Color.rgb(128, 222, 234);
            case 5: return Color.rgb(0, 188, 212);
            case 6: return Color.rgb(77, 182, 172);
            case 7: return Color.rgb(38, 166, 154);
            default: return Color.WHITE;
        }
    }

    private static Paint getNormalBrickColor(int colorCode) {
        switch (colorCode) {
            case 0: return Color.TRANSPARENT;
            case 1: return Color.rgb(186, 104, 200);
            case 2: return Color.rgb(156, 39, 176);
            case 3: return Color.rgb(123, 31, 162);
            case 4: return Color.rgb(103, 58, 183);
            case 5: return Color.rgb(149, 117, 205);
            case 6: return Color.rgb(94, 53, 177);
            case 7: return Color.rgb(74, 20, 140);
            default: return Color.WHITE;
        }
    }

    private static Paint getZenGhostColor(int colorCode) {
        switch (colorCode) {
            case 0: return Color.TRANSPARENT;
            case 1: return Color.rgb(38, 104, 113);
            case 2: return Color.rgb(50, 90, 123);
            case 3: return Color.rgb(19, 99, 109);
            case 4: return Color.rgb(64, 111, 117);
            case 5: return Color.rgb(0, 94, 106);
            case 6: return Color.rgb(38, 91, 86);
            case 7: return Color.rgb(19, 83, 77);
            default: return Color.GRAY;
        }
    }

    private static Paint getNormalGhostColor(int colorCode) {
        switch (colorCode) {
            case 0: return Color.TRANSPARENT;
            case 1: return Color.rgb(112, 62, 120);
            case 2: return Color.rgb(94, 23, 106);
            case 3: return Color.rgb(74, 19, 97);
            case 4: return Color.rgb(62, 35, 110);
            case 5: return Color.rgb(89, 70, 123);
            case 6: return Color.rgb(56, 32, 106);
            case 7: return Color.rgb(44, 12, 84);
            default: return Color.GRAY;
        }
    }
}