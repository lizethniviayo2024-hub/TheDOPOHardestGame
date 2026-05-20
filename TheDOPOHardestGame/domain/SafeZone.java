package domain;

/**
 * Zona segura. Puede ser inicial, intermedia o final.
 * En modos PvP las zonas finales tienen un propietario (ownerName)
 * para que cada jugador tenga su zona opuesta.
 */
public class SafeZone extends Zone implements Collidable {

    public enum Type { INITIAL, INTERMEDIATE, FINAL }

    private Type type;

    /**
     * Propietario de la zona. null = cualquier jugador puede usarla (modo single).
     * En PvP se asigna el nombre del jugador al crear la zona opuesta para P2.
     */
    private String ownerName;

    public SafeZone(int x, int y, int width, int height, Type type) {
        super(x, y, width, height);
        this.type = type;
        this.ownerName = null;
    }

    public SafeZone(int x, int y, int width, int height, Type type, String ownerName) {
        super(x, y, width, height);
        this.type = type;
        this.ownerName = ownerName;
    }

    @Override
    public void onPlayerEnter(Player player) {
        if (type == Type.INTERMEDIATE || type == Type.FINAL) {
            player.setSpawnX(xPosition);
            player.setSpawnY(yPosition);
        }
    }

    @Override
    public void update() {
        // no hace nada por defecto
    }

    @Override
    public void onPlayerCollision(Player player, ScoreController score) {
        onPlayerEnter(player);
    }

    public Type    getType()      { return type; }
    public boolean isFinal()      { return type == Type.FINAL; }
    public String  getOwnerName() { return ownerName; }
    public void    setOwnerName(String name) { this.ownerName = name; }
}
