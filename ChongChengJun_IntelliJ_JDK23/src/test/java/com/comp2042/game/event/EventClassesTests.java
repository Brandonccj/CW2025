package com.comp2042.game.event;

import com.comp2042.game.view.ViewData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

/**
 * Test class for Event-related classes.
 * Tests event data structures and enums.
 */
class EventClassesTests {

    @Test
    @DisplayName("MoveEvent should store event type correctly")
    void testMoveEventType() {
        MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.USER);

        assertEquals(EventType.DOWN, event.getEventType());
    }

    @Test
    @DisplayName("MoveEvent should store event source correctly")
    void testMoveEventSource() {
        MoveEvent event = new MoveEvent(EventType.LEFT, EventSource.THREAD);

        assertEquals(EventSource.THREAD, event.getEventSource());
    }

    @Test
    @DisplayName("ClearRow should store lines removed correctly")
    void testClearRowLinesRemoved() {
        int[][] matrix = {{0, 0}, {0, 0}};
        ClearRow clearRow = new ClearRow(3, matrix, 150);

        assertEquals(3, clearRow.getLinesRemoved());
    }

    @Test
    @DisplayName("ClearRow should store score bonus correctly")
    void testClearRowScoreBonus() {
        int[][] matrix = {{0, 0}, {0, 0}};
        ClearRow clearRow = new ClearRow(2, matrix, 200);

        assertEquals(200, clearRow.getScoreBonus());
    }

    @Test
    @DisplayName("ClearRow should return independent matrix copy")
    void testClearRowMatrixIndependence() {
        int[][] matrix = {{1, 2}, {3, 4}};
        ClearRow clearRow = new ClearRow(1, matrix, 50);

        int[][] retrieved = clearRow.getNewMatrix();
        retrieved[0][0] = 99;

        assertEquals(1, clearRow.getNewMatrix()[0][0]);
    }

    @Test
    @DisplayName("DownData should store ClearRow correctly")
    void testDownDataClearRow() {
        int[][] matrix = {{0, 0}};
        ClearRow clearRow = new ClearRow(1, matrix, 50);
        ViewData viewData = new ViewData(matrix, 0, 0, new ArrayList<>(), 0, null);

        DownData downData = new DownData(clearRow, viewData);

        assertEquals(clearRow, downData.getClearRow());
    }

    @Test
    @DisplayName("DownData should store ViewData correctly")
    void testDownDataViewData() {
        int[][] matrix = {{0, 0}};
        ViewData viewData = new ViewData(matrix, 5, 10, new ArrayList<>(), 2, null);

        DownData downData = new DownData(null, viewData);

        assertEquals(viewData, downData.getViewData());
    }

    @Test
    @DisplayName("DownData should handle null ClearRow")
    void testDownDataNullClearRow() {
        int[][] matrix = {{0, 0}};
        ViewData viewData = new ViewData(matrix, 0, 0, new ArrayList<>(), 0, null);

        DownData downData = new DownData(null, viewData);

        assertNull(downData.getClearRow());
    }

    @Test
    @DisplayName("EventType enum should have all movement types")
    void testEventTypeValues() {
        EventType[] types = EventType.values();

        assertTrue(types.length >= 4);
        assertTrue(containsType(types, EventType.DOWN));
        assertTrue(containsType(types, EventType.LEFT));
        assertTrue(containsType(types, EventType.RIGHT));
        assertTrue(containsType(types, EventType.ROTATE));
    }

    @Test
    @DisplayName("EventSource enum should have USER and THREAD")
    void testEventSourceValues() {
        EventSource[] sources = EventSource.values();

        assertEquals(2, sources.length);
        assertTrue(containsSource(sources, EventSource.USER));
        assertTrue(containsSource(sources, EventSource.THREAD));
    }

    @Test
    @DisplayName("GameMode enum should have NORMAL and ZEN")
    void testGameModeValues() {
        GameMode[] modes = GameMode.values();

        assertEquals(2, modes.length);
        assertTrue(containsMode(modes, GameMode.NORMAL));
        assertTrue(containsMode(modes, GameMode.ZEN));
    }

    private boolean containsType(EventType[] types, EventType target) {
        for (EventType type : types) {
            if (type == target) return true;
        }
        return false;
    }

    private boolean containsSource(EventSource[] sources, EventSource target) {
        for (EventSource source : sources) {
            if (source == target) return true;
        }
        return false;
    }

    private boolean containsMode(GameMode[] modes, GameMode target) {
        for (GameMode mode : modes) {
            if (mode == target) return true;
        }
        return false;
    }
}