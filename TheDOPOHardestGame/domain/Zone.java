package domain;

/**
 * Zona del tablero. Puede ser segura (inicio, intermedia, final) o pared.
 */
public abstract class Zone extends GameEntity {

    public Zone(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public abstract void onPlayerEnter(Player player);
}