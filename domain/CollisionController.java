package domain;

import java.util.List;

/**
 * Detecta y resuelve colisiones entre objetos del juego.
 */
public class CollisionController {

    private List<Collidable> collidables;

    public CollisionController(List<Collidable> collidables) {
        this.collidables = collidables;
    }

    /**
     * Revisa colisiones del jugador contra todos los collidables.
     * Retorna true si el nivel fue completado.
     */
    public boolean checkCollisions(Player player, List<Coin> coins,
                                   List<SafeZone> safeZones,
                                   ScoreController score) {
        for (Collidable c : collidables) {
            GameEntity go = (GameEntity) c;
            if (player.getBounds().intersects(go.getBounds())) {
                c.onPlayerCollision(player, score);
            }
        }

        for (SafeZone zone : safeZones) {
            if (zone.isFinal()
                    && player.getBounds().intersects(zone.getBounds())
                    && allCoinsCollected(coins)) {
                return true;
            }
        }

        return false;
    }

    private boolean allCoinsCollected(List<Coin> coins) {
        for (Coin coin : coins) {
            if (!coin.isCollected()) return false;
        }
        return true;
    }

    public boolean collidesWithWall(int x, int y, int w, int h, List<Walls> walls) {
        java.awt.Rectangle rect = new java.awt.Rectangle(x, y, w, h);
        for (Walls wall : walls) {
            if (rect.intersects(wall.getBounds())) return true;
        }
        return false;
    }
}