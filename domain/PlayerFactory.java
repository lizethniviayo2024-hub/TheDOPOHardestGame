package domain;
import java.awt.Color;

public class PlayerFactory {

    public static Player createPlayer(
    String type,
    String name,
    int x,
    int y,
    int size,
    int speed) {

        Player p;

        switch (type.toUpperCase()) {

            case "BLUE":
                p = new BluePlayer(name, x, y, size, speed);
                break;

            case "GREEN":
                p = new GreenPlayer(name, x, y, size, speed);
                break;

            default:
                p = new RedPlayer(name, x, y, size, speed);
        }

        
        p.setBorderColor(Color.BLACK);

        return p;
    }
}