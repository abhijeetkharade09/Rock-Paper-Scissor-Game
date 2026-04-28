package server;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * RPSServer — Multiplayer Rock Paper Scissors server.
 *
 * Responsibilities:
 *   • Accept two player connections per game session
 *   • Spawn a GameSession thread per pair
 *   • Handle disconnections gracefully
 */
public class RPSServer {

    private static final int PORT = 5000;
    private static final AtomicInteger totalGames = new AtomicInteger(0);

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║   Rock Paper Scissors Server v2.0    ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║  Port: 5000  |  Waiting for players  ║");
        System.out.println("╚══════════════════════════════════════╝");

        ExecutorService pool = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                System.out.println("\n[Server] Waiting for Player 1...");
                Socket player1 = serverSocket.accept();
                System.out.println("[Server] Player 1 connected: " + player1.getInetAddress());

                System.out.println("[Server] Waiting for Player 2...");
                Socket player2 = serverSocket.accept();
                System.out.println("[Server] Player 2 connected: " + player2.getInetAddress());

                int gameId = totalGames.incrementAndGet();
                System.out.println("[Server] Starting Game Session #" + gameId);
                pool.execute(new GameSession(player1, player2, gameId));
            }
        } catch (IOException e) {
            System.err.println("[Server] Fatal error: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }
}