package com.comp2042.game.control;

import com.comp2042.game.controller.audio.SoundManager;
import com.comp2042.game.controller.game.GameController;  // ← THIS IS THE KEY LINE!
import com.comp2042.game.event.GameMode;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the main menu screen.
 * Handles menu navigation and game mode selection.
 */
public class MenuController implements Initializable {

    @FXML
    private StackPane instructionsOverlay;

    @FXML
    private Label musicStatusLabel;

    @FXML
    private Label sfxStatusLabel;

    private SoundManager soundManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        soundManager = SoundManager.getInstance();

        Platform.runLater(() -> {
            soundManager.playMusic("/sounds/menu_music.mp3");

            updateMusicStatusLabel();
            updateSfxStatusLabel();
        });

        Platform.runLater(() -> {
            if (instructionsOverlay.getScene() != null) {
                instructionsOverlay.getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent keyEvent) {
                        if (keyEvent.getCode() == KeyCode.M) {
                            boolean currentState = soundManager.isMusicEnabled();
                            soundManager.setMusicEnabled(!currentState);
                            updateMusicStatusLabel();
                            keyEvent.consume();
                        }
                        if (keyEvent.getCode() == KeyCode.K) {
                            boolean currentState = soundManager.isSfxEnabled();
                            soundManager.setSfxEnabled(!currentState);
                            updateSfxStatusLabel();
                            keyEvent.consume();
                        }
                    }
                });
            }
        });
    }

    private void updateMusicStatusLabel() {
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

    private void updateSfxStatusLabel() {
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

    @FXML
    private void startNormalMode() {
        soundManager.playSound("button_click");
        startGame(GameMode.NORMAL);
    }

    @FXML
    private void startZenMode() {
        soundManager.playSound("button_click");
        startGame(GameMode.ZEN);
    }

    private void startGame(GameMode mode) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gameLayout.fxml"));
            Parent gameRoot = loader.load();
            GuiController guiController = loader.getController();

            Stage stage = (Stage) instructionsOverlay.getScene().getWindow();

            Scene gameScene = new Scene(gameRoot, 620, 600);
            stage.setScene(gameScene);

            new GameController(guiController, mode);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error starting game: " + e.getMessage());
        }
    }

    @FXML
    private void showInstructions() {
        soundManager.playSound("button_click");
        instructionsOverlay.setVisible(true);
    }

    @FXML
    private void hideInstructions() {
        soundManager.playSound("button_click");
        instructionsOverlay.setVisible(false);
    }

    @FXML
    private void exitGame() {
        soundManager.playSound("button_click");
        soundManager.stopMusic();
        System.exit(0);
    }
}