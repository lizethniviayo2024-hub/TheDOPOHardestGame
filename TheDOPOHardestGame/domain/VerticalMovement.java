package domain;

/**
 * Movimiento vertical que rebota entre minY y maxY.
 */
public class VerticalMovement implements Movement {
    private int minY;
    private int maxY;
    private int speed;
    private int direction; // +1 abajo, -1 arriba
    private Enemy enemy;

    public VerticalMovement(Enemy enemy, int minY, int maxY, int speed) {
        this.enemy = enemy;
        this.minY = minY;
        this.maxY = maxY;
        this.speed = speed;
        this.direction = 1;
    }

    @Override
    public void move() {
        int newY = enemy.getY() + (speed * direction);

        if (newY <= minY) {
            newY = minY;
            direction = 1;
        } else if (newY + enemy.getHeight() >= maxY) {
            newY = maxY - enemy.getHeight();
            direction = -1;
        }

       enemy.setY(newY);
    }
}
