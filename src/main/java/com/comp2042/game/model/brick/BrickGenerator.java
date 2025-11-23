package com.comp2042.game.model.brick;

import java.util.List;

public interface BrickGenerator {

    Brick getBrick();

    List<Brick> getNextBricks(int count);
}
