package domain;

import java.util.*;

/**
 * Estrategia de IA experta basada en BFS.
 * Busca la moneda más cercana y traza un camino hasta ella.
 *
 * Lógica extraída de AIPlayerExpert, que antes la tenía mezclada
 * con herencia de RedPlayer y necesitaba saber su propio tamaño
 * a través de getWidth()/getSpeed() del Player.
 *
 * Ahora recibe esa info limpiamente via AIContext en cada tick.
 * El mundo externo (monedas, paredes) se actualiza con los setters.
 */
public class BFSAIStrategy implements AIStrategy {

    private final int boardWidth;
    private final int boardHeight;

    private List<int[]> coinPositions  = new ArrayList<>();
    private List<int[]> enemyPositions = new ArrayList<>();
    private List<int[]> wallRects      = new ArrayList<>();

    private List<String> path          = new ArrayList<>();
    private int          pathIndex     = 0;
    private int          updateCounter = 0;

    private static final int RECALCULATE_INTERVAL = 30;

    public BFSAIStrategy(int boardWidth, int boardHeight) {
        this.boardWidth  = boardWidth;
        this.boardHeight = boardHeight;
    }

    // ─────────────────────────────────────────────────────────────
    // AIStrategy
    // ─────────────────────────────────────────────────────────────

    @Override
    public String computeDirection(AIContext ctx) {

        updateCounter++;

        if (updateCounter >= RECALCULATE_INTERVAL) {
            updateCounter = 0;
            recalculate(ctx);
        }

        if (pathIndex < path.size()) {
            return path.get(pathIndex++);
        }

        return "IDLE";
    }

    // ─────────────────────────────────────────────────────────────
    // Actualización del mundo
    // ─────────────────────────────────────────────────────────────

    public void setCoinPositions(List<int[]> coins) {
        this.coinPositions = coins;
    }

    public void setEnemyPositions(List<int[]> enemies) {
        this.enemyPositions = enemies;
    }

    public void setWallRects(List<int[]> walls) {
        this.wallRects = walls;
    }

    // ─────────────────────────────────────────────────────────────
    // BFS interno
    // ─────────────────────────────────────────────────────────────

    private void recalculate(AIContext ctx) {
        if (coinPositions.isEmpty()) return;

        int[] nearest = findNearestCoin(ctx);
        if (nearest == null) return;

        path      = findPathBFS(ctx, nearest[0], nearest[1]);
        pathIndex = 0;
    }

    private int[] findNearestCoin(AIContext ctx) {
        int[] nearest = null;
        int   minDist = Integer.MAX_VALUE;

        for (int[] coin : coinPositions) {
            int dist = Math.abs(ctx.x - coin[0]) + Math.abs(ctx.y - coin[1]);
            if (dist < minDist) {
                minDist = dist;
                nearest = coin;
            }
        }

        return nearest;
    }

    private List<String> findPathBFS(AIContext ctx,
    int goalX, int goalY) {

        Queue<Node>  queue   = new LinkedList<>();
        Set<String>  visited = new HashSet<>();

        queue.add(new Node(ctx.x, ctx.y, new ArrayList<>()));
        visited.add(ctx.x + "," + ctx.y);

        int[][] deltas   = { {0, -ctx.speed}, {0, ctx.speed}, {-ctx.speed, 0}, {ctx.speed, 0} };
        String[] names   = { "UP", "DOWN", "LEFT", "RIGHT" };

        while (!queue.isEmpty()) {

            Node current = queue.poll();

            if (Math.abs(current.x - goalX) < ctx.width
            && Math.abs(current.y - goalY) < ctx.width) {
                return current.path;
            }

            for (int i = 0; i < deltas.length; i++) {
                int nx = current.x + deltas[i][0];
                int ny = current.y + deltas[i][1];

                if (!isValidMove(nx, ny, ctx.width)) continue;

                String key = nx + "," + ny;
                if (visited.contains(key)) continue;

                visited.add(key);
                List<String> newPath = new ArrayList<>(current.path);
                newPath.add(names[i]);
                queue.add(new Node(nx, ny, newPath));
            }
        }

        return new ArrayList<>();
    }

    private boolean isValidMove(int x, int y, int size) {
        if (x < 0 || y < 0 || x + size > boardWidth || y + size > boardHeight) {
            return false;
        }
        for (int[] wall : wallRects) {
            if (rectsCollide(x, y, size, size, wall[0], wall[1], wall[2], wall[3])) {
                return false;
            }
        }
        return true;
    }

    private boolean rectsCollide(int x1, int y1, int w1, int h1,
    int x2, int y2, int w2, int h2) {
        return x1 < x2 + w2 && x1 + w1 > x2
        && y1 < y2 + h2 && y1 + h1 > y2;
    }

    // ─────────────────────────────────────────────────────────────
    // Nodo BFS
    // ─────────────────────────────────────────────────────────────

    private static class Node {
        final int          x, y;
        final List<String> path;

        Node(int x, int y, List<String> path) {
            this.x    = x;
            this.y    = y;
            this.path = path;
        }
    }
}