package com.comp2042.game.controller.input;

import com.comp2042.game.controller.animation.AnimationManager;
import com.comp2042.game.controller.state.GameStateController;
import com.comp2042.game.controller.ui.GameViewController;
import com.comp2042.game.event.*;
import com.comp2042.game.view.ViewData;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

/**
 * Test class for InputHandler.
 * Tests keyboard input processing and command routing.
 */
class InputHandlerTest {

    private InputHandler inputHandler;
    private TestInputEventListener eventListener;
    private TestGameViewController viewController;
    private TestAnimationManager animationManager;
    private TestGameStateController stateController;
    private BooleanProperty isPause;
    private BooleanProperty isGameOver;
    private GridPane gamePanel;

    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Already initialized
        }
    }

    @BeforeEach
    void setUp() {
        eventListener = new TestInputEventListener();
        viewController = new TestGameViewController();
        animationManager = new TestAnimationManager();
        stateController = new TestGameStateController();
        isPause = new SimpleBooleanProperty(false);
        isGameOver = new SimpleBooleanProperty(false);
        gamePanel = new GridPane();

        inputHandler = new InputHandler(
                eventListener, viewController, animationManager,
                stateController, isPause, isGameOver
        );

        inputHandler.setupKeyHandlers(gamePanel);
    }

    @Test
    @DisplayName("Should setup key handlers on game panel")
    void testSetupKeyHandlers() {
        GridPane panel = new GridPane();

        assertDoesNotThrow(() -> inputHandler.setupKeyHandlers(panel));
        assertTrue(panel.isFocusTraversable());
    }

    @Test
    @DisplayName("Should handle left arrow key")
    void testLeftArrowKey() {
        isPause.set(false);
        isGameOver.set(false);

        KeyEvent event = createKeyEvent(KeyCode.LEFT, KeyEvent.KEY_PRESSED);
        gamePanel.getOnKeyPressed().handle(event);

        assertTrue(eventListener.isLeftEventCalled());
    }

    @Test
    @DisplayName("Should handle right arrow key")
    void testRightArrowKey() {
        isPause.set(false);
        isGameOver.set(false);

        KeyEvent event = createKeyEvent(KeyCode.RIGHT, KeyEvent.KEY_PRESSED);
        gamePanel.getOnKeyPressed().handle(event);

        assertTrue(eventListener.isRightEventCalled());
    }

    @Test
    @DisplayName("Should handle up arrow key for rotation")
    void testUpArrowKey() {
        isPause.set(false);
        isGameOver.set(false);

        KeyEvent event = createKeyEvent(KeyCode.UP, KeyEvent.KEY_PRESSED);
        gamePanel.getOnKeyPressed().handle(event);

        assertTrue(eventListener.isRotateEventCalled());
    }

    @Test
    @DisplayName("Should handle down arrow key")
    void testDownArrowKey() {
        isPause.set(false);
        isGameOver.set(false);

        KeyEvent event = createKeyEvent(KeyCode.DOWN, KeyEvent.KEY_PRESSED);
        gamePanel.getOnKeyPressed().handle(event);

        assertTrue(animationManager.isMoveDownCalled());
    }

    @Test
    @DisplayName("Should handle WASD keys")
    void testWASDKeys() {
        isPause.set(false);
        isGameOver.set(false);

        // W for rotate
        KeyEvent wEvent = createKeyEvent(KeyCode.W, KeyEvent.KEY_PRESSED);
        gamePanel.getOnKeyPressed().handle(wEvent);
        assertTrue(eventListener.isRotateEventCalled());

        eventListener.reset();

        // A for left
        KeyEvent aEvent = createKeyEvent(KeyCode.A, KeyEvent.KEY_PRESSED);
        gamePanel.getOnKeyPressed().handle(aEvent);
        assertTrue(eventListener.isLeftEventCalled());

        eventListener.reset();

        // D for right
        KeyEvent dEvent = createKeyEvent(KeyCode.D, KeyEvent.KEY_PRESSED);
        gamePanel.getOnKeyPressed().handle(dEvent);
        assertTrue(eventListener.isRightEventCalled());

        animationManager.reset();

        // S for down
        KeyEvent sEvent = createKeyEvent(KeyCode.S, KeyEvent.KEY_PRESSED);
        gamePanel.getOnKeyPressed().handle(sEvent);
        assertTrue(animationManager.isMoveDownCalled());
    }

    @Test
    @DisplayName("Should handle C key for hold")
    void testCKeyForHold() {
        isPause.set(false);
        isGameOver.set(false);

        KeyEvent event = createKeyEvent(KeyCode.C, KeyEvent.KEY_PRESSED);
        gamePanel.getOnKeyPressed().handle(event);

        assertTrue(eventListener.isHoldEventCalled());
    }

    @Test
    @DisplayName("Should handle P key for pause toggle")
    void testPKeyForPause() {
        KeyEvent event = createKeyEvent(KeyCode.P, KeyEvent.KEY_PRESSED);
        gamePanel.getOnKeyPressed().handle(event);

        assertTrue(stateController.isTogglePauseCalled());
    }

    @Test
    @DisplayName("Should handle N key for new game")
    void testNKeyForNewGame() {
        KeyEvent event = createKeyEvent(KeyCode.N, KeyEvent.KEY_PRESSED);
        gamePanel.getOnKeyPressed().handle(event);

        assertTrue(stateController.isNewGameCalled());
    }

    @Test
    @DisplayName("Should not process gameplay input when paused")
    void testNoInputWhenPaused() {
        isPause.set(true);
        isGameOver.set(false);

        KeyEvent event = createKeyEvent(KeyCode.LEFT, KeyEvent.KEY_PRESSED);
        gamePanel.getOnKeyPressed().handle(event);

        assertFalse(eventListener.isLeftEventCalled());
    }

    @Test
    @DisplayName("Should not process gameplay input when game over")
    void testNoInputWhenGameOver() {
        isPause.set(false);
        isGameOver.set(true);

        KeyEvent event = createKeyEvent(KeyCode.RIGHT, KeyEvent.KEY_PRESSED);
        gamePanel.getOnKeyPressed().handle(event);

        assertFalse(eventListener.isRightEventCalled());
    }

    @Test
    @DisplayName("Should allow global controls when paused")
    void testGlobalControlsWhenPaused() {
        isPause.set(true);

        KeyEvent event = createKeyEvent(KeyCode.N, KeyEvent.KEY_PRESSED);
        gamePanel.getOnKeyPressed().handle(event);

        assertTrue(stateController.isNewGameCalled());
    }

    @Test
    @DisplayName("Should handle space key press")
    void testSpaceKeyPress() {
        isPause.set(false);
        isGameOver.set(false);
        animationManager.setDropping(false);
        animationManager.spaceKeyPressedProperty().setValue(false);

        KeyEvent event = createKeyEvent(KeyCode.SPACE, KeyEvent.KEY_PRESSED);
        gamePanel.getOnKeyPressed().handle(event);

        assertTrue(animationManager.isInstantDropCalled());
    }

    @Test
    @DisplayName("Should handle space key release")
    void testSpaceKeyRelease() {
        animationManager.spaceKeyPressedProperty().setValue(true);

        KeyEvent event = createKeyEvent(KeyCode.SPACE, KeyEvent.KEY_RELEASED);
        gamePanel.getOnKeyReleased().handle(event);

        assertFalse(animationManager.spaceKeyPressedProperty().get());
    }

    @Test
    @DisplayName("Should prevent instant drop when already dropping")
    void testPreventInstantDropWhenDropping() {
        isPause.set(false);
        isGameOver.set(false);
        animationManager.setDropping(true);

        KeyEvent event = createKeyEvent(KeyCode.SPACE, KeyEvent.KEY_PRESSED);
        gamePanel.getOnKeyPressed().handle(event);

        // Should not call instant drop when already dropping
        // (handled by AnimationManager itself)
        assertTrue(animationManager.isDropping());
    }

    private KeyEvent createKeyEvent(KeyCode code, javafx.event.EventType<KeyEvent> type) {
        return new KeyEvent(
                type,
                "",
                "",
                code,
                false, false, false, false
        );
    }

    // Test helper classes
    private static class TestInputEventListener implements InputEventListener {
        private boolean leftEventCalled = false;
        private boolean rightEventCalled = false;
        private boolean rotateEventCalled = false;
        private boolean holdEventCalled = false;

        @Override
        public DownData onDownEvent(MoveEvent event) {
            int[][] matrix = {{0, 0}};
            ViewData viewData = new ViewData(matrix, 0, 0, new ArrayList<>(), 0, null);
            return new DownData(null, viewData);
        }

        @Override
        public ViewData onLeftEvent(MoveEvent event) {
            leftEventCalled = true;
            int[][] matrix = {{0, 0}};
            return new ViewData(matrix, 0, 0, new ArrayList<>(), 0, null);
        }

        @Override
        public ViewData onRightEvent(MoveEvent event) {
            rightEventCalled = true;
            int[][] matrix = {{0, 0}};
            return new ViewData(matrix, 0, 0, new ArrayList<>(), 0, null);
        }

        @Override
        public ViewData onRotateEvent(MoveEvent event) {
            rotateEventCalled = true;
            int[][] matrix = {{0, 0}};
            return new ViewData(matrix, 0, 0, new ArrayList<>(), 0, null);
        }

        @Override
        public ViewData onHoldEvent(MoveEvent event) {
            holdEventCalled = true;
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

        public boolean isLeftEventCalled() { return leftEventCalled; }
        public boolean isRightEventCalled() { return rightEventCalled; }
        public boolean isRotateEventCalled() { return rotateEventCalled; }
        public boolean isHoldEventCalled() { return holdEventCalled; }

        public void reset() {
            leftEventCalled = false;
            rightEventCalled = false;
            rotateEventCalled = false;
            holdEventCalled = false;
        }
    }

    private static class TestGameViewController extends GameViewController {
        public TestGameViewController() {
            super(null, null, null, null, null, null, null, null, null, null);
        }

        @Override
        public void refreshBrick(ViewData brick) {
        }

        @Override
        public void updateMusicStatusLabel() {
        }

        @Override
        public void updateSfxStatusLabel() {
        }
    }

    private static class TestAnimationManager extends AnimationManager {
        private boolean moveDownCalled = false;
        private boolean instantDropCalled = false;

        public TestAnimationManager() {
            super(null, null, new SimpleBooleanProperty(false));
        }

        @Override
        public void moveDown(MoveEvent event) {
            moveDownCalled = true;
        }

        @Override
        public void instantDrop() {
            instantDropCalled = true;
        }

        public boolean isMoveDownCalled() { return moveDownCalled; }
        public boolean isInstantDropCalled() { return instantDropCalled; }

        public void reset() {
            moveDownCalled = false;
            instantDropCalled = false;
        }
    }

    private static class TestGameStateController extends GameStateController {
        private boolean togglePauseCalled = false;
        private boolean newGameCalled = false;

        public TestGameStateController() {
            super(null, null, null, null);
        }

        @Override
        public void togglePause() {
            togglePauseCalled = true;
        }

        @Override
        public void newGame() {
            newGameCalled = true;
        }

        public boolean isTogglePauseCalled() { return togglePauseCalled; }
        public boolean isNewGameCalled() { return newGameCalled; }
    }
}