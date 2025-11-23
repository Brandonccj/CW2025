package com.comp2042.game.control;

import com.comp2042.game.controller.animation.AnimationManager;
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

    private PauseMenu pauseMenu;
    private GameOverPanel gameOverPanel;
    private InputEventListener eventListener;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("determination.ttf").toExternalForm(), 38);

        // Initialize UI components
        gameOverPanel = new GameOverPanel();
        if (gameOverOverlay != null) {
            gameOverOverlay.getChildren().clear();
            gameOverOverlay.getChildren().add(gameOverPanel);
            gameOverOverlay.setVisible(false);
        }
        gameOverPanel.setVisible(false);
    }

    public void initGameView(int[][] boardMatrix, ViewData brick, GameMode mode) {
        // Create view controller
        viewController = new GameViewController(
                gamePanel, nextBrickGrid, heldBrickGrid, groupNotification,
                scoreLabel, highScoreLabel, levelLabel, linesLabel,
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

    private void setupButtonHandlers() {
        pauseMenu.getResumeButton().setOnAction(e -> stateController.resumeGame());
        pauseMenu.getRestartButton().setOnAction(e -> stateController.restartGame());
        pauseMenu.getMainMenuButton().setOnAction(e -> stateController.returnToMainMenu());
        gameOverPanel.getMainMenuButton().setOnAction(e -> stateController.returnToMainMenu());
    }

    // Delegation methods

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void bindScore(IntegerProperty integerProperty) {
        viewController.bindScore(integerProperty);
    }

    public void bindLines(IntegerProperty linesProperty) {
        viewController.bindLines(linesProperty);
    }

    public void refreshGameBackground(int[][] board) {
        viewController.refreshGameBackground(board);
    }

    public void gameOver() {
        stateController.gameOver();
    }

    public void levelUp(int newLevel) {
        animationManager.levelUp(newLevel);
    }

    public void showZenClearNotification() {
        viewController.showZenClearNotification();
    }
}