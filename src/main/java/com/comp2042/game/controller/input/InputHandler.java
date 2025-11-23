package com.comp2042.game.controller.input;

import com.comp2042.game.controller.animation.AnimationManager;
import com.comp2042.game.controller.audio.SoundManager;
import com.comp2042.game.controller.state.GameStateController;
import com.comp2042.game.controller.ui.GameViewController;
import com.comp2042.game.event.EventSource;
import com.comp2042.game.event.EventType;
import com.comp2042.game.event.InputEventListener;
import com.comp2042.game.event.MoveEvent;
import javafx.beans.property.BooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

/**
 * Handles all keyboard input and routes commands to appropriate controllers.
 * Manages input validation and state-dependent input handling.
 */
public class InputHandler {

    private InputEventListener eventListener;
    private GameViewController viewController;
    private AnimationManager animationManager;
    private GameStateController stateController;
    private SoundManager soundManager;
    private BooleanProperty isPause;
    private BooleanProperty isGameOver;

    public InputHandler(InputEventListener eventListener, GameViewController viewController,
                        AnimationManager animationManager, GameStateController stateController,
                        BooleanProperty isPause, BooleanProperty isGameOver) {
        this.eventListener = eventListener;
        this.viewController = viewController;
        this.animationManager = animationManager;
        this.stateController = stateController;
        this.isPause = isPause;
        this.isGameOver = isGameOver;
        this.soundManager = SoundManager.getInstance();
    }

    public void setupKeyHandlers(GridPane gamePanel) {
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(createKeyPressedHandler());
        gamePanel.setOnKeyReleased(createKeyReleasedHandler());
    }

    private EventHandler<KeyEvent> createKeyPressedHandler() {
        return keyEvent -> {
            // Global controls (work anytime)
            if (keyEvent.getCode() == KeyCode.P) {
                stateController.togglePause();
                keyEvent.consume();
                return;
            }
            if (keyEvent.getCode() == KeyCode.N) {
                stateController.newGame();
                keyEvent.consume();
                return;
            }
            if (keyEvent.getCode() == KeyCode.M) {
                boolean currentState = soundManager.isMusicEnabled();
                soundManager.setMusicEnabled(!currentState);
                viewController.updateMusicStatusLabel();
                keyEvent.consume();
                return;
            }
            if (keyEvent.getCode() == KeyCode.K) {
                boolean currentState = soundManager.isSfxEnabled();
                soundManager.setSfxEnabled(!currentState);
                viewController.updateSfxStatusLabel();
                keyEvent.consume();
                return;
            }

            // Game controls (only when playing)
            if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
                handleGameplayInput(keyEvent);
            }
        };
    }

    private void handleGameplayInput(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
            viewController.refreshBrick(eventListener.onLeftEvent(
                    new MoveEvent(EventType.LEFT, EventSource.USER)));
            keyEvent.consume();
        }
        else if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
            viewController.refreshBrick(eventListener.onRightEvent(
                    new MoveEvent(EventType.RIGHT, EventSource.USER)));
            keyEvent.consume();
        }
        else if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
            viewController.refreshBrick(eventListener.onRotateEvent(
                    new MoveEvent(EventType.ROTATE, EventSource.USER)));
            keyEvent.consume();
        }
        else if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
            animationManager.moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
            keyEvent.consume();
        }
        else if (keyEvent.getCode() == KeyCode.SPACE) {
            if (!animationManager.isDropping() &&
                    !animationManager.spaceKeyPressedProperty().getValue()) {
                animationManager.spaceKeyPressedProperty().setValue(true);
                animationManager.instantDrop();
            }
            keyEvent.consume();
        }
        else if (keyEvent.getCode() == KeyCode.C) {
            viewController.refreshBrick(eventListener.onHoldEvent(
                    new MoveEvent(EventType.ROTATE, EventSource.USER)));
            keyEvent.consume();
        }
    }

    private EventHandler<KeyEvent> createKeyReleasedHandler() {
        return keyEvent -> {
            if (keyEvent.getCode() == KeyCode.SPACE) {
                animationManager.spaceKeyPressedProperty().setValue(false);
                keyEvent.consume();
            }
        };
    }
}