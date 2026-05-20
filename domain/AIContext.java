package domain;

/**
 * Snapshot de información que AIStrategy necesita para tomar decisiones.
 *
 * Por qué existe esta clase:
 * Si AIStrategy recibiera el Player directamente, estaría acoplada
 * a la jerarquía de jugadores. AIContext es un objeto de datos plano
 * (value object) que la estrategia puede leer sin saber nada de Player.
 *
 * Se construye en AIControlledPlayer.update() y se pasa a la estrategia.
 * Es inmutable: la estrategia solo lee, nunca modifica al jugador.
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