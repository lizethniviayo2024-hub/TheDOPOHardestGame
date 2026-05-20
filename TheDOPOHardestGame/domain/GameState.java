package domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Contiene el estado de una partida en curso.
 */
public class GameState {

    //  Entidades
    final List<Player>    players    = new ArrayList<>();
    final List<Enemy>     enemies    = new ArrayList<>();
    final List<Coin>      coins      = new ArrayList<>();
    final List<SafeZone>  safeZones  = new ArrayList<>();
    final List<Walls>     walls      = new ArrayList<>();
    final List<Collidable> collidables = new ArrayList<>();

    //  Tiempo
    int timeLimit;
    int timeRemaining;

    //  Control
    boolean levelCompleted = false;
    int     currentLevel   = 1;
    String  gameMode       = "SINGLE";
    final List<String> playersReachedFinal = new ArrayList<>();  // Nombres de jugadores que llegaron a la zona final

    /**
     * Limpia todas las listas y reinicia flags.
     */
    void clear() {
        players.clear();
        enemies.clear();
        coins.clear();
        safeZones.clear();
        walls.clear();
        collidables.clear();
        levelCompleted = false;
        currentLevel   = 1;
        playersReachedFinal.clear();
    }

    /**
     * Distribuye las entidades del LevelConfig en sus listas correspondientes
     * y construye la lista de collidables.
     */
    void populateFromConfig(LevelConfig config) {
        clear();

        timeLimit     = config.timeLimit;
        timeRemaining = config.timeLimit;

        for (GameEntity entity : config.entities) {
            if (entity instanceof Enemy) {
                enemies.add((Enemy) entity);

            } else if (entity instanceof Coin) {
                coins.add((Coin) entity);

            } else if (entity instanceof SafeZone) {
                safeZones.add((SafeZone) entity);

            } else if (entity instanceof Walls) {
                walls.add((Walls) entity);
            }
        }

        collidables.addAll(enemies);
        collidables.addAll(coins);
        collidables.addAll(safeZones);
    }

    //  Acceso de solo lectura para Board y la vista

    public List<Player>    getPlayers()     { return players; }
    public List<Enemy>     getEnemies()     { return enemies; }
    public List<Coin>      getCoins()       { return coins; }
    public List<SafeZone>  getSafeZones()   { return safeZones; }
    public List<Walls>     getWalls()       { return walls; }
    public List<Collidable> getCollidables(){ return collidables; }

    public int     getTimeRemaining()  { return timeRemaining; }
    public int     getTimeLimit()      { return timeLimit; }
    public boolean isLevelCompleted()  { return levelCompleted; }
    public int     getCurrentLevel()   { return currentLevel; }
    public String  getGameMode()       { return gameMode; }
}