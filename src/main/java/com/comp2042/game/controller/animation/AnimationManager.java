package com.comp2042.game.controller.animation;

import com.comp2042.game.controller.audio.SoundManager;
import com.comp2042.game.controller.ui.GameViewController;
import com.comp2042.game.event.DownData;
import com.comp2042.game.event.EventSource;
import com.comp2042.game.event.EventType;
import com.comp2042.game.event.InputEventListener;
import com.comp2042.game.event.MoveEvent;
import com.comp2042.game.view.NotificationPanel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.util.Duration;

/**
 * Manages all animations, timelines, and timing-related functionality.
 * Handles game loop, instant drop, timer, and level progression.
 */
public class AnimationManager {

    private static final int BASE_SPEED = 600;

    // Timelines
    private Timeline timeLine;
    private Timeline timerTimeline;
    private Timeline instantDropTimeline;

    // State
    private long startTime;
    private int currentLevel = 1;
    private boolean isDropping = false;
    private final BooleanProperty spaceKeyPressed = new SimpleBooleanProperty(false);

    // Dependencies
    private InputEventListener eventListener;
    private GameViewController viewController;
    private SoundManager soundManager;
    private BooleanProperty isPause;

    public AnimationManager(InputEventListener eventListener, GameViewController viewController,
                            BooleanProperty isPause) {
        this.eventListener = eventListener;
        this.viewController = viewController;
        this.isPause = isPause;
        this.soundManager = SoundManager.getInstance();
    }

    public void startGameTimeline() {
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(BASE_SPEED),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();

        startTime = System.currentTimeMillis();
        timerTimeline = new Timeline(new KeyFrame(
                Duration.seconds(1),
                ae -> updateTimer()
        ));
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        timerTimeline.play();
    }

    public void stopAllTimelines() {
        if (timeLine != null) timeLine.stop();
        if (timerTimeline != null) timerTimeline.stop();
        if (instantDropTimeline != null) instantDropTimeline.stop();
    }

    public void pauseTimelines() {
        if (timeLine != null) timeLine.pause();
        if (timerTimeline != null) timerTimeline.pause();
        if (instantDropTimeline != null) instantDropTimeline.stop();
    }

    public void resumeTimelines() {
        if (timeLine != null) timeLine.play();
        if (timerTimeline != null) timerTimeline.play();
    }

    public void moveDown(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onDownEvent(event);
            viewController.showClearRowNotification(downData.getClearRow());
            viewController.refreshBrick(downData.getViewData());
        }
        viewController.getGamePanel().requestFocus();
    }

    public void instantDrop() {
        if (isPause.getValue() || isDropping) return;

        isDropping = true;
        soundManager.playSound("hard_drop");

        if (instantDropTimeline != null) {
            instantDropTimeline.stop();
        }

        instantDropTimeline = new Timeline(
                new KeyFrame(Duration.millis(1), ae -> {
                    DownData down = eventListener.onDownEvent(
                            new MoveEvent(EventType.DOWN, EventSource.USER));
                    viewController.refreshBrick(down.getViewData());
                    viewController.showClearRowNotification(down.getClearRow());

                    if (down.getClearRow() != null) {
                        instantDropTimeline.stop();
                        isDropping = false;
                    }
                })
        );
        instantDropTimeline.setCycleCount(Timeline.INDEFINITE);
        instantDropTimeline.setOnFinished(event -> isDropping = false);
        instantDropTimeline.play();
    }

    private void updateTimer() {
        // Timer update is handled by GameStateController now
        // This method can notify a callback if needed
    }

    public void levelUp(int newLevel) {
        currentLevel = newLevel;
        viewController.updateLevelLabel(newLevel);

        int newSpeed = Math.max(100, (int)(BASE_SPEED * Math.pow(0.9, newLevel - 1)));

        timeLine.stop();
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(newSpeed),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();

        Timeline delayTimeline = new Timeline(new KeyFrame(
                Duration.millis(1000),
                ae -> {
                    soundManager.playSound("level_up");
                    NotificationPanel levelUpNotif = new NotificationPanel("LEVEL " + newLevel + "!");
                    viewController.getGroupNotification().getChildren().add(levelUpNotif);
                    levelUpNotif.showScore(viewController.getGroupNotification().getChildren());
                }
        ));
        delayTimeline.play();
    }

    public void resetStartTime() {
        startTime = System.currentTimeMillis();
    }

    public long getStartTime() {
        return startTime;
    }

    public BooleanProperty spaceKeyPressedProperty() {
        return spaceKeyPressed;
    }

    public boolean isDropping() {
        return isDropping;
    }

    public void setDropping(boolean dropping) {
        isDropping = dropping;
    }
}