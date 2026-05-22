package presentation;
import java.awt.Graphics2D;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.*;

class OutlineButton extends JButton {

    private Color textColor;
    private Color outlineColor;
    private int thickness;

    public OutlineButton(String text, Color textColor, Color outlineColor, int thickness) {
        super(text);
        this.textColor = textColor;
        this.outlineColor = outlineColor;
        this.thickness = thickness;

        setFont(new Font("Arial Black", Font.BOLD, 24));
        setContentAreaFilled(false); // sin fondo
        setFocusPainted(false);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 3)); // borde boton
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        // Fondo del botpn
        g2.setColor(new Color(180, 190, 230));
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();

        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = (getHeight() + fm.getAscent()) / 2 - 5;

        // Dibujar borde del texto
        g2.setColor(outlineColor);
        for (int i = -thickness; i <= thickness; i++) {
            for (int j = -thickness; j <= thickness; j++) {
                g2.drawString(getText(), x + i, y + j);
            }
        }

        // Dibujar texto principal
        g2.setColor(textColor);
        g2.drawString(getText(), x, y);
    }
}