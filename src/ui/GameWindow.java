package ui;

import ui.components.*;
import ui.theme.AppTheme;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * GameWindow — Builds and owns all UI panels and components.
 * Has zero networking or game logic — it only constructs the view
 * and exposes update methods that RPSClientGUI calls.
 *
 * Separation of concerns:
 *   RPSClientGUI  → network + game state
 *   GameWindow    → UI layout + rendering
 *   AppTheme      → colors + fonts
 *   ui.components → reusable widgets
 */
public class GameWindow extends JFrame {

    // ─── Public UI handles (read by RPSClientGUI) ─────────────────────────────
    public JLabel  statusLabel;
    public JLabel  roundLabel;
    public JLabel  myScoreLabel, oppScoreLabel;
    public JLabel  resultLabel;
    public JLabel  myChoiceDisplay, oppChoiceDisplay;
    public JLabel  connectionDot;
    public JPanel  historyPanel;
    public JPanel  movePanel;
    public MoveButton rockBtn, paperBtn, scissorsBtn;

    // ─── Internal state ───────────────────────────────────────────────────────
    private final String playerName;

    // ─────────────────────────────────────────────────────────────────────────
    public GameWindow(String playerName) {
        this.playerName = playerName;
        buildFrame();
    }

    // ─── Frame Setup ─────────────────────────────────────────────────────────
    private void buildFrame() {
        setTitle("⚡ Rock Paper Scissors — " + playerName);
        setSize(760, 700);
        setMinimumSize(new Dimension(680, 620));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, AppTheme.BG_DARK,
                    getWidth(), getHeight(), new Color(15, 18, 32)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // subtle grid overlay
                g2.setColor(new Color(255, 255, 255, 5));
                for (int x = 0; x < getWidth();  x += 40) g2.drawLine(x, 0, x, getHeight());
                for (int y = 0; y < getHeight(); y += 40) g2.drawLine(0, y, getWidth(), y);
                g2.dispose();
            }
        };
        root.setBorder(new EmptyBorder(0, 0, 0, 0));
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildBottom(), BorderLayout.SOUTH);

        setContentPane(root);
        setVisible(true);
    }

    // ─── Header ──────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(12, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(14, 17, 30));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setPaint(new GradientPaint(
                    0, getHeight() - 2, AppTheme.ACCENT_BLUE,
                    getWidth() / 2, getHeight() - 2, AppTheme.ACCENT_PINK
                ));
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2.dispose();
            }
        };
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        // Left: title
        JLabel title = new JLabel("⚡ RPS BATTLE");
        title.setFont(new Font("Dialog", Font.BOLD, 22));
        title.setForeground(AppTheme.TEXT_PRIMARY);

        // Center: round + status
        JPanel centerInfo = new JPanel(new GridLayout(2, 1, 0, 2));
        centerInfo.setOpaque(false);
        roundLabel = new JLabel("Connecting to server...", SwingConstants.CENTER);
        roundLabel.setFont(AppTheme.FONT_TITLE);
        roundLabel.setForeground(AppTheme.ACCENT_BLUE);
        statusLabel = new JLabel(playerName, SwingConstants.CENTER);
        statusLabel.setFont(AppTheme.FONT_BODY);
        statusLabel.setForeground(AppTheme.TEXT_DIM);
        centerInfo.add(roundLabel);
        centerInfo.add(statusLabel);

        // Right: connection dot
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setOpaque(false);
        connectionDot = new JLabel("●");
        connectionDot.setFont(new Font("Dialog", Font.BOLD, 18));
        connectionDot.setForeground(AppTheme.LOSE_COLOR);
        JLabel connText = new JLabel("OFFLINE");
        connText.setFont(AppTheme.FONT_MONO);
        connText.setForeground(AppTheme.TEXT_DIM);
        rightPanel.add(connectionDot);
        rightPanel.add(connText);

        header.add(title, BorderLayout.WEST);
        header.add(centerInfo, BorderLayout.CENTER);
        header.add(rightPanel, BorderLayout.EAST);
        return header;
    }

    // ─── Center ──────────────────────────────────────────────────────────────
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(16, 20, 8, 20));
        center.add(buildScoreboard(), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT, buildArena(), buildHistoryPanel()
        );
        split.setOpaque(false);
        split.setBorder(null);
        split.setDividerSize(6);
        split.setDividerLocation(420);
        split.setResizeWeight(0.6);
        split.setBackground(AppTheme.BG_DARK);
        center.add(split, BorderLayout.CENTER);
        return center;
    }

    // ─── Scoreboard ──────────────────────────────────────────────────────────
    private JPanel buildScoreboard() {
        JPanel board = new JPanel(new GridLayout(1, 3, 12, 0));
        board.setOpaque(false);
        board.setPreferredSize(new Dimension(0, 80));

        myScoreLabel  = makeScoreLabel();
        oppScoreLabel = makeScoreLabel();

        board.add(buildScoreCard(playerName,  myScoreLabel,  AppTheme.ACCENT_BLUE));
        board.add(buildVsCard());
        board.add(buildScoreCard("Opponent",  oppScoreLabel, AppTheme.ACCENT_PINK));
        return board;
    }

    private JLabel makeScoreLabel() {
        JLabel lbl = new JLabel("0", SwingConstants.CENTER);
        lbl.setFont(AppTheme.FONT_SCORE);
        return lbl;
    }

    private JPanel buildScoreCard(String name, JLabel scoreLabel, Color accent) {
        GlowPanel card = new GlowPanel(accent);
        card.setLayout(new GridLayout(2, 1, 0, 2));
        card.setBorder(new EmptyBorder(6, 10, 6, 10));
        scoreLabel.setForeground(accent);

        JLabel nameLabel = new JLabel(name, SwingConstants.CENTER);
        nameLabel.setFont(AppTheme.FONT_BODY);
        nameLabel.setForeground(AppTheme.TEXT_DIM);

        card.add(scoreLabel);
        card.add(nameLabel);
        return card;
    }

    private JPanel buildVsCard() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        JLabel vs = new JLabel("VS", SwingConstants.CENTER);
        vs.setFont(new Font("Dialog", Font.BOLD, 20));
        vs.setForeground(new Color(80, 90, 120));
        p.add(vs);
        return p;
    }

    // ─── Arena ───────────────────────────────────────────────────────────────
    private JPanel buildArena() {
        JPanel arena = new JPanel(new BorderLayout(0, 10));
        arena.setOpaque(false);

        // Choice row
        JPanel choiceRow = new JPanel(new GridLayout(1, 3, 8, 0));
        choiceRow.setOpaque(false);
        choiceRow.setPreferredSize(new Dimension(0, 110));

        myChoiceDisplay  = buildChoiceLabel(AppTheme.ACCENT_BLUE);
        oppChoiceDisplay = buildChoiceLabel(AppTheme.ACCENT_PINK);

        JPanel vsSmall = new JPanel(new GridBagLayout());
        vsSmall.setOpaque(false);
        JLabel vsLbl = new JLabel("VS");
        vsLbl.setFont(new Font("Dialog", Font.BOLD, 13));
        vsLbl.setForeground(new Color(80, 90, 120));
        vsSmall.add(vsLbl);

        choiceRow.add(wrapChoiceCard(myChoiceDisplay,  playerName,  AppTheme.ACCENT_BLUE));
        choiceRow.add(vsSmall);
        choiceRow.add(wrapChoiceCard(oppChoiceDisplay, "Opponent",  AppTheme.ACCENT_PINK));
        arena.add(choiceRow, BorderLayout.NORTH);

        // Result label
        resultLabel = new JLabel("Make your move!", SwingConstants.CENTER);
        resultLabel.setFont(AppTheme.FONT_RESULT);
        resultLabel.setForeground(AppTheme.TEXT_DIM);
        resultLabel.setBorder(new EmptyBorder(4, 0, 4, 0));
        arena.add(resultLabel, BorderLayout.CENTER);

        // Move buttons
        movePanel = new JPanel(new GridLayout(1, 3, 12, 0));
        movePanel.setOpaque(false);
        movePanel.setPreferredSize(new Dimension(0, 90));

        rockBtn     = new MoveButton("🪨", "ROCK",     AppTheme.ACCENT_BLUE);
        paperBtn    = new MoveButton("📄", "PAPER",    AppTheme.ACCENT_GREEN);
        scissorsBtn = new MoveButton("✂",  "SCISSORS", AppTheme.ACCENT_PINK);

        movePanel.add(rockBtn);
        movePanel.add(paperBtn);
        movePanel.add(scissorsBtn);
        arena.add(movePanel, BorderLayout.SOUTH);

        return arena;
    }

    private JLabel buildChoiceLabel(Color color) {
        JLabel lbl = new JLabel("?", SwingConstants.CENTER);
        lbl.setFont(new Font("Dialog", Font.PLAIN, 42));
        lbl.setForeground(color);
        return lbl;
    }

    private JPanel wrapChoiceCard(JLabel choiceLbl, String name, Color accent) {
        GlowPanel card = new GlowPanel(accent);
        card.setLayout(new GridLayout(2, 1, 0, 2));
        card.setBorder(new EmptyBorder(8, 8, 8, 8));

        JLabel nameLbl = new JLabel(name, SwingConstants.CENTER);
        nameLbl.setFont(new Font("Dialog", Font.BOLD, 11));
        nameLbl.setForeground(accent);

        card.add(choiceLbl);
        card.add(nameLbl);
        return card;
    }

    // ─── History Panel ────────────────────────────────────────────────────────
    private JPanel buildHistoryPanel() {
        JPanel container = new JPanel(new BorderLayout(0, 8));
        container.setOpaque(false);

        JLabel histTitle = new JLabel("ROUND HISTORY", SwingConstants.CENTER);
        histTitle.setFont(new Font("Dialog", Font.BOLD, 11));
        histTitle.setForeground(AppTheme.TEXT_DIM);
        histTitle.setBorder(new EmptyBorder(4, 0, 6, 0));

        historyPanel = new JPanel();
        historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));
        historyPanel.setBackground(AppTheme.BG_CARD);
        historyPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane scroll = new JScrollPane(historyPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setBackground(AppTheme.BG_CARD);
        scroll.setBorder(new RoundBorder(new Color(40, 48, 80), 1, 8));
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        container.add(histTitle, BorderLayout.NORTH);
        container.add(scroll, BorderLayout.CENTER);
        return container;
    }

    // ─── Bottom Bar ───────────────────────────────────────────────────────────
    private JPanel buildBottom() {
        JPanel bottom = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(12, 15, 25));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(40, 48, 80));
                g2.drawLine(0, 0, getWidth(), 0);
                g2.dispose();
            }
        };
        bottom.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel hint = new JLabel("🎮  Choose Rock, Paper, or Scissors to play  •  First to score wins the round");
        hint.setFont(AppTheme.FONT_BODY);
        hint.setForeground(AppTheme.TEXT_DIM);

        JLabel version = new JLabel("v2.0");
        version.setFont(AppTheme.FONT_MONO);
        version.setForeground(new Color(50, 60, 90));

        bottom.add(hint,    BorderLayout.WEST);
        bottom.add(version, BorderLayout.EAST);
        return bottom;
    }

    // ─── Public Update Methods (called by RPSClientGUI) ───────────────────────

    /** Updates the connection dot and label */
    public void setConnected(boolean connected) {
        connectionDot.setForeground(connected ? AppTheme.WIN_COLOR : AppTheme.LOSE_COLOR);
        for (Component c : connectionDot.getParent().getComponents()) {
            if (c instanceof JLabel lbl && c != connectionDot) {
                lbl.setText(connected ? "ONLINE" : "OFFLINE");
                lbl.setForeground(connected ? AppTheme.WIN_COLOR : AppTheme.LOSE_COLOR);
            }
        }
    }

    /** Adds a row to the round history panel */
    public void addHistoryEntry(int round, String myMove, String oppMove, String outcome) {
        Color badgeColor = switch (outcome) {
            case "win"  -> AppTheme.WIN_COLOR;
            case "loss" -> AppTheme.LOSE_COLOR;
            default     -> AppTheme.DRAW_COLOR;
        };
        String badge = outcome.equals("win") ? "WIN" : outcome.equals("loss") ? "LOSS" : "DRAW";

        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        row.setBackground(new Color(28, 33, 55));
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, badgeColor),
            new EmptyBorder(4, 8, 4, 8)
        ));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel info = new JLabel("R" + round + "  " + capitalize(myMove) + " vs " + capitalize(oppMove));
        info.setFont(AppTheme.FONT_HISTORY);
        info.setForeground(AppTheme.TEXT_DIM);

        JLabel badgeLbl = new JLabel(badge);
        badgeLbl.setFont(new Font("Dialog", Font.BOLD, 10));
        badgeLbl.setForeground(badgeColor);

        row.add(info,     BorderLayout.WEST);
        row.add(badgeLbl, BorderLayout.EAST);

        historyPanel.add(row);
        historyPanel.add(Box.createVerticalStrut(3));
        historyPanel.revalidate();
        historyPanel.repaint();

        // Auto-scroll to latest entry
        SwingUtilities.invokeLater(() -> {
            JScrollPane sp = (JScrollPane) historyPanel.getParent().getParent();
            sp.getVerticalScrollBar().setValue(sp.getVerticalScrollBar().getMaximum());
        });
    }

    /** Enables or disables the three move buttons */
    public void setMovesEnabled(boolean enabled) {
        rockBtn.setEnabled(enabled);
        paperBtn.setEnabled(enabled);
        scissorsBtn.setEnabled(enabled);
        movePanel.repaint();
    }

    /**
     * Shows a full-screen Game Over dialog with the final winner.
     * Called after all 10 rounds are complete.
     */
    public void showGameOverDialog(boolean iWon, boolean isDraw,
                                   int p1Score, int p2Score,
                                   String playerRole, String playerName) {
        // Build a modal dialog styled to match the dark theme
        JDialog dialog = new JDialog(this, "Game Over", true);
        dialog.setUndecorated(true);
        dialog.setSize(420, 320);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                // colored top bar
                Color barColor = isDraw ? AppTheme.DRAW_COLOR
                               : iWon  ? AppTheme.WIN_COLOR
                               :         AppTheme.LOSE_COLOR;
                g2.setColor(barColor);
                g2.fillRoundRect(0, 0, getWidth(), 6, 4, 4);
                // border
                g2.setColor(new Color(barColor.getRed(), barColor.getGreen(), barColor.getBlue(), 100));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 20, 20);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(28, 36, 28, 36));

        // Trophy / result emoji
        String resultEmoji = isDraw ? "🤝" : iWon ? "🏆" : "💀";
        JLabel emojiLbl = new JLabel(resultEmoji, SwingConstants.CENTER);
        emojiLbl.setFont(new Font("Dialog", Font.PLAIN, 52));

        // Main result heading
        String heading = isDraw ? "It's a Draw!" : iWon ? "You Win!" : "You Lose!";
        JLabel headingLbl = new JLabel(heading, SwingConstants.CENTER);
        headingLbl.setFont(new Font("Dialog", Font.BOLD, 28));
        Color headingColor = isDraw ? AppTheme.DRAW_COLOR
                           : iWon  ? AppTheme.WIN_COLOR
                           :         AppTheme.LOSE_COLOR;
        headingLbl.setForeground(headingColor);

        // Score summary
        String myScore  = playerRole.equals("PLAYER1") ? String.valueOf(p1Score) : String.valueOf(p2Score);
        String oppScore = playerRole.equals("PLAYER1") ? String.valueOf(p2Score) : String.valueOf(p1Score);
        JLabel scoreLbl = new JLabel(
            playerName + "  " + myScore + "  —  " + oppScore + "  Opponent",
            SwingConstants.CENTER
        );
        scoreLbl.setFont(new Font("Dialog", Font.BOLD, 15));
        scoreLbl.setForeground(AppTheme.TEXT_DIM);

        JLabel subLbl = new JLabel("Final score after 10 rounds", SwingConstants.CENTER);
        subLbl.setFont(new Font("Dialog", Font.PLAIN, 12));
        subLbl.setForeground(new Color(80, 90, 120));

        // Close button
        GlowButton closeBtn = new GlowButton("Close", AppTheme.ACCENT_BLUE);
        closeBtn.addActionListener(e -> {
            dialog.dispose();
            System.exit(0);
        });

        // Layout
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 0, 6));
        topPanel.setOpaque(false);
        topPanel.add(emojiLbl);
        topPanel.add(headingLbl);

        JPanel midPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        midPanel.setOpaque(false);
        midPanel.setBorder(new EmptyBorder(14, 0, 14, 0));
        midPanel.add(scoreLbl);
        midPanel.add(subLbl);

        panel.add(topPanel,  BorderLayout.NORTH);
        panel.add(midPanel,  BorderLayout.CENTER);
        panel.add(closeBtn,  BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setBackground(new Color(0, 0, 0, 0));
        dialog.getRootPane().setOpaque(false);
        dialog.setVisible(true);
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}