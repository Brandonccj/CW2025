package com.comp2042.game.model.brick;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * Random brick generator using the "bag" system.
 * Ensures fair distribution by shuffling all 7 pieces before repeating.
 */
public class RandomBrickGenerator implements BrickGenerator {

    private final List<Brick> brickList;
    private final Deque<Brick> nextBricks = new ArrayDeque<>();

    public RandomBrickGenerator() {
        brickList = new ArrayList<>();
        brickList.add(new IBrick());
        brickList.add(new JBrick());
        brickList.add(new LBrick());
        brickList.add(new OBrick());
        brickList.add(new SBrick());
        brickList.add(new TBrick());
        brickList.add(new ZBrick());
        fillBag();
        fillBag();
    }

    private void fillBag() {
        List<Brick> bag = new ArrayList<>(brickList);
        Collections.shuffle(bag);
        nextBricks.addAll(bag);
    }

    @Override
    public Brick getBrick() {
        if (nextBricks.size() <= 7) {
            fillBag();
        }
        return nextBricks.poll();
    }

    @Override
    public List<Brick> getNextBricks(int count) {
        while (nextBricks.size() < count) {
            fillBag();
        }

        List<Brick> upcomingBricks = new ArrayList<>();
        int i = 0;
        for (Brick brick : nextBricks) {
            if (i >= count) break;
            upcomingBricks.add(brick);
            i++;
        }
        return upcomingBricks;
    }
}