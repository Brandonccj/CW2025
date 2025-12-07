package com.comp2042.game.controller.ui;

import com.comp2042.game.controller.audio.SoundManager;
import com.comp2042.game.event.ClearRow;
import com.comp2042.game.event.GameMode;
import com.comp2042.game.util.HighScoreManager;
import com.comp2042.game.view.NotificationPanel;
import com.comp2042.game.view.ViewData;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

/**
 * Main coordinator for game view components.
 * Delegates rendering to specialized renderer classes.
 */
public class GameViewController {

    // Renderers
    private final BrickRenderer brickRenderer;
    private final GridRenderer gridRenderer;
    private final GameSetupManager setupManager;

    // UI Components
    private final GridPane gamePanel;
    private final Group groupNotification;
    private final Label scoreLabel;
    private final Label highScoreLabel;
    private final Label levelLabel;
    private final Label linesLabel;
    private final Label timeLabel;
    private final Label musicStatusLabel;
    private final Label sfxStatusLabel;

    // State
    private GameMode currentGameMode;
    private int highScore = HighScoreManager.loadHighScore();
    private SoundManager soundManager;

    /**
     * Creates a new game view controller without timer label.
     *
     * @param gamePanel the main game grid panel
     * @param nextBrickGrid the next brick preview grid
     * @param heldBrickGrid the held brick display grid
     * @param groupNotification the notification group for score popups
     * @param scoreLabel the score display label
     * @param highScoreLabel the high score display label
     * @param levelLabel the level display label
     * @param linesLabel the lines cleared display label
     * @param musicStatusLabel the music on/off status label
     * @param sfxStatusLabel the sound effects on/off status label
     */
    public GameViewController(GridPane gamePanel, GridPane nextBrickGrid, GridPane heldBrickGrid,
                              Group groupNotification, Label scoreLabel, Label highScoreLabel,
                              Label levelLabel, Label linesLabel, Label musicStatusLabel,
                              Label sfxStatusLabel) {
        this.gamePanel = gamePanel;
        this.groupNotification = groupNotification;
        this.scoreLabel = scoreLabel;
        this.highScoreLabel = highScoreLabel;
        this.levelLabel = levelLabel;
        this.linesLabel = linesLabel;
        this.musicStatusLabel = musicStatusLabel;
        this.sfxStatusLabel = sfxStatusLabel;
        this.soundManager = SoundManager.getInstance();

        // Initialize timeLabel to null - will be set from GuiController
        this.timeLabel = null;

        // Create renderers
        this.brickRenderer = new BrickRenderer(gamePanel);
        this.gridRenderer = new GridRenderer(nextBrickGrid, heldBrickGrid);
        this.setupManager = new GameSetupManager(gamePanel, highScoreLabel, linesLabel, levelLabel);
    }

    /**
     * Creates a new game view controller with timer label.
     *
     * @param gamePanel the main game grid panel
     * @param nextBrickGrid the next brick preview grid
     * @param heldBrickGrid the held brick display grid
     * @param groupNotification the notification group for score popups
     * @param scoreLabel the score display label
     * @param highScoreLabel the high score display label
     * @param levelLabel the level display label
     * @param linesLabel the lines cleared display label
     * @param timeLabel the elapsed time display label
     * @param musicStatusLabel the music on/off status label
     * @param sfxStatusLabel the sound effects on/off status label
     */
    public GameViewController(GridPane gamePanel, GridPane nextBrickGrid, GridPane heldBrickGrid,
                              Group groupNotification, Label scoreLabel, Label highScoreLabel,
                              Label levelLabel, Label linesLabel, Label timeLabel,
                              Label musicStatusLabel, Label sfxStatusLabel) {
        this.gamePanel = gamePanel;
        this.groupNotification = groupNotification;
        this.scoreLabel = scoreLabel;
        this.highScoreLabel = highScoreLabel;
        this.levelLabel = levelLabel;
        this.linesLabel = linesLabel;
        this.timeLabel = timeLabel;
        this.musicStatusLabel = musicStatusLabel;
        this.sfxStatusLabel = sfxStatusLabel;
        this.soundManager = SoundManager.getInstance();

        // Create renderers
        this.brickRenderer = new BrickRenderer(gamePanel);
        this.gridRenderer = new GridRenderer(nextBrickGrid, heldBrickGrid);
        this.setupManager = new GameSetupManager(gamePanel, highScoreLabel, linesLabel, levelLabel);
    }

    /**
     * Initializes the game view with initial state.
     * Sets up renderers, game mode styling, and display matrices.
     *
     * @param boardMatrix the initial board matrix
     * @param brick the initial brick data
     * @param mode the game mode (NORMAL or ZEN)
     */
    public void initGameView(int[][] boardMatrix, ViewData brick, GameMode mode) {
        this.currentGameMode = mode;

        StackPane rootPane = (StackPane) gamePanel.getScene().getRoot();
        setupManager.setupGameMode(mode, rootPane, highScore, soundManager);

        brickRenderer.initializeDisplayMatrix(boardMatrix);
        brickRenderer.initializeBrickRectangles(brick, this::getFillColor);
        gridRenderer.initPreviewGrid();
        gridRenderer.initHoldGrid();

        highScoreLabel.setText("High Score: " + highScore);

        // Initialize timer display
        if (timeLabel != null) {
            timeLabel.setText("Time: 0:00");
        }

        Platform.runLater(() -> {
            updateMusicStatusLabel();
            updateSfxStatusLabel();
        });
    }

    /**
     * Updates the brick display with new position and data.
     * Also updates preview grids and held brick display.
     *
     * @param brick the updated brick view data
     */
    public void refreshBrick(ViewData brick) {
        brickRenderer.refreshBrick(brick, this::getFillColor, this::getGhostColor, currentGameMode);
        gridRenderer.updatePreviewGrid(brick.getNextBricksData(), this::getFillColor);
        gridRenderer.updateHoldGrid(brick.getHeldBrickData(), this::getFillColor);
    }

    public void refreshGameBackground(int[][] board) {
        brickRenderer.refreshGameBackground(board, this::getFillColor);
    }

    /**
     * Displays an animated score notification when rows are cleared.
     *
     * @param clearRow the clear row data containing score bonus
     */
    public void showClearRowNotification(ClearRow clearRow) {
        if (clearRow != null && clearRow.getLinesRemoved() > 0) {
            soundManager.playSound("clear_row");
            NotificationPanel notificationPanel = new NotificationPanel("+" + clearRow.getScoreBonus());
            groupNotification.getChildren().add(notificationPanel);
            notificationPanel.showScore(groupNotification.getChildren());
        }
    }

    /**
     * Displays a special notification when the board is cleared in Zen mode.
     */
    public void showZenClearNotification() {
        soundManager.playSound("zen_clear");
        NotificationPanel zenNotif = new NotificationPanel("BOARD CLEARED!");
        javafx.scene.control.Label label = (javafx.scene.control.Label) zenNotif.getCenter();
        label.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");
        groupNotification.getChildren().add(zenNotif);
        zenNotif.showScore(groupNotification.getChildren());
    }

    private javafx.scene.paint.Paint getFillColor(int i) {
        return ColorScheme.getBrickColor(i, currentGameMode);
    }

    private javafx.scene.paint.Paint getGhostColor(int colorCode) {
        return ColorScheme.getGhostColor(colorCode, currentGameMode);
    }

    /**
     * Binds the score property to the score label and tracks high score updates.
     *
     * @param integerProperty the score property to bind
     */
    public void bindScore(IntegerProperty integerProperty) {
        scoreLabel.textProperty().bind(integerProperty.asString("Score: %d"));
        integerProperty.addListener((obs, oldVal, newVal) -> {
            updateHighScore(newVal.intValue());
        });
    }

    /**
     * Binds the lines cleared property to update the lines label display.
     * Formats differently based on game mode (with/without level target).
     *
     * @param linesProperty the lines cleared property to bind
     */
    public void bindLines(IntegerProperty linesProperty) {
        linesProperty.addListener((obs, oldVal, newVal) -> {
            int totalLines = newVal.intValue();

            if (currentGameMode == GameMode.ZEN) {
                linesLabel.setText(String.valueOf(totalLines));
            } else {
                int currentLevel = (totalLines / 5) + 1;
                int targetLines = currentLevel * 5;
                linesLabel.setText(totalLines + "/" + targetLines);
            }
        });
    }

    /**
     * Updates and persists the high score if current score exceeds it.
     *
     * @param currentScore the current game score
     */
    private void updateHighScore(int currentScore) {
        if (currentScore > highScore) {
            highScore = currentScore;
            highScoreLabel.setText("High Score: " + highScore);
            HighScoreManager.saveHighScore(highScore);
        }
    }

    /**
     * Updates the music status label to show current on/off state with styling.
     */
    public void updateMusicStatusLabel() {
        boolean isEnabled = soundManager.isMusicEnabled();
        musicStatusLabel.setText("Music: " + (isEnabled ? "ON" : "OFF"));

        if (isEnabled) {
            musicStatusLabel.getStyleClass().remove("disabled");
        } else {
            if (!musicStatusLabel.getStyleClass().contains("disabled")) {
                musicStatusLabel.getStyleClass().add("disabled");
            }
        }
    }

    /**
     * Updates the sound effects status label to show current on/off state with styling.
     */
    public void updateSfxStatusLabel() {
        boolean isEnabled = soundManager.isSfxEnabled();
        sfxStatusLabel.setText("SFX: " + (isEnabled ? "ON" : "OFF"));
        if (isEnabled) {
            sfxStatusLabel.getStyleClass().remove("disabled");
        } else {
            if (!sfxStatusLabel.getStyleClass().contains("disabled")) {
                sfxStatusLabel.getStyleClass().add("disabled");
            }
        }
    }

    public void updateLevelLabel(int level) {
        levelLabel.setText(String.valueOf(level));
    }

    /**
     * Updates the timer label with formatted elapsed time.
     *
     * @param timeString the formatted time string (m:ss)
     */
    public void updateTimerLabel(String timeString) {
        if (timeLabel != null) {
            Platform.runLater(() -> timeLabel.setText("Time: " + timeString));
        }
    }

    // Getters
    public GameMode getCurrentGameMode() {
        return currentGameMode;
    }

    public int getHighScore() {
        return highScore;
    }

    public GridPane getGamePanel() {
        return gamePanel;
    }

    public Group getGroupNotification() {
        return groupNotification;
    }

    public Label getScoreLabel() {
        return scoreLabel;
    }

    public Label getLinesLabel() {
        return linesLabel;
    }

    public Label getTimeLabel() {
        return timeLabel;
    }
}