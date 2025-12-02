package com.comp2042.game.controller.state;

import com.comp2042.game.controller.animation.AnimationManager;
import com.comp2042.game.controller.ui.GameViewController;
import com.comp2042.game.event.*;
import com.comp2042.game.view.GameOverPanel;
import com.comp2042.game.view.PauseMenu;
import com.comp2042.game.view.ViewData;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

/**
 * Test class for GameStateController.
 * Tests game state management including pause, resume, and game over logic.
 */
class GameStateControllerTest {

    private GameStateController stateController;
    private StackPane pauseOverlay;
    private StackPane gameOverOverlay;
    private PauseMenu pauseMenu;
    private GameOverPanel gameOverPanel;
    private TestAnimationManager animationManager;
    private TestGameViewController viewController;
    private TestInputEventListener eventListener;

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
        pauseOverlay = new StackPane();
        gameOverOverlay = new StackPane();
        pauseMenu = new PauseMenu();
        gameOverPanel = new GameOverPanel();

        stateController = new GameStateController(
                pauseOverlay, gameOverOverlay, pauseMenu, gameOverPanel
        );

        animationManager = new TestAnimationManager();
        viewController = new TestGameViewController();
        eventListener = new TestInputEventListener();

        stateController.setDependencies(animationManager, viewController, eventListener);
    }

    @Test
    @DisplayName("Should start with pause state false")
    void testInitialPauseState() {
        assertFalse(stateController.isPauseProperty().get());
    }

    @Test
    @DisplayName("Should start with game over state false")
    void testInitialGameOverState() {
        assertFalse(stateController.isGameOverProperty().get());
    }

    @Test
    @DisplayName("Should pause game when toggle called")
    void testPauseGame() {
        stateController.pauseGame();

        assertTrue(stateController.isPauseProperty().get());
        assertTrue(pauseOverlay.isVisible());
    }

    @Test
    @DisplayName("Should resume game when toggle called from paused state")
    void testResumeGame() {
        stateController.pauseGame();
        stateController.resumeGame();

        assertFalse(stateController.isPauseProperty().get());
        assertFalse(pauseOverlay.isVisible());
    }

    @Test
    @DisplayName("Should toggle between pause and resume")
    void testTogglePause() {
        assertFalse(stateController.isPauseProperty().get());

        stateController.togglePause();
        assertTrue(stateController.isPauseProperty().get());

        stateController.togglePause();
        assertFalse(stateController.isPauseProperty().get());
    }

    @Test
    @DisplayName("Should not toggle pause when game over")
    void testTogglePauseWhenGameOver() {
        stateController.gameOver();

        stateController.togglePause();

        // Should remain not paused because game is over
        assertFalse(stateController.isPauseProperty().get());
    }

    @Test
    @DisplayName("Should set game over state")
    void testGameOver() {
        stateController.gameOver();

        assertTrue(stateController.isGameOverProperty().get());
        assertTrue(gameOverOverlay.isVisible());
    }

    @Test
    @DisplayName("Should pause timelines when pausing")
    void testPauseTimelines() {
        stateController.pauseGame();

        assertTrue(animationManager.isPauseTimelinesCalled());
    }

    @Test
    @DisplayName("Should resume timelines when resuming")
    void testResumeTimelines() {
        stateController.pauseGame();
        stateController.resumeGame();

        assertTrue(animationManager.isResumeTimelinesCalled());
    }

    @Test
    @DisplayName("Should stop timelines on game over")
    void testStopTimelinesOnGameOver() {
        stateController.gameOver();

        assertTrue(animationManager.isStopTimelinesCalled());
    }

    @Test
    @DisplayName("Should reset game state on new game")
    void testNewGame() {
        stateController.pauseGame();
        stateController.newGame();

        assertFalse(stateController.isPauseProperty().get());
        assertFalse(stateController.isGameOverProperty().get());
        assertFalse(pauseOverlay.isVisible());
    }

    @Test
    @DisplayName("Should create new game through event listener")
    void testNewGameCallsEventListener() {
        stateController.newGame();

        assertTrue(eventListener.isCreateNewGameCalled());
    }

    @Test
    @DisplayName("Should restart game and reset state")
    void testRestartGame() {
        stateController.pauseGame();
        stateController.restartGame();

        assertFalse(stateController.isPauseProperty().get());
        assertFalse(pauseOverlay.isVisible());
    }

    @Test
    @DisplayName("Pause property should be observable")
    void testPausePropertyObservable() {
        final boolean[] observed = {false};

        stateController.isPauseProperty().addListener((obs, oldVal, newVal) -> {
            observed[0] = true;
        });

        stateController.pauseGame();

        assertTrue(observed[0]);
    }

    @Test
    @DisplayName("Game over property should be observable")
    void testGameOverPropertyObservable() {
        final boolean[] observed = {false};

        stateController.isGameOverProperty().addListener((obs, oldVal, newVal) -> {
            observed[0] = true;
        });

        stateController.gameOver();

        assertTrue(observed[0]);
    }

    @Test
    @DisplayName("Should reset drop state on new game")
    void testResetDropStateOnNewGame() {
        animationManager.setDropping(true);

        stateController.newGame();

        assertFalse(animationManager.isDropping());
    }

    @Test
    @DisplayName("Should reset space key state on new game")
    void testResetSpaceKeyOnNewGame() {
        animationManager.spaceKeyPressedProperty().setValue(true);

        stateController.newGame();

        assertFalse(animationManager.spaceKeyPressedProperty().get());
    }

    // Test helper classes
    private static class TestAnimationManager extends AnimationManager {
        private boolean pauseTimelinesCalled = false;
        private boolean resumeTimelinesCalled = false;
        private boolean stopTimelinesCalled = false;

        public TestAnimationManager() {
            super(null, null, new javafx.beans.property.SimpleBooleanProperty(false));
        }

        @Override
        public void pauseTimelines() {
            pauseTimelinesCalled = true;
        }

        @Override
        public void resumeTimelines() {
            resumeTimelinesCalled = true;
        }

        @Override
        public void stopAllTimelines() {
            stopTimelinesCalled = true;
        }

        @Override
        public void startGameTimeline() {
            // No-op
        }

        @Override
        public void resetStartTime() {
            // No-op
        }

        @Override
        public long getStartTime() {
            return System.currentTimeMillis();
        }

        public boolean isPauseTimelinesCalled() {
            return pauseTimelinesCalled;
        }

        public boolean isResumeTimelinesCalled() {
            return resumeTimelinesCalled;
        }

        public boolean isStopTimelinesCalled() {
            return stopTimelinesCalled;
        }
    }

    private static class TestGameViewController extends GameViewController {
        public TestGameViewController() {
            super(null, null, null, null, null, null, null, null, null, null);
        }

        @Override
        public javafx.scene.layout.GridPane getGamePanel() {
            return new javafx.scene.layout.GridPane();
        }

        @Override
        public void refreshBrick(ViewData brick) {
            // No-op
        }

        @Override
        public javafx.scene.control.Label getScoreLabel() {
            javafx.scene.control.Label label = new javafx.scene.control.Label();
            label.setText("Score: 0");
            return label;
        }

        @Override
        public javafx.scene.control.Label getLinesLabel() {
            javafx.scene.control.Label label = new javafx.scene.control.Label();
            label.setText("0");
            return label;
        }

        @Override
        public GameMode getCurrentGameMode() {
            return GameMode.NORMAL;
        }

        @Override
        public int getHighScore() {
            return 0;
        }
    }

    private static class TestInputEventListener implements InputEventListener {
        private boolean createNewGameCalled = false;

        @Override
        public DownData onDownEvent(MoveEvent event) {
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
            createNewGameCalled = true;
        }

        @Override
        public ViewData getViewData() {
            int[][] matrix = {{0, 0}};
            return new ViewData(matrix, 0, 0, new ArrayList<>(), 0, null);
        }

        public boolean isCreateNewGameCalled() {
            return createNewGameCalled;
        }
    }
}