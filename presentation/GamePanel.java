package presentation;

import domain.Board;
import domain.HardestGameException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class GamePanel extends JPanel implements KeyListener {

    private Board board;

    private Set<Integer> keysPressed;

    private Timer gameLoopTimer;
    private Timer secondTimer;

    private boolean isPaused = false;
    private Runnable onPause;
    private Runnable onResume;

    private Runnable onLevelComplete;
    private Runnable onTimeOut;
    private Runnable onHudUpdate;

    private static final Color COLOR_SAFE_ZONE  = new Color(144, 238, 144);
    private static final Color COLOR_ENEMY      = new Color(30, 100, 220);
    private static final Color COLOR_COIN       = new Color(255, 215, 0);
    private static final Color COLOR_BACKGROUND = Color.WHITE;
    private static final Color COLOR_WALL       = new Color(80, 80, 80);

    public GamePanel() {
        keysPressed = new HashSet<>();
        setFocusable(true);
        addKeyListener(this);
        setBackground(COLOR_BACKGROUND);
        addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    keysPressed.clear();
                }
            });
    }

    // =========================================================
    // START LEVEL — SINGLE
    // =========================================================

    public void startLevel(int level, String playerType) {
        stopTimers();
        keysPressed.clear();
        board = new Board(boardWidth(), boardHeight());
        try {
            board.loadFromFile(levelFile(level), playerType);
        } catch (HardestGameException e) {
            showError(e.getMessage());
            return;
        }
        startTimers();
        requestFocusInWindow();
        repaint();
    }

    public void startLevel(int level) {
        startLevel(level, "RED");
    }

    public void startLevel(int level, String playerType, Color borderColor) {
        stopTimers();
        keysPressed.clear();
        board = new Board(boardWidth(), boardHeight());
        try {
            board.loadFromFile(levelFile(level), playerType, borderColor);
        } catch (HardestGameException e) {
            showError(e.getMessage());
            return;
        }
        startTimers();
        requestFocusInWindow();
        repaint();
    }

    // =========================================================
    // START LEVEL — PLAYER VS PLAYER
    // =========================================================

    public void startLevelPvP(int level,
    String player1Name,
    String p1Type, Color p1Border,
    String player2Name,
    String p2Type, Color p2Border) {

        stopTimers();

        keysPressed.clear();

        board = new Board(boardWidth(), boardHeight());

        try {

            board.loadPvPMode(
                levelFile(level),
                player1Name,
                p1Type,
                p1Border,
                player2Name,
                p2Type,
                p2Border
            );

        } catch (HardestGameException e) {

            showError(e.getMessage());

            return;
        }

        startTimers();

        requestFocusInWindow();

        repaint();
    }

    // =========================================================
    // START LEVEL — PLAYER VS MACHINE RANDOM
    // =========================================================

    public void startLevelPvMAIRandom(int level,
    String playerType,
    Color borderColor) {
        stopTimers();
        keysPressed.clear();
        board = new Board(boardWidth(), boardHeight());
        try {
            board.loadPvMAIRandomMode(levelFile(level), playerType, borderColor);
        } catch (HardestGameException e) {
            showError(e.getMessage());
            return;
        }
        startTimers();
        requestFocusInWindow();
        repaint();
    }

    // =========================================================
    // START LEVEL — PLAYER VS MACHINE EXPERT
    // =========================================================

    public void startLevelPvMAIExpert(int level,
    String playerType,
    Color borderColor) {
        stopTimers();
        keysPressed.clear();
        board = new Board(boardWidth(), boardHeight());
        try {
            board.loadPvMAIExpertMode(levelFile(level), playerType, borderColor);
        } catch (HardestGameException e) {
            showError(e.getMessage());
            return;
        }
        startTimers();
        requestFocusInWindow();
        repaint();
    }

    // =========================================================
    // GAME LOOP
    // =========================================================

    private void startTimers() {

        gameLoopTimer = new Timer(16, e -> {
                    if (board == null) return;

                    if (isPaused) return;  // No actualizar si está pausado

                    processHumanMovement();  // solo mueve jugadores humanos
                    board.update();          // Board mueve IAs y resuelve colisiones
                    repaint();

                    if (onHudUpdate != null) onHudUpdate.run();

                    if (board.isLevelCompleted()) {
                        stopTimers();
                        if (onLevelComplete != null) onLevelComplete.run();
                    }
            });

        gameLoopTimer.start();

        secondTimer = new Timer(1000, e -> {
                    if (board == null) return;

                    if (isPaused) return;  // No actualizar tiempo si está pausado

                    boolean timesUp = board.tickTime();
                    if (onHudUpdate != null) onHudUpdate.run();

                    if (timesUp) {
                        stopTimers();
                        if (onTimeOut != null) onTimeOut.run();
                    }
            });

        secondTimer.start();
    }

    // =========================================================
    // MOVIMIENTO — solo jugadores humanos
    //
    // Antes este método también detectaba AIPlayer y AIPlayerExpert
    // con instanceof y los movía manualmente. Eso ya no es necesario:
    // Board.update() llama a update() en cada AIControlledPlayer,
    // la estrategia calcula la dirección, y Board aplica movePlayer().
    // GamePanel no sabe ni le importa qué estrategia usa la IA.
    // =========================================================

    private void processHumanMovement() {
        if (board == null || board.isLevelCompleted()) return;

        String gameMode = board.getGameMode();

        if ("PVSP".equals(gameMode)) {
            processPlayer1Movement();
            processPlayer2Movement();
        } else {
            processPlayer1Movement();
        }
    }

    private void processPlayer1Movement() {
        movePlayerFromKeys(0,
            keysPressed.contains(KeyEvent.VK_W),
            keysPressed.contains(KeyEvent.VK_S),
            keysPressed.contains(KeyEvent.VK_A),
            keysPressed.contains(KeyEvent.VK_D)
        );
    }

    private void processPlayer2Movement() {
        movePlayerFromKeys(1,
            keysPressed.contains(KeyEvent.VK_UP),
            keysPressed.contains(KeyEvent.VK_DOWN),
            keysPressed.contains(KeyEvent.VK_LEFT),
            keysPressed.contains(KeyEvent.VK_RIGHT)
        );
    }

    private void movePlayerFromKeys(int idx,
    boolean up, boolean down,
    boolean left, boolean right) {
        if (up    && left)  { board.movePlayer(idx, "UP_LEFT");    return; }
        if (up    && right) { board.movePlayer(idx, "UP_RIGHT");   return; }
        if (down  && left)  { board.movePlayer(idx, "DOWN_LEFT");  return; }
        if (down  && right) { board.movePlayer(idx, "DOWN_RIGHT"); return; }
        if (up)             { board.movePlayer(idx, "UP");         return; }
        if (down)           { board.movePlayer(idx, "DOWN");       return; }
        if (left)           { board.movePlayer(idx, "LEFT");       return; }
        if (right)          { board.movePlayer(idx, "RIGHT"); }
    }

    // =========================================================
    // TIMERS
    // =========================================================

    private void stopTimers() {
        if (gameLoopTimer != null) gameLoopTimer.stop();
        if (secondTimer   != null) secondTimer.stop();
    }

    public void stopGame() {
        stopTimers();
        keysPressed.clear();
        isPaused = false;
    }

    private void togglePause() {
        if (board == null || board.isLevelCompleted()) {
            return;
        }

        isPaused = !isPaused;
        repaint();

        if (isPaused) {
            if (onPause != null) onPause.run();
        } else {
            if (onResume != null) onResume.run();
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
        repaint();
    }

    public void setOnPause(Runnable r) { onPause = r; }
    public void setOnResume(Runnable r) { onResume = r; }

    // =========================================================
    // INPUT
    // =========================================================

    @Override
    public void keyPressed(KeyEvent e) {
        // Tecla P para pausar/reanudar
        if (e.getKeyCode() == KeyEvent.VK_P) {
            togglePause();
            return;
        }
        keysPressed.add(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keysPressed.remove(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    // =========================================================
    // DRAW
    // =========================================================

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (board == null) {
            drawWaitingScreen(g);
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        for (int[] z : board.getSafeZoneRects()) {
            g2.setColor(COLOR_SAFE_ZONE);
            g2.fillRect(z[0], z[1], z[2], z[3]);
        }

        for (int[] w : board.getWallRects()) {
            g2.setColor(COLOR_WALL);
            g2.fillRect(w[0], w[1], w[2], w[3]);
        }

        for (int[] c : board.getCoinRects()) {
            g2.setColor(COLOR_COIN);
            g2.fillOval(c[0], c[1], c[2], c[3]);
        }

        for (int[] e : board.getEnemyRects()) {
            g2.setColor(COLOR_ENEMY);
            g2.fillOval(e[0], e[1], e[2], e[3]);
        }

        for (int[] p : board.getPlayerRects()) {
            Color color  = new Color(clamp(p[4]), clamp(p[5]), clamp(p[6]));
            Color border = new Color(clamp(p[8]), clamp(p[9]), clamp(p[10]));

            g2.setColor(border);
            g2.fillRect(p[0] - 2, p[1] - 2, p[2] + 4, p[3] + 4);

            g2.setColor(color);
            g2.fillRect(p[0], p[1], p[2], p[3]);

            if (p[7] == 1) {
                g2.setColor(Color.YELLOW);
                g2.setStroke(new BasicStroke(3));
                g2.drawRect(p[0] - 3, p[1] - 3, p[2] + 6, p[3] + 6);
                g2.setStroke(new BasicStroke(1));
            }
        }

        // Dibujar pantalla de pausa si está pausado
        if (isPaused) {
            drawPauseScreen(g2);
        }
    }

    private void drawPauseScreen(Graphics2D g2) {
        // Fondo semi-transparente
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Texto de pausa
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        String pauseText = "PAUSA";
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(pauseText)) / 2;
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(pauseText, x, y);

        // Instrucciones
        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        String instructionText = "Presiona P para reanudar";
        fm = g2.getFontMetrics();
        x = (getWidth() - fm.stringWidth(instructionText)) / 2;
        y = y + 50;
        g2.drawString(instructionText, x, y);
    }

    private int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }

    private void drawWaitingScreen(Graphics g) {
        g.setColor(Color.GRAY);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Selecciona un nivel para comenzar", 80, getHeight() / 2);
    }

    // =========================================================
    // HELPERS PRIVADOS
    // =========================================================

    private int boardWidth()  { return getWidth()  > 0 ? getWidth()  : 600; }

    private int boardHeight() { return getHeight() > 0 ? getHeight() : 400; }

    private File levelFile(int level) {
        return new File("levels/level" + level + ".txt");
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(null, msg,
            "Error al cargar nivel", JOptionPane.ERROR_MESSAGE);
    }

    // =========================================================
    // CALLBACKS
    // =========================================================

    public void setOnLevelComplete(Runnable r) { onLevelComplete = r; }

    public void setOnTimeOut(Runnable r)       { onTimeOut = r; }

    public void setOnHudUpdate(Runnable r)     { onHudUpdate = r; }

    // =========================================================
    // HUD
    // =========================================================

    public int  getDeaths()         { return board != null ? board.getTotalDeaths()   : 0; }

    public int  getCoinsCollected() { return board != null ? board.getCoinsCollected(): 0; }

    public int getPlayerDeaths(String playerName) {

        if(board == null) {
            return 0;
        }

        return board.getPlayerDeaths(playerName);
    }

    public int getPlayerCoins(String playerName) {

        if(board == null) {
            return 0;
        }

        return board.getPlayerCoins(playerName);
    }

    public int  getTotalCoins()     { return board != null ? board.getTotalCoins()    : 0; }

    public int  getTimeRemaining()  { return board != null ? board.getTimeRemaining() : 0; }

    public String getPvPWinner() {
        return board != null ? board.getPvPWinner() : null;
    }

    public void addDeath() {
        if (board != null) board.getScoreController().addDeath();
    }
}