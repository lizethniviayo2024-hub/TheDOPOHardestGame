package presentation;

import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.*;

class OutlineLabel extends JLabel {

    private Color outlineColor;
    private int thickness;

    public OutlineLabel(String text, Color textColor, Color outlineColor, int thickness) {
        super(text);
        this.setForeground(textColor);
        this.outlineColor = outlineColor;
        this.thickness = thickness;
        setFont(new Font("Arial Black", Font.BOLD, 72));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int x = 0;
        int y = fm.getAscent();

        // Dibujar borde
        g2.setColor(outlineColor);
        for (int i = -thickness; i <= thickness; i++) {
            for (int j = -thickness; j <= thickness; j++) {
                g2.drawString(getText(), x + i, y + j);
            }
        }

        // Dibujar texto encima
        g2.setColor(getForeground());
        g2.drawString(getText(), x, y);
    }
}