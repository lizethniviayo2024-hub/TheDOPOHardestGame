package domain;

/**
 * Define cómo una IA decide su próxima dirección de movimiento.
 *
 * Cualquier estrategia nueva (A*, MCTS, scripted, etc.) solo necesita
 * implementar esta interfaz. Ninguna clase de jugador ni Board cambia.
 *
 * Contrato:
 * - computeDirection() se llama una vez por tick desde AIControlledPlayer.update().
 * - Devuelve un String de dirección: "UP", "DOWN", "LEFT", "RIGHT",
 *   "UP_LEFT", "UP_RIGHT", "DOWN_LEFT", "DOWN_RIGHT" o "IDLE".
 * - La implementación es libre de mantener estado interno entre ticks.
 */
public interface AIStrategy {

    /**
     * Calcula la dirección a tomar en el tick actual.
     *
     * @param context  Información de la posición y tamaño del jugador IA.
     *                 Las implementaciones pueden ignorar lo que no necesiten.
     * @return         Dirección como String, nunca null. "IDLE" si no hay
     *                 movimiento deseado.
     */
    String computeDirection(AIContext context);
}