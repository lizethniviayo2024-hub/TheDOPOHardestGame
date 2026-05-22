package domain;

import java.awt.Rectangle;

/**
 * Clase base para todos los objetos del juego.
 * Contiene posición y tamaño. Todos los objetos del juego heredan de esta.
 */
public abstract class GameEntity {
    protected int xPosition;
    protected int yPosition;
    protected int width;
    protected int height;

    public GameEntity(int xPosition, int yPosition, int width, int height) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.width = width;
        this.height = height;
    }

    /**
     * Actualiza el estado de la entidad en cada tick del game loop.
     */
    public abstract void update();

    public int getX()      { return xPosition; }

    public int getY()      { return yPosition; }

    public int getWidth()  { return width; }

    public int getHeight() { return height; }

    public void setX(int x) { this.xPosition = x; }

    public void setY(int y) { this.yPosition = y; }

    /**
     * Retorna el rectángulo de colisión del objeto.
     * Se usa en CollisionController.
     */
    public Rectangle getBounds() {
        return new Rectangle(xPosition, yPosition, width, height);
    }
}