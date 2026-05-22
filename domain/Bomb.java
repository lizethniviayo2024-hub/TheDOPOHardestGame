package domain;

import java.awt.Color;

/**
 * Bomba. Elemento estático que destruye al jugador Y a cualquier enemigo
 * que la toque.
 *
 * <p>Al activarse desaparece del tablero. No es requerida para completar
 * el nivel ({@code requiredForCompletion = false}).
 *
 * <p>La destrucción de enemigos se delega a {@link Board} a través del
 * flag {@link #triggered}: en cada tick, {@code Board.update()} revisa
 * si alguna bomba fue activada y elimina al enemigo responsable.
 * Esto mantiene la responsabilidad de gestión de entidades en el dominio,
 * no en la colisión.
 */
public class Bomb extends Collectible {

    private static final Color COLOR = new Color(180, 0, 0);

    /**
     * Referencia al enemigo que tocó la bomba, si fue un enemigo.
     * null si fue el jugador quien la activó.
     * Board usa este campo para eliminar al enemigo del estado.
     */
    private Enemy triggeringEnemy;

    /**
     * @param x      posición X
     * @param y      posición Y
     * @param width  ancho
     * @param height alto
     */
    public Bomb(int x, int y, int width, int height) {
        super(x, y, width, height, false);
    }

    /**
     * El jugador toca la bomba: reaparece en su spawn y la bomba desaparece.
     * La muerte se registra en el ScoreController.
     */
    @Override
    public void onPlayerCollision(Player player, ScoreController score) {
        if (active) {
            consume();
            player.respawn();
            score.addDeath(player.getName());
        }
    }

    /**
     * Un enemigo toca la bomba. Guarda la referencia para que
     * {@code Board} lo elimine en el mismo tick.
     *
     * @param enemy el enemigo que activó la bomba
     */
    public void onEnemyCollision(Enemy enemy) {
        if (active) {
            consume();
            triggeringEnemy = enemy;
        }
    }

    /**
     * @return el enemigo que activó esta bomba, o null si fue el jugador
     */
    public Enemy getTriggeringEnemy() {
        return triggeringEnemy;
    }

    @Override
    public Color getColor() {
        return COLOR;
    }

    @Override
    public Shape getShape() {
        return Shape.RECT;
    }
}
