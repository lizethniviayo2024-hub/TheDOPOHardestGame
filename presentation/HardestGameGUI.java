package presentation;
import domain.Board; 
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;

public class HardestGameGUI extends JFrame {

    // Panels de cada pantalla
    private JPanel container;
    private CardLayout cardLayout;

    // Pantalla 1 - Menu
    private JButton btnPlayGame;
    private JButton btnLeaderBoard;
    private JButton btnExitGame;
    private int currentLevel = 1;
    //para el juego
    private GamePanel gamePanel;
    private JLabel lblDeaths;
    private JLabel lblCoins;
    private JLabel lblTime;
    public HardestGameGUI() {
        setTitle("The DOPO Hardest Game");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        prepareElements();
        prepareActions();
    }

    private void prepareElements() {
        setSize(800, 550);
        setLocationRelativeTo(null);

        // Panel externo BLANCO (marco)
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(Color.WHITE);
        outerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); // grosor del borde blanco

        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        container.add(prepareElementsMenu(), "MENU");

        outerPanel.add(container, BorderLayout.CENTER);
        add(outerPanel);

        cardLayout.show(container, "MENU");
        container.add(prepareElementsMode(), "MODE");
        container.add(prepareElementsLEADERBOARD(), "LEADERBOARD");
        container.add(prepareElementsONEPLAY(), "ONEPLAY");
        container.add(prepareElementsLEVELS(), "LEVELS");
    }

    private JPanel prepareElementsMenu() {
        JPanel menuPanel = new JPanel(new BorderLayout());

        // 
        menuPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));

        // NORTH - Titulo
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(180, 190, 230));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(40, 30, 0, 30));
        
        JLabel lblSubtitulo = new JLabel("The DOPO...");
        lblSubtitulo.setFont(new Font("Arial Black", Font.BOLD, 18));
        lblSubtitulo.setForeground(new Color(50, 50, 100));
        
        JLabel lblTitulo = new JLabel("HARDEST GAME");
        lblTitulo.setFont(new Font("Arial Black", Font.BOLD, 72));
        lblTitulo.setForeground(new Color(70, 120, 200));

        titlePanel.add(lblSubtitulo, BorderLayout.NORTH);
        titlePanel.add(lblTitulo, BorderLayout.CENTER);
        menuPanel.add(titlePanel, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        btnPanel.setBackground(new Color(180, 190, 230));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(40, 80, 60, 80));

        btnPlayGame    = createMenuButton("PLAY GAME",    Color.RED);
        btnLeaderBoard = createMenuButton("LEADER BOARD", new Color(70, 120, 200));

        btnPanel.add(btnPlayGame);
        btnPanel.add(btnLeaderBoard);
        btnPanel.add(btnExitGame = createMenuButton("EXIT GAME", new Color(34, 139, 34)));

        menuPanel.add(btnPanel, BorderLayout.CENTER);

        return menuPanel;
    }

    private JPanel prepareElementsMode() {
        JPanel modePanel = new JPanel(new BorderLayout());
        modePanel.setBackground(new Color(100, 100, 230));
        modePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));

        // TÍTULO
        JLabel lblTitle = new JLabel("SELECT MODE", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial Black", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        modePanel.add(lblTitle, BorderLayout.NORTH);

        // BOTONES
        JPanel btnPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        btnPanel.setBackground(new Color(180, 190, 230));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(40, 200, 40, 200));

        JButton btnPlayer = createMenuButton("PLAYER", Color.RED);
        JButton btnPVP    = createMenuButton("Player vs Player ", Color.BLUE);
        JButton btnPVM    = createMenuButton("Player vs Machine", Color.GREEN);
        JButton btnBack   = createMenuButton("BACK", Color.BLACK);

        btnPanel.add(btnPlayer);
        btnPanel.add(btnPVP);
        btnPanel.add(btnPVM);
        btnPanel.add(btnBack);

        modePanel.add(btnPanel, BorderLayout.CENTER);

        // ACCIONES
        btnBack.addActionListener(e -> cardLayout.show(container, "MENU"));
        btnPlayer.addActionListener(e -> cardLayout.show(container, "LEVELS"));

        btnPlayer.addActionListener(e -> cardLayout.show(container, "ONEPLAY"));

        return modePanel;
    }

    private JPanel prepareElementsLEADERBOARD() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(100, 100, 230));
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));

        //  TÍTULO
        JLabel title = new JLabel("LEADERBOARD", JLabel.CENTER);
        title.setFont(new Font("Arial Black", Font.BOLD, 30));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        panel.add(title, BorderLayout.NORTH);

        // PANEL SUPERIOR (input + botón)
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(180, 190, 230));

        JLabel lblName = new JLabel("Name:");
        JTextField txtName = new JTextField(15);
        JButton btnAdd = new JButton("Add");

        topPanel.add(lblName);
        topPanel.add(txtName);
        topPanel.add(btnAdd);

        panel.add(topPanel, BorderLayout.SOUTH);

        //  TABLA
        String[] columns = {"Player", "Deaths"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(scrollPane, BorderLayout.CENTER);

        // BOTÓN BACK
        JButton btnBack = new JButton("BACK");
        panel.add(btnBack, BorderLayout.WEST);

        btnBack.addActionListener(e -> 
                cardLayout.show(container, "MENU")
        );

        return panel;
    }

    private JPanel prepareElementsLEVELS() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(100, 100, 230));
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));

        // TÍTULO
        JLabel title = new JLabel("SELECT LEVEL", JLabel.CENTER);
        title.setFont(new Font("Arial Black", Font.BOLD, 30));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        panel.add(title, BorderLayout.NORTH);

        // BOTONES DE NIVELES
        JPanel grid = new JPanel(new GridLayout(2, 3, 20, 20));
        grid.setBackground(new Color(180, 190, 230));
        grid.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));

        JButton lvl1 = createMenuButton("LEVEL 1", Color.RED);
        JButton lvl2 = createMenuButton("LEVEL 2", Color.BLUE);
        JButton lvl3 = createMenuButton("LEVEL 3", Color.GREEN);

        grid.add(lvl1);
        grid.add(lvl2);
        grid.add(lvl3);

        panel.add(grid, BorderLayout.CENTER);

        // BACK
        JButton btnBack = createMenuButton("BACK", Color.BLACK);
        panel.add(btnBack, BorderLayout.SOUTH);

        // ACCIONES
        btnBack.addActionListener(e -> cardLayout.show(container, "MODE"));

        lvl1.addActionListener(e -> {
                    currentLevel = 1;
                    cardLayout.show(container, "ONEPLAY");
                      SwingUtilities.invokeLater(() -> gamePanel.startLevel(currentLevel));
            });

        lvl2.addActionListener(e -> {
                    currentLevel = 2;
                    cardLayout.show(container, "ONEPLAY");
                      SwingUtilities.invokeLater(() -> gamePanel.startLevel(currentLevel));
            });

        lvl3.addActionListener(e -> {
                    currentLevel = 3;
                    cardLayout.show(container, "ONEPLAY");
                      SwingUtilities.invokeLater(() -> gamePanel.startLevel(currentLevel));
            });

        return panel;
    }

    private JPanel prepareElementsONEPLAY() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(100, 100, 230));
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));

        //  NORTH
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(new Color(180, 190, 230));

        JLabel title = new JLabel("LEVEL " + currentLevel, JLabel.CENTER);
        title.setFont(new Font("Arial Black", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        // muertes, monedas, tiempo
        JPanel hudPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 5));
        hudPanel.setBackground(new Color(230, 190, 190));

        lblDeaths = new JLabel("Deaths: 0");
        lblDeaths.setFont(new Font("Arial Black", Font.BOLD, 14));

        lblCoins = new JLabel("Coins: 0/0");
        lblCoins.setFont(new Font("Arial Black", Font.BOLD, 14));

        lblTime = new JLabel("Time: 60");
        lblTime.setFont(new Font("Arial Black", Font.BOLD, 14));

        hudPanel.add(lblDeaths);
        hudPanel.add(lblCoins);
        hudPanel.add(lblTime);

        northPanel.add(title, BorderLayout.NORTH);
        northPanel.add(hudPanel, BorderLayout.CENTER);
        panel.add(northPanel, BorderLayout.NORTH);

        // CENTER: GamePanel
        gamePanel = new GamePanel();
        gamePanel.setPreferredSize(new Dimension(500, 300));
        gamePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));

        // actualizar HUD desde el game loop
        gamePanel.setOnHudUpdate(() -> SwingUtilities.invokeLater(() -> {
                            lblDeaths.setText("Deaths: " + gamePanel.getDeaths());
                            lblCoins.setText("Coins: " + gamePanel.getCoinsCollected() + "/" + gamePanel.getTotalCoins());
                            lblTime.setText("Time: " + gamePanel.getTimeRemaining());
                    }));

        // nivel completado
        gamePanel.setOnLevelComplete(() -> SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this,
                                "¡Nivel completado! Muertes: " + gamePanel.getDeaths(),
                                "¡Ganaste!", JOptionPane.INFORMATION_MESSAGE);
                            cardLayout.show(container, "LEVELS");
                    }));

        //  tiempo agotado
        gamePanel.setOnTimeOut(() -> SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this,
                                "¡Se acabó el tiempo! Muertes: " + gamePanel.getDeaths(),
                                "Tiempo agotado", JOptionPane.WARNING_MESSAGE);
                            cardLayout.show(container, "LEVELS");
                    }));

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(new Color(100, 100, 230));
        centerWrapper.add(gamePanel);
        panel.add(centerWrapper, BorderLayout.CENTER);

        // SOUTH
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(new Color(180, 190, 230));

        JButton btnBack = new JButton("BACK");
        btnBack.addActionListener(e -> {
                    gamePanel.stopGame();
                    cardLayout.show(container, "LEVELS");
            });

        southPanel.add(btnBack, BorderLayout.EAST);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Metodo auxiliar para crear botones 
    private JButton createMenuButton(String texto, Color color) {
        JButton btn = new OutlineButton(texto, color, Color.BLACK, 2);

        btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
                }

                public void mouseExited(MouseEvent e) {
                    btn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
                }
            });

        return btn;
    }

    private void prepareActions() {
        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) { exit(); }
            });

        btnExitGame.addActionListener(e -> exit());

        btnPlayGame.addActionListener(e -> 
                cardLayout.show(container, "MODE"));

        btnLeaderBoard.addActionListener(e -> 
                cardLayout.show(container, "LEADERBOARD"));

    }

    private void exit() {
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea salir?",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        HardestGameGUI gui = new HardestGameGUI();
        gui.setVisible(true);
    }
}