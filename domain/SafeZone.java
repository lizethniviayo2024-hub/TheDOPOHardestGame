package domain;

/**
 * Zona segura. Puede ser inicial, intermedia o final.
 */
public class SafeZone extends Zone implements Collidable {

    public enum Type { INITIAL, INTERMEDIATE, FINAL }

    private Type type;

    public SafeZone(int x, int y, int width, int height, Type type) {
        super(x, y, width, height);
        this.type = type;
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

    public Type getType()  { return type; }
    public boolean isFinal() { return type == Type.FINAL; }
}