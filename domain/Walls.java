package domain;

/**
 * Pared sólida. Nadie puede atravesarla.
 */
public class Walls extends Zone {

    public Walls(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void update() {
        // no hace nada por defecto
    }

    @Override
    public void onPlayerEnter(Player player) {
        // La colisión impide la entrada — no hace nada
    }
}