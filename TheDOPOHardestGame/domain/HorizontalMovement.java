package domain;

/**
 * Movimiento horizontal que rebota entre minX y maxX.
 * El enemigo se mueve en línea recta y rebota al llegar al límite.
 */
public class HorizontalMovement implements Movement {
    private int minX;
    private int maxX;
    private int speed;
    private int direction; // +1 derecha, -1 izquierda
    private Enemy enemy; 

    public HorizontalMovement(Enemy enemy, int minX, int maxX, int speed) {
        this.enemy = enemy;
        this.minX = minX;
        this.maxX = maxX;
        this.speed = speed;
        this.direction = 1;
    }

    @Override
    public void move() {
        int newX = enemy.getX() + (speed * direction);

        // Rebote
        if (newX <= minX) {
            newX = minX;
            direction = 1;
        } else if (newX + enemy.getWidth() >= maxX) {
            newX = maxX - enemy.getWidth();
            direction = -1;
        }

        enemy.setX(newX);
    }
}
