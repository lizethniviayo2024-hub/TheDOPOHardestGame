package domain;

import java.awt.Color;

public class RedPlayer extends Player {

    public RedPlayer(String name,
                     int x,
                     int y,
                     int size,
                     int baseSpeed) {

        super(
            name,
            x,
            y,
            size,
            size,
            baseSpeed,
            new Color(220, 50, 50)
        );
    }
}