package domain;

/**
 * Jugador del juego.
 * Se mueve con el teclado, tiene muertes y monedas recolectadas.
 */
public class Player extends GameEntity {
    private int deaths;
    private int coinsCollected;
    private boolean isAlive;
    private String name;
    private int spawnX;
    private int spawnY;
    private int speed;

    public Player(String name, int x, int y, int size, int speed) {
        super(x, y, size, size);
        this.name = name;
        this.deaths = 0;
        this.coinsCollected = 0;
        this.isAlive = true;
        this.speed = speed;
        this.spawnX = x;
        this.spawnY = y;
    }

    @Override
    public void update() {
        // no hace nada por defecto
    }

    /** El jugador muere: suma una muerte y reaparece en el spawn. */
    public void die() {
        deaths++;
        xPosition = spawnX;
        yPosition = spawnY;
    }

    public void collectCoin() { coinsCollected++; }

    public void moveUp()        { yPosition -= speed; }

    public void moveDown()      { yPosition += speed; }

    public void moveLeft()      { xPosition -= speed; }

    public void moveRight()     { xPosition += speed; }

    public void moveUpLeft()    { xPosition -= speed; yPosition -= speed; }

    public void moveUpRight()   { xPosition += speed; yPosition -= speed; }

    public void moveDownLeft()  { xPosition -= speed; yPosition += speed; }

    public void moveDownRight() { xPosition += speed; yPosition += speed; }

    public int getDeaths()         { return deaths; }

    public int getCoinsCollected() { return coinsCollected; }

    public boolean isAlive()       { return isAlive; }

    public String getName()        { return name; }

    public int getSpeed()          { return speed; }

    public void setSpawnX(int x) { this.spawnX = x; }

    public void setSpawnY(int y) { this.spawnY = y; }
}