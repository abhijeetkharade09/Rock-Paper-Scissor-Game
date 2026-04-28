package server;

import java.io.*;
import java.net.*;

/**
 * GameSession — Handles one full game between two connected players.
 * Runs on its own thread so the server can host multiple games at once.
 */
public class GameSession implements Runnable {

    private final Socket p1;
    private final Socket p2;
    private final int    gameId;
    private int score1 = 0;
    private int score2 = 0;
    private int roundNumber = 0;

    public GameSession(Socket p1, Socket p2, int gameId) {
        this.p1     = p1;
        this.p2     = p2;
        this.gameId = gameId;
    }

    @Override
    public void run() {
        System.out.println("[Game #" + gameId + "] Session started.");
        try (
            BufferedReader in1  = new BufferedReader(new InputStreamReader(p1.getInputStream()));
            BufferedReader in2  = new BufferedReader(new InputStreamReader(p2.getInputStream()));
            PrintWriter    out1 = new PrintWriter(p1.getOutputStream(), true);
            PrintWriter    out2 = new PrintWriter(p2.getOutputStream(), true)
        ) {
            out1.println("WELCOME:PLAYER1:Game #" + gameId);
            out2.println("WELCOME:PLAYER2:Game #" + gameId);

            sendScores(out1, out2);

            while (true) {
                roundNumber++;
                out1.println("ROUND:" + roundNumber);
                out2.println("ROUND:" + roundNumber);

                String move1 = in1.readLine();
                String move2 = in2.readLine();

                if (move1 == null || move2 == null) {
                    System.out.println("[Game #" + gameId + "] A player disconnected.");
                    if (move1 == null) out2.println("OPPONENT_LEFT");
                    if (move2 == null) out1.println("OPPONENT_LEFT");
                    break;
                }

                move1 = move1.trim().toLowerCase();
                move2 = move2.trim().toLowerCase();
                System.out.println("[Game #" + gameId + "] P1=" + move1 + " | P2=" + move2);

                String resultCode = getResultCode(move1, move2);
                if (resultCode.equals("P1_WIN"))      score1++;
                else if (resultCode.equals("P2_WIN")) score2++;

                // Each player gets: RESULT:<opponent_move>:<result_code>:<round>
                out1.println("RESULT:" + move2 + ":" + resultCode + ":" + roundNumber);
                out2.println("RESULT:" + move1 + ":" + resultCode + ":" + roundNumber);

                sendScores(out1, out2);

                System.out.println("[Game #" + gameId + "] " + resultCode
                    + " | Score: " + score1 + "-" + score2);
            }
        } catch (IOException e) {
            System.out.println("[Game #" + gameId + "] Session ended: " + e.getMessage());
        } finally {
            closeQuietly(p1);
            closeQuietly(p2);
            System.out.println("[Game #" + gameId + "] Closed. Final: " + score1 + "-" + score2);
        }
    }

    /**
     * Sends scores to both players — each player sees their own score first.
     */
    private void sendScores(PrintWriter out1, PrintWriter out2) {
        out1.println("SCORE:" + score1 + ":" + score2);
        out2.println("SCORE:" + score2 + ":" + score1);
    }

    /**
     * Returns P1_WIN, P2_WIN, or DRAW based on the two moves.
     */
    private String getResultCode(String m1, String m2) {
        if (m1.equals(m2)) return "DRAW";
        if ((m1.equals("rock")     && m2.equals("scissors")) ||
            (m1.equals("paper")    && m2.equals("rock"))     ||
            (m1.equals("scissors") && m2.equals("paper")))   return "P1_WIN";
        return "P2_WIN";
    }

    private void closeQuietly(Socket s) {
        try { if (s != null) s.close(); } catch (IOException ignored) {}
    }
}