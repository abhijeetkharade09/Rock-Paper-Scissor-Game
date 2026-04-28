package ui.components;

import javax.swing.border.AbstractBorder;
import java.awt.*;

/**
 * RoundBorder — A reusable rounded rectangle border for panels and text fields.
 */
public class RoundBorder extends AbstractBorder {

    private final Color color;
    private final int thickness;
    private final int radius;

    public RoundBorder(Color color, int thickness, int radius) {
        this.color     = color;
        this.thickness = thickness;
        this.radius    = radius;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(thickness));
        g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        int pad = thickness + 4;
        return new Insets(pad, pad, pad, pad);
    }
}