package domain;

import java.awt.Color;

public abstract class Player extends GameEntity {

    private String name;

    private int spawnX;
    private int spawnY;
    private String playerType;

    protected int baseSpeed;
    protected int speed;

    protected Color playerColor;
    protected Color borderColor;

    public Player(
    String name,
    int x,
    int y,
    int width,
    int height,
    int baseSpeed,
    Color color
    ) {

        super(x, y, width, height);

        this.name = name;

        this.baseSpeed = baseSpeed;
        this.speed = baseSpeed;

        this.playerColor = color;
        this.borderColor = Color.BLACK;

        this.spawnX = x;
        this.spawnY = y;

    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = (borderColor != null) ? borderColor : Color.BLACK;
    }

    public Color getBorderColor() {
        return borderColor != null ? borderColor : Color.BLACK;
    }

    @Override
    public void update() {

    }

    public void hit() {
        respawn();
    }

    /**
     * Mueve al jugador a su punto de reaparición.
     */
    public void respawn() {
        setX(spawnX);
        setY(spawnY);
    }

    public int getSpeed() {
        return speed;
    }

    public Color getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(Color c) {
        this.playerColor = c;
    }

    public String getName() {
        return name;
    }

    public void setSpawnX(int x) {
        spawnX = x;
    }

    public void setSpawnY(int y) {
        spawnY = y;
    }

    public boolean isShieldActive() {
        return false;
    }

}
