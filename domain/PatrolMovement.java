package domain;

/**
 * Movimiento de patrulla que sigue una ruta geométrica predefinida (circular/cuadrada).
 * El enemigo recorre waypoints en orden y luego regresa al inicio (ciclo continuo).
 * 
 * Soporta rutas:
 * - Circular: (cx, cy, radio) - movimiento en círculo
 * - Cuadrada: (x1, y1, x2, y2) - rectángulo de patrulla
 * - Triangular: (x1, y1, x2, y2, x3, y3) - triángulo de patrulla
 * - Ruta libre: (x1, y1, x2, y2, ...) - waypoints arbitrarios
 */
public class PatrolMovement implements Movement {
    
    private Enemy enemy;
    private int[] waypoints;          // Puntos [x1, y1, x2, y2, x3, y3, ...]
    private int currentWaypointIndex; // Índice del waypoint actual
    private float speed;
    private float posX, posY;         // Posición actual del enemigo (con decimales para precisión)
    
    /**
     * Constructor para ruta con waypoints explícitos.
     * 
     * @param enemy         Referencia al enemigo que se mueve
     * @param speed         Velocidad de movimiento (píxeles por frame)
     * @param waypoints     Array con coordenadas de waypoints: [x1, y1, x2, y2, ...]
     */
    public PatrolMovement(Enemy enemy, float speed, int... waypoints) {
        if (waypoints.length < 2 || waypoints.length % 2 != 0) {
            throw new IllegalArgumentException(
                "Waypoints debe tener cantidad par de valores (pares x,y)"
            );
        }
        
        this.enemy = enemy;
        this.speed = speed;
        this.waypoints = waypoints;
        this.currentWaypointIndex = 0;
        this.posX = enemy.getX();
        this.posY = enemy.getY();
    }
    
    /**
     * Constructor para ruta circular.
     * 
     * @param enemy     Referencia al enemigo
     * @param centerX   Centro X del círculo
     * @param centerY   Centro Y del círculo
     * @param radius    Radio del círculo
     * @param speed     Velocidad angular (aumentar para más rápido)
     */
    public static PatrolMovement circular(Enemy enemy, int centerX, int centerY, 
                                          int radius, float speed) {
        // Generar waypoints circulares (8 puntos alrededor del círculo)
        int[] points = new int[16];
        double angleDelta = 2 * Math.PI / 8;
        
        for (int i = 0; i < 8; i++) {
            double angle = i * angleDelta;
            points[2 * i]     = (int)(centerX + radius * Math.cos(angle));
            points[2 * i + 1] = (int)(centerY + radius * Math.sin(angle));
        }
        
        return new PatrolMovement(enemy, speed, points);
    }
    
    /**
     * Constructor para ruta rectangular (cuadrado/rectángulo).
     * 
     * @param enemy   Referencia al enemigo
     * @param x1      Esquina superior izquierda X
     * @param y1      Esquina superior izquierda Y
     * @param x2      Esquina inferior derecha X
     * @param y2      Esquina inferior derecha Y
     * @param speed   Velocidad de movimiento
     */
    public static PatrolMovement rectangular(Enemy enemy, int x1, int y1, 
                                             int x2, int y2, float speed) {
        // 4 waypoints: esquinas del rectángulo
        int[] points = {
            x1, y1,  // Superior izquierda
            x2, y1,  // Superior derecha
            x2, y2,  // Inferior derecha
            x1, y2   // Inferior izquierda
        };
        
        return new PatrolMovement(enemy, speed, points);
    }
    
    /**
     * Obtiene el waypoint actual (x, y).
     */
    private int[] getCurrentWaypoint() {
        int idx = currentWaypointIndex * 2;
        return new int[]{waypoints[idx], waypoints[idx + 1]};
    }
    
    /**
     * Obtiene el siguiente waypoint (con ciclo).
     */
    private int[] getNextWaypoint() {
        int nextIdx = (currentWaypointIndex + 1) % (waypoints.length / 2);
        int idx = nextIdx * 2;
        return new int[]{waypoints[idx], waypoints[idx + 1]};
    }
    
    /**
     * Calcula la distancia entre dos puntos.
     */
    private float distance(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float)Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * Mueve el enemigo un paso hacia el siguiente waypoint.
     */
    @Override
    public void move() {
        int[] nextWp = getNextWaypoint();
        float targetX = nextWp[0];
        float targetY = nextWp[1];
        
        // Calcular distancia al siguiente waypoint
        float dist = distance(posX, posY, targetX, targetY);
        
        // Si ya llegó, pasar al siguiente waypoint
        if (dist < speed) {
            currentWaypointIndex = (currentWaypointIndex + 1) % (waypoints.length / 2);
            nextWp = getNextWaypoint();
            targetX = nextWp[0];
            targetY = nextWp[1];
            dist = distance(posX, posY, targetX, targetY);
        }
        
        // Mover hacia el waypoint
        if (dist > 0) {
            float ratio = speed / dist;
            posX += (targetX - posX) * ratio;
            posY += (targetY - posY) * ratio;
        }
        
        // Actualizar posición del enemigo
        enemy.setX((int)posX);
        enemy.setY((int)posY);
    }
    
    // Getters para debug
    public int getCurrentWaypointIndex() { return currentWaypointIndex; }
    public int getTotalWaypoints()        { return waypoints.length / 2; }
    public float getCurrentX()            { return posX; }
    public float getCurrentY()            { return posY; }
}
