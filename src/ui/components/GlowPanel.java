package ui.components;

import ui.theme.AppTheme;
import javax.swing.*;
import java.awt.*;

/**
 * GlowPanel — A rounded card panel with a colored accent border glow.
 * Used for score cards, choice display cards, etc.
 */
public class GlowPanel extends JPanel {

    private final Color accent;

    public GlowPanel(Color accent) {
        this.accent = accent;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Card background
        g2.setColor(AppTheme.BG_CARD2);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

        // Accent border glow
        g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 120));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 14, 14);

        g2.dispose();
        super.paintComponent(g);
    }
}