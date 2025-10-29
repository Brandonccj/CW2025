package com.comp2042.game.control;

import com.comp2042.game.ui.Score;
import com.comp2042.game.ui.ViewData;
import com.comp2042.game.event.ClearRow;

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
}
