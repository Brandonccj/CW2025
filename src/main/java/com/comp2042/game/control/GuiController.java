package com.comp2042.game.control;

import com.comp2042.game.event.*;
import com.comp2042.game.ui.GameOverPanel;
import com.comp2042.game.ui.NotificationPanel;
import com.comp2042.game.ui.ViewData;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.util.Duration;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
    private GameOverPanel gameOverPanel;

    @FXML
    private Label scoreLabel;

    @FXML
    private GridPane nextBrickGrid;

    @FXML
    private GridPane heldBrickGrid;

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    private Timeline instantDropTimeline;

    private final List<Rectangle> ghostNodes = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
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
                        instantDrop();
                        keyEvent.consume();
                    }
                }
                if (keyEvent.getCode() == KeyCode.N) {
                    newGame(null);
                }
            }
        });
        gameOverPanel.setVisible(false);

        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
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
    }

    private void initHoldGrid() {
        heldBrickGrid.getChildren().clear();
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                Rectangle rec = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rec.setFill(Color.TRANSPARENT);
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
        double yPos = GAME_BOARD_OFFSET_Y + brick.getyPosition() * (BRICK_SIZE + 1) - 34;
        brickPanel.setLayoutX(xPos);
        brickPanel.setLayoutY(yPos);
    }

    private void initPreviewGrid() {
        nextBrickGrid.getChildren().clear();
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 5; col++) {
                Rectangle rec = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rec.setFill(Color.TRANSPARENT);
                nextBrickGrid.add(rec, col, row);
            }
        }
    }

    private Paint getFillColor(int i) {
        Paint returnPaint;
        switch (i) {
            case 0:
                returnPaint = Color.TRANSPARENT;
                break;
            case 1:
                returnPaint = Color.AQUA;
                break;
            case 2:
                returnPaint = Color.BLUEVIOLET;
                break;
            case 3:
                returnPaint = Color.DARKGREEN;
                break;
            case 4:
                returnPaint = Color.YELLOW;
                break;
            case 5:
                returnPaint = Color.RED;
                break;
            case 6:
                returnPaint = Color.BEIGE;
                break;
            case 7:
                returnPaint = Color.BURLYWOOD;
                break;
            default:
                returnPaint = Color.WHITE;
                break;
        }
        return returnPaint;
    }


    private void refreshBrick(ViewData brick) {
        clearGhosts();
        if (isPause.getValue() == Boolean.FALSE) {
            positionBrickPanel(brick);
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setRectangleData(brick.getBrickData()[i][j], rectangles[i][j]);
                }
            }
        }

        List<int[][]> nextBricks = brick.getNextBricksData();
        updatePreviewGrid(nextBricks);
        int drop = brick.getDropDistance();
        int[][] shape = brick.getBrickData();
        for (int r = 0; r < shape.length; r++) {
            for (int c = 0; c < shape[0].length; c++) {
                if (shape[r][c] != 0) {
                    Rectangle ghost = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                    ghost.setFill(getGhostColor(shape[r][c]));
                    ghost.setStroke(Color.WHITE);
                    ghost.setStrokeWidth(1);
                    ghost.setStrokeType(StrokeType.INSIDE);
                    ghost.setArcHeight(9);
                    ghost.setArcWidth(9);
                    ghost.setOpacity(0.35);

                    int targetRow = brick.getyPosition() + r + drop;
                    if (targetRow < 2 || targetRow >= 25) continue;

                    gamePanel.add(ghost, brick.getxPosition() + c, targetRow - 2);
                    ghostNodes.add(ghost);
                }
            }
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
    }

    public void gameOver() {
        timeLine.stop();
        gameOverPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);
        if (instantDropTimeline != null) instantDropTimeline.stop();
    }

    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);
        eventListener.createNewGame();
        gamePanel.requestFocus();
        timeLine.play();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
        if (instantDropTimeline != null) instantDropTimeline.stop();
    }

    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }
    private void instantDrop() {
        if (isPause.getValue() || isGameOver.getValue()) return;

        if (instantDropTimeline != null) instantDropTimeline.stop();

        instantDropTimeline = new Timeline(
                new KeyFrame(Duration.millis(1), ae -> {
                    DownData down = eventListener.onDownEvent(
                            new MoveEvent(EventType.DOWN, EventSource.USER));
                    refreshBrick(down.getViewData());

                    showClearRowNotification(down.getClearRow()); // Use the method here

                    if (down.getClearRow() != null) {
                        instantDropTimeline.stop();
                    }
                })
        );
        instantDropTimeline.setCycleCount(Timeline.INDEFINITE);
        instantDropTimeline.play();
    }

    private Paint getGhostColor(int colorCode) {
        switch (colorCode) {
            case 0: return Color.TRANSPARENT;
            case 1: return Color.DARKBLUE;
            case 2: return Color.PURPLE.darker();
            case 3: return Color.DARKGREEN.darker();
            case 4: return Color.GOLDENROD;
            case 5: return Color.DARKRED;
            case 6: return Color.BEIGE.darker();
            case 7: return Color.BURLYWOOD.darker();
            default: return Color.GRAY;
        }
    }

    private void clearGhosts() {
        ghostNodes.forEach(g -> gamePanel.getChildren().remove(g));
        ghostNodes.clear();
    }
}


