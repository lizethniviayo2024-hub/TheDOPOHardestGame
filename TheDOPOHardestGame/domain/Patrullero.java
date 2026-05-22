package domain;

/**
 * Enemigo Patrullero que sigue una ruta geométrica predefinida (circular, rectangular, etc).
 * El patrullero recorre un camino configurable de manera continua en ciclos.
 * 
 * Tipos de rutas disponibles:
 * - CIRCULAR: Se mueve en círculo
 * - RECTANGULAR: Se mueve en rectángulo/cuadrado
 * - TRIANGULAR: Se mueve en triángulo
 * - PERSONALIZADO: Ruta libre con waypoints arbitrarios
 */
public class Patrullero extends Enemy {
    
    private PatrolMovement patrolMovement;
    private RouteType routeType;
    
    /**
     * Tipos de rutas disponibles para el patrullero.
     */
    public enum RouteType {
        CIRCULAR,
        RECTANGULAR,
        TRIANGULAR,
        PERSONALIZADO
    }
    
    /**
     * Constructor básico del Patrullero.
     * 
     * @param x         Posición X inicial
     * @param y         Posición Y inicial
     * @param size      Tamaño (ancho y alto)
     * @param speed     Velocidad de movimiento
     */
    public Patrullero(int x, int y, int size, float speed) {
        super(x, y, size, speed);
    }
    
    /**
     * Crea un patrullero que sigue una ruta circular.
     * 
     * @param x         Posición X inicial
     * @param y         Posición Y inicial
     * @param size      Tamaño del enemigo
     * @param speed     Velocidad de movimiento
     * @param centerX   Centro X del círculo
     * @param centerY   Centro Y del círculo
     * @param radius    Radio del círculo
     * @return          Patrullero configurado con ruta circular
     */
    public static Patrullero circular(int x, int y, int size, float speed, 
                                      int centerX, int centerY, int radius) {
        Patrullero patrullero = new Patrullero(x, y, size, speed);
        patrullero.patrolMovement = PatrolMovement.circular(patrullero, centerX, centerY, radius, speed);
        patrullero.routeType = RouteType.CIRCULAR;
        patrullero.setMovement(patrullero.patrolMovement);
        return patrullero;
    }
    
    /**
     * Versión con speed como entero (para facilitar parseo desde archivos).
     */
    public static Patrullero circular(int x, int y, int size, int speed, 
                                      int centerX, int centerY, int radius) {
        return circular(x, y, size, (float) speed, centerX, centerY, radius);
    }
    
    /**
     * Crea un patrullero que sigue una ruta rectangular.
     * 
     * @param x         Posición X inicial
     * @param y         Posición Y inicial
     * @param size      Tamaño del enemigo
     * @param speed     Velocidad de movimiento
     * @param x1        Esquina superior izquierda X
     * @param y1        Esquina superior izquierda Y
     * @param x2        Esquina inferior derecha X
     * @param y2        Esquina inferior derecha Y
     * @return          Patrullero configurado con ruta rectangular
     */
    public static Patrullero rectangular(int x, int y, int size, float speed,
                                         int x1, int y1, int x2, int y2) {
        Patrullero patrullero = new Patrullero(x, y, size, speed);
        patrullero.patrolMovement = PatrolMovement.rectangular(patrullero, x1, y1, x2, y2, speed);
        patrullero.routeType = RouteType.RECTANGULAR;
        patrullero.setMovement(patrullero.patrolMovement);
        return patrullero;
    }
    
    /**
     * Versión con speed como entero (para facilitar parseo desde archivos).
     */
    public static Patrullero rectangular(int x, int y, int size, int speed,
                                         int x1, int y1, int x2, int y2) {
        return rectangular(x, y, size, (float) speed, x1, y1, x2, y2);
    }
    
    /**
     * Crea un patrullero que sigue una ruta triangular.
     * 
     * @param x         Posición X inicial
     * @param y         Posición Y inicial
     * @param size      Tamaño del enemigo
     * @param speed     Velocidad de movimiento
     * @param x1        Vértice 1 X
     * @param y1        Vértice 1 Y
     * @param x2        Vértice 2 X
     * @param y2        Vértice 2 Y
     * @param x3        Vértice 3 X
     * @param y3        Vértice 3 Y
     * @return          Patrullero configurado con ruta triangular
     */
    public static Patrullero triangular(int x, int y, int size, float speed,
                                        int x1, int y1, int x2, int y2, int x3, int y3) {
        Patrullero patrullero = new Patrullero(x, y, size, speed);
        int[] waypoints = {x1, y1, x2, y2, x3, y3};
        patrullero.patrolMovement = new PatrolMovement(patrullero, speed, waypoints);
        patrullero.routeType = RouteType.TRIANGULAR;
        patrullero.setMovement(patrullero.patrolMovement);
        return patrullero;
    }
    
    /**
     * Versión con speed como entero (para facilitar parseo desde archivos).
     */
    public static Patrullero triangular(int x, int y, int size, int speed,
                                        int x1, int y1, int x2, int y2, int x3, int y3) {
        return triangular(x, y, size, (float) speed, x1, y1, x2, y2, x3, y3);
    }
    
    /**
     * Crea un patrullero con una ruta personalizada (waypoints arbitrarios).
     * 
     * @param x         Posición X inicial
     * @param y         Posición Y inicial
     * @param size      Tamaño del enemigo
     * @param speed     Velocidad de movimiento
     * @param waypoints Array con coordenadas: [x1, y1, x2, y2, x3, y3, ...]
     * @return          Patrullero configurado con ruta personalizada
     */
    public static Patrullero personalizado(int x, int y, int size, float speed,
                                           int... waypoints) {
        Patrullero patrullero = new Patrullero(x, y, size, speed);
        patrullero.patrolMovement = new PatrolMovement(patrullero, speed, waypoints);
        patrullero.routeType = RouteType.PERSONALIZADO;
        patrullero.setMovement(patrullero.patrolMovement);
        return patrullero;
    }
    
    /**
     * Versión con speed como entero (para facilitar parseo desde archivos).
     */
    public static Patrullero personalizado(int x, int y, int size, int speed,
                                           int... waypoints) {
        return personalizado(x, y, size, (float) speed, waypoints);
    }
    
    /**
     * Obtiene el tipo de ruta del patrullero.
     */
    public RouteType getRouteType() {
        return routeType;
    }
    
    /**
     * Obtiene el movimiento de patrulla.
     */
    public PatrolMovement getPatrolMovement() {
        return patrolMovement;
    }
    
    /**
     * Cambia la ruta del patrullero a una nueva ruta circular.
     * 
     * @param centerX   Centro X del círculo
     * @param centerY   Centro Y del círculo
     * @param radius    Radio del círculo
     */
    public void changeRouteToCircular(int centerX, int centerY, int radius) {
        this.patrolMovement = PatrolMovement.circular(this, centerX, centerY, radius, this.getSpeed());
        this.routeType = RouteType.CIRCULAR;
        this.setMovement(this.patrolMovement);
    }
    
    /**
     * Cambia la ruta del patrullero a una nueva ruta rectangular.
     * 
     * @param x1 Esquina superior izquierda X
     * @param y1 Esquina superior izquierda Y
     * @param x2 Esquina inferior derecha X
     * @param y2 Esquina inferior derecha Y
     */
    public void changeRouteToRectangular(int x1, int y1, int x2, int y2) {
        this.patrolMovement = PatrolMovement.rectangular(this, x1, y1, x2, y2, this.getSpeed());
        this.routeType = RouteType.RECTANGULAR;
        this.setMovement(this.patrolMovement);
    }
    
    /**
     * Cambia la ruta del patrullero a una nueva ruta personalizada.
     * 
     * @param waypoints Array con coordenadas: [x1, y1, x2, y2, ...]
     */
    public void changeRouteToPersonalizado(int... waypoints) {
        this.patrolMovement = new PatrolMovement(this, this.getSpeed(), waypoints);
        this.routeType = RouteType.PERSONALIZADO;
        this.setMovement(this.patrolMovement);
    }
    
    /**
     * Obtiene información del patrullero para debugging.
     */
    @Override
    public String toString() {
        if (patrolMovement == null) {
            return String.format("Patrullero[pos=(%d, %d), size=%d, speed=%.1f, ruta=SIN_CONFIGURAR]",
                    getX(), getY(), getWidth(), getSpeed());
        }
        return String.format("Patrullero[pos=(%d, %d), size=%d, speed=%.1f, ruta=%s, waypoint=%d/%d]",
                getX(), getY(), getWidth(), getSpeed(), routeType,
                patrolMovement.getCurrentWaypointIndex() + 1,
                patrolMovement.getTotalWaypoints());
    }
}
