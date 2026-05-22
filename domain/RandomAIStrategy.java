package domain;

import java.util.Random;

/**
 * Estrategia de IA aleatoria.
 * Cambia de dirección cada N ticks (N varía aleatoriamente entre 5 y 20).
 *
 * Lógica extraída de AIPlayer, que antes la tenía mezclada
 * con la herencia de RedPlayer.
 */
public class RandomAIStrategy implements AIStrategy {

    private static final String[] DIRECTIONS = {
        "UP", "DOWN", "LEFT", "RIGHT",
        "UP_LEFT", "UP_RIGHT", "DOWN_LEFT", "DOWN_RIGHT", "IDLE"
    };

    private final Random random;

    private int moveCounter;
    private int moveInterval;
    private String currentDirection;

    public RandomAIStrategy() {
        this.random           = new Random();
        this.moveCounter      = 0;
        this.moveInterval     = random.nextInt(10) + 5;
        this.currentDirection = "IDLE";
    }

    @Override
    public String computeDirection(AIContext context) {

        moveCounter++;

        if (moveCounter >= moveInterval) {
            moveCounter      = 0;
            moveInterval     = random.nextInt(15) + 5;
            currentDirection = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
        }

        return currentDirection;
    }
}