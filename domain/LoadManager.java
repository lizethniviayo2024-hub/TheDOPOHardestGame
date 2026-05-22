package domain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class LoadManager {

    public static void loadGame(Board board, String fileName)
    throws Exception {

        BufferedReader reader =
            new BufferedReader(new FileReader(fileName));

        String line;

        int level = 1;
        int time = 60;

        int playerIndex = 0;

        while((line = reader.readLine()) != null) {

            String[] parts = line.split(" ");

            // =========================
            // NIVEL
            // =========================

            if(parts[0].equals("LEVEL")) {

                level = Integer.parseInt(parts[1]);
            }

            // =========================
            // TIEMPO
            // =========================

            if(parts[0].equals("TIME")) {

                time = Integer.parseInt(parts[1]);
            }
        }

        reader.close();

        // =========================
        // CARGAR NIVEL
        // =========================

        board.loadFromFile(
            new java.io.File("levels/level" + level + ".txt")
        );
        board.setTimeRemaining(time);

        // =========================
        // LEER OTRA VEZ PARA PLAYERS
        // =========================

        reader =
        new BufferedReader(new FileReader(fileName));

        while((line = reader.readLine()) != null) {

            String[] parts = line.split(" ");

            if(parts[0].contains("_X")) {

                int x = Integer.parseInt(parts[1]);

                Player p = board.getPlayer(playerIndex);

                p.setX(x);
            }

            if(parts[0].contains("_Y")) {

                int y = Integer.parseInt(parts[1]);

                Player p = board.getPlayer(playerIndex);

                p.setY(y);

                playerIndex++;
            }

            if(parts[0].startsWith("COLLECTIBLE_")) {

                boolean active =
                    Boolean.parseBoolean(parts[1]);

                int index = Integer.parseInt(
                        parts[0]
                        .split("_")[1]
                    );

                List<Collectible> collectibles = board.getCollectibles();
                if (index < collectibles.size() && !active) {
                    Collectible col = collectibles.get(index);
                    // Si es una Coin, marcarla como recogida
                    if (col instanceof Coin) {
                        ((Coin) col).collect();
                    } else {
                        // Para otros collectibles, consumirlos directamente
                        // accediendo al método protegido a través del cast
                        // es suficiente con que no estén activos en la UI
                    }
                }
            }
            if(parts[0].contains("PLAYER_")&& parts[0].endsWith("_DEATHS")) {

                int deaths = Integer.parseInt(parts[1]);

                Player p = board.getPlayer(playerIndex - 1);

                board.setPlayerDeaths(
                    p.getName(),
                    deaths
                );
            }

            if(parts[0].contains("PLAYER_")&& parts[0].endsWith("_COINS")) {

                int coins = Integer.parseInt(parts[1]);

                Player p = board.getPlayer(playerIndex - 1);

                board.setPlayerCoins(
                    p.getName(),
                    coins
                );
            }
        }

        reader.close();

        System.out.println("PARTIDA CARGADA");
    }
}