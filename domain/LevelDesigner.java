package domain;

import java.util.Random;

/**
 * Generador programático de LevelConfig para niveles de dificultad creciente.
 * Diseñado para ser extensible: se usan las entidades existentes y la API de LevelConfig.
 */
public class LevelDesigner {

    private final int cols;
    private final int rows;
    private final Random rnd = new Random(12345);

    public LevelDesigner(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
    }

    /**
     * Genera un LevelConfig sencillo según una dificultad (1..5).
     * - difficulty 1: pocos enemigos básicos, pocas monedas
     * - difficulty 5: varios enemigos, patrulleros y acelerados
     */
    public LevelConfig generate(int difficulty) {
        if (difficulty < 1) difficulty = 1;
        if (difficulty > 5) difficulty = 5;

        LevelConfig cfg = new LevelConfig();
        cfg.timeLimit = 120; // default
        // player default
        cfg.playerSize = 16;
        cfg.playerSpeed = 4;
        cfg.playerX = 20;
        cfg.playerY = 20;

        // Coins: increase with difficulty
        int coins = 3 + (difficulty - 1) * 2;
        for (int i = 0; i < coins; i++) {
            int x = 30 + rnd.nextInt(Math.max(1, cols - 60));
            int y = 30 + rnd.nextInt(Math.max(1, rows - 60));
            cfg.entities.add(new Coin(x, y, 8));
        }

        // Walls and safezones minimal
        cfg.entities.add(new SafeZone(10, 10, 32, 32, SafeZone.Type.INITIAL));
        cfg.entities.add(new SafeZone(cols - 42, rows - 42, 32, 32, SafeZone.Type.FINAL));

        // Enemies: scale with difficulty
        int baseEnemies = difficulty; // 1..5
        for (int i = 0; i < baseEnemies; i++) {
            // Alternate horizontal and vertical basic enemies
            int ex = 40 + i * 30;
            int ey = 50 + i * 20;
            // base enemy speed token (constructor param 4)
            int baseSpeed = 1 + difficulty / 2; // 1..3
            Enemy e;
            if (i % 2 == 0) {
                // Horizontal enemy: tokens: x y size baseSpeed minX maxX movementSpeed
                e = new Enemy(ex, ey, 16, baseSpeed);
                e.setMovement(new HorizontalMovement(e, 10, cols - 10, 2 + difficulty));
            } else {
                e = new Enemy(ex, ey, 16, baseSpeed);
                e.setMovement(new VerticalMovement(e, 10, rows - 10, 2 + difficulty));
            }
            cfg.entities.add(e);
        }

        // Add a patrullero at higher difficulties
        if (difficulty >= 3) {
            int cx = cols / 2;
            int cy = rows / 2;
            cfg.entities.add(Patrullero.circular(cx, cy, 16, 2.0f, cx, cy, 60));
        }

        // Add an accelerated enemy at top difficulties
        if (difficulty >= 4) {
            // Use same format as ENEMY_ACCELERATED: create Enemy with speed*2 and horizontal movement
            Enemy a = new Enemy(60, 60, 16, 2 * 2); // baseSpeed * 2
            a.setMovement(new HorizontalMovement(a, 20, cols - 20, 6));
            cfg.entities.add(a);
        }

        return cfg;
    }
}
