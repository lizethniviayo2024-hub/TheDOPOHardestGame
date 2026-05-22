package domain;
import java.util.Map;
import java.util.HashMap;
import java.awt.Color;

public class PlayerFactory {

    private static final Map<String, PlayerCreator> creators = new HashMap<>();

    public interface PlayerCreator {
        Player create(String name, int x, int y, int size, int speed);
    }

    static {
        register("RED",   (n,x,y,s,sp) -> new RedPlayer(n,x,y,s,sp));
        register("BLUE",  (n,x,y,s,sp) -> new BluePlayer(n,x,y,s,sp));
        register("GREEN", (n,x,y,s,sp) -> new GreenPlayer(n,x,y,s,sp));
    }

    public static void register(String type, PlayerCreator creator) {
        creators.put(type.toUpperCase(), creator);
    }

    public static Player createPlayer(String type, String name, int x, int y, int size, int speed) {
        PlayerCreator creator = creators.get(type.toUpperCase());
        if (creator == null) creator = creators.get("RED"); 
        Player p = creator.create(name, x, y, size, speed);
        p.setBorderColor(Color.BLACK);
        return p;
    }
}
