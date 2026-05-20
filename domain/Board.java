package domain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador central del juego.
 * Coordina jugadores, enemigos, monedas, zonas, colisiones y puntaje.
 */
public class Board {

    private int rows;
    private int cols;

    private List<Player>     players;
    private List<Enemy>      enemies;
    private List<Coin>       coins;
    private List<SafeZone>   safeZones;
    private List<Walls>      walls;
    private List<Collidable> collidables;

    private CollisionController collisionController;
    private ScoreController     scoreController;

    private boolean levelCompleted;
    private int currentLevel;
    private int timeLimit;
    private int timeRemaining;

    public Board(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        this.players     = new ArrayList<>();
        this.enemies     = new ArrayList<>();
        this.coins       = new ArrayList<>();
        this.safeZones   = new ArrayList<>();
        this.walls       = new ArrayList<>();
        this.collidables = new ArrayList<>();
        this.scoreController = new ScoreController();
        this.levelCompleted  = false;
    }

    /**
     * Carga un nivel desde un archivo .txt.
     * @throws HardestGameException si el archivo tiene errores.
     */
    public void loadFromFile(File file) throws HardestGameException {
        LevelConfig config = LevelLoader.load(file);
        loadFromConfig(config);
    }

    /**
     * Aplica un LevelConfig al tablero.
     * Separado para poder testear sin archivo físico.
     */
    public void loadFromConfig(LevelConfig config) {
        currentLevel   = 1;
        timeLimit      = config.timeLimit;
        timeRemaining  = config.timeLimit;
        levelCompleted = false;
        scoreController.reset();

        players.clear();
        enemies.clear();
        coins.clear();
        safeZones.clear();
        walls.clear();
        collidables.clear();

        // Jugador
        Player player = new Player("Player 1",
                config.playerX, config.playerY,
                config.playerSize, config.playerSpeed);
        players.add(player);

        // Clasificar entidades por tipo
        for (GameEntity entity : config.entities) {
            if (entity instanceof Enemy)         enemies.add((Enemy) entity);
            else if (entity instanceof Coin)     coins.add((Coin) entity);
            else if (entity instanceof SafeZone) safeZones.add((SafeZone) entity);
            else if (entity instanceof Walls)    walls.add((Walls) entity);
        }

        collidables.addAll(enemies);
        collidables.addAll(coins);
        collidables.addAll(safeZones);

        collisionController = new CollisionController(collidables);
    }

    /** Actualiza el estado del juego en cada tick. */
    public void update() {
        if (levelCompleted || players.isEmpty()) return;

        for (Enemy enemy : enemies) enemy.update();

        Player player = players.get(0);
        levelCompleted = collisionController.checkCollisions(
            player, coins, safeZones, scoreController
        );
    }

    /** Mueve al jugador en la dirección indicada si no choca con pared. */
    public void movePlayer(String direction) {
        if (players.isEmpty() || levelCompleted) return;
        Player player = players.get(0);

        int newX = player.getX();
        int newY = player.getY();
        int spd  = player.getSpeed();
        int size = player.getWidth();

        switch (direction) {
            case "UP":         newY -= spd; break;
            case "DOWN":       newY += spd; break;
            case "LEFT":       newX -= spd; break;
            case "RIGHT":      newX += spd; break;
            case "UP_LEFT":    newX -= spd; newY -= spd; break;
            case "UP_RIGHT":   newX += spd; newY -= spd; break;
            case "DOWN_LEFT":  newX -= spd; newY += spd; break;
            case "DOWN_RIGHT": newX += spd; newY += spd; break;
            default: break;
        }

        newX = Math.max(0, Math.min(newX, cols - size));
        newY = Math.max(0, Math.min(newY, rows - size));

        if (!collisionController.collidesWithWall(newX, newY, size, size, walls)) {
            player.setX(newX);
            player.setY(newY);
        }
    }

    /** Descuenta un segundo. Retorna true si el tiempo se agotó. */
    public boolean tickTime() {
        if (timeRemaining > 0) timeRemaining--;
        return timeRemaining <= 0;
    }

    /**
     * Retorna info de todas las entidades para que GamePanel las pinte.
     * GamePanel no necesita importar ninguna clase de dominio excepto Board.
     */
    public List<int[]> getSafeZoneRects() {
        List<int[]> rects = new ArrayList<>();
        for (SafeZone z : safeZones)
            rects.add(new int[]{z.getX(), z.getY(), z.getWidth(), z.getHeight()});
        return rects;
    }

    public List<int[]> getWallRects() {
        List<int[]> rects = new ArrayList<>();
        for (Walls w : walls)
            rects.add(new int[]{w.getX(), w.getY(), w.getWidth(), w.getHeight()});
        return rects;
    }

    public List<int[]> getCoinRects() {
        List<int[]> rects = new ArrayList<>();
        for (Coin c : coins)
            if (!c.isCollected())
                rects.add(new int[]{c.getX(), c.getY(), c.getWidth(), c.getHeight()});
        return rects;
    }

    public List<int[]> getEnemyRects() {
        List<int[]> rects = new ArrayList<>();
        for (Enemy e : enemies)
            rects.add(new int[]{e.getX(), e.getY(), e.getWidth(), e.getHeight()});
        return rects;
    }

    /** Retorna {x, y, w, h} del jugador, o null si no hay jugador. */
    public int[] getPlayerRect() {
        Player p = getPlayer();
        if (p == null) return null;
        return new int[]{p.getX(), p.getY(), p.getWidth(), p.getHeight()};
    }

    public Player getPlayer()            { return players.isEmpty() ? null : players.get(0); }

    public List<Enemy> getEnemies()      { return enemies; }

    public List<Coin> getCoins()         { return coins; }

    public List<SafeZone> getSafeZones() { return safeZones; }

    public List<Walls> getWalls()        { return walls; }

    public boolean isLevelCompleted()    { return levelCompleted; }

    public int getTimeRemaining()        { return timeRemaining; }

    public int getTotalDeaths()          { return scoreController.getTotalDeaths(); }

    public int getCoinsCollected()       { return scoreController.getTotalCoins(); }

    public int getTotalCoins()           { return coins.size(); }

    public int getCurrentLevel()         { return currentLevel; }

    public int getCols()                 { return cols; }

    public int getRows()                 { return rows; }
}