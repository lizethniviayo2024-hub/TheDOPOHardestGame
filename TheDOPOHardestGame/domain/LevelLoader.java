package domain;

import java.io.*;
import java.util.*;

/**
 * Lee archivos .txt de configuración de niveles y construye un LevelConfig.
 * Para añadir un nuevo tipo de entidad: solo registrar  con register().
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
        register("ENEMY_PATROL", t -> {
            Enemy e = new Enemy(num(t,1), num(t,2), num(t,3), num(t,4));
            int[] waypoints = new int[t.length - 5];
            for (int i = 0; i < waypoints.length; i++) {
                waypoints[i] = Integer.parseInt(t[5 + i]);
            }
            e.setMovement(new PatrolMovement(e, num(t,4), waypoints));
            return e;
        });
        
        // Patrulleros con rutas geométricas predefinidas
        register("PATRULLERO_CIRCULAR", t -> {
            // Formato: PATRULLERO_CIRCULAR x y size speed centerX centerY radius
            return Patrullero.circular(num(t,1), num(t,2), num(t,3), num(t,4), 
                                      num(t,5), num(t,6), num(t,7));
        });
        
        register("PATRULLERO_RECTANGULO", t -> {
            // Formato: PATRULLERO_RECTANGULO x y size speed x1 y1 x2 y2
            return Patrullero.rectangular(num(t,1), num(t,2), num(t,3), num(t,4),
                                         num(t,5), num(t,6), num(t,7), num(t,8));
        });
        
        register("PATRULLERO_TRIANGULAR", t -> {
            // Formato: PATRULLERO_TRIANGULAR x y size speed x1 y1 x2 y2 x3 y3
            return Patrullero.triangular(num(t,1), num(t,2), num(t,3), num(t,4),
                                        num(t,5), num(t,6), num(t,7), num(t,8),
                                        num(t,9), num(t,10));
        });
        
        register("PATRULLERO_PERSONALIZADO", t -> {
            // Formato: PATRULLERO_PERSONALIZADO x y size speed x1 y1 x2 y2 ... xn yn
            int[] waypoints = new int[t.length - 5];
            for (int i = 0; i < waypoints.length; i++) {
                waypoints[i] = Integer.parseInt(t[5 + i]);
            }
            return Patrullero.personalizado(num(t,1), num(t,2), num(t,3), num(t,4), waypoints);
        });

        // Enemigo acelerado: se comporta como enemigo en línea recta pero con velocidad multiplicada (2x)
        // Formato similar a ENEMY_HORIZONTAL: ENEMY_ACCELERATED x y size baseSpeed minX maxX movementSpeed
        register("ENEMY_ACCELERATED", t -> {
            // La velocidad base del Enemy se multiplica por 2
            Enemy e = new Enemy(num(t,1), num(t,2), num(t,3), num(t,4) * 2);
            // El movimiento horizontal también se hace más rápido (movement speed * 2)
            e.setMovement(new HorizontalMovement(e, num(t,5), num(t,6), num(t,7) * 2));
            return e;
        });
    }


    public static void register(String type, EntityParser parser) {
        PARSERS.put(type.toUpperCase(), parser);
    }

    private static int num(String[] tokens, int i) {
        return Integer.parseInt(tokens[i]);
    }
    
 
    private static float f(String[] tokens, int i) {
        return Float.parseFloat(tokens[i]);
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