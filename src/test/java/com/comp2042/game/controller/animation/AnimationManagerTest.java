package com.comp2042.game.controller.animation;

import com.comp2042.game.controller.ui.GameViewController;
import com.comp2042.game.event.*;
import com.comp2042.game.view.ViewData;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

/**
 * Test class for AnimationManager.
 * Tests timeline management, drop state, and game timing logic.
 */
class AnimationManagerTest {

    private AnimationManager animationManager;
    private TestInputEventListener eventListener;
    private TestGameViewController viewController;
    private BooleanProperty isPause;

    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Already initialized
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        eventListener = new TestInputEventListener();
        viewController = new TestGameViewController();
        isPause = new SimpleBooleanProperty(false);

        animationManager = new AnimationManager(eventListener, viewController, isPause);
    }

    @Test
    @DisplayName("Should track space key pressed state")
    void testSpaceKeyPressedProperty() {
        assertFalse(animationManager.spaceKeyPressedProperty().get());

        animationManager.spaceKeyPressedProperty().setValue(true);

        assertTrue(animationManager.spaceKeyPressedProperty().get());
    }

    @Test
    @DisplayName("Should track dropping state")
    void testIsDropping() {
        assertFalse(animationManager.isDropping());

        animationManager.setDropping(true);

        assertTrue(animationManager.isDropping());
    }

    @Test
    @DisplayName("Should set dropping state correctly")
    void testSetDropping() {
        animationManager.setDropping(true);
        assertTrue(animationManager.isDropping());

        animationManager.setDropping(false);
        assertFalse(animationManager.isDropping());
    }

    @Test
    @DisplayName("Should handle moveDown event")
    void testMoveDown() {
        MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.USER);

        // Should not throw exception
        assertDoesNotThrow(() -> animationManager.moveDown(event));
    }

    @Test
    @DisplayName("Should not move down when paused")
    void testMoveDownWhenPaused() {
        isPause.set(true);
        MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.USER);

        int initialCalls = eventListener.downEventCalls;
        animationManager.moveDown(event);

        // Should not call event listener when paused
        assertEquals(initialCalls, eventListener.downEventCalls);
    }

    @Test
    @DisplayName("Should move down when not paused")
    void testMoveDownWhenNotPaused() {
        isPause.set(false);
        MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.USER);

        int initialCalls = eventListener.downEventCalls;
        animationManager.moveDown(event);

        // Should call event listener when not paused
        assertEquals(initialCalls + 1, eventListener.downEventCalls);
    }

    @Test
    @DisplayName("Should initialize start time")
    void testStartTime() {
        long startTime = animationManager.getStartTime();

        // Should be a valid timestamp (could be 0 initially)
        assertTrue(startTime >= 0, "Start time should be non-negative");
    }

    @Test
    @DisplayName("Should reset start time")
    void testResetStartTime() throws InterruptedException {
        long originalStartTime = animationManager.getStartTime();

        Thread.sleep(10); // Small delay
        animationManager.resetStartTime();

        long newStartTime = animationManager.getStartTime();

        assertTrue(newStartTime > originalStartTime);
    }

    @Test
    @DisplayName("Should prevent instant drop when already dropping")
    void testInstantDropPrevention() {
        animationManager.setDropping(true);

        // Try to instant drop
        animationManager.instantDrop();

        // Should still be in dropping state
        assertTrue(animationManager.isDropping());
    }

    @Test
    @DisplayName("Should prevent instant drop when paused")
    void testInstantDropWhenPaused() {
        isPause.set(true);

        animationManager.instantDrop();

        // Should not start dropping when paused
        assertFalse(animationManager.isDropping());
    }

    @Test
    @DisplayName("Space key property should be observable")
    void testSpaceKeyPropertyObservable() {
        final boolean[] observed = {false};

        animationManager.spaceKeyPressedProperty().addListener((obs, oldVal, newVal) -> {
            observed[0] = true;
        });

        animationManager.spaceKeyPressedProperty().setValue(true);

        assertTrue(observed[0]);
    }

    // Test helper classes
    private static class TestInputEventListener implements InputEventListener {
        int downEventCalls = 0;

        @Override
        public DownData onDownEvent(MoveEvent event) {
            downEventCalls++;
            int[][] matrix = {{0, 0}};
            ViewData viewData = new ViewData(matrix, 0, 0, new ArrayList<>(), 0, null);
            return new DownData(null, viewData);
        }

        @Override
        public ViewData onLeftEvent(MoveEvent event) {
            int[][] matrix = {{0, 0}};
            return new ViewData(matrix, 0, 0, new ArrayList<>(), 0, null);
        }

        @Override
        public ViewData onRightEvent(MoveEvent event) {
            int[][] matrix = {{0, 0}};
            return new ViewData(matrix, 0, 0, new ArrayList<>(), 0, null);
        }

        @Override
        public ViewData onRotateEvent(MoveEvent event) {
            int[][] matrix = {{0, 0}};
            return new ViewData(matrix, 0, 0, new ArrayList<>(), 0, null);
        }

        @Override
        public ViewData onHoldEvent(MoveEvent event) {
            int[][] matrix = {{0, 0}};
            return new ViewData(matrix, 0, 0, new ArrayList<>(), 0, null);
        }

        @Override
        public void createNewGame() {
        }

        @Override
        public ViewData getViewData() {
            int[][] matrix = {{0, 0}};
            return new ViewData(matrix, 0, 0, new ArrayList<>(), 0, null);
        }
    }

    private static class TestGameViewController extends GameViewController {
        public TestGameViewController() {
            super(null, null, null, null, null, null, null, null, null, null);
        }

        @Override
        public void refreshBrick(ViewData brick) {
            // No-op
        }

        @Override
        public void showClearRowNotification(ClearRow clearRow) {
            // No-op
        }

        @Override
        public javafx.scene.layout.GridPane getGamePanel() {
            return new javafx.scene.layout.GridPane();
        }

        @Override
        public javafx.scene.Group getGroupNotification() {
            return new javafx.scene.Group();
        }

        @Override
        public void updateTimerLabel(String timeString) {
            // No-op
        }

        @Override
        public void updateLevelLabel(int level) {
            // No-op
        }
    }
}