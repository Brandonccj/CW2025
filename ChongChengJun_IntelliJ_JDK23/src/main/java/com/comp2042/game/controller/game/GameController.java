package com.comp2042.game.controller.game;

import com.comp2042.game.control.GuiController;
import com.comp2042.game.controller.audio.SoundManager;
import com.comp2042.game.event.*;
import com.comp2042.game.model.board.Board;
import com.comp2042.game.view.ViewData;

/**
 * Main game controller handling game logic and coordinating between
 * the board model and GUI controller.
 */
public class GameController implements InputEventListener {

    private Board board;
    private final GuiController viewGuiController;
    private int currentLevel = 1;
    private static final int LINES_PER_LEVEL = 5;
    private final GameMode gameMode;
    private final SoundManager soundManager;

    /**
     * Creates a new game controller and initializes the game.
     * Sets up the board, binds UI elements, and starts the game.
     *
     * @param c the GUI controller for view management
     * @param mode the game mode (NORMAL or ZEN)
     */
    public GameController(GuiController c, GameMode mode) {
        this.gameMode = mode;
        this.board = new BoardController(25, 10, mode);
        this.viewGuiController = c;
        this.soundManager = SoundManager.getInstance();

        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData(), mode);
        viewGuiController.bindScore(board.getScore().scoreProperty());
        viewGuiController.bindLines(board.getScore().linesClearedProperty());
    }

    @Override
    public ViewData getViewData() {
        return board.getViewData();
    }

    /**
     * Handles brick movement down events.
     * Processes collision, line clearing, scoring, and level progression.
     *
     * @param event the move event containing source information
     * @return DownData containing clear row information and updated view data
     */
    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;

        if (!canMove) {
            board.mergeBrickToBackground();
            clearRow = board.clearRows();

            if (clearRow.getLinesRemoved() > 0) {
                board.getScore().add(clearRow.getScoreBonus());
                board.getScore().addLines(clearRow.getLinesRemoved());

                if (gameMode == GameMode.NORMAL) {
                    checkLevelUp();
                }
            }

            boolean collision = board.createNewBrick();

            if (collision) {
                if (gameMode == GameMode.ZEN) {
                    board.clearBoard();
                    board.createNewBrick();
                    viewGuiController.refreshGameBackground(board.getBoardMatrix());
                    viewGuiController.showZenClearNotification();
                } else {
                    viewGuiController.gameOver();
                }
            }

            viewGuiController.refreshGameBackground(board.getBoardMatrix());

        } else {
            if (event.getEventSource() == EventSource.USER) {
                board.getScore().add(1);
            }
        }
        return new DownData(clearRow, board.getViewData());
    }

    /**
     * Checks if the player has advanced to a new level based on lines cleared.
     * Triggers level up sequence if threshold is reached.
     */
    private void checkLevelUp() {
        int totalLines = board.getScore().getLinesCleared();
        int newLevel = (totalLines / LINES_PER_LEVEL) + 1;

        if (newLevel > currentLevel) {
            currentLevel = newLevel;
            viewGuiController.levelUp(currentLevel);
        }
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    @Override
    public ViewData onHoldEvent(MoveEvent event) {
        boolean success = board.holdBrick();
        if (success) {
            soundManager.playSound("hold_piece");
        }
        return board.getViewData();
    }

    /**
     * Creates a new game by resetting the board and restarting timelines.
     */
    @Override
    public void createNewGame() {
        board.newGame();
        currentLevel = 1;
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }
}