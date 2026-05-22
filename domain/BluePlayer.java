package domain;

import java.awt.Color;

public class BluePlayer extends Player {

    public BluePlayer(String name,
                      int x,
                      int y,
                      int size,
                      int baseSpeed) {

        super(
            name,
            x,
            y,
            Math.round(size * 1.5f),
            Math.round(size * 1.5f),
            Math.round(baseSpeed * 1.5f),
            new Color(50, 100, 220)
        );
    }
}