package domain;

/**
 * Moneda amarilla. Debe ser recolectada para completar el nivel.
 */
public class Coin extends GameEntity implements Collidable {
    private boolean isCollected;

    public Coin(int x, int y, int size) {
        super(x, y, size, size);
        this.isCollected = false;
    }

    @Override
    public void update() {
        // no hace nada por defecto
    }

    @Override
    public void onPlayerCollision(Player player, ScoreController score) {
        if (!isCollected) {
            isCollected = true;
            score.addCoin(player.getName());
        }
    }

    public boolean isCollected() { return isCollected; }

    public void collect()        { this.isCollected = true; }
}
