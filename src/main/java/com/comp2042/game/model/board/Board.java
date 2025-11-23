package com.comp2042.game.model.board;

import com.comp2042.game.event.ClearRow;
import com.comp2042.game.event.GameMode;
import com.comp2042.game.view.ViewData;

/**
 * Interface defining the contract for a Tetris game board.
 * Provides methods for brick manipulation and game state management.
 */
public interface Board {

    boolean moveBrickDown();

    boolean moveBrickLeft();

    boolean moveBrickRight();

    boolean rotateLeftBrick();

    boolean createNewBrick();

    int[][] getBoardMatrix();

    ViewData getViewData();

    void mergeBrickToBackground();

    ClearRow clearRows();

    Score getScore();

    void newGame();

    boolean holdBrick();

    int[][] getHeldBrickData();

    GameMode getGameMode();

    void clearBoard();
}