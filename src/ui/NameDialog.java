package ui;

import ui.components.GlowButton;
import ui.components.RoundBorder;
import ui.theme.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * NameDialog — Modal dialog shown at startup to collect the player's name.
 * Fully self-contained UI; returns the entered name via getName().
 */
public class NameDialog {

    private String playerName = "Player";

    public NameDialog() {
        showDialog();
    }

    private void showDialog() {
        JDialog dialog = new JDialog();
        dialog.setUndecorated(true);
        dialog.setModal(true);
        dialog.setSize(400, 260);
        dialog.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(AppTheme.ACCENT_BLUE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(30, 35, 30, 35));

        // Title
        JLabel title = new JLabel("ENTER YOUR NAME", SwingConstants.CENTER);
        title.setFont(new Font("Dialog", Font.BOLD, 18));
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("to join the battle", SwingConstants.CENTER);
        sub.setFont(AppTheme.FONT_BODY);
        sub.setForeground(AppTheme.TEXT_DIM);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(sub);

        // Name input
        JTextField nameInput = new JTextField("Player");
        nameInput.setFont(new Font("Dialog", Font.BOLD, 16));
        nameInput.setForeground(AppTheme.TEXT_PRIMARY);
        nameInput.setBackground(new Color(30, 35, 58));
        nameInput.setCaretColor(AppTheme.ACCENT_BLUE);
        nameInput.setBorder(BorderFactory.createCompoundBorder(
            new RoundBorder(AppTheme.ACCENT_BLUE, 2, 10),
            new EmptyBorder(10, 14, 10, 14)
        ));
        nameInput.setHorizontalAlignment(JTextField.CENTER);
        nameInput.selectAll();

        // Start button
        GlowButton joinBtn = new GlowButton("START GAME", AppTheme.ACCENT_BLUE);
        joinBtn.addActionListener(e -> {
            String n = nameInput.getText().trim();
            playerName = n.isEmpty() ? "Player" : n;
            dialog.dispose();
        });
        nameInput.addActionListener(e -> joinBtn.doClick());

        // Layout
        JPanel inputArea = new JPanel(new GridLayout(3, 1, 0, 12));
        inputArea.setOpaque(false);
        inputArea.setBorder(new EmptyBorder(16, 0, 0, 0));
        inputArea.add(nameInput);
        inputArea.add(Box.createVerticalStrut(4));
        inputArea.add(joinBtn);

        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(Box.createVerticalStrut(16), BorderLayout.CENTER);
        panel.add(inputArea, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setBackground(new Color(0, 0, 0, 0));
        dialog.getRootPane().setOpaque(false);
        dialog.setVisible(true);
    }

    /** Returns the name entered by the player. */
    public String getName() {
        return playerName;
    }
}