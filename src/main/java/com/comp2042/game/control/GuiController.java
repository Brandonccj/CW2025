package com.comp2042.game.control;

import com.comp2042.game.event.*;
import com.comp2042.game.ui.GameOverPanel;
import com.comp2042.game.ui.NotificationPanel;
import com.comp2042.game.ui.ViewData;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.util.Duration;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import com.comp2042.game.ui.PauseMenu;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;
    private static final int GAME_BOARD_OFFSET_X = 180;
    private static final int GAME_BOARD_OFFSET_Y = 30;

    @FXML
    private GridPane gamePanel;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel;

    @FXML
    private Label scoreLabel;

    @FXML
    private GridPane nextBrickGrid;

    @FXML
    private GridPane heldBrickGrid;

    @FXML
    private Label timeLabel;

    @FXML
    private Label highScoreLabel;

    @FXML
    private Label levelLabel;

    @FXML
    private Label linesLabel;

    @FXML
    private StackPane gameOverOverlay;

    @FXML
    private StackPane pauseOverlay;

    @FXML
    private Label musicStatusLabel;

    @FXML
    private Label sfxStatusLabel;

    private PauseMenu pauseMenu;

    private GameOverPanel gameOverPanel;

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;

    private Rectangle[][] shadowRectangles;

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    private Timeline instantDropTimeline;

    private long startTime;
    private Timeline timerTimeline;
    private int highScore = HighScoreManager.loadHighScore();

    private int currentLevel = 1;
    private static final int BASE_SPEED = 600;

    private GameMode currentGameMode;
    private Label gameModeLabel;

    private boolean isDropping = false;
    private final BooleanProperty spaceKeyPressed = new SimpleBooleanProperty(false);

    private SoundManager soundManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("determination.ttf").toExternalForm(), 38);
        gameOverPanel = new GameOverPanel();
        if (gameOverOverlay != null) {
            gameOverOverlay.getChildren().clear();
            gameOverOverlay.getChildren().add(gameOverPanel);
            gameOverOverlay.setVisible(false);
        }

        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.P) {
                    togglePause();
                    keyEvent.consume();
                    return;
                }
                if (keyEvent.getCode() == KeyCode.N) {
                    newGame(null);
                    keyEvent.consume();
                    return;
                }
                if (keyEvent.getCode() == KeyCode.M) {
                    boolean currentState = soundManager.isMusicEnabled();
                    soundManager.setMusicEnabled(!currentState);
                    updateMusicStatusLabel();
                    keyEvent.consume();
                    return;
                }
                if (keyEvent.getCode() == KeyCode.K) {
                    boolean currentState = soundManager.isSfxEnabled();
                    soundManager.setSfxEnabled(!currentState);
                    updateSfxStatusLabel();
                    keyEvent.consume();
                    return;
                }
                if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
                    if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.SPACE) {
                        if (!isDropping && !spaceKeyPressed.getValue()) {
                            spaceKeyPressed.setValue(true);
                            instantDrop();
                        }
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.C) {
                        refreshBrick(eventListener.onHoldEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                        keyEvent.consume();
                    }

                }
            }

        });
        gamePanel.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.SPACE) {
                    spaceKeyPressed.setValue(false);
                    keyEvent.consume();
                }
            }
        });
        gameOverPanel.setVisible(false);

        shadowRectangles = new Rectangle[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                shadowRectangles[i][j] = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                shadowRectangles[i][j].setFill(Color.TRANSPARENT);
                shadowRectangles[i][j].setArcWidth(6);
                shadowRectangles[i][j].setArcHeight(6);
                shadowRectangles[i][j].setOpacity(0.35);
            }
        }
    }



    public void initGameView(int[][] boardMatrix, ViewData brick,GameMode mode) {
        this.currentGameMode = mode;
        soundManager = SoundManager.getInstance();

        StackPane rootPane = (StackPane) gamePanel.getScene().getRoot();
        if (mode == GameMode.ZEN) {
            if (!rootPane.getStyleClass().contains("zen-mode")) {
                rootPane.getStyleClass().add("zen-mode");
            }
            List<String> zenPlaylist = Arrays.asList(
                    "/sounds/zen_music_1.mp3",
                    "/sounds/zen_music_2.mp3"
            );
            soundManager.playPlaylist("zen_mode", zenPlaylist, false);
            gameModeLabel = new Label("ZEN MODE");
            gameModeLabel.setStyle("-fx-font-family: 'Determination'; -fx-font-size: 18px; -fx-text-fill: #26A8B7; -fx-font-weight: bold;");
            highScoreLabel.setVisible(false);
            linesLabel.setText("0");
            gamePanel.setStyle("-fx-effect: dropshadow(gaussian, rgba(77, 208, 225, 0.3), 5, 0.5, 0, 0);");

        } else {
            rootPane.getStyleClass().remove("zen-mode");
            List<String> normalPlaylist = Arrays.asList(
                    "/sounds/normal_music_1.mp3",
                    "/sounds/normal_music_2.mp3",
                    "/sounds/normal_music_3.mp3",
                    "/sounds/normal_music_4.mp3"
            );
            soundManager.playPlaylist("normal_mode", normalPlaylist, true);
            gameModeLabel = new Label("NORMAL MODE");
            gameModeLabel.setStyle("-fx-font-family: 'Determination'; -fx-font-size: 18px; -fx-text-fill: #00FF00; -fx-font-weight: bold;");
            highScoreLabel.setText("High Score: " + highScore);
            highScoreLabel.setVisible(true);
            linesLabel.setText("0/5");
            gamePanel.setStyle("-fx-effect: dropshadow(gaussian, rgba(156, 39, 176, 0.3), 5, 0.5, 0, 0);");
        }

        gameModeLabel.setLayoutX(270);
        gameModeLabel.setLayoutY(35);
        ((javafx.scene.layout.Pane) gamePanel.getParent()).getChildren().add(gameModeLabel);
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }

        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(getFillColor(brick.getBrickData()[i][j]));
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
        positionBrickPanel(brick);

        timeLine = new Timeline(new KeyFrame(
                Duration.millis(600),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();

        initPreviewGrid();
        initHoldGrid();

        highScoreLabel.setText("High Score: " + highScore);

        startTime = System.currentTimeMillis();
        timerTimeline = new Timeline(new KeyFrame(
                Duration.seconds(1),
                ae -> updateTimer()
        ));
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        timerTimeline.play();
        pauseMenu = new PauseMenu();
        if (pauseOverlay != null) {
            pauseOverlay.getChildren().clear();
            pauseOverlay.getChildren().add(pauseMenu);
            pauseOverlay.setVisible(false);

            pauseMenu.setGameMode(mode);

            pauseMenu.getResumeButton().setOnAction(e -> {
                soundManager.playSound("button_click");
                resumeGame();
            });

            pauseMenu.getRestartButton().setOnAction(e -> {
                soundManager.playSound("button_click");
                restartGame();
            });

            pauseMenu.getMainMenuButton().setOnAction(e -> {
                soundManager.playSound("button_click");
                returnToMainMenu();
            });

        }

        if (gameOverPanel != null) {
            gameOverPanel.getMainMenuButton().setOnAction(e -> {
                soundManager.playSound("button_click");
                returnToMainMenu();
            });
        }
        if (mode == GameMode.ZEN) {
            levelLabel.setVisible(false);
            levelLabel.getParent().setVisible(false);
        }

        if (mode == GameMode.NORMAL) {
            highScoreLabel.setText("High Score: " + highScore);
        } else {
            highScoreLabel.setText("Best: N/A");
        }

        Platform.runLater(() -> {
            updateMusicStatusLabel();
            updateSfxStatusLabel();
        });
    }

    private void initHoldGrid() {
        heldBrickGrid.getChildren().clear();
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                Rectangle rec = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rec.setFill(Color.TRANSPARENT);
                rec.setArcWidth(9);
                rec.setArcHeight(9);
                heldBrickGrid.add(rec, c, r);
            }
        }
    }
    private void updateHoldGrid(int[][] heldBrick) {
        for (int i = 0; i < heldBrickGrid.getChildren().size(); i++) {
            Rectangle cell = (Rectangle) heldBrickGrid.getChildren().get(i);
            cell.setFill(Color.TRANSPARENT);
        }

        if (heldBrick != null) {
            int offsetX = (4 - heldBrick[0].length) / 2;
            int offsetY = (4 - heldBrick.length) / 2;

            for (int r = 0; r < heldBrick.length; r++) {
                for (int c = 0; c < heldBrick[r].length; c++) {
                    if (heldBrick[r][c] != 0) {
                        int gridRow = r + offsetY;
                        int gridCol = c + offsetX;
                        int gridIndex = gridRow * 4 + gridCol;

                        if (gridIndex < heldBrickGrid.getChildren().size()) {
                            Rectangle cell = (Rectangle) heldBrickGrid.getChildren().get(gridIndex);
                            cell.setFill(getFillColor(heldBrick[r][c]));
                        }
                    }
                }
            }
        }
    }

    private void positionBrickPanel(ViewData brick) {
        double xPos = GAME_BOARD_OFFSET_X + 11.5 + brick.getxPosition() * (BRICK_SIZE + 1);
        double yPos = GAME_BOARD_OFFSET_Y + brick.getyPosition() * (BRICK_SIZE + 1) - 4;
        brickPanel.setLayoutX(xPos);
        brickPanel.setLayoutY(yPos);
    }

    private void initPreviewGrid() {
        nextBrickGrid.getChildren().clear();
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 5; col++) {
                Rectangle rec = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rec.setFill(Color.TRANSPARENT);
                rec.setArcWidth(9);
                rec.setArcHeight(9);
                nextBrickGrid.add(rec, col, row);
            }
        }
    }

    private Paint getFillColor(int i) {
        if (currentGameMode == GameMode.ZEN) {
            switch (i) {
                case 0: return Color.TRANSPARENT;
                case 1: return Color.rgb(77, 208, 225);    // Cyan
                case 2: return Color.rgb(100, 181, 246);   // Light blue
                case 3: return Color.rgb(38, 198, 218);    // Teal
                case 4: return Color.rgb(128, 222, 234);   // Pale cyan
                case 5: return Color.rgb(0, 188, 212);     // Dark cyan
                case 6: return Color.rgb(77, 182, 172);    // Teal-green
                case 7: return Color.rgb(38, 166, 154);    // Sea green
                default: return Color.WHITE;
            }
        } else {
            switch (i) {
                case 0: return Color.TRANSPARENT;
                case 1: return Color.rgb(186, 104, 200);   // Light purple
                case 2: return Color.rgb(156, 39, 176);    // Deep purple
                case 3: return Color.rgb(123, 31, 162);    // Rich purple
                case 4: return Color.rgb(103, 58, 183);    // Indigo-purple
                case 5: return Color.rgb(149, 117, 205);   // Soft lavender
                case 6: return Color.rgb(94, 53, 177);     // Medium-dark purple
                case 7: return Color.rgb(74, 20, 140);     // Dark violet
                default: return Color.WHITE;
            }
        }
    }


    private void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {
            for (Rectangle[] row : rectangles) {
                for (Rectangle r : row) {
                    gamePanel.getChildren().remove(r);
                    r.setFill(Color.TRANSPARENT);
                }
            }

            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    if (brick.getBrickData()[i][j] != 0) {
                        int gridX = brick.getxPosition() + j;
                        int gridY = brick.getyPosition() + i;
                        if (gridY >= 2 && gridY < 25) {
                            rectangles[i][j].setFill(getFillColor(brick.getBrickData()[i][j]));
                            rectangles[i][j].setArcWidth(6);
                            rectangles[i][j].setArcHeight(6);
                            gamePanel.add(rectangles[i][j], gridX, gridY - 2);
                        }
                    }
                }
            }

            updateShadow(brick);

            List<int[][]> nextBricks = brick.getNextBricksData();
            updatePreviewGrid(nextBricks);

            updateHoldGrid(brick.getHeldBrickData());
        }
    }

    private void updatePreviewGrid(List<int[][]> nextBricks) {
        for (javafx.scene.Node node : nextBrickGrid.getChildren()) {
            ((Rectangle) node).setFill(Color.TRANSPARENT);
        }

        for (int brickIndex = 0; brickIndex < Math.min(3, nextBricks.size()); brickIndex++) {
            int[][] brickData = nextBricks.get(brickIndex);

            int minRow = 4, maxRow = -1, minCol = 4, maxCol = -1;
            for (int r = 0; r < 4; r++) {
                for (int c = 0; c < 4; c++) {
                    if (brickData[r][c] != 0) {
                        minRow = Math.min(minRow, r);
                        maxRow = Math.max(maxRow, r);
                        minCol = Math.min(minCol, c);
                        maxCol = Math.max(maxCol, c);
                    }
                }
            }

            if (maxRow < 0) continue;

            int width = maxCol - minCol + 1;
            int height = maxRow - minRow + 1;
            int colOffset = (5 - width) / 2;
            int rowOffset = (4 - height) / 2;

            for (int r = minRow; r <= maxRow; r++) {
                for (int c = minCol; c <= maxCol; c++) {
                    if (brickData[r][c] != 0) {
                        int displayRow = brickIndex * 5 + rowOffset + (r - minRow);
                        int displayCol = colOffset + (c - minCol);

                        Rectangle cell = getRectangleAt(nextBrickGrid, displayCol, displayRow);
                        if (cell != null) {
                            cell.setFill(getFillColor(brickData[r][c]));
                        }
                    }
                }
            }
        }
    }

    // Helper method to get rectangle at specific grid position
    private Rectangle getRectangleAt(GridPane grid, int col, int row) {
        for (javafx.scene.Node node : grid.getChildren()) {
            Integer nodeCol = GridPane.getColumnIndex(node);
            Integer nodeRow = GridPane.getRowIndex(node);
            if (nodeCol != null && nodeRow != null && nodeCol == col && nodeRow == row) {
                return (Rectangle) node;
            }
        }
        return null;
    }
    public void refreshGameBackground(int[][] board) {
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
    }

    private void showClearRowNotification(ClearRow clearRow) {
        if (clearRow != null && clearRow.getLinesRemoved() > 0) {
            soundManager.playSound("clear_row");
            NotificationPanel notificationPanel = new NotificationPanel("+" + clearRow.getScoreBonus());
            groupNotification.getChildren().add(notificationPanel);
            notificationPanel.showScore(groupNotification.getChildren());
        }
    }

    private void moveDown(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onDownEvent(event);
            showClearRowNotification(downData.getClearRow());
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void bindScore(IntegerProperty integerProperty) {
        scoreLabel.textProperty().bind(integerProperty.asString("Score: %d"));
        integerProperty.addListener((obs, oldVal, newVal) -> {
            updateHighScore(newVal.intValue());
        });
    }

    public void gameOver() {
        timeLine.stop();
        if (timerTimeline != null) timerTimeline.stop();
        isGameOver.setValue(Boolean.TRUE);
        if (instantDropTimeline != null) instantDropTimeline.stop();

        soundManager.stopMusic();
        soundManager.playSound("board_clear");

        Timeline musicDelayTimeline = new Timeline(new KeyFrame(
                Duration.millis(2000),
                ae -> {
                    soundManager.playMusic("/sounds/gameover_music.mp3");
                }
        ));
        musicDelayTimeline.play();

        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        long minutes = elapsed / 60;
        long seconds = elapsed % 60;
        String timeString = String.format("%d:%02d", minutes, seconds);

        String scoreText = scoreLabel.getText().replace("Score: ", "");
        int currentScore = Integer.parseInt(scoreText);

        String linesText = linesLabel.getText();
        int totalLines;
        if (currentGameMode == GameMode.ZEN) {
            totalLines = Integer.parseInt(linesText);
        } else {
            totalLines = Integer.parseInt(linesText.split("/")[0]);
        }

        if (gameOverPanel != null) {
            // In Normal mode, show high score; in Zen mode, just show current stats
            int displayHighScore = (currentGameMode == GameMode.NORMAL) ? highScore : currentScore;
            gameOverPanel.updateStats(timeString, currentScore, displayHighScore, totalLines);
            gameOverPanel.setVisible(true);
        }

        if (gameOverOverlay != null) {
            gameOverOverlay.setVisible(true);
            gameOverOverlay.toFront();
        }
    }
    public void newGame(ActionEvent actionEvent) {
        if (timeLine != null) timeLine.stop();
        if (timerTimeline != null) timerTimeline.stop();
        if (instantDropTimeline != null) instantDropTimeline.stop();

        isDropping = false;
        spaceKeyPressed.setValue(false);

        if (pauseOverlay != null) pauseOverlay.setVisible(false);
        isPause.setValue(Boolean.FALSE);

        gameOverOverlay.setVisible(false);
        isGameOver.setValue(Boolean.FALSE);

        eventListener.createNewGame();

        ViewData viewData = eventListener.getViewData();
        refreshBrick(viewData);

        timeLine = new Timeline(new KeyFrame(
                Duration.millis(BASE_SPEED),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();

        startTime = System.currentTimeMillis();
        timeLabel.setText("Time: 0:00");
        timerTimeline = new Timeline(new KeyFrame(
                Duration.seconds(1),
                ae -> updateTimer()
        ));
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        timerTimeline.play();

        gamePanel.requestFocus();
    }

    private void instantDrop() {
        if (isPause.getValue() || isGameOver.getValue() || isDropping) return;

        isDropping = true; // Set flag to prevent concurrent drops
        soundManager.playSound("hard_drop");

        if (instantDropTimeline != null) {
            instantDropTimeline.stop();
        }

        instantDropTimeline = new Timeline(
                new KeyFrame(Duration.millis(1), ae -> {
                    DownData down = eventListener.onDownEvent(
                            new MoveEvent(EventType.DOWN, EventSource.USER));
                    refreshBrick(down.getViewData());

                    showClearRowNotification(down.getClearRow());

                    if (down.getClearRow() != null) {
                        instantDropTimeline.stop();
                        isDropping = false;
                    }
                })
        );
        instantDropTimeline.setCycleCount(Timeline.INDEFINITE);

        instantDropTimeline.setOnFinished(event -> {
            isDropping = false;
        });

        instantDropTimeline.play();
    }

    private Paint getGhostColor(int colorCode) {
        if (currentGameMode == GameMode.ZEN) {
            switch (colorCode) {
                case 0: return Color.TRANSPARENT;
                case 1: return Color.rgb(38, 104, 113);    // Dark cyan
                case 2: return Color.rgb(50, 90, 123);     // Dark light blue
                case 3: return Color.rgb(19, 99, 109);     // Dark teal
                case 4: return Color.rgb(64, 111, 117);    // Dark pale cyan
                case 5: return Color.rgb(0, 94, 106);      // Very dark cyan
                case 6: return Color.rgb(38, 91, 86);      // Dark teal-green
                case 7: return Color.rgb(19, 83, 77);      // Dark sea green
                default: return Color.GRAY;
            }
        } else {
            switch (colorCode) {
                case 0: return Color.TRANSPARENT;
                case 1: return Color.rgb(112,  62, 120);   // ghost light purple
                case 2: return Color.rgb( 94,  23, 106);   // ghost deep purple
                case 3: return Color.rgb( 74,  19,  97);   // ghost rich purple
                case 4: return Color.rgb( 62,  35, 110);   // ghost indigo-purple
                case 5: return Color.rgb( 89,  70, 123);   // ghost soft lavender
                case 6: return Color.rgb( 56,  32, 106);   // ghost medium-dark purple
                case 7: return Color.rgb( 44,  12,  84);   // ghost dark violet
                default: return Color.GRAY;
            }
        }
    }

    private void updateShadow(ViewData brick) {
        // Clear old shadow pieces - REMOVE from gamePanel first
        for (int i = 0; i < shadowRectangles.length; i++) {
            for (int j = 0; j < shadowRectangles[i].length; j++) {
                gamePanel.getChildren().remove(shadowRectangles[i][j]);
                shadowRectangles[i][j].setFill(Color.TRANSPARENT);
            }
        }

        // Calculate shadow Y position
        int shadowYPosition = brick.getyPosition() + brick.getDropDistance();

        // Only draw shadow if it's different from current position
        if (shadowYPosition != brick.getyPosition()) {
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    if (brick.getBrickData()[i][j] != 0) {
                        int gridX = brick.getxPosition() + j;
                        int gridY = shadowYPosition + i;
                        if (gridY >= 2 && gridY < 25) {
                            shadowRectangles[i][j].setFill(getGhostColor(brick.getBrickData()[i][j]));
                            shadowRectangles[i][j].setArcHeight(6);
                            shadowRectangles[i][j].setArcWidth(6);
                            shadowRectangles[i][j].setOpacity(0.35);
                            shadowRectangles[i][j].setStroke(Color.WHITE);
                            shadowRectangles[i][j].setStrokeWidth(1);
                            shadowRectangles[i][j].setStrokeType(StrokeType.INSIDE);
                            gamePanel.add(shadowRectangles[i][j], gridX, gridY - 2);
                        }
                    }
                }
            }
        }
    }

    private void updateTimer() {
        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        long minutes = elapsed / 60;
        long seconds = elapsed % 60;
        timeLabel.setText(String.format("Time: %d:%02d", minutes, seconds));
    }

    private void updateHighScore(int currentScore) {
        if (currentScore > highScore) {
            highScore = currentScore;
            highScoreLabel.setText("High Score: " + highScore);
            HighScoreManager.saveHighScore(highScore);
        }
    }

    public void bindLines(IntegerProperty linesProperty) {
        linesProperty.addListener((obs, oldVal, newVal) -> {
            int totalLines = newVal.intValue();

            if (currentGameMode == GameMode.ZEN) {
                linesLabel.setText(String.valueOf(totalLines));
            } else {
                // Normal mode
                int currentLevel = (totalLines / 5) + 1;
                int targetLines = currentLevel * 5;
                linesLabel.setText(totalLines + "/" + targetLines);
            }
        });
    }

    public void levelUp(int newLevel) {
        currentLevel = newLevel;
        levelLabel.setText(String.valueOf(newLevel));

        // Calculate new speed: speed decreases as level increases
        // Level 1: 600ms, Level 2: 540ms, Level 3: 486ms, etc.
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
                    groupNotification.getChildren().add(levelUpNotif);
                    levelUpNotif.showScore(groupNotification.getChildren());
                }
        ));
        delayTimeline.play();
    }
    private void togglePause() {
        if (isGameOver.getValue()) return;

        if (isPause.getValue()) {
            resumeGame();
        } else {
            pauseGame();
        }
    }

    private void pauseGame() {
        isPause.setValue(Boolean.TRUE);
        timeLine.stop();
        if (timerTimeline != null) timerTimeline.stop();
        if (instantDropTimeline != null) instantDropTimeline.stop();

        soundManager.pauseMusic();

        if (pauseOverlay != null) {
            pauseOverlay.setVisible(true);
            pauseOverlay.toFront();
        }
        gamePanel.requestFocus();
    }

    private void resumeGame() {
        isPause.setValue(Boolean.FALSE);
        timeLine.play();
        if (timerTimeline != null) timerTimeline.play();

        soundManager.resumeMusic();

        if (pauseOverlay != null) {
            pauseOverlay.setVisible(false);
        }

        gamePanel.requestFocus();
    }

    private void restartGame() {
        if (timeLine != null) timeLine.stop();
        if (timerTimeline != null) timerTimeline.stop();
        if (instantDropTimeline != null) instantDropTimeline.stop();

        isDropping = false;
        spaceKeyPressed.setValue(false);

        pauseOverlay.setVisible(false);

        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);

        eventListener.createNewGame();
        ViewData viewData = eventListener.getViewData();
        refreshBrick(viewData);

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

        gamePanel.requestFocus();
    }

    private void returnToMainMenu() {
        try {
            if (timeLine != null) timeLine.stop();
            if (timerTimeline != null) timerTimeline.stop();
            if (instantDropTimeline != null) instantDropTimeline.stop();

            soundManager.stopMusic();
            soundManager.playMusic("/sounds/menu_music.mp3");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/mainMenu.fxml"));
            Parent menuRoot = loader.load();

            Stage stage = (Stage) gamePanel.getScene().getWindow();

            Scene menuScene = new Scene(menuRoot, 620, 600);
            stage.setScene(menuScene);

        } catch (Exception e) {
            e.printStackTrace();
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

}