package presentation;

import domain.PlayerType;
import javax.swing.*;
import java.awt.*;

/**
 * Diálogo para seleccionar el TIPO de jugador (skin).
 * Muestra los 3 tipos definidos en el enunciado: Blinky (RED), Inky (BLUE), Clyde (GREEN).
 * Cada botón describe claramente las estadísticas del skin para que el jugador elija informado.
 */
public class PlayerTypeSelectorDialog extends JDialog {

    private PlayerType selectedType;
    private boolean confirmed;

    public PlayerTypeSelectorDialog(Frame owner, String title) {
        super(owner, title, true);
        this.selectedType = PlayerType.RED;  // default
        this.confirmed = false;
        prepareUI();
    }

    private void prepareUI() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(getOwner());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(30, 30, 50));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título
        JLabel lblTitle = new JLabel("Choose your character:", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial Black", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Panel de los 3 skins
        JPanel skinsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        skinsPanel.setBackground(new Color(30, 30, 50));

        skinsPanel.add(buildSkinCard(
            PlayerType.RED,
            "BLINKY",
            new String[]{
                "Speed: normal (1x)",
                "Size:  normal (1x)",
                "No special ability"
            }
        ));

        skinsPanel.add(buildSkinCard(
            PlayerType.BLUE,
            "INKY",
            new String[]{
                "Speed: fast (1.5x)",
                "Size:  large (1.5x)",
                "No special ability"
            }
        ));

        skinsPanel.add(buildSkinCard(
            PlayerType.GREEN,
            "CLYDE",
            new String[]{
                "Speed: normal (1x)",
                "Size:  normal (1x)",
                "Absorbs 1st hit \u2192 slows to 0.7x"
            }
        ));

        mainPanel.add(skinsPanel, BorderLayout.CENTER);

        // Cancel
        JButton btnCancel = new JButton("Cancel");
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setBackground(new Color(80, 80, 80));
        btnCancel.setFocusPainted(false);
        btnCancel.addActionListener(e -> dispose());

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.setBackground(new Color(30, 30, 50));
        southPanel.add(btnCancel);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        add(mainPanel);
        pack();
        setSize(520, 260);
    }

    /**
     * Construye una tarjeta visual para cada skin.
     * Muestra un cuadrado del color del tipo, el nombre y las estadísticas.
     */
    private JPanel buildSkinCard(PlayerType type, String label, String[] stats) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(new Color(50, 50, 80));
        card.setBorder(BorderFactory.createLineBorder(type.getColor(), 3));

        // Cuadrado de color (representación visual del personaje)
        JPanel colorBox = new JPanel();
        colorBox.setBackground(type.getColor());
        colorBox.setPreferredSize(new Dimension(60, 60));

        JPanel boxWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
        boxWrapper.setBackground(new Color(50, 50, 80));
        boxWrapper.add(colorBox);
        card.add(boxWrapper, BorderLayout.NORTH);

        // Stats
        JPanel statsPanel = new JPanel(new GridLayout(stats.length, 1, 0, 2));
        statsPanel.setBackground(new Color(50, 50, 80));
        for (String stat : stats) {
            JLabel lbl = new JLabel(stat, JLabel.CENTER);
            lbl.setFont(new Font("Arial", Font.PLAIN, 11));
            lbl.setForeground(new Color(200, 200, 200));
            statsPanel.add(lbl);
        }
        card.add(statsPanel, BorderLayout.CENTER);

        // Botón de selección
        JButton btn = new JButton("Play as " + label);
        btn.setBackground(type.getColor());
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            selectedType = type;
            confirmed = true;
            dispose();
        });
        card.add(btn, BorderLayout.SOUTH);

        return card;
    }

    public PlayerType getSelectedType() { return selectedType; }
    public boolean isConfirmed()        { return confirmed; }
}