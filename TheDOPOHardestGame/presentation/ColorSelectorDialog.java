package presentation;

import javax.swing.*;
import java.awt.*;

/**
 * Diálogo para seleccionar color del jugador.
 * Soporta colores predefinidos y personalizados.
 */
public class ColorSelectorDialog extends JDialog {
    private Color selectedColor;
    private boolean confirmed;

    private static final Color[] PREDEFINED_COLORS = {
        Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
        Color.MAGENTA, Color.CYAN, Color.ORANGE, new Color(255, 192, 203),
        new Color(128, 0, 128), new Color(0, 128, 128)
    };

    private static final String[] COLOR_NAMES = {
        "Red", "Blue", "Green", "Yellow",
        "Magenta", "Cyan", "Orange", "Pink",
        "Purple", "Teal"
    };

    public ColorSelectorDialog(Frame owner, String title, Color initialColor) {
        super(owner, title, true);
        this.selectedColor = initialColor != null ? initialColor : Color.RED;
        this.confirmed = false;
        prepareUI();
    }

    private void prepareUI() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(getOwner());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 240, 240));

        // Título
        JLabel lblTitle = new JLabel("Select Your Color:", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        // Panel de colores predefinidos
        JPanel colorPanel = new JPanel(new GridLayout(2, 5, 10, 10));
        colorPanel.setBackground(new Color(240, 240, 240));

        for (int i = 0; i < PREDEFINED_COLORS.length; i++) {
            final Color color = PREDEFINED_COLORS[i];
            final String name = COLOR_NAMES[i];
            
            JButton btnColor = new JButton(name);
            btnColor.setBackground(color);
            btnColor.setForeground(Color.WHITE);
            btnColor.setFont(new Font("Arial", Font.BOLD, 12));
            btnColor.setFocusPainted(false);
            
            if (color.equals(selectedColor)) {
                btnColor.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));
            } else {
                btnColor.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
            }
            
            btnColor.addActionListener(e -> {
                selectedColor = color;
                confirmed = true;
                dispose();
            });
            
            colorPanel.add(btnColor);
        }

        mainPanel.add(colorPanel, BorderLayout.CENTER);

        // Bottom panel - Botones
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setBackground(new Color(240, 240, 240));

        JButton btnCustom = new JButton("Custom Color");
        btnCustom.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Choose a color", selectedColor);
            if (chosen != null) {
                selectedColor = chosen;
                confirmed = true;
                dispose();
            }
        });

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        btnPanel.add(btnCustom);
        btnPanel.add(btnCancel);

        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);
        pack();
        setSize(500, 250);
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
