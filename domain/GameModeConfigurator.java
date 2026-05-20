package domain;

import java.awt.Color;
import java.io.File;

/**
 * Sabe cómo armar cada modo de juego y deja el GameState listo para Board.
 *
 * Responsabilidad única: construir jugadores y poblar GameState
 * según el modo pedido. No sabe nada de colisiones, tiempo ni rendering.
 *
 * Para agregar un modo nuevo: agregar un método configure___() aquí.
 * Board y GameState no se tocan.
 */
public class GameModeConfigurator {

    private final int boardCols;
    private final int boardRows;

    public GameModeConfigurator(int boardCols, int boardRows) {
        this.boardCols = boardCols;
        this.boardRows = boardRows;
    }

    // ─────────────────────────────────────────────────────────────
    // SINGLE PLAYER
    // ─────────────────────────────────────────────────────────────

    /**
     * Carga un nivel de un jugador desde archivo.
     */
    public GameState configureSingle(File file,
                                     String playerType,
                                     Color borderColor)
    throws HardestGameException {

        LevelConfig config = LevelLoader.load(file);
        return configureSingle(config, playerType, borderColor);
    }

    /**
     * Carga un nivel de un jugador desde config ya parseada.
     * Útil para tests (no necesita archivo en disco).
     */
    public GameState configureSingle(LevelConfig config,
                                     String playerType,
                                     Color borderColor) {

        GameState state = new GameState();
        state.gameMode = "SINGLE";
        state.populateFromConfig(config);

        Player p1 = buildPlayer(
            playerType, "Player 1",
            config.playerX, config.playerY,
            config.playerSize, config.playerSpeed,
            borderColor
        );

        state.players.add(p1);
        return state;
    }

    // ─────────────────────────────────────────────────────────────
    // PLAYER VS PLAYER
    // ─────────────────────────────────────────────────────────────

    public GameState configurePvP(File file,
                                  String p1Name, String p1Type, Color p1Border,
                                  String p2Name, String p2Type, Color p2Border)
    throws HardestGameException {

        LevelConfig config = LevelLoader.load(file);
        return configurePvP(config, p1Name, p1Type, p1Border, p2Name, p2Type, p2Border);
    }

    public GameState configurePvP(LevelConfig config,
                                  String p1Name, String p1Type, Color p1Border,
                                  String p2Name, String p2Type, Color p2Border) {

        GameState state = new GameState();
        state.gameMode = "PVSP";
        state.populateFromConfig(config);

        Player p1 = buildPlayer(
            p1Type, p1Name,
            config.playerX, config.playerY,
            config.playerSize, config.playerSpeed,
            p1Border
        );
        state.players.add(p1);

        // P2 empieza en la esquina opuesta
        int p2X = boardCols - config.playerX - p1.getWidth();
        int p2Y = boardRows - config.playerY - p1.getHeight();

        Player p2 = buildPlayer(
            p2Type, p2Name,
            p2X, p2Y,
            config.playerSize, config.playerSpeed,
            p2Border
        );
        p2.setSpawnX(p2X);
        p2.setSpawnY(p2Y);
        state.players.add(p2);

        return state;
    }

    // ─────────────────────────────────────────────────────────────
    // PLAYER VS MACHINE — IA ALEATORIA
    // ─────────────────────────────────────────────────────────────

    public GameState configurePvAIRandom(File file,
                                         String playerType,
                                         Color borderColor)
    throws HardestGameException {

        LevelConfig config = LevelLoader.load(file);
        return configurePvAIRandom(config, playerType, borderColor);
    }

    public GameState configurePvAIRandom(LevelConfig config,
                                         String playerType,
                                         Color borderColor) {

        GameState state = new GameState();
        state.gameMode = "PVSM_RANDOM";
        state.populateFromConfig(config);

        Player p1 = buildPlayer(
            playerType, "Player 1",
            config.playerX, config.playerY,
            config.playerSize, config.playerSpeed,
            borderColor
        );
        state.players.add(p1);

        int p2X = boardCols - config.playerX - config.playerSize;
        int p2Y = boardRows - config.playerY - config.playerSize;

        AIControlledPlayer aiPlayer = new AIControlledPlayer(
            "AI Random", p2X, p2Y,
            config.playerSize, config.playerSpeed,
            new Color(100, 150, 255),
            new RandomAIStrategy()
        );
        aiPlayer.setSpawnX(p2X);
        aiPlayer.setSpawnY(p2Y);
        state.players.add(aiPlayer);

        return state;
    }

    // ─────────────────────────────────────────────────────────────
    // PLAYER VS MACHINE — IA EXPERTA (BFS)
    // ─────────────────────────────────────────────────────────────

    public GameState configurePvAIExpert(File file,
                                         String playerType,
                                         Color borderColor)
    throws HardestGameException {

        LevelConfig config = LevelLoader.load(file);
        return configurePvAIExpert(config, playerType, borderColor);
    }

    public GameState configurePvAIExpert(LevelConfig config,
                                         String playerType,
                                         Color borderColor) {

        GameState state = new GameState();
        state.gameMode = "PVSM_EXPERT";
        state.populateFromConfig(config);

        Player p1 = buildPlayer(
            playerType, "Player 1",
            config.playerX, config.playerY,
            config.playerSize, config.playerSpeed,
            borderColor
        );
        state.players.add(p1);

        int p2X = boardCols - config.playerX - config.playerSize;
        int p2Y = boardRows - config.playerY - config.playerSize;

        AIControlledPlayer aiExpert = new AIControlledPlayer(
            "AI Expert", p2X, p2Y,
            config.playerSize, config.playerSpeed,
            new Color(255, 100, 100),
            new BFSAIStrategy(boardCols, boardRows)
        );
        aiExpert.setSpawnX(p2X);
        aiExpert.setSpawnY(p2Y);
        state.players.add(aiExpert);

        return state;
    }

    // ─────────────────────────────────────────────────────────────
    // HELPER PRIVADO
    // ─────────────────────────────────────────────────────────────

    private Player buildPlayer(String type, String name,
                                int x, int y, int size, int speed,
                                Color borderColor) {

        Player player = PlayerFactory.createPlayer(type, name, x, y, size, speed);

        if (borderColor != null) {
            player.setBorderColor(borderColor);
        }

        return player;
    }
}