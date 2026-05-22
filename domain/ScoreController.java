package domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador de puntaje
 * Calcula puntaje individual por jugador y puntaje total de partida.
 */
public class ScoreController {
    private Map<String, Integer> playerDeaths; // Muertes por jugador
    private Map<String, Integer> playerCoins;  // Monedas por jugador
    private Map<String, Integer> playerLives;  // Vidas extra por jugador (LifeSource)
    private int totalDeaths;
    private int totalCoins;

    public ScoreController() {
        this.playerDeaths = new HashMap<>();
        this.playerCoins  = new HashMap<>();
        this.playerLives  = new HashMap<>();
        this.totalDeaths  = 0;
        this.totalCoins   = 0;
    }

    /**
     * Registra una muerte para un jugador específico.
     */
    public void addDeath(String playerName) {
        playerDeaths.put(playerName, playerDeaths.getOrDefault(playerName, 0) + 1);
        totalDeaths++;
    }

    /**
     * Registra una moneda para un jugador específico.
     */
    public void addCoin(String playerName) {
        playerCoins.put(playerName, playerCoins.getOrDefault(playerName, 0) + 1);
        totalCoins++;
    }

    public void addDeath() {
        totalDeaths++;
    }

    public void addCoin() {
        totalCoins++;
    }

    /**
     * Calcula el puntaje final de un jugador
     */
    public int calculatePlayerScore(String playerName) {
        int coins = playerCoins.getOrDefault(playerName, 0);
        int deaths = playerDeaths.getOrDefault(playerName, 0);
        return (coins * 10) - (deaths * 5);
    }

    // Getters
    public int getTotalDeaths() { return totalDeaths; }

    public int getTotalCoins() { return totalCoins; }

    public int getPlayerDeaths(String playerName) {
        return playerDeaths.getOrDefault(playerName, 0);
    }

    public int getPlayerCoins(String playerName) {
        return playerCoins.getOrDefault(playerName, 0);
    }

    /**
     * Registra una vida extra obtenida por el jugador (LifeSource).
     */
    public void addLife(String playerName) {
        playerLives.put(playerName, playerLives.getOrDefault(playerName, 0) + 1);
    }

    public int getPlayerLives(String playerName) {
        return playerLives.getOrDefault(playerName, 0);
    }

    public void reset() {
        playerDeaths.clear();
        playerCoins.clear();
        playerLives.clear();
        totalDeaths = 0;
        totalCoins  = 0;
    }

    public void setPlayerDeaths(String playerName, int deaths) {

        playerDeaths.put(playerName, deaths);
    }

    public void setPlayerCoins(String playerName, int coins) {

        playerCoins.put(playerName, coins);
    }
}
