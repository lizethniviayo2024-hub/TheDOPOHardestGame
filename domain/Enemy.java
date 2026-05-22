package domain;

/**
 * Enemigo del juego. Tiene un patrón de movimiento.
 * Si toca al jugador, lo elimina (o le quita el escudo si aplica).
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

    
    @Override
    public void update() {
        if (movement != null) movement.move();
    }

    /**
     * Delega en player.hit() para que cada subtipo maneje su lógica
     * (GreenPlayer absorbe el primer golpe; los demás reaparecen).
     *
     *si el escudo estaba activo antes del golpe ya no lo está
     * después → golpe absorbido, NO muere. En cualquier otro caso → muere.
     */
    @Override
    public void onPlayerCollision(Player player, ScoreController score) {
        boolean hadShield = player.isShieldActive();
        player.hit();
        boolean stillHasShield = player.isShieldActive();
        // Sitenía escudo y ahora no lo tiene, no cuenta muerte
        if (hadShield && !stillHasShield) {
            return; // solo perdió el escudo
        }
        // En cualquier otro caso el jugador reaparece y registrar muerte
        score.addDeath(player.getName());
    }

    public float getSpeed() { return speed; }
}
