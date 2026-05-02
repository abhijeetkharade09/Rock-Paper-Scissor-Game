package client;

import ui.GameWindow;
import ui.NameDialog;
import ui.theme.AppTheme;

import javax.swing.*;
import java.io.*;
import java.net.*;

/**
 * RPSClientGUI — Network + game state controller.
 * Handles 10-round game loop and final GAME_OVER winner display.
 */
public class RPSClientGUI {

    private PrintWriter    out;
    private BufferedReader in;

    private String playerName = "Player";
    private String playerRole = "PLAYER1";
    private int    currentRound = 0;
    private String lastMyMove   = "";
    private String lastOppMove  = "";

    private GameWindow window;

    public RPSClientGUI() {
        playerName = new NameDialog().getName();
        window     = new GameWindow(playerName);

        window.rockBtn    .addActionListener(e -> sendMove("rock",     "🪨"));
        window.paperBtn   .addActionListener(e -> sendMove("paper",    "📄"));
        window.scissorsBtn.addActionListener(e -> sendMove("scissors", "✂"));

        window.setMovesEnabled(false);
        connectToServer();
    }

    // ─── Networking ───────────────────────────────────────────────────────────
    private void connectToServer() {
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() {
                try {
                    Socket socket = new Socket("localhost", 5000);
                    in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(socket.getOutputStream(), true);
                    SwingUtilities.invokeLater(() -> window.setConnected(true));
                    listenLoop();
                } catch (IOException e) {
                    SwingUtilities.invokeLater(() -> {
                        window.setConnected(false);
                        showError("Could not connect to server.\nMake sure RPSServer is running on port 5000.");
                    });
                }
                return null;
            }
        }.execute();
    }

    private void listenLoop() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                final String msg = line;
                SwingUtilities.invokeLater(() -> handleMessage(msg));
            }
        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> {
                window.setConnected(false);
                window.setMovesEnabled(false);
                window.roundLabel.setText("Disconnected from server");
            });
        }
    }

    // ─── Message Handler ──────────────────────────────────────────────────────
    private void handleMessage(String msg) {

        if (msg.startsWith("WELCOME:")) {
            String[] p = msg.split(":");
            playerRole = p[1];
            window.roundLabel.setText("Connected  —  " + playerRole);
            window.statusLabel.setText(playerName + "  •  " + p[2] + "  •  10 rounds");
            window.setMovesEnabled(true);

        } else if (msg.startsWith("ROUND:")) {
            // ROUND:<current>:<total>
            String[] p    = msg.split(":");
            currentRound  = Integer.parseInt(p[1]);
            int total     = Integer.parseInt(p[2]);
            window.roundLabel.setText("ROUND  " + currentRound + " / " + total);
            window.resultLabel.setText("Choose your weapon...");
            window.resultLabel.setForeground(AppTheme.TEXT_DIM);
            window.myChoiceDisplay.setText("?");
            window.oppChoiceDisplay.setText("?");

        } else if (msg.startsWith("SCORE:")) {
            String[] p = msg.split(":");
            window.myScoreLabel .setText(p[1]);
            window.oppScoreLabel.setText(p[2]);

        } else if (msg.startsWith("RESULT:")) {
            // RESULT:<oppMove>:<resultCode>:<round>
            String[] p    = msg.split(":");
            lastOppMove   = p[1];
            String result = p[2];
            int roundNum  = p.length > 3 ? Integer.parseInt(p[3]) : currentRound;

            window.oppChoiceDisplay.setText(moveToEmoji(lastOppMove));
            window.myChoiceDisplay .setText(moveToEmoji(lastMyMove));

            boolean iAmP1  = playerRole.equals("PLAYER1");
            boolean iWon   = (iAmP1 && result.equals("P1_WIN")) || (!iAmP1 && result.equals("P2_WIN"));
            boolean isDraw = result.equals("DRAW");

            String outcome;
            if (isDraw) {
                window.resultLabel.setText("🤝  Draw!");
                window.resultLabel.setForeground(AppTheme.DRAW_COLOR);
                outcome = "draw";
            } else if (iWon) {
                window.resultLabel.setText("🏆  You win this round!");
                window.resultLabel.setForeground(AppTheme.WIN_COLOR);
                outcome = "win";
            } else {
                window.resultLabel.setText("💀  You lose this round!");
                window.resultLabel.setForeground(AppTheme.LOSE_COLOR);
                outcome = "loss";
            }

            window.addHistoryEntry(roundNum, lastMyMove, lastOppMove, outcome);
            window.setMovesEnabled(true);

        } else if (msg.startsWith("GAME_OVER:")) {
            // GAME_OVER:<winner>:<p1score>:<p2score>
            String[] p        = msg.split(":");
            String winner     = p[1];
            int    p1Score    = Integer.parseInt(p[2]);
            int    p2Score    = Integer.parseInt(p[3]);

            boolean iAmP1     = playerRole.equals("PLAYER1");
            boolean iWon      = (iAmP1 && winner.equals("P1_WIN")) || (!iAmP1 && winner.equals("P2_WIN"));
            boolean isDraw    = winner.equals("DRAW");

            // Disable buttons — game is over
            window.setMovesEnabled(false);
            window.roundLabel.setText("GAME OVER  —  10 / 10 rounds");

            // Show winner dialog
            window.showGameOverDialog(iWon, isDraw, p1Score, p2Score, playerRole, playerName);

        } else if (msg.equals("OPPONENT_LEFT")) {
            window.resultLabel.setText("Opponent disconnected!");
            window.resultLabel.setForeground(AppTheme.LOSE_COLOR);
            window.roundLabel.setText("Game Over — Opponent left");
            window.setMovesEnabled(false);
        }
    }

    // ─── Send Move ────────────────────────────────────────────────────────────
    private void sendMove(String move, String emoji) {
        if (out == null) return;
        lastMyMove = move;
        window.myChoiceDisplay .setText(emoji);
        window.oppChoiceDisplay.setText("⏳");
        window.resultLabel.setText("Waiting for opponent...");
        window.resultLabel.setForeground(AppTheme.TEXT_DIM);
        window.setMovesEnabled(false);
        out.println(move);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private void showError(String msg) {
        JOptionPane.showMessageDialog(window, msg, "Connection Error", JOptionPane.ERROR_MESSAGE);
    }

    private static String moveToEmoji(String move) {
        return switch (move.toLowerCase()) {
            case "rock"     -> "🪨";
            case "paper"    -> "📄";
            case "scissors" -> "✂";
            default         -> "?";
        };
    }

    public static void main(String[] args) {
        AppTheme.apply();
        SwingUtilities.invokeLater(RPSClientGUI::new);
    }
}