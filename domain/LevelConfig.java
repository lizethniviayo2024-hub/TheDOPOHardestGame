package domain;

import java.util.ArrayList;
import java.util.List;

public class LevelConfig {
    public int timeLimit;
    public int playerX, playerY, playerSize, playerSpeed;
    public List<GameEntity> entities = new ArrayList<>(); // todo lo demás vive aquí
}