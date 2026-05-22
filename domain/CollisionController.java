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

	public boolean checkCollisions(Player player, List<Collectible> collectibles, List<SafeZone> safeZones,
			ScoreController score, String gameMode) {
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
			if (zone.isFinal() && player.getBounds().intersects(zone.getBounds())
					&& allRequiredCollected(collectibles)) {
				return true;
			}
		}

		return false;
	}

	public boolean checkFinalZoneReachedForPlayer(Player player, List<SafeZone> safeZones,
			List<Collectible> collectibles) {
		for (SafeZone zone : safeZones) {
			if (!zone.isFinal())
				continue;
			if (zone.getOwnerName() != null && !zone.getOwnerName().equals(player.getName())) {
				continue;
			}
			if (player.getBounds().intersects(zone.getBounds())) {
				return true;
			}
		}
		return false;
	}

	private boolean allRequiredCollected(List<Collectible> collectibles) {
		for (Collectible c : collectibles) {
			if (c.isRequiredForCompletion() && c.isActive())
				return false;
		}
		return true;
	}

	public boolean collidesWithWall(int x, int y, int w, int h, List<Walls> walls) {
		java.awt.Rectangle rect = new java.awt.Rectangle(x, y, w, h);
		for (Walls wall : walls) {
			if (rect.intersects(wall.getBounds()))
				return true;
		}
		return false;
	}

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

	public void checkAllPlayersCollision(List<Player> players, ScoreController score, List<SafeZone> safeZones) {
		for (int i = 0; i < players.size(); i++) {
			for (int j = i + 1; j < players.size(); j++) {
				Player p1 = players.get(i);
				Player p2 = players.get(j);
				if (isInSafeZone(p1, safeZones) || isInSafeZone(p2, safeZones))
					continue;
				checkPlayerCollision(p1, p2, score);
			}
		}
	}

	private boolean isInSafeZone(Player player, List<SafeZone> safeZones) {
		for (SafeZone zone : safeZones) {
			if (!zone.isFinal() && player.getBounds().intersects(zone.getBounds())) {
				return true;
			}
		}
		return false;
	}
}