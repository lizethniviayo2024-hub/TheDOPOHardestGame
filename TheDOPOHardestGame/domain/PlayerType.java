package domain;

import java.awt.Color;

/**
 * Tipos de jugador disponibles según el enunciado.
 * Cada tipo define velocidad, tamaño y habilidades especiales.
 *
 *  Rojo  (Blinky): velocidad 1x, tamaño 1x, sin habilidades especiales.
 *  Azul  (Inky):   velocidad 1.5x, tamaño 1.5x.
 *  Verde (Clyde):  velocidad 1x, absorbe el primer golpe sin morir (baja velocidad a 0.7x).
 */
public enum PlayerType {

    RED("Blinky", new Color(220, 50, 50),   1.0f, 1.0f),
    BLUE("Inky",  new Color(50, 100, 220),  1.5f, 1.5f),
    GREEN("Clyde",new Color(50, 180, 50),   1.0f, 1.0f);

    private final String skinName;
    private final Color  color;
    private final float  speedMultiplier;
    private final float  sizeMultiplier;

    PlayerType(String skinName, Color color, float speedMultiplier, float sizeMultiplier) {
        this.skinName        = skinName;
        this.color           = color;
        this.speedMultiplier = speedMultiplier;
        this.sizeMultiplier  = sizeMultiplier;
    }

    public String getSkinName()       { return skinName; }
    public Color  getColor()          { return color; }
    public float  getSpeedMultiplier(){ return speedMultiplier; }
    public float  getSizeMultiplier() { return sizeMultiplier; }
}