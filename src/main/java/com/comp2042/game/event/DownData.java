package com.comp2042.game.event;

import com.comp2042.game.ui.ViewData;

public final class DownData {
    private final ClearRow clearRow;
    private final ViewData viewData;

    public DownData(ClearRow clearRow, ViewData viewData) {
        this.clearRow = clearRow;
        this.viewData = viewData;
    }

    public ClearRow getClearRow() {
        return clearRow;
    }

    public ViewData getViewData() {
        return viewData;
    }
}
