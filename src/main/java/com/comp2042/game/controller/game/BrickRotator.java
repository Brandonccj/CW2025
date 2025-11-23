package com.comp2042.game.controller.game;

import com.comp2042.game.model.brick.Brick;
import com.comp2042.game.view.NextShapeInfo;

/**
 * Manages brick rotation state and provides rotation functionality.
 * Tracks current rotation state and calculates next rotation.
 */
public class BrickRotator {

    private Brick brick;
    private int currentShape = 0;

    public NextShapeInfo getNextShape() {
        int nextShape = currentShape;
        nextShape = (++nextShape) % brick.getShapeMatrix().size();
        return new NextShapeInfo(brick.getShapeMatrix().get(nextShape), nextShape);
    }

    public int[][] getCurrentShape() {
        return brick.getShapeMatrix().get(currentShape);
    }

    public void setCurrentShape(int currentShape) {
        this.currentShape = currentShape;
    }

    public void setBrick(Brick brick) {
        this.brick = brick;
        currentShape = 0;
    }

    public Brick getBrick() {
        return brick;
    }
}