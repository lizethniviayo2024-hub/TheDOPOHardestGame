package domain;

import java.awt.Color;

/**
 * Clase base para todos los elementos coleccionables o interactivos del tablero
 * (monedas, fuentes de vida, bombas, monedas skin, etc.).
 *
 * <p>
 * Extiende {@link GameEntity} e implementa {@link Collidable} para que el
 * {@link CollisionController} los procese de forma uniforme, sin importar el
 * tipo concreto.
 *
 * <p>
 * Para agregar un elemento nuevo basta con:
 * <ol>
 * <li>Crear una subclase de {@code Collectible}.</li>
 * <li>Implementar {@link #onPlayerCollision(Player, ScoreController)} con el
 * efecto deseado.</li>
 * <li>Implementar {@link #getColor()} para que {@code GamePanel} lo pinte.</li>
 * <li>Registrar el token en {@link LevelLoader} con {@code register(...)}.</li>
 * </ol>
 * No es necesario tocar {@code GameState}, {@code Board} ni {@code GamePanel}.
 */
public abstract class Collectible extends GameEntity implements Collidable {

	/**
	 * Indica si el elemento sigue activo en el tablero. Un elemento inactivo no se
	 * pinta ni procesa colisiones.
	 */
	protected boolean active;

	/**
	 * Indica si este elemento debe recolectarse para completar el nivel (true en
	 * monedas, false en bombas y fuentes de vida).
	 */
	private final boolean requiredForCompletion;

	/**
	 * @param x                     posición X
	 * @param y                     posición Y
	 * @param width                 ancho
	 * @param height                alto
	 * @param requiredForCompletion true si el nivel exige recolectarlo para
	 *                              completarse
	 */
	protected Collectible(int x, int y, int width, int height, boolean requiredForCompletion) {
		super(x, y, width, height);
		this.active = true;
		this.requiredForCompletion = requiredForCompletion;
	}

	/**
	 * Actualiza el estado interno del elemento en cada tick. La implementación por
	 * defecto no hace nada; las subclases pueden sobrescribirlo para efectos
	 * animados, temporizadores, etc.
	 */
	@Override
	public void update() {
	}

	/**
	 * Color con el que {@code GamePanel} pinta este elemento. Cada subclase define
	 * su propio color sin tocar la capa de presentación.
	 *
	 * @return color de relleno del elemento
	 */
	public abstract Color getColor();

	/**
	 * Forma de dibujo. Por defecto es óvalo; las subclases pueden devolver
	 * {@code Shape.RECT} para dibujarse como rectángulo.
	 */
	public enum Shape {
		OVAL, RECT
	}

	/**
	 * Forma con la que {@code GamePanel} dibuja este elemento. Por defecto oval;
	 * subclases pueden sobreescribir.
	 *
	 * @return forma de dibujo
	 */
	public Shape getShape() {
		return Shape.OVAL;
	}

	/** @return true si el elemento sigue visible y activo en el tablero */
	public boolean isActive() {
		return active;
	}

	/**
	 * Desactiva el elemento (desaparece del tablero). Las subclases llaman a esto
	 * cuando el efecto se ha aplicado.
	 */
	protected void consume() {
		active = false;
	}

	/** @return true si este elemento debe recolectarse para completar el nivel */
	public boolean isRequiredForCompletion() {
		return requiredForCompletion;
	}

	/**
	 * Reactiva el elemento (vuelve a aparecer en el tablero). Se usa cuando el
	 * nivel se reinicia por timeout.
	 */
	public void reset() {
		active = true;
	}
}
