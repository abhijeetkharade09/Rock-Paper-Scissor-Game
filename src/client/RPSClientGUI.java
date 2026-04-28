package client;

// Import custom UI classes
import ui.GameWindow;
import ui.NameDialog;
import ui.theme.AppTheme;

// Java Swing imports
import javax.swing.*;

// Input-output and networking
import java.io.*;
import java.net.*;

/**
 * RPSClientGUI
 * ----------------
 * This class handles complete client-side game control.
 *
 * Main responsibilities:
 * 1) Ask player name
 * 2) Create game window
 * 3) Connect to server
 * 4) Send player moves
 * 5) Receive server messages
 * 6) Update GUI based on results
 *
 * NOTE:
 * This class controls game flow only.
 * UI design/layout is handled inside GameWindow.
 */
public class RPSClientGUI {

    // ================= NETWORK VARIABLES =================

    // Used to send data to server
    private PrintWriter out;

    // Used to read data from server
    private BufferedReader in;

    // ================= GAME STATE VARIABLES =================

    // Default player name
    private String playerName = "Player";

    // Stores whether player is PLAYER1 or PLAYER2
    private String playerRole = "PLAYER1";

    // Current round number
    private int currentRound = 0;

    // Last move selected by this player
    private String lastMyMove = "";

    // Last move selected by opponent
    private String lastOppMove = "";

    // ================= UI WINDOW =================

    // Main game window reference
    private GameWindow window;

    /**
     * Constructor
     * Runs when client starts
     */
    public RPSClientGUI() {

        // Step 1: Ask player name using popup dialog
        playerName = new NameDialog().getName();

        // Step 2: Create main game window
        window = new GameWindow(playerName);

        // Step 3: Add button click actions for moves
        window.rockBtn.addActionListener(e -> sendMove("rock", "🪨"));
        window.paperBtn.addActionListener(e -> sendMove("paper", "📄"));
        window.scissorsBtn.addActionListener(e -> sendMove("scissors", "✂"));

        // Step 4: Disable move buttons until server connection is ready
        window.setMovesEnabled(false);

        // Step 5: Connect to server
        connectToServer();
    }

    // ================= SERVER CONNECTION =================
    private void connectToServer() {

        /**
         * SwingWorker is used so server connection happens
         * in background thread without freezing GUI
         */
        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() {
                try {
                    // Connect to local server on port 5000
                    Socket socket = new Socket("localhost", 5000);

                    // Setup input stream
                    in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                    );

                    // Setup output stream
                    out = new PrintWriter(socket.getOutputStream(), true);

                    // Update UI as connected
                    SwingUtilities.invokeLater(() ->
                        window.setConnected(true)
                    );

                    // Start listening to server messages
                    listenLoop();

                } catch (IOException e) {

                    // If connection fails
                    SwingUtilities.invokeLater(() -> {
                        window.setConnected(false);

                        showError(
                            "Could not connect to server.\n" +
                            "Make sure RPSServer is running on port 5000."
                        );
                    });
                }
                return null;
            }
        }.execute();
    }

    // ================= SERVER LISTENER =================
    private void listenLoop() {
        try {
            String line;

            // Continuously read messages from server
            while ((line = in.readLine()) != null) {
                final String msg = line;

                // Update UI safely on Swing thread
                SwingUtilities.invokeLater(() -> handleMessage(msg));
            }

        } catch (IOException e) {

            // If server disconnects
            SwingUtilities.invokeLater(() -> {
                window.setConnected(false);
                window.setMovesEnabled(false);
                window.roundLabel.setText("Disconnected from server");
            });
        }
    }

    // ================= MESSAGE HANDLER =================
    private void handleMessage(String msg) {

        // Welcome message from server
        if (msg.startsWith("WELCOME:")) {

            // Format: WELCOME:<role>:<gameInfo>
            String[] p = msg.split(":");

            playerRole = p[1];

            window.roundLabel.setText("✅  Connected — " + playerRole);
            window.statusLabel.setText(playerName + "  •  " + p[2]);

            // Enable move buttons
            window.setMovesEnabled(true);

        }

        // Round start message
        else if (msg.startsWith("ROUND:")) {

            // Format: ROUND:<number>
            currentRound = Integer.parseInt(msg.split(":")[1]);

            window.roundLabel.setText("ROUND  " + currentRound);
            window.resultLabel.setText("Choose your weapon...");
            window.resultLabel.setForeground(AppTheme.TEXT_DIM);

            // Reset move display
            window.myChoiceDisplay.setText("?");
            window.oppChoiceDisplay.setText("?");
        }

        // Score update
        else if (msg.startsWith("SCORE:")) {

            // Format: SCORE:<myScore>:<oppScore>
            String[] p = msg.split(":");

            window.myScoreLabel.setText(p[1]);
            window.oppScoreLabel.setText(p[2]);
        }

        // Round result message
        else if (msg.startsWith("RESULT:")) {

            // Format: RESULT:<oppMove>:<resultCode>:<round>
            String[] p = msg.split(":");

            lastOppMove = p[1];
            String result = p[2];

            int roundNum =
                p.length > 3
                    ? Integer.parseInt(p[3])
                    : currentRound;

            // Update move emojis
            window.oppChoiceDisplay.setText(moveToEmoji(lastOppMove));
            window.myChoiceDisplay.setText(moveToEmoji(lastMyMove));

            // Check whether current player is Player 1
            boolean iAmP1 = playerRole.equals("PLAYER1");

            // Decide if current player won
            boolean iWon =
                (iAmP1 && result.equals("P1_WIN")) ||
                (!iAmP1 && result.equals("P2_WIN"));

            boolean isDraw = result.equals("DRAW");

            String outcome;

            // Update result label
            if (isDraw) {
                window.resultLabel.setText("🤝  It's a DRAW!");
                window.resultLabel.setForeground(AppTheme.DRAW_COLOR);
                outcome = "draw";

            } else if (iWon) {
                window.resultLabel.setText("🏆  YOU WIN this round!");
                window.resultLabel.setForeground(AppTheme.WIN_COLOR);
                outcome = "win";

            } else {
                window.resultLabel.setText("💀  You LOSE this round!");
                window.resultLabel.setForeground(AppTheme.LOSE_COLOR);
                outcome = "loss";
            }

            // Save result in history panel
            window.addHistoryEntry(
                roundNum,
                lastMyMove,
                lastOppMove,
                outcome
            );

            // Enable buttons for next round
            window.setMovesEnabled(true);
        }

        // Opponent disconnected
        else if (msg.equals("OPPONENT_LEFT")) {
            window.resultLabel.setText("⚠️  Opponent disconnected!");
            window.resultLabel.setForeground(AppTheme.LOSE_COLOR);
            window.roundLabel.setText("Game Over — Opponent left");

            // Disable moves
            window.setMovesEnabled(false);
        }
    }

    // ================= SEND PLAYER MOVE =================
    private void sendMove(String move, String emoji) {

        // If output stream is not ready
        if (out == null) return;

        // Save last selected move
        lastMyMove = move;

        // Update move display
        window.myChoiceDisplay.setText(emoji);
        window.oppChoiceDisplay.setText("⏳");

        // Waiting message
        window.resultLabel.setText("Waiting for opponent...");
        window.resultLabel.setForeground(AppTheme.TEXT_DIM);

        // Disable buttons until result comes
        window.setMovesEnabled(false);

        // Send move to server
        out.println(move);
    }

    // ================= HELPER METHODS =================

    // Show popup error message
    private void showError(String msg) {
        JOptionPane.showMessageDialog(
            window,
            msg,
            "Connection Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    // Convert move text into emoji
    private static String moveToEmoji(String move) {
        return switch (move.toLowerCase()) {
            case "rock" -> "🪨";
            case "paper" -> "📄";
            case "scissors" -> "✂";
            default -> "?";
        };
    }

    // ================= MAIN METHOD =================
    public static void main(String[] args) {

        // Apply global UI theme
        AppTheme.apply();

        // Start GUI safely on Swing thread
        SwingUtilities.invokeLater(RPSClientGUI::new);
    }
}