package domain;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class SaveManager {

    public static void saveGame(Board board, String fileName)
    throws IOException {

        PrintWriter writer = new PrintWriter(
                new FileWriter(fileName)
            );

        ////Para guardar partida es con G

        // =========================
        // DATOS GENERALES
        // =========================

        writer.println("MODE " + board.getGameMode());

        writer.println("LEVEL " + board.getCurrentLevel());

        writer.println("TIME " + board.getTimeRemaining());

        // =========================
        // JUGADORES
        // =========================

        int i = 1;

        for(Player p : board.getPlayers()) {

            writer.println("PLAYER_" + i + "_NAME " + p.getName());
            writer.println("PLAYER_" + i + "_X " + p.getX());

            writer.println("PLAYER_" + i + "_Y " + p.getY());

            writer.println(
                "PLAYER_" + i + "_DEATHS "
                + board.getPlayerDeaths(p.getName())
            );

            writer.println(
                "PLAYER_" + i + "_COINS "
                + board.getPlayerCoins(p.getName())
            );

            writer.println();

            i++;
        }

        // =========================
        // COLECCIONABLES (monedas y otros elementos requeridos)
        // =========================

        int c = 0;

        for(Collectible col : board.getCollectibles()) {

            writer.println(
                "COLLECTIBLE_" + c + "_ACTIVE "
                + col.isActive()
            );

            c++;
        }

        writer.close();
    }

}