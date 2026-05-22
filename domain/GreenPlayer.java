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

    /**
     * Primer golpe: absorbe con escudo (reduce velocidad, no reaparece).
     * Segundo golpe: reaparece en spawn. Enemy.onPlayerCollision detecta
     * cuál caso ocurrió y registra la muerte solo en el segundo caso.
     */
    @Override
    public void hit() {

        if (shieldActive) {
            // Primer golpe: perder escudo y bajar velocidad
            shieldActive = false;
            speed = Math.round(baseSpeed * 0.7f);
        } else {
            // Segundo golpe: reaparecer (Enemy registra la muerte)
            respawn();
        }
    }

    /**
     * Restablece escudo y velocidad al reaparecer.
     */
    @Override
    public void respawn() {
        super.respawn();
        shieldActive = true;
        speed = baseSpeed;
    }

    @Override
    public boolean isShieldActive() {
        return shieldActive;
    }
}
