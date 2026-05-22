package domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Contiene el estado de una partida en curso.
 *
 * <p>Todos los elementos interactivos del tablero (monedas, bombas,
 * fuentes de vida, etc.) se almacenan en una sola lista de
 * {@link Collectible}. Para agregar un tipo nuevo basta con crear
 * una subclase de {@code Collectible} — no hay que tocar esta clase.
 */
public class GameState {

    // Entidades
    final List<Player>      players     = new ArrayList<>();
    final List<Enemy>       enemies     = new ArrayList<>();
    final List<Collectible> collectibles = new ArrayList<>();   // coins + lifeSources + bombs + futuros
    final List<SafeZone>    safeZones   = new ArrayList<>();
    final List<Walls>       walls       = new ArrayList<>();
    final List<Collidable>  collidables = new ArrayList<>();

    // Tiempo
    int timeLimit;
    int timeRemaining;

    // Control
    boolean levelCompleted = false;
    int     currentLevel   = 1;
    String  gameMode       = "SINGLE";
    final List<String> playersReachedFinal = new ArrayList<>();

    /**
     * Limpia todas las listas y reinicia flags.
     */
    void clear() {
        players.clear();
        enemies.clear();
        collectibles.clear();
        safeZones.clear();
        walls.clear();
        collidables.clear();
        levelCompleted = false;
        currentLevel   = 1;
        playersReachedFinal.clear();
    }

    /**
     * Distribuye las entidades del LevelConfig en sus listas y construye
     * la lista de collidables.
     *
     * <p>Gracias a la jerarquía unificada, cualquier {@link Collectible}
     * nuevo se clasifica aquí automáticamente — no se necesita añadir
     * un {@code instanceof} adicional.
     */
    void populateFromConfig(LevelConfig config) {
        clear();

        timeLimit     = config.timeLimit;
        timeRemaining = config.timeLimit;

        for (GameEntity entity : config.entities) {
            if (entity instanceof Enemy) {
                enemies.add((Enemy) entity);
            } else if (entity instanceof Collectible) {
                collectibles.add((Collectible) entity);
            } else if (entity instanceof SafeZone) {
                safeZones.add((SafeZone) entity);
            } else if (entity instanceof Walls) {
                walls.add((Walls) entity);
            }
        }

        collidables.addAll(enemies);
        collidables.addAll(collectibles);
        collidables.addAll(safeZones);
    }

    // Acceso de solo lectura para Board y la vista

    public List<Player>      getPlayers()      { return players; }
    public List<Enemy>       getEnemies()       { return enemies; }
    public List<Collectible> getCollectibles()  { return collectibles; }
    public List<SafeZone>    getSafeZones()     { return safeZones; }
    public List<Walls>       getWalls()         { return walls; }
    public List<Collidable>  getCollidables()   { return collidables; }

    public int     getTimeRemaining()  { return timeRemaining; }
    public int     getTimeLimit()      { return timeLimit; }
    public boolean isLevelCompleted()  { return levelCompleted; }
    public int     getCurrentLevel()   { return currentLevel; }
    public String  getGameMode()       { return gameMode; }
}
