package presentation;

import domain.PlayerType;
import java.awt.Color;

/**
 * Almacena información del modo de juego seleccionado.
 * Se pasa entre pantallas para configurar el GamePanel.
 *
 * CORRECCIÓN: agrega playerType para cada jugador.
 * El color aquí es el color de BORDE (cosmético), no el skin.
 * El skin (PlayerType) define velocidad, tamaño y habilidades.
 */
public class GameModeData {
    public enum Mode {
        SINGLE_PLAYER,
        PLAYER_VS_PLAYER,
        PLAYER_VS_AI_RANDOM,
        PLAYER_VS_AI_EXPERT
    }

    private Mode mode;
    private int level;

    // ---- Jugador 1 ----
    private PlayerType player1Type;   // skin elegido
    private Color      player1Border; // color de borde cosmético (null = usa el del tipo)

    // ---- Jugador 2 (solo PvsP) ----
    private PlayerType player2Type;
    private Color      player2Border;

    public GameModeData(Mode mode, int level) {
        this.mode = mode;
        this.level = level;
        // defaults
        this.player1Type   = PlayerType.RED;
        this.player1Border = null;
        this.player2Type   = PlayerType.RED;
        this.player2Border = null;
    }

    // Getters / Setters
    public Mode getMode()              { return mode; }
    public void setMode(Mode mode)     { this.mode = mode; }

    public int  getLevel()             { return level; }
    public void setLevel(int level)    { this.level = level; }

    public PlayerType getPlayer1Type()            { return player1Type; }
    public void       setPlayer1Type(PlayerType t){ this.player1Type = t; }

    public Color getPlayer1Border()               { return player1Border; }
    public void  setPlayer1Border(Color c)         { this.player1Border = c; }

    public PlayerType getPlayer2Type()            { return player2Type; }
    public void       setPlayer2Type(PlayerType t){ this.player2Type = t; }

    public Color getPlayer2Border()               { return player2Border; }
    public void  setPlayer2Border(Color c)         { this.player2Border = c; }

    // ---- Compatibilidad con código viejo ----
    /** @deprecated usar getPlayer1Border() */
    public Color getPlayer1Color() { return player1Border != null ? player1Border : player1Type.getColor(); }
    /** @deprecated usar setPlayer1Border() */
    public void  setPlayer1Color(Color c) { this.player1Border = c; }

    /** @deprecated usar getPlayer2Border() */
    public Color getPlayer2Color() { return player2Border != null ? player2Border : player2Type.getColor(); }
    /** @deprecated usar setPlayer2Border() */
    public void  setPlayer2Color(Color c) { this.player2Border = c; }
}