package domain;

import java.awt.Color;

/**
 * Moneda amarilla estándar. Debe recolectarse para completar el nivel.
 *
 * <p>Al contacto con el jugador desaparece y suma un punto de moneda
 * al {@link ScoreController}. Es {@code requiredForCompletion = true}
 * por lo que el {@link CollisionController} la considera al verificar
 * si todas las monedas fueron recogidas.
 */
public class Coin extends Collectible {

    private static final Color COLOR = new Color(255, 215, 0);

    /**
     * @param x    posición X
     * @param y    posición Y
     * @param size ancho y alto (moneda cuadrada)
     */
    public Coin(int x, int y, int size) {
        super(x, y, size, size, true);
    }

    @Override
    public void onPlayerCollision(Player player, ScoreController score) {
        if (active) {
            consume();
            score.addCoin(player.getName());
        }
    }

    @Override
    public Color getColor() {
        return COLOR;
    }

    /** @return true si la moneda ya fue recogida */
    public boolean isCollected() {
        return !active;
    }

    /**
     * Marca la moneda como recogida sin pasar por colisión.
     * Usado por {@link domain.LoadManager} al restaurar partida guardada.
     */
    public void collect() {
        consume();
    }
}
