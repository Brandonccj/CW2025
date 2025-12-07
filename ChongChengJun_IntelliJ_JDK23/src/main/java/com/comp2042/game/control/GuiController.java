package com.comp2042.game.control;

import com.comp2042.game.controller.animation.AnimationManager;
import com.comp2042.game.controller.audio.SoundManager;
import com.comp2042.game.controller.input.InputHandler;
import com.comp2042.game.controller.state.GameStateController;
import com.comp2042.game.controller.ui.GameViewController;
import com.comp2042.game.event.GameMode;
import com.comp2042.game.event.InputEventListener;
import com.comp2042.game.view.GameOverPanel;
import com.comp2042.game.view.PauseMenu;
import com.comp2042.game.view.ViewData;
import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Main GUI Controller - Coordinates all UI-related controllers.
 * Acts as the FXML controller and delegates work to specialized controllers.
 */
public class GuiController implements Initializable {

    @FXML private GridPane gamePanel;
    @FXML private Group groupNotification;
    @FXML private GridPane nextBrickGrid;
    @FXML private GridPane heldBrickGrid;
    @FXML private Label scoreLabel;
    @FXML private Label timeLabel;
    @FXML private Label highScoreLabel;
    @FXML private Label levelLabel;
    @FXML private Label linesLabel;
    @FXML private StackPane gameOverOverlay;
    @FXML private StackPane pauseOverlay;
    @FXML private Label musicStatusLabel;
    @FXML private Label sfxStatusLabel;

    // Specialized controllers
    private GameViewController viewController;
    private AnimationManager animationManager;
    private InputHandler inputHandler;
    private GameStateController stateController;
    private SoundManager soundManager;

    private PauseMenu pauseMenu;
    private GameOverPanel gameOverPanel;
    private InputEventListener eventListener;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("determination.ttf").toExternalForm(), 38);

        soundManager = SoundManager.getInstance();

        // Initialize UI components
        gameOverPanel = new GameOverPanel();
        if (gameOverOverlay != null) {
            gameOverOverlay.getChildren().clear();
            gameOverOverlay.getChildren().add(gameOverPanel);
            gameOverOverlay.setVisible(false);
        }
        gameOverPanel.setVisible(false);
    }


    /**
     * Initializes the game view with all necessary controllers and components.
     * Sets up the game board, input handlers, animation manager, and state controller.
     *
     * @param boardMatrix the initial game board matrix
     * @param brick the initial brick data
     * @param mode the game mode (NORMAL or ZEN)
     */
    public void initGameView(int[][] boardMatrix, ViewData brick, GameMode mode) {

        viewController = new GameViewController(
                gamePanel, nextBrickGrid, heldBrickGrid, groupNotification,
                scoreLabel, highScoreLabel, levelLabel, linesLabel, timeLabel,
                musicStatusLabel, sfxStatusLabel
        );

        // Create state controller
        pauseMenu = new PauseMenu();
        if (pauseOverlay != null) {
            pauseOverlay.getChildren().clear();
            pauseOverlay.getChildren().add(pauseMenu);
            pauseOverlay.setVisible(false);
            pauseMenu.setGameMode(mode);
        }

        stateController = new GameStateController(
                pauseOverlay, gameOverOverlay, pauseMenu, gameOverPanel
        );

        // Create animation manager
        animationManager = new AnimationManager(
                eventListener, viewController, stateController.isPauseProperty()
        );

        // Set dependencies
        stateController.setDependencies(animationManager, viewController, eventListener);

        // Create input handler
        inputHandler = new InputHandler(
                eventListener, viewController, animationManager, stateController,
                stateController.isPauseProperty(), stateController.isGameOverProperty()
        );

        // Setup UI
        viewController.initGameView(boardMatrix, brick, mode);
        inputHandler.setupKeyHandlers(gamePanel);

        // Setup button handlers
        setupButtonHandlers();

        // Start game timeline
        animationManager.startGameTimeline();
    }


    /**
     * Sets up event handlers for all menu buttons (pause, restart, main menu).
     * Connects button actions to appropriate state controller methods.
     */
    private void setupButtonHandlers() {
        pauseMenu.getResumeButton().setOnAction(e -> {
            soundManager.playSound("button_click");
            stateController.resumeGame();
        });
        pauseMenu.getRestartButton().setOnAction(e -> {
            soundManager.playSound("button_click");
            stateController.restartGame();
        });
        pauseMenu.getMainMenuButton().setOnAction(e -> {
            soundManager.playSound("button_click");
            stateController.returnToMainMenu();
        });
        gameOverPanel.getMainMenuButton().setOnAction(e -> {
            soundManager.playSound("button_click");
            stateController.returnToMainMenu();
        });
    }

    // Delegation methods

    /**
     * Sets the event listener for handling game input events.
     *
     * @param eventListener the listener to receive game events
     */
    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    /**
     * Binds the score property to the UI score label.
     *
     * @param integerProperty the score property to bind
     */
    public void bindScore(IntegerProperty integerProperty) {
        viewController.bindScore(integerProperty);
    }

    /**
     * Binds the lines cleared property to the UI lines label.
     *
     * @param linesProperty the lines cleared property to bind
     */
    public void bindLines(IntegerProperty linesProperty) {
        viewController.bindLines(linesProperty);
    }

    /**
     * Refreshes the game board background display.
     *
     * @param board the updated board matrix
     */
    public void refreshGameBackground(int[][] board) {
        viewController.refreshGameBackground(board);
    }

    /**
     * Triggers the game over sequence and displays the game over screen.
     */
    public void gameOver() {
        stateController.gameOver();
    }

    /**
     * Handles level progression and displays level up notification.
     *
     * @param newLevel the new level number
     */
    public void levelUp(int newLevel) {
        animationManager.levelUp(newLevel);
    }

    /**
     * Displays a notification when the board is cleared in Zen mode.
     */
    public void showZenClearNotification() {
        viewController.showZenClearNotification();
    }
}