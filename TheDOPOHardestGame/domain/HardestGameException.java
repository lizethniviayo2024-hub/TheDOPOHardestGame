package domain;

/**
 * Excepción propia del juego. Se lanza en errores de configuración y persistencia.
 */
public class HardestGameException extends Exception {
    public HardestGameException(String message) {
        super(message);
    }
}