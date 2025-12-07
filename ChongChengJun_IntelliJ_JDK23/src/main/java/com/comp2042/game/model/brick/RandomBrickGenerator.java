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

    /**
     * Creates a new random brick generator using the bag system.
     * Initializes the brick list and pre-fills the bag with two sets of bricks.
     */

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

    /**
     * Fills the bag with a shuffled set of all 7 brick types.
     * Ensures fair distribution of pieces over time.
     */
    private void fillBag() {
        List<Brick> bag = new ArrayList<>(brickList);
        Collections.shuffle(bag);
        nextBricks.addAll(bag);
    }

    /**
     * Returns the next brick from the bag.
     * Automatically refills the bag when running low.
     *
     * @return the next brick to use
     */
    @Override
    public Brick getBrick() {
        if (nextBricks.size() <= 7) {
            fillBag();
        }
        return nextBricks.poll();
    }

    /**
     * Returns a preview of upcoming bricks without removing them from the queue.
     *
     * @param count the number of bricks to preview
     * @return list of upcoming bricks
     */
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