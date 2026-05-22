========================================================================
THE DOPO HARDEST GAME - PROYECTO FINAL
========================================================================

PROJECT TITLE: The DOPO Hardest Game

PURPOSE OF PROJECT:
Implementar un juego tipo "The Hardest Game" en Java con:
- Movimiento del jugador en 8 direcciones (4 cardinales + 4 diagonales)
- Enemigos con movimiento horizontal y vertical
- Sistema de colisiones con paredes, bordes y enemigos
- Recolección de monedas
- Zonas seguras (inicial, intermedia y final)
- Sistema de vidas y contador de monedas
- Múltiples niveles programables

VERSION: 1.0 - Entrega Parcial Final

HOW TO START THIS PROJECT:
1. Abrir el proyecto en Eclipse (versión 4.31+)
2. Asegurar que se está usando Java 21
3. Compilar el proyecto
4. Ejecutar: presentation.HardestGameGUI

PROJECT STRUCTURE:
/domain
  - Board.java: Controlador central del juego
  - Player.java: Lógica del jugador y movimiento
  - Enemy.java: Lógica de enemigos
  - HorizontalMovement.java: Movimiento horizontal de enemigos
  - VerticalMovement.java: Movimiento vertical de enemigos
  - Coin.java: Sistema de monedas
  - SafeZone.java: Zonas seguras
  - Walls.java: Paredes
  - CollisionController.java: Detección de colisiones
  - ScoreController.java: Gestión de puntuación
  - LevelLoader.java & EntityParser.java: Carga de niveles
  - GameEntity.java: Clase base de entidades
  - Movement.java: Interfaz de movimiento

/presentation
  - HardestGameGUI.java: Menú principal y gestor de niveles
  - GamePanel.java: Panel de juego y renderizado
  - OutlineButton.java & OutlineLabel.java: Componentes UI personalizados

/levels
  - level1.txt a level4.txt: Definición de niveles

AUTHORS:
Grupo 7 - DOPO 2026

USER INSTRUCTIONS:
1. MENÚ PRINCIPAL:
   - Seleccionar un nivel (1-4) con los botones
   - Ver información de muertes y monedas recolectadas

2. CONTROLES DE JUEGO:
   - Movimiento horizontal: Flechas izquierda/derecha o A/D
   - Movimiento vertical: Flechas arriba/abajo o W/S
   - Movimiento diagonal: Presionar 2 teclas simultáneamente
     * Arriba+Izquierda = Diagonal arriba-izquierda
     * Arriba+Derecha = Diagonal arriba-derecha
     * Abajo+Izquierda = Diagonal abajo-izquierda
     * Abajo+Derecha = Diagonal abajo-derecha

3. OBJETIVOS DEL JUEGO:
   - Recolectar TODAS las monedas
   - Evitar colisiones con enemigos
   - Llegar a la zona segura final (verde)
   - Completar antes de que se agote el tiempo

4. MECÁNICAS:
   - Si chocas con enemigo: mueres (+1 muerte) y reapareces en inicio
   - Si toca zona segura intermedia: punto de respawn
   - Las paredes bloquean el movimiento (en cualquier dirección)
   - Los bordes del juego limitan el movimiento de bordes

5. NIVELES:
   - Nivel 1: Tutorial básico (horizontal)
   - Nivel 2: Movimiento diagonal obligatorio
   - Nivel 3: Movimiento vertical complejo
   - Nivel 4: Desafío combinado (recomendado intentar último)

========================================================================
