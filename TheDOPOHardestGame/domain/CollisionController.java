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
     * Revisa colisiones del jugador contra todos 
     * Retorna true si el nivel fue completado (solo en modo single player).
     * En PvP retorna false porque se maneja en checkFinalZoneReachedForPlayer.
     */
    public boolean checkCollisions(Player player, List<Coin> coins, List<SafeZone> safeZones, ScoreController score, String gameMode) {
        for (Collidable c : collidables) {
            GameEntity go = (GameEntity) c;
            if (player.getBounds().intersects(go.getBounds())) {
                c.onPlayerCollision(player, score);
            }
        }

        if ("PVSP".equals(gameMode)) {
            return false;
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

    /**
     * Verifica si un jugador llegó a su zona final en modo PvP.
     * Respeta el ownerName de la zona: si tiene dueño, solo aplica a ese jugador.
     */
    public boolean checkFinalZoneReachedForPlayer(Player player, List<SafeZone> safeZones, List<Coin> coins) {
        // In PvP we allow a player to reach the final zone regardless of whether
        // all coins are collected. The winner determination (who actually wins)
        // will be handled by Board using coin/death counts. Therefore we no
        // longer block final-zone detection on allCoinsCollected.
        
        for (SafeZone zone : safeZones) {
            if (!zone.isFinal()) continue;
            if (zone.getOwnerName() != null
                    && !zone.getOwnerName().equals(player.getName())) {
                continue;
            }
            if (player.getBounds().intersects(zone.getBounds())) {
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
     * Colisión entre dos jugadores: ambos reaparecen y se registran sus muertes.
     */
    public boolean checkPlayerCollision(Player p1, Player p2, ScoreController score) {
        if (p1.getBounds().intersects(p2.getBounds())) {
            if (score != null) {
                score.addDeath(p1.getName());
                score.addDeath(p2.getName());
            }
            p1.respawn();
            p2.respawn();
            return true;
        }
        return false;
    }

    public void checkAllPlayersCollision(List<Player> players, ScoreController score) {
        for (int i = 0; i < players.size(); i++) {
            for (int j = i + 1; j < players.size(); j++) {
                checkPlayerCollision(players.get(i), players.get(j), score);
            }
        }
    }
}
