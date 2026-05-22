package domain;

/**
 * Define cómo una IA decide su próxima dirección de movimiento.
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