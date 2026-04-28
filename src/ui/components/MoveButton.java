package ui.components;

import ui.theme.AppTheme;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * MoveButton — Stylish emoji + label button used for Rock, Paper, Scissors choices.
 * Features hover glow, disabled state, and rounded corners.
 */
public class MoveButton extends JButton {

    private final Color accent;
    private boolean hovered = false;

    public MoveButton(String emoji, String label, Color accent) {
        super();
        this.accent = accent;
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setLayout(new GridLayout(2, 1, 0, 2));

        JLabel emojiLbl = new JLabel(emoji, SwingConstants.CENTER);
        emojiLbl.setFont(new Font("Dialog", Font.PLAIN, 26));
        emojiLbl.setForeground(Color.WHITE);

        JLabel textLbl = new JLabel(label, SwingConstants.CENTER);
        textLbl.setFont(new Font("Dialog", Font.BOLD, 10));
        textLbl.setForeground(accent);

        add(emojiLbl);
        add(textLbl);

        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if (isEnabled()) { hovered = true;  repaint(); } }
            public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color bg, border;
        if (!isEnabled()) {
            bg     = new Color(20, 24, 42);
            border = new Color(40, 48, 70);
        } else if (hovered) {
            bg     = new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 60);
            border = accent;
        } else {
            bg     = AppTheme.BG_CARD2;
            border = new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 160);
        }

        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
        g2.setColor(border);
        g2.setStroke(new BasicStroke(hovered ? 2f : 1.5f));
        g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 14, 14);
        g2.dispose();

        super.paintComponent(g);
    }
}