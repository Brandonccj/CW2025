package com.comp2042.game.controller.ui;

import com.comp2042.game.controller.audio.SoundManager;
import com.comp2042.game.event.GameMode;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.util.Arrays;
import java.util.List;

/**
 * Handles game mode setup and initialization.
 * Manages visual styling, music playlists, and UI configuration per game mode.
 */
public class GameSetupManager {

    private final GridPane gamePanel;
    private final Label highScoreLabel;
    private final Label linesLabel;
    private final Label levelLabel;
    private Label gameModeLabel;

    public GameSetupManager(GridPane gamePanel, Label highScoreLabel, Label linesLabel, Label levelLabel) {
        this.gamePanel = gamePanel;
        this.highScoreLabel = highScoreLabel;
        this.linesLabel = linesLabel;
        this.levelLabel = levelLabel;
    }

    /**
     * Sets up the game mode with appropriate styling, music, and UI configuration.
     */
    public void setupGameMode(GameMode mode, StackPane rootPane, int highScore, SoundManager soundManager) {
        if (mode == GameMode.ZEN) {
            setupZenMode(rootPane, soundManager);
        } else {
            setupNormalMode(rootPane, highScore, soundManager);
        }

        // Add game mode label to parent pane
        gameModeLabel.setLayoutX(270);
        gameModeLabel.setLayoutY(35);
        ((javafx.scene.layout.Pane) gamePanel.getParent()).getChildren().add(gameModeLabel);
    }

    private void setupZenMode(StackPane rootPane, SoundManager soundManager) {
        // Apply zen styling
        if (!rootPane.getStyleClass().contains("zen-mode")) {
            rootPane.getStyleClass().add("zen-mode");
        }

        // Setup zen music playlist
        List<String> zenPlaylist = Arrays.asList(
                "/sounds/zen_music_1.mp3",
                "/sounds/zen_music_2.mp3"
        );
        soundManager.playPlaylist("zen_mode", zenPlaylist, false);

        // Configure UI for zen mode
        gameModeLabel = new Label("ZEN MODE");
        gameModeLabel.setStyle("-fx-font-family: 'Determination'; -fx-font-size: 18px; -fx-text-fill: #26A8B7; -fx-font-weight: bold;");

        highScoreLabel.setText("Best: N/A");
        highScoreLabel.setVisible(false);

        linesLabel.setText("0");

        levelLabel.setVisible(false);
        levelLabel.getParent().setVisible(false);

        gamePanel.setStyle("-fx-effect: dropshadow(gaussian, rgba(77, 208, 225, 0.3), 5, 0.5, 0, 0);");
    }

    private void setupNormalMode(StackPane rootPane, int highScore, SoundManager soundManager) {
        // Remove zen styling if present
        rootPane.getStyleClass().remove("zen-mode");

        // Setup normal music playlist
        List<String> normalPlaylist = Arrays.asList(
                "/sounds/normal_music_1.mp3",
                "/sounds/normal_music_2.mp3",
                "/sounds/normal_music_3.mp3",
                "/sounds/normal_music_4.mp3"
        );
        soundManager.playPlaylist("normal_mode", normalPlaylist, true);

        // Configure UI for normal mode
        gameModeLabel = new Label("NORMAL MODE");
        gameModeLabel.setStyle("-fx-font-family: 'Determination'; -fx-font-size: 18px; -fx-text-fill: #00FF00; -fx-font-weight: bold;");

        highScoreLabel.setText("High Score: " + highScore);
        highScoreLabel.setVisible(true);

        linesLabel.setText("0/5");

        levelLabel.setVisible(true);
        if (levelLabel.getParent() != null) {
            levelLabel.getParent().setVisible(true);
        }

        gamePanel.setStyle("-fx-effect: dropshadow(gaussian, rgba(156, 39, 176, 0.3), 5, 0.5, 0, 0);");
    }
}