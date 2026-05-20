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
     * Retorna true si el nivel fue completado (solo en modo single player).
     * En PvP, retorna false porque se maneja diferente.
     */
    public boolean checkCollisions(Player player, List<Coin> coins,
                                   List<SafeZone> safeZones,
                                   ScoreController score,
                                   String gameMode) {
        for (Collidable c : collidables) {
            GameEntity go = (GameEntity) c;
            if (player.getBounds().intersects(go.getBounds())) {
                c.onPlayerCollision(player, score);
            }
        }

        // En PvP, no completar el nivel cuando un jugador llega
        if ("PVSP".equals(gameMode)) {
            return false;
        }

        // En single player, completar cuando se llega a zona final con todas las monedas
        for (SafeZone zone : safeZones) {
            if (zone.isFinal()
                    && player.getBounds().intersects(zone.getBounds())
                    && allCoinsCollected(coins)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Verifica si un jugador llegó a la zona final en modo PvP.
     */
    public boolean checkFinalZoneReached(Player player, List<SafeZone> safeZones,
                                        List<Coin> coins) {
        if (!allCoinsCollected(coins)) {
            return false;
        }

        for (SafeZone zone : safeZones) {
            if (zone.isFinal() && player.getBounds().intersects(zone.getBounds())) {
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

    /**
     * Detecta colisión entre dos jugadores.
     * Si colisionan, ambos mueren y reaparecen.
     */
    public boolean checkPlayerCollision(Player p1, Player p2) {
        if (p1.getBounds().intersects(p2.getBounds())) {
            p1.die();
            p2.die();
            return true;
        }
        return false;
    }

    /**
     * Verifica colisiones de múltiples jugadores.
     */
    public void checkAllPlayersCollision(List<Player> players) {
        for (int i = 0; i < players.size(); i++) {
            for (int j = i + 1; j < players.size(); j++) {
                checkPlayerCollision(players.get(i), players.get(j));
            }
        }
    }
}