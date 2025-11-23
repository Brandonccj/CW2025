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
    private final Label musicStatusLabel;
    private final Label sfxStatusLabel;

    // State
    private GameMode currentGameMode;
    private int highScore = HighScoreManager.loadHighScore();
    private SoundManager soundManager;

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

        // Create renderers
        this.brickRenderer = new BrickRenderer(gamePanel);
        this.gridRenderer = new GridRenderer(nextBrickGrid, heldBrickGrid);
        this.setupManager = new GameSetupManager(gamePanel, highScoreLabel, linesLabel, levelLabel);
    }

    public void initGameView(int[][] boardMatrix, ViewData brick, GameMode mode) {
        this.currentGameMode = mode;

        StackPane rootPane = (StackPane) gamePanel.getScene().getRoot();
        setupManager.setupGameMode(mode, rootPane, highScore, soundManager);

        brickRenderer.initializeDisplayMatrix(boardMatrix);
        brickRenderer.initializeBrickRectangles(brick, this::getFillColor);
        gridRenderer.initPreviewGrid();
        gridRenderer.initHoldGrid();

        highScoreLabel.setText("High Score: " + highScore);

        Platform.runLater(() -> {
            updateMusicStatusLabel();
            updateSfxStatusLabel();
        });
    }

    public void refreshBrick(ViewData brick) {
        brickRenderer.refreshBrick(brick, this::getFillColor, this::getGhostColor, currentGameMode);
        gridRenderer.updatePreviewGrid(brick.getNextBricksData(), this::getFillColor);
        gridRenderer.updateHoldGrid(brick.getHeldBrickData(), this::getFillColor);
    }

    public void refreshGameBackground(int[][] board) {
        brickRenderer.refreshGameBackground(board, this::getFillColor);
    }

    public void showClearRowNotification(ClearRow clearRow) {
        if (clearRow != null && clearRow.getLinesRemoved() > 0) {
            soundManager.playSound("clear_row");
            NotificationPanel notificationPanel = new NotificationPanel("+" + clearRow.getScoreBonus());
            groupNotification.getChildren().add(notificationPanel);
            notificationPanel.showScore(groupNotification.getChildren());
        }
    }

    public void showZenClearNotification() {
        soundManager.playSound("board_clear");
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

    public void bindScore(IntegerProperty integerProperty) {
        scoreLabel.textProperty().bind(integerProperty.asString("Score: %d"));
        integerProperty.addListener((obs, oldVal, newVal) -> {
            updateHighScore(newVal.intValue());
        });
    }

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

    private void updateHighScore(int currentScore) {
        if (currentScore > highScore) {
            highScore = currentScore;
            highScoreLabel.setText("High Score: " + highScore);
            HighScoreManager.saveHighScore(highScore);
        }
    }

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
}