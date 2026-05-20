package domain;

import java.io.*;
import java.util.*;

/**
 * Lee archivos .txt de configuración de niveles y construye un LevelConfig.
 * Para añadir un nuevo tipo de entidad: solo registrar un parser con register().
 * LevelLoader nunca necesita modificarse.
 */
public class LevelLoader {

    private static final Map<String, EntityParser> PARSERS = new HashMap<>();

    static {
        register("COIN",             t -> new Coin(num(t,1), num(t,2), num(t,3)));
        register("SAFEZONE_INITIAL", t -> new SafeZone(num(t,1), num(t,2), num(t,3), num(t,4), SafeZone.Type.INITIAL));
        register("SAFEZONE_FINAL",   t -> new SafeZone(num(t,1), num(t,2), num(t,3), num(t,4), SafeZone.Type.FINAL));
        register("SAFEZONE_MIDDLE",  t -> new SafeZone(num(t,1), num(t,2), num(t,3), num(t,4), SafeZone.Type.INTERMEDIATE));
        register("WALL",             t -> new Walls(num(t,1), num(t,2), num(t,3), num(t,4)));
        register("ENEMY_HORIZONTAL", t -> {
            Enemy e = new Enemy(num(t,1), num(t,2), num(t,3), num(t,4));
            e.setMovement(new HorizontalMovement(e, num(t,5), num(t,6), num(t,7)));
            return e;
        });
        register("ENEMY_VERTICAL", t -> {
            Enemy e = new Enemy(num(t,1), num(t,2), num(t,3), num(t,4));
            e.setMovement(new VerticalMovement(e, num(t,5), num(t,6), num(t,7)));
            return e;
        });
    }

    /** Registra un parser para un tipo de línea del .txt. */
    public static void register(String type, EntityParser parser) {
        PARSERS.put(type.toUpperCase(), parser);
    }

    /** Helper para parsear entero desde token. */
    private static int num(String[] tokens, int i) {
        return Integer.parseInt(tokens[i]);
    }

    /**
     * Lee un archivo .txt y retorna su LevelConfig.
     * @throws HardestGameException si hay errores de formato o lectura.
     */
    public static LevelConfig load(File file) throws HardestGameException {
        LevelConfig config = new LevelConfig();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] tokens = line.split("\\s+");
                String type = tokens[0].toUpperCase();

                if (type.equals("TIME")) {
                    config.timeLimit = num(tokens, 1);
                    continue;
                }
                if (type.equals("PLAYER")) {
                    config.playerX     = num(tokens, 1);
                    config.playerY     = num(tokens, 2);
                    config.playerSize  = num(tokens, 3);
                    config.playerSpeed = num(tokens, 4);
                    continue;
                }

                EntityParser parser = PARSERS.get(type);
                if (parser == null) {
                    throw new HardestGameException(
                        "Tipo desconocido '" + type + "' en línea " + lineNumber
                    );
                }

                try {
                    config.entities.add(parser.parse(tokens));
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    throw new HardestGameException(
                        "Error en línea " + lineNumber + ": '" + line + "' — faltan datos o formato incorrecto"
                    );
                }
            }

        } catch (IOException e) {
            throw new HardestGameException("No se pudo leer el archivo: " + file.getName());
        }

        return config;
    }
}