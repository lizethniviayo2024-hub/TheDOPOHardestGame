package domain;

/**
 la estrategia solo lee, nunca modifica al jugador.
 */
public final class AIContext {

    public final int x;
    public final int y;
    public final int width;
    public final int height;
    public final int speed;

    public AIContext(int x, int y, int width, int height, int speed) {
        this.x      = x;
        this.y      = y;
        this.width  = width;
        this.height = height;
        this.speed  = speed;
    }
}