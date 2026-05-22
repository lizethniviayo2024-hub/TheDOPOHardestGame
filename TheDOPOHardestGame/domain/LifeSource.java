package domain;

import java.awt.Color;

/**
 * Fuente de vida. Elemento estático que otorga +1 vida al jugador al contacto.
 *
 * <p>Al ser tocada desaparece del tablero. No es requerida para completar
 * el nivel ({@code requiredForCompletion = false}).
 *
 * <p>Para cambiar el efecto (por ejemplo otorgar más de una vida) basta
 * con sobrescribir {@link #onPlayerCollision} en una subclase —
 * sin tocar {@code GameState}, {@code Board} ni {@code GamePanel}.
 */
public class LifeSource extends Collectible {

    private static final Color COLOR = new Color(50, 200, 100);

    /**
     * @param x      posición X
     * @param y      posición Y
     * @param width  ancho
     * @param height alto
     */
    public LifeSource(int x, int y, int width, int height) {
        super(x, y, width, height, false);
    }

    @Override
    public void onPlayerCollision(Player player, ScoreController score) {
        if (active) {
            consume();
            score.addLife(player.getName());
        }
    }

    @Override
    public Color getColor() {
        return COLOR;
    }
}
