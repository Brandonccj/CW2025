package com.comp2042.game.controller.state;

import com.comp2042.game.controller.animation.AnimationManager;
import com.comp2042.game.controller.audio.SoundManager;
import com.comp2042.game.controller.ui.GameViewController;
import com.comp2042.game.event.GameMode;
import com.comp2042.game.event.InputEventListener;
import com.comp2042.game.view.GameOverPanel;
import com.comp2042.game.view.PauseMenu;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.List;

/**
 * Manages game state transitions including pause, resume, game over, and new game.
 * Controls overlay visibility and handles state-dependent behaviors.
 */
public class GameStateController {

    private final BooleanProperty isPause = new SimpleBooleanProperty();
    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    private StackPane pauseOverlay;
    private StackPane gameOverOverlay;
    private PauseMenu pauseMenu;
    private GameOverPanel gameOverPanel;

    private AnimationManager animationManager;
    private GameViewController viewController;
    private InputEventListener eventListener;
    private SoundManager soundManager;

    public GameStateController(StackPane pauseOverlay, StackPane gameOverOverlay,
                               PauseMenu pauseMenu, GameOverPanel gameOverPanel) {
        this.pauseOverlay = pauseOverlay;
        this.gameOverOverlay = gameOverOverlay;
        this.pauseMenu = pauseMenu;
        this.gameOverPanel = gameOverPanel;
        this.soundManager = SoundManager.getInstance();
    }

    public void setDependencies(AnimationManager animationManager, GameViewController viewController,
                                InputEventListener eventListener) {
        this.animationManager = animationManager;
        this.viewController = viewController;
        this.eventListener = eventListener;
    }

    public void togglePause() {
        if (isGameOver.getValue()) return;

        if (isPause.getValue()) {
            resumeGame();
        } else {
            pauseGame();
        }
    }

    public void pauseGame() {
        isPause.setValue(Boolean.TRUE);
        animationManager.pauseTimelines();
        soundManager.pauseMusic();

        if (pauseOverlay != null) {
            pauseOverlay.setVisible(true);
            pauseOverlay.toFront();
        }
        viewController.getGamePanel().requestFocus();
    }

    public void resumeGame() {
        isPause.setValue(Boolean.FALSE);
        animationManager.resumeTimelines();
        soundManager.resumeMusic();

        if (pauseOverlay != null) {
            pauseOverlay.setVisible(false);
        }
        viewController.getGamePanel().requestFocus();
    }

    public void gameOver() {
        animationManager.stopAllTimelines();
        isGameOver.setValue(Boolean.TRUE);

        // Stop current music and play game over sequence
        soundManager.stopMusic();
        soundManager.playSound("gameover");

        Timeline musicDelayTimeline = new Timeline(new KeyFrame(
                Duration.millis(2000),
                ae -> {
                    // Play game over music (will be stored as pending if muted)
                    soundManager.playMusic("/sounds/gameover_music.mp3");
                }
        ));
        musicDelayTimeline.play();

        long elapsed = (System.currentTimeMillis() - animationManager.getStartTime()) / 1000;
        long minutes = elapsed / 60;
        long seconds = elapsed % 60;
        String timeString = String.format("%d:%02d", minutes, seconds);

        String scoreText = viewController.getScoreLabel().getText().replace("Score: ", "");
        int currentScore = Integer.parseInt(scoreText);

        String linesText = viewController.getLinesLabel().getText();
        int totalLines;
        if (viewController.getCurrentGameMode() == GameMode.ZEN) {
            totalLines = Integer.parseInt(linesText);
        } else {
            totalLines = Integer.parseInt(linesText.split("/")[0]);
        }

        if (gameOverPanel != null) {
            int displayHighScore = (viewController.getCurrentGameMode() == GameMode.NORMAL)
                    ? viewController.getHighScore() : currentScore;
            gameOverPanel.updateStats(timeString, currentScore, displayHighScore, totalLines);
            gameOverPanel.setVisible(true);
        }

        if (gameOverOverlay != null) {
            gameOverOverlay.setVisible(true);
            gameOverOverlay.toFront();
        }
    }

    public void newGame() {
        animationManager.stopAllTimelines();
        animationManager.setDropping(false);
        animationManager.spaceKeyPressedProperty().setValue(false);

        if (pauseOverlay != null) pauseOverlay.setVisible(false);
        isPause.setValue(Boolean.FALSE);

        gameOverOverlay.setVisible(false);
        isGameOver.setValue(Boolean.FALSE);

        eventListener.createNewGame();
        viewController.refreshBrick(eventListener.getViewData());

        // Stop any current music (like game over music) and start game music
        soundManager.stopMusic();
        restartGameMusic();

        animationManager.startGameTimeline();
        animationManager.resetStartTime();

        viewController.getGamePanel().requestFocus();
    }

    public void restartGame() {
        animationManager.stopAllTimelines();
        animationManager.setDropping(false);
        animationManager.spaceKeyPressedProperty().setValue(false);

        pauseOverlay.setVisible(false);
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);

        eventListener.createNewGame();
        viewController.refreshBrick(eventListener.getViewData());

        // Restart game music based on mode
        soundManager.stopMusic();
        restartGameMusic();

        animationManager.startGameTimeline();
        animationManager.resetStartTime();

        viewController.getGamePanel().requestFocus();
    }

    private void restartGameMusic() {
        GameMode mode = viewController.getCurrentGameMode();
        if (mode == GameMode.ZEN) {
            List<String> zenPlaylist = Arrays.asList(
                    "/sounds/zen_music_1.mp3",
                    "/sounds/zen_music_2.mp3",
                    "/sounds/zen_music_3.mp3"
            );
            soundManager.playPlaylist("zen_mode", zenPlaylist, true);
        } else {
            List<String> normalPlaylist = Arrays.asList(
                    "/sounds/normal_music_1.mp3",
                    "/sounds/normal_music_2.mp3",
                    "/sounds/normal_music_3.mp3",
                    "/sounds/normal_music_4.mp3",
                    "/sounds/normal_music_5.mp3",
                    "/sounds/normal_music_6.mp3"
            );
            soundManager.playPlaylist("normal_mode", normalPlaylist, true);
        }
    }

    public void returnToMainMenu() {
        try {
            animationManager.stopAllTimelines();

            // Stop current music completely and start menu music
            soundManager.stopMusic();
            soundManager.playMusic("/sounds/menu_music.mp3");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainMenu.fxml"));
            Parent menuRoot = loader.load();

            // Get the current scene and replace its root
            Scene currentScene = viewController.getGamePanel().getScene();
            currentScene.setRoot(menuRoot);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BooleanProperty isPauseProperty() {
        return isPause;
    }

    public BooleanProperty isGameOverProperty() {
        return isGameOver;
    }
}