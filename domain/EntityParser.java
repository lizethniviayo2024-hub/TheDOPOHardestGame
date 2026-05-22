package domain;

public interface EntityParser {
    /**
     * Recibe los tokens de una línea del .txt y construye la entidad correspondiente.
     */
    GameEntity parse(String[] tokens) throws HardestGameException;
}