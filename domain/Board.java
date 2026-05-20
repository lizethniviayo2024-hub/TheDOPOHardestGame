package domain;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Orquesta el loop de juego: movimiento, colisiones y tiempo.
 *
 * Lo que Board ya NO hace (vs la versión anterior):
 * - No sabe cómo armar modos (eso es GameModeConfigurator).
 * - No instancia CollisionController ni ScoreController internamente;
 *   los recibe inyectados o los crea una sola vez al construirse.
 * - No instancia AIPlayer ni AIPlayerExpert directamente.
 *
 * Lo que Board sí hace:
 * - Guarda el estado activo (GameState).
 * - Corre update() cada tick.
 * - Mueve jugadores y valida límites + paredes.
 * - Expone datos visuales a la capa de presentación.
 */
public class Board {

    private final int cols;
    private final int rows;

    // Inyectados: fáciles de mockear en tests
    private final ScoreController       scoreController;
    private final GameModeConfigurator  configurator;

    // Estado activo de la partida
    private GameState state;

    // CollisionController se reconstruye cuando llega un GameState nuevo
    private CollisionController collisionController;

    // ─────────────────────────────────────────────────────────────
    // CONSTRUCCIÓN
    // ─────────────────────────────────────────────────────────────

    /**
     * Constructor principal. Recibe dependencias desde afuera.
     * Permite tests: pasá un ScoreController o configurador mockeado.
     */
    public Board(int cols, int rows,
    ScoreController scoreController,
    GameModeConfigurator configurator) {

        this.cols            = cols;
        this.rows            = rows;
        this.scoreController = scoreController;
        this.configurator    = configurator;
        this.state           = new GameState();
    }

    /**
     * Constructor de conveniencia para uso normal (sin inyección manual).
     */
    public Board(int cols, int rows) {
        this(cols, rows,
            new ScoreController(),
            new GameModeConfigurator(cols, rows));
    }

    // ─────────────────────────────────────────────────────────────
    // CARGA DE NIVELES — delega completamente al configurador
    // ─────────────────────────────────────────────────────────────

    public void loadFromFile(File file) throws HardestGameException {
        applyState(configurator.configureSingle(file, "RED", null));
    }

    public void loadFromFile(File file,
    String playerType) throws HardestGameException {
        applyState(configurator.configureSingle(file, playerType, null));
    }

    public void loadFromFile(File file,
    String playerType,
    Color borderColor) throws HardestGameException {
        applyState(configurator.configureSingle(file, playerType, borderColor));
    }

    public void loadFromConfig(LevelConfig config) {
        applyState(configurator.configureSingle(config, "RED", null));
    }

    public void loadFromConfig(LevelConfig config, String playerType) {
        applyState(configurator.configureSingle(config, playerType, null));
    }

    // ── PvP ──────────────────────────────────────────────────────

    public void loadPvPMode(File file,
    String p1Name, String p1Type, Color p1Border,
    String p2Name, String p2Type, Color p2Border)
    throws HardestGameException {
        applyState(configurator.configurePvP(file, p1Name, p1Type, p1Border, p2Name, p2Type, p2Border));
    }

    public void loadPvPMode(File file,
    Color p1Color,
    Color p2Color) throws HardestGameException {
        applyState(configurator.configurePvP(file, "Player 1", "RED", p1Color, "Player 2", "BLUE", p2Color));
    }

    // ── PvM Random ───────────────────────────────────────────────

    public void loadPvMAIRandomMode(File file,
    String playerType,
    Color borderColor)
    throws HardestGameException {
        applyState(configurator.configurePvAIRandom(file, playerType, borderColor));
    }

    public void loadPvMAIRandomMode(File file,
    Color borderColor) throws HardestGameException {
        applyState(configurator.configurePvAIRandom(file, "RED", borderColor));
    }

    // ── PvM Expert ───────────────────────────────────────────────

    public void loadPvMAIExpertMode(File file,
    String playerType,
    Color borderColor)
    throws HardestGameException {
        applyState(configurator.configurePvAIExpert(file, playerType, borderColor));
    }

    public void loadPvMAIExpertMode(File file,
    Color borderColor) throws HardestGameException {
        applyState(configurator.configurePvAIExpert(file, "RED", borderColor));
    }

    // ─────────────────────────────────────────────────────────────
    // APLICAR ESTADO — único punto donde llega un GameState nuevo
    // ─────────────────────────────────────────────────────────────

    /**
     * Reemplaza el estado activo y reconstruye el CollisionController.
     * scoreController se resetea aquí, no en el configurador
     * (el score es responsabilidad del Board, no del modo de juego).
     */
    private void applyState(GameState newState) {
        this.state = newState;
        this.scoreController.reset();
        this.collisionController = new CollisionController(state.getCollidables());
    }

    // ─────────────────────────────────────────────────────────────
    // LOOP DE JUEGO
    // ─────────────────────────────────────────────────────────────

    public void update() {

        if (state.levelCompleted || state.players.isEmpty()) {
            return;
        }

        // Mover enemigos
        for (Enemy enemy : state.enemies) {
            enemy.update();
        }

        // Actualizar jugadores no-humanos (índice > 0: IA, P2, etc.)
        for (int i = 1; i < state.players.size(); i++) {
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

            // Mover la IA según la dirección que calculó su estrategia
            if (p instanceof AIControlledPlayer) {
                String dir = ((AIControlledPlayer) p).getCurrentDirection();
                movePlayer(i, dir);
            }
        }

        // Colisiones jugador vs entidades del nivel
        if ("PVSP".equals(state.gameMode)) {
            // En PvP, verificar si cada jugador llegó a la zona final
            for (Player player : state.players) {
                collisionController.checkCollisions(
                    player, state.coins, state.safeZones, scoreController, state.gameMode
                );
                // Registrar si el jugador llegó a la zona final
                if (collisionController.checkFinalZoneReached(player, state.safeZones, state.coins)) {
                    if (!state.playersReachedFinal.contains(player.getName())) {
                        state.playersReachedFinal.add(player.getName());
                    }
                }
            }
            // Completar nivel cuando ambos jugadores hayan llegado
            if (state.playersReachedFinal.size() == 2) {
                state.levelCompleted = true;
            }
        } else {
            // En single player, verificar normalmente
            for (Player player : state.players) {
                state.levelCompleted = collisionController.checkCollisions(
                    player, state.coins, state.safeZones, scoreController, state.gameMode
                );
                if (state.levelCompleted) break;
            }
        }

        // Colisiones entre jugadores (PvP / PvM)
        if (state.players.size() > 1) {
            collisionController.checkAllPlayersCollision(state.players);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // MOVIMIENTO
    // ─────────────────────────────────────────────────────────────

    public void movePlayer(String direction) {
        movePlayer(0, direction);
    }

    public void movePlayer(int playerIndex, String direction) {

        if (playerIndex >= state.players.size()) return;
        if (state.levelCompleted)               return;
        if (direction == null || direction.equals("IDLE")) return;

        Player player = state.players.get(playerIndex);

        int newX  = player.getX();
        int newY  = player.getY();
        int speed = player.getSpeed();
        int size  = player.getWidth();

        switch (direction) {
            case "UP":         newY -= speed;              break;
            case "DOWN":       newY += speed;              break;
            case "LEFT":       newX -= speed;              break;
            case "RIGHT":      newX += speed;              break;
            case "UP_LEFT":    newX -= speed; newY -= speed; break;
            case "UP_RIGHT":   newX += speed; newY -= speed; break;
            case "DOWN_LEFT":  newX -= speed; newY += speed; break;
            case "DOWN_RIGHT": newX += speed; newY += speed; break;
        }

        newX = Math.max(0, Math.min(newX, cols - size));
        newY = Math.max(0, Math.min(newY, rows - size));

        if (!collisionController.collidesWithWall(newX, newY, size, size, state.walls)) {
            player.setX(newX);
            player.setY(newY);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // TIEMPO
    // ─────────────────────────────────────────────────────────────

    public boolean tickTime() {
        if (state.timeRemaining > 0) {
            state.timeRemaining--;
        }
        return state.timeRemaining <= 0;
    }

    // ─────────────────────────────────────────────────────────────
    // RESET
    // ─────────────────────────────────────────────────────────────

    public void resetLevel() {
        scoreController.addDeath();
        state.levelCompleted  = false;
        state.timeRemaining   = state.timeLimit;

        // Regresar jugadores a spawn
        for (Player p : state.players) {
            p.die();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // HELPERS INTERNOS — datos del mundo para estrategias de IA
    // ─────────────────────────────────────────────────────────────

    private java.util.List<int[]> buildCoinPositions() {
        java.util.List<int[]> list = new ArrayList<>();
        for (Coin c : state.coins) {
            if (!c.isCollected()) {
                list.add(new int[]{ c.getX(), c.getY() });
            }
        }
        return list;
    }

    private java.util.List<int[]> buildWallRects() {
        java.util.List<int[]> list = new ArrayList<>();
        for (Walls w : state.walls) {
            list.add(new int[]{ w.getX(), w.getY(), w.getWidth(), w.getHeight() });
        }
        return list;
    }

    // ─────────────────────────────────────────────────────────────
    // DATOS VISUALES — la capa de presentación solo llama estos
    // ─────────────────────────────────────────────────────────────

    public List<int[]> getSafeZoneRects() {
        List<int[]> rects = new ArrayList<>();
        for (SafeZone z : state.safeZones) {
            rects.add(new int[]{ z.getX(), z.getY(), z.getWidth(), z.getHeight() });
        }
        return rects;
    }

    public List<int[]> getWallRects() {
        List<int[]> rects = new ArrayList<>();
        for (Walls w : state.walls) {
            rects.add(new int[]{ w.getX(), w.getY(), w.getWidth(), w.getHeight() });
        }
        return rects;
    }

    public List<int[]> getCoinRects() {
        List<int[]> rects = new ArrayList<>();
        for (Coin c : state.coins) {
            if (!c.isCollected()) {
                rects.add(new int[]{ c.getX(), c.getY(), c.getWidth(), c.getHeight() });
            }
        }
        return rects;
    }

    public List<int[]> getEnemyRects() {
        List<int[]> rects = new ArrayList<>();
        for (Enemy e : state.enemies) {
            rects.add(new int[]{ e.getX(), e.getY(), e.getWidth(), e.getHeight() });
        }
        return rects;
    }

    public List<int[]> getPlayerRects() {
        List<int[]> rects = new ArrayList<>();
        for (Player p : state.players) {
            Color c = p.getPlayerColor();
            Color b = p.getBorderColor();
            rects.add(new int[]{
                    p.getX(), p.getY(), p.getWidth(), p.getHeight(),
                    c.getRed(), c.getGreen(), c.getBlue(),
                    p.isShieldActive() ? 1 : 0,
                    b.getRed(), b.getGreen(), b.getBlue()
                });
        }
        return rects;
    }

    // ─────────────────────────────────────────────────────────────
    // GETTERS
    // ─────────────────────────────────────────────────────────────

    public Player getPlayer()            { return state.players.isEmpty() ? null : state.players.get(0); }

    public Player getPlayer(int index)   { return index < state.players.size() ? state.players.get(index) : null; }

    public List<Player>   getPlayers()   { return state.players; }

    public int            getNumPlayers(){ return state.players.size(); }

    public String  getGameMode()         { return state.gameMode; }

    public boolean isLevelCompleted()    { return state.levelCompleted; }

    public int     getTimeRemaining()    { return state.timeRemaining; }

    public int     getCurrentLevel()     { return state.currentLevel; }

    public int getTotalDeaths()          { return scoreController.getTotalDeaths(); }


    public int getCoinsCollected() {
        return scoreController.getTotalCoins();
    }

    public int getPlayerDeaths(String playerName) {
        return scoreController.getPlayerDeaths(playerName);
    }

    public int getPlayerCoins(String playerName) {
        return scoreController.getPlayerCoins(playerName);
    }

    /**
     * Determina el ganador en modo PvP.
     * Criterios:
     * 1. Quien llegó primero (orden en playersReachedFinal)
     * 2. Si ambos llegaron al mismo tiempo, quien tiene más monedas
     * 3. Si tienen las mismas monedas, quien tiene menos muertes
     * Retorna el nombre del ganador, o null si no hay ganador aún.
     */
    public String getPvPWinner() {
        if (state.playersReachedFinal.size() < 2) {
            return null;
        }

        String player1 = state.playersReachedFinal.get(0);
        String player2 = state.playersReachedFinal.get(1);

        int p1Coins = scoreController.getPlayerCoins(player1);
        int p2Coins = scoreController.getPlayerCoins(player2);

        // Si uno tiene más monedas, es el ganador
        if (p1Coins > p2Coins) {
            return player1;
        } else if (p2Coins > p1Coins) {
            return player2;
        }

        // Si tienen las mismas monedas, el que tiene menos muertes gana
        int p1Deaths = scoreController.getPlayerDeaths(player1);
        int p2Deaths = scoreController.getPlayerDeaths(player2);

        if (p1Deaths < p2Deaths) {
            return player1;
        } else if (p2Deaths < p1Deaths) {
            return player2;
        }

        // Si todo es igual, retornar el que llegó primero
        return player1;
    }

    public int getTotalCoins()           { return state.coins.size(); }

    public ScoreController getScoreController() { return scoreController; }

    public int getCols()                 { return cols; }

    public int getRows()                 { return rows; }

    public List<Enemy>   getEnemies()    { return state.enemies; }

    public List<Coin>    getCoins()      { return state.coins; }

    public List<SafeZone> getSafeZones() { return state.safeZones; }

    public List<Walls>   getWalls()      { return state.walls; }
}