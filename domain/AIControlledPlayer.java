package domain;

import java.awt.Color;

/**
 * Jugador controlado por una AIStrategy intercambiable.
 *
 * Antes, la lógica de IA vivía en AIPlayer y AIPlayerExpert,
 * que heredaban de RedPlayer por conveniencia de color.
 * Esto violaba LSP: un RedPlayer sustituido por un AIPlayer
 * tiene un update() completamente diferente.
 *
 * Ahora:
 * - AIControlledPlayer ES un Player, sin asumir color ni tipo concreto.
 * - La estrategia de decisión (aleatoria, BFS, A*, etc.) llega por constructor.
 * - Cambiar de estrategia = pasar otra implementación de AIStrategy. Nada más.
 *
 * El Board llama a update() igual que con cualquier Player;
 * y luego lee getCurrentDirection() para mover el jugador.
 */
public class AIControlledPlayer extends Player {

    private final AIStrategy strategy;
    private String currentDirection;

    /**
     * @param name      Nombre visible del jugador IA ("AI Random", "AI Expert", etc.)
     * @param x         Posición X inicial
     * @param y         Posición Y inicial
     * @param size      Tamaño del jugador
     * @param speed     Velocidad en píxeles por tick
     * @param color     Color visual (ya no forzado a rojo)
     * @param strategy  Estrategia de decisión. Se puede cambiar en runtime si se quiere.
     */
    public AIControlledPlayer(String name,
                               int x, int y,
                               int size, int speed,
                               Color color,
                               AIStrategy strategy) {

        super(name, x, y, size, size, speed, color);
        this.strategy         = strategy;
        this.currentDirection = "IDLE";
    }

    /**
     * Delega la decisión de movimiento a la estrategia.
     * Board llama a este método cada tick, igual que con cualquier Player.
     */
    @Override
    public void update() {
        AIContext ctx = new AIContext(getX(), getY(), getWidth(), getHeight(), getSpeed());
        currentDirection = strategy.computeDirection(ctx);
    }

    /**
     * Board lee esta dirección después de update() para mover al jugador.
     */
    public String getCurrentDirection() {
        return currentDirection;
    }

    /**
     * Devuelve la estrategia activa.
     * Útil para que el Board actualice el mundo en BFSAIStrategy
     * (setCoinPositions, setWallRects) sin conocer la implementación concreta.
     *
     * Ejemplo de uso en Board.update():
     *   if (p.getStrategy() instanceof BFSAIStrategy bfs) {
     *       bfs.setCoinPositions(coinPositions);
     *   }
     */
    public AIStrategy getStrategy() {
        return strategy;
    }
}