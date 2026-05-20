package domain;

import java.awt.Color;

public class GreenPlayer extends Player {

    private boolean shieldActive;

    public GreenPlayer(String name,
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
            new Color(50, 180, 50)
        );

        shieldActive = true;
    }

    @Override
    public void hit() {

        if (shieldActive) {

            shieldActive = false;

            speed = Math.round(baseSpeed * 0.7f);

        } else {

            die();
        }
    }

    @Override
    public void die() {

        super.die();

        shieldActive = true;

        speed = baseSpeed;
    }

    @Override
    public boolean isShieldActive() {
        return shieldActive;
    }
}