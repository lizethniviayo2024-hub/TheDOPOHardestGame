package presentation;

import domain.Board;
import domain.HardestGameException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class GamePanel extends JPanel implements KeyListener {

    private Board board;
    private Set<Integer> keysPressed = new HashSet<>();

    private Timer gameLoopTimer;
    private Timer secondTimer;

    private Runnable onLevelComplete;
    private Runnable onTimeOut;
    private Runnable onHudUpdate;

    private static final Color COLOR_SAFE_ZONE  = new Color(144, 238, 144);
    private static final Color COLOR_PLAYER     = Color.RED;
    private static final Color COLOR_ENEMY      = new Color(30, 100, 220);
    private static final Color COLOR_COIN       = new Color(255, 215, 0);
    private static final Color COLOR_BACKGROUND = Color.WHITE;
    private static final Color COLOR_WALL       = new Color(80, 80, 80);

    public GamePanel() {
        setFocusable(true);
        addKeyListener(this);
        setBackground(COLOR_BACKGROUND);
    }

    public void startLevel(int level) {
        stopTimers();

        int w = getWidth()  > 0 ? getWidth()  : 600;
        int h = getHeight() > 0 ? getHeight() : 400;

        board = new Board(w, h);

        File levelFile = new File("levels/level" + level + ".txt");
        try {
            board.loadFromFile(levelFile);
        } catch (HardestGameException e) {
            JOptionPane.showMessageDialog(null,
                e.getMessage(), "Error al cargar nivel",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        startTimers();
        requestFocusInWindow();
        repaint();
        System.out.println("Panel size: " + w + " x " + h);
    }

    public void stopGame() { stopTimers(); }

    public void setOnLevelComplete(Runnable r) { this.onLevelComplete = r; }
    public void setOnTimeOut(Runnable r)       { this.onTimeOut = r; }
    public void setOnHudUpdate(Runnable r)     { this.onHudUpdate = r; }

    private void startTimers() {
        gameLoopTimer = new Timer(16, e -> {
            if (board == null) return;
            
            // Procesar teclas presionadas simultáneamente
            processMovement();
            
            board.update();
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
            boolean timesUp = board.tickTime();
            if (onHudUpdate != null) onHudUpdate.run();
            if (timesUp) {
                stopTimers();
                if (onTimeOut != null) onTimeOut.run();
            }
        });
        secondTimer.start();
    }

    /**
     * Procesa las teclas presionadas simultáneamente para detectar movimiento diagonal.
     */
    private void processMovement() {
        if (keysPressed.isEmpty()) return;
        
        boolean up    = keysPressed.contains(KeyEvent.VK_UP)    || keysPressed.contains(KeyEvent.VK_W);
        boolean down  = keysPressed.contains(KeyEvent.VK_DOWN)  || keysPressed.contains(KeyEvent.VK_S);
        boolean left  = keysPressed.contains(KeyEvent.VK_LEFT)  || keysPressed.contains(KeyEvent.VK_A);
        boolean right = keysPressed.contains(KeyEvent.VK_RIGHT) || keysPressed.contains(KeyEvent.VK_D);
        
        // Determinar dirección (diagonal tiene prioridad)
        if (up && left) {
            board.movePlayer("UP_LEFT");
        } else if (up && right) {
            board.movePlayer("UP_RIGHT");
        } else if (down && left) {
            board.movePlayer("DOWN_LEFT");
        } else if (down && right) {
            board.movePlayer("DOWN_RIGHT");
        } else if (up) {
            board.movePlayer("UP");
        } else if (down) {
            board.movePlayer("DOWN");
        } else if (left) {
            board.movePlayer("LEFT");
        } else if (right) {
            board.movePlayer("RIGHT");
        }
    }

    private void stopTimers() {
        if (gameLoopTimer != null) gameLoopTimer.stop();
        if (secondTimer   != null) secondTimer.stop();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (board == null) return;
        int keyCode = e.getKeyCode();
        
        // Agregar tecla a las presionadas
        if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W ||
            keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S ||
            keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A ||
            keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
            keysPressed.add(keyCode);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        keysPressed.remove(keyCode);
    }

    @Override 
    public void keyTyped(KeyEvent e) {}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (board == null) {
            drawWaitingScreen(g);
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(COLOR_BACKGROUND);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Zonas seguras
        for (int[] z : board.getSafeZoneRects()) {
            g2.setColor(COLOR_SAFE_ZONE);
            g2.fillRect(z[0], z[1], z[2], z[3]);
            g2.setColor(new Color(50, 160, 50));
            g2.drawRect(z[0], z[1], z[2], z[3]);
        }

        // Paredes
        for (int[] w : board.getWallRects()) {
            g2.setColor(COLOR_WALL);
            g2.fillRect(w[0], w[1], w[2], w[3]);
        }

        // Monedas
        for (int[] c : board.getCoinRects()) {
            g2.setColor(COLOR_COIN);
            g2.fillOval(c[0], c[1], c[2], c[3]);
            g2.setColor(new Color(200, 160, 0));
            g2.drawOval(c[0], c[1], c[2], c[3]);
        }

        // Enemigos
        for (int[] e : board.getEnemyRects()) {
            g2.setColor(COLOR_ENEMY);
            g2.fillOval(e[0], e[1], e[2], e[3]);
        }

        // Jugador
        int[] p = board.getPlayerRect();
        if (p != null) {
            g2.setColor(COLOR_PLAYER);
            g2.fillRect(p[0], p[1], p[2], p[3]);
            g2.setColor(Color.DARK_GRAY);
            g2.drawRect(p[0], p[1], p[2], p[3]);
        }

        if (board.isLevelCompleted()) {
            drawMessage(g2, "¡NIVEL COMPLETADO!", new Color(0, 180, 0));
        }
    }

    private void drawWaitingScreen(Graphics g) {
        g.setColor(Color.GRAY);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Selecciona un nivel para comenzar", 80, getHeight() / 2);
    }

    private void drawMessage(Graphics2D g2, String msg, Color color) {
        g2.setFont(new Font("Arial Black", Font.BOLD, 28));
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(msg)) / 2;
        int y = getHeight() / 2;
        g2.setColor(Color.BLACK);
        g2.drawString(msg, x + 2, y + 2);
        g2.setColor(color);
        g2.drawString(msg, x, y);
    }

    public int getDeaths()         { return board != null ? board.getTotalDeaths()    : 0; }
    public int getCoinsCollected() { return board != null ? board.getCoinsCollected() : 0; }
    public int getTotalCoins()     { return board != null ? board.getTotalCoins()     : 0; }
    public int getTimeRemaining()  { return board != null ? board.getTimeRemaining()  : 0; }
}