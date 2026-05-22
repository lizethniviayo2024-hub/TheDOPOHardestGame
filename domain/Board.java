package domain;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase board - Guarda el estado activo (GameState). - Corre update() cada
 * tick. - Mueve jugadores y valida límites + paredes. - Expone datos visuales a
 * la capa de presentación.
 */
public class Board {

	private final int cols;
	private final int rows;
	private final ScoreController scoreController;
	private final GameModeConfigurator configurator;
	private GameState state;
	private CollisionController collisionController;

	// CONSTRUCCIÓN
	/**
	 * Constructor principal.
	 */
	public Board(int cols, int rows, ScoreController scoreController, GameModeConfigurator configurator) {

		this.cols = cols;
		this.rows = rows;
		this.scoreController = scoreController;
		this.configurator = configurator;
		this.state = new GameState();
	}

	/**
	 * Constructor auxiliar
	 */
	public Board(int cols, int rows) {
		this(cols, rows, new ScoreController(), new GameModeConfigurator(cols, rows));
	}

	// CARGA DE NIVELES

	public void loadFromFile(File file) throws HardestGameException {
		applyState(configurator.configureSingle(file, "RED", null));
	}

	public void loadFromFile(File file, String playerType) throws HardestGameException {
		applyState(configurator.configureSingle(file, playerType, null));
	}

	public void loadFromFile(File file, String playerType, Color borderColor) throws HardestGameException {
		applyState(configurator.configureSingle(file, playerType, borderColor));
	}

	public void loadFromConfig(LevelConfig config) {
		applyState(configurator.configureSingle(config, "RED", null));
	}

	public void loadFromConfig(LevelConfig config, String playerType) {
		applyState(configurator.configureSingle(config, playerType, null));
	}

	// PvP

	public void loadPvPMode(File file, String p1Name, String p1Type, Color p1Border, String p2Name, String p2Type,
			Color p2Border) throws HardestGameException {
		applyState(configurator.configurePvP(file, p1Name, p1Type, p1Border, p2Name, p2Type, p2Border));
	}

	public void loadPvPMode(File file, Color p1Color, Color p2Color) throws HardestGameException {
		applyState(configurator.configurePvP(file, "Player 1", "RED", p1Color, "Player 2", "BLUE", p2Color));
	}

	// PvM Random

	public void loadPvMAIRandomMode(File file, String playerType, Color borderColor) throws HardestGameException {
		applyState(configurator.configurePvAIRandom(file, playerType, borderColor));
	}

	public void loadPvMAIRandomMode(File file, Color borderColor) throws HardestGameException {
		applyState(configurator.configurePvAIRandom(file, "RED", borderColor));
	}

	// PvM Expert

	public void loadPvMAIExpertMode(File file, String playerType, Color borderColor) throws HardestGameException {
		applyState(configurator.configurePvAIExpert(file, playerType, borderColor));
	}

	public void loadPvMAIExpertMode(File file, Color borderColor) throws HardestGameException {
		applyState(configurator.configurePvAIExpert(file, "RED", borderColor));
	}

	/**
	 * Reemplaza el estado activo y reconstruye el CollisionController.
	 */
	private void applyState(GameState newState) {
		this.state = newState;
		this.scoreController.reset();
		this.collisionController = new CollisionController(state.getCollidables());
	}

	// LOOP DE JUEGO

	public void update() {
		if (state.levelCompleted || state.players.isEmpty()) {
			return;
		}
		for (Enemy enemy : state.enemies) {// Mover enemigos
			enemy.update();
		}
		for (int i = 1; i < state.players.size(); i++) { // Actualizar jugadores no-humanos
			Player p = state.players.get(i);
			// Si es una IA con BFS, actualizar el estado del mundo antes de que decida
			if (p instanceof AIControlledPlayer) {
				AIStrategy strategy = ((AIControlledPlayer) p).getStrategy();
				if (strategy instanceof BFSAIStrategy) {
					BFSAIStrategy bfs = (BFSAIStrategy) strategy;
					bfs.setCoinPositions(buildCoinPositions());
					bfs.setWallRects(buildWallRects());
				}
			}
			p.update();
			if (p instanceof AIControlledPlayer) { // Mover la IA según la dirección que calculó su estrategia
				String dir = ((AIControlledPlayer) p).getCurrentDirection();
				movePlayer(i, dir);
			}
		}
		if ("PVSP".equals(state.gameMode)) {
			for (Player player : state.players) {
				collisionController.checkCollisions(player, state.collectibles, state.getSafeZones(), scoreController,
						state.gameMode);

				if (!allCoinsCollected())
					continue; // ← nadie puede ganar sin todas las monedas

				if (collisionController.checkFinalZoneReachedForPlayer(player, state.getSafeZones(),
						state.collectibles)) {
					String pname = player.getName();
					if (!state.playersReachedFinal.contains(pname)) {
						state.playersReachedFinal.add(pname);

						if (state.playersReachedFinal.size() == 1) {
							// Primero en llegar con todas las monedas → gana
							state.levelCompleted = true;

						} else {
							// Ambos llegaron en el mismo tick → desempate por monedas/muertes
							String p1 = state.playersReachedFinal.get(0);
							String p2 = state.playersReachedFinal.get(1);
							int p1Coins = scoreController.getPlayerCoins(p1);
							int p2Coins = scoreController.getPlayerCoins(p2);
							String winner;
							if (p1Coins > p2Coins) {
								winner = p1;
							} else if (p2Coins > p1Coins) {
								winner = p2;
							} else {
								int p1Deaths = scoreController.getPlayerDeaths(p1);
								int p2Deaths = scoreController.getPlayerDeaths(p2);
								winner = (p1Deaths <= p2Deaths) ? p1 : p2;
							}
							state.playersReachedFinal.clear();
							state.playersReachedFinal.add(winner);
							state.levelCompleted = true;
						}
					}
				}
			}
		} else {
			for (Player player : state.players) { // player
				state.levelCompleted = collisionController.checkCollisions(player, state.collectibles,
						state.getSafeZones(), scoreController, state.gameMode);
				if (state.levelCompleted)
					break;
			}
		}
		if (state.players.size() > 1) {// Colisiones entre jugadores
			collisionController.checkAllPlayersCollision(state.players, scoreController, state.getSafeZones());
		}

		// Bombas que matan enemigos
		for (Collectible c : state.collectibles) {
			if (!(c instanceof Bomb) || c.isActive())
				continue;
			Bomb bomb = (Bomb) c;
			Enemy target = bomb.getTriggeringEnemy();
			if (target != null) {
				state.enemies.remove(target);
				// También quitarlo de collidables para que no siga procesándose
				state.collidables.remove(target);
				bomb.getTriggeringEnemy(); // already consumed
			}
		}

		// Que las bombas activas detecten colisión con enemigos
		for (Collectible c : state.collectibles) {
			if (!(c instanceof Bomb) || !c.isActive())
				continue;
			Bomb bomb = (Bomb) c;
			for (Enemy enemy : new java.util.ArrayList<>(state.enemies)) {
				if (bomb.getBounds().intersects(enemy.getBounds())) {
					bomb.onEnemyCollision(enemy);
					break;
				}
			}
		}
	}

	private boolean allCoinsCollected() {
		for (Collectible c : state.collectibles) {
			if (c.isRequiredForCompletion() && c.isActive())
				return false;
		}
		return true;
	}

	public void loadFromFile(File file, String playerName, String playerType, Color borderColor)
			throws HardestGameException {
		applyState(configurator.configureSingle(file, playerName, playerType, borderColor));
	}

	// MOVIMIENTO

	public void movePlayer(String direction) {
		movePlayer(0, direction);
	}

	public void movePlayer(int playerIndex, String direction) {

		if (playerIndex >= state.players.size())
			return;
		if (state.levelCompleted)
			return;
		if (direction == null || direction.equals("IDLE"))
			return;

		Player player = state.players.get(playerIndex);

		int newX = player.getX();
		int newY = player.getY();
		int speed = player.getSpeed();
		int size = player.getWidth();

		switch (direction) {
		case "UP":
			newY -= speed;
			break;
		case "DOWN":
			newY += speed;
			break;
		case "LEFT":
			newX -= speed;
			break;
		case "RIGHT":
			newX += speed;
			break;
		case "UP_LEFT":
			newX -= speed;
			newY -= speed;
			break;
		case "UP_RIGHT":
			newX += speed;
			newY -= speed;
			break;
		case "DOWN_LEFT":
			newX -= speed;
			newY += speed;
			break;
		case "DOWN_RIGHT":
			newX += speed;
			newY += speed;
			break;
		}

		newX = Math.max(0, Math.min(newX, cols - size));
		newY = Math.max(0, Math.min(newY, rows - size));

		if (!collisionController.collidesWithWall(newX, newY, size, size, state.getWalls())) {
			player.setX(newX);
			player.setY(newY);
		}
	}

	// TIEMPO

	public boolean tickTime() {
		if (state.timeRemaining > 0) {
			state.timeRemaining--;
		}
		return state.timeRemaining <= 0;
	}

	// RESET

	/**
	 * Reinicia el nivel por timeout. Suma una muerte a cada jugador humano (no a
	 * las IAs) y los devuelve a su punto de reaparición
	 */
	public void resetLevel() {
		state.levelCompleted = false;
		state.timeRemaining = state.timeLimit;
		for (Player p : state.players) {
			if (!(p instanceof AIControlledPlayer)) {
				scoreController.addDeath(p.getName());
			}
			p.respawn();
		}
	}

	public void timeoutReset() {
		// Resetear monedas
		for (Collectible c : state.collectibles) {
			c.reset(); // reactivar
		}
		// Resetear tiempo y sumar muerte (lo que ya hace resetLevel)
		resetLevel();
	}
	// HELPERS INTERNOS

	private java.util.List<int[]> buildCoinPositions() {
		java.util.List<int[]> list = new ArrayList<>();
		for (Collectible c : state.collectibles) {
			if (c.isRequiredForCompletion() && c.isActive()) {
				list.add(new int[] { c.getX(), c.getY() });
			}
		}
		return list;
	}

	private java.util.List<int[]> buildWallRects() {
		java.util.List<int[]> list = new ArrayList<>();
		for (Walls w : state.getWalls()) {
			list.add(new int[] { w.getX(), w.getY(), w.getWidth(), w.getHeight() });
		}
		return list;
	}

	// DATOS VISUALES (para presentación)

	public List<int[]> getSafeZoneRects() {
		List<int[]> rects = new ArrayList<>();
		for (SafeZone z : state.getSafeZones()) {
			rects.add(new int[] { z.getX(), z.getY(), z.getWidth(), z.getHeight() });
		}
		return rects;
	}

	public List<int[]> getWallRects() {
		List<int[]> rects = new ArrayList<>();
		for (Walls w : state.getWalls()) {
			rects.add(new int[] { w.getX(), w.getY(), w.getWidth(), w.getHeight() });
		}
		return rects;
	}

	/**
	 * Retorna los datos visuales de todos los {@link Collectible} activos. Cada
	 * entrada es: [x, y, width, height, r, g, b, shape] donde shape = 0 (óvalo) o 1
	 * (rectángulo).
	 *
	 * <p>
	 * GamePanel usa este único getter para pintar monedas, bombas, fuentes de vida
	 * y cualquier coleccionable futuro — sin conocer los tipos concretos.
	 */
	public List<int[]> getCollectibleRects() {
		List<int[]> rects = new ArrayList<>();
		for (Collectible c : state.collectibles) {
			if (c.isActive()) {
				Color col = c.getColor();
				int shape = (c.getShape() == Collectible.Shape.RECT) ? 1 : 0;
				rects.add(new int[] { c.getX(), c.getY(), c.getWidth(), c.getHeight(), col.getRed(), col.getGreen(),
						col.getBlue(), shape });
			}
		}
		return rects;
	}

	public List<int[]> getEnemyRects() {
		List<int[]> rects = new ArrayList<>();
		for (Enemy e : state.enemies) {
			rects.add(new int[] { e.getX(), e.getY(), e.getWidth(), e.getHeight() });
		}
		return rects;
	}

	public List<int[]> getPlayerRects() {
		List<int[]> rects = new ArrayList<>();
		for (Player p : state.players) {
			Color c = p.getPlayerColor();
			Color b = p.getBorderColor();
			rects.add(new int[] { p.getX(), p.getY(), p.getWidth(), p.getHeight(), c.getRed(), c.getGreen(),
					c.getBlue(), p.isShieldActive() ? 1 : 0, b.getRed(), b.getGreen(), b.getBlue() });
		}
		return rects;
	}

	// GETTERS

	public Player getPlayer() {
		return state.players.isEmpty() ? null : state.players.get(0);
	}

	public Player getPlayer(int index) {
		return index < state.players.size() ? state.players.get(index) : null;
	}

	public List<Player> getPlayers() {
		return state.players;
	}

	public int getNumPlayers() {
		return state.players.size();
	}

	public String getGameMode() {
		return state.gameMode;
	}

	public boolean isLevelCompleted() {
		return state.levelCompleted;
	}

	public int getTimeRemaining() {
		return state.timeRemaining;
	}

	public int getCurrentLevel() {
		return state.currentLevel;
	}

	public int getTotalDeaths() {
		return scoreController.getTotalDeaths();
	}

	public int getCoinsCollected() {
		int count = 0;
		for (Collectible c : state.collectibles) {
			if (c.isRequiredForCompletion() && !c.isActive())
				count++;
		}
		return count;
	}

	public int getTotalCoins() {
		int count = 0;
		for (Collectible c : state.collectibles) {
			if (c.isRequiredForCompletion())
				count++;
		}
		return count;
	}

	public List<Collectible> getCollectibles() {
		return state.collectibles;
	}

	public int getPlayerDeaths(String playerName) {
		return scoreController.getPlayerDeaths(playerName);
	}

	public int getPlayerCoins(String playerName) {
		return scoreController.getPlayerCoins(playerName);
	}

	/**
	 * Determina el ganador en modo PvP. Criterio principal: quien llegó primero a
	 * su zona final Desempate: quien tiene menos muertes. Retorna el nombre del
	 * ganador, o null si nadie ha llegado aún.
	 */
	public String getPvPWinner() {
		if (state.playersReachedFinal.isEmpty()) {
			return null;
		}

		// El primero en llegar gana directamente
		if (state.playersReachedFinal.size() == 1) {
			return state.playersReachedFinal.get(0);
		}

		// Llegaron en el mismo tick: desempate por menos muertes
		String player1 = state.playersReachedFinal.get(0);
		String player2 = state.playersReachedFinal.get(1);

		int p1Deaths = scoreController.getPlayerDeaths(player1);
		int p2Deaths = scoreController.getPlayerDeaths(player2);

		if (p1Deaths <= p2Deaths) {
			return player1;
		} else {
			return player2;
		}
	}

	public ScoreController getScoreController() {
		return scoreController;
	}

	public int getCols() {
		return cols;
	}

	public int getRows() {
		return rows;
	}

	public List<Enemy> getEnemies() {
		return state.enemies;
	}

	public List<SafeZone> getSafeZones() {
		return state.getSafeZones();
	}

	public List<Walls> getWalls() {
		return state.getWalls();
	}

	public void setTimeRemaining(int time) {

		state.timeRemaining = time;
	}

	public void setPlayerDeaths(String playerName, int deaths) {

		scoreController.setPlayerDeaths(playerName, deaths);
	}

	public void setPlayerCoins(String playerName, int coins) {

		scoreController.setPlayerCoins(playerName, coins);
	}
}