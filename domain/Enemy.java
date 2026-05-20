package domain;

/**
 * Enemigo del juego. Tiene un patrón de movimiento.
 * Si toca al jugador, lo elimina.
 */
public class Enemy extends GameEntity implements Collidable {
    private float speed;
    private Movement movement;

    public Enemy(int x, int y, int size, float speed) {
        super(x, y, size, size);
        this.speed = speed;
    }

    public void setMovement(Movement movement) {
        this.movement = movement;
    }

    /** Board llama a esto en cada tick del game loop. */
    @Override
    public void update() {
        if (movement != null) movement.move();
    }

    @Override
    public void onPlayerCollision(Player player, ScoreController score) {
        player.die();
        score.addDeath();
    }

    public float getSpeed() { return speed; }
}