package domain;

/**
 * Controlador de puntaje global de la partida.
 * Acumula muertes y monedas de todos los jugadores.
 * Board lo actualiza y la presentación lo consulta para mostrar 
 */
public class ScoreController {
    private int totalDeaths;
    private int totalCoins;

    public ScoreController() {
        this.totalDeaths = 0;
        this.totalCoins = 0;
    }

    public void addDeath() { totalDeaths++; }
    public void addCoin()  { totalCoins++; }

    public int getTotalDeaths() { return totalDeaths; }
    public int getTotalCoins()  { return totalCoins; }

    public void reset() {
        totalDeaths = 0;
        totalCoins = 0;
    }
}
