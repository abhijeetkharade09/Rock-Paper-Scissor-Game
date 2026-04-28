╔══════════════════════════════════════════════════════════════╗
║         Rock Paper Scissors Multiplayer v2.0                 ║
║         Clean Package-Based Project Structure                ║
╚══════════════════════════════════════════════════════════════╝

PROJECT STRUCTURE
command: mkdir RPS_Game\src\server, RPS_Game\src\client, RPS_Game\src\ui\theme, RPS_Game\src\ui\components; ni RPS_Game\src\server\RPSServer.java, RPS_Game\src\server\GameSession.java, RPS_Game\src\client\RPSClientGUI.java, RPS_Game\src\ui\GameWindow.java, RPS_Game\src\ui\NameDialog.java, RPS_Game\src\ui\theme\AppTheme.java, RPS_Game\src\ui\components\GlowPanel.java, RPS_Game\src\ui\components\MoveButton.java, RPS_Game\src\ui\components\GlowButton.java, RPS_Game\src\ui\components\RoundBorder.java
─────────────────────────────────────────────────────────────
RPS_Game/
└── src/
    ├── server/
    │   ├── RPSServer.java       ← Entry point: starts server on port 5000
    │   └── GameSession.java     ← Handles one game between two players
    │
    ├── client/
    │   └── RPSClientGUI.java    ← Network logic + game state (no UI code)
    │
    └── ui/
        ├── GameWindow.java      ← Builds the entire main window layout
        ├── NameDialog.java      ← Startup name entry popup
        ├── theme/
        │   └── AppTheme.java    ← ALL colors, fonts, UIManager setup
        └── components/
            ├── GlowPanel.java   ← Rounded card panel with accent border
            ├── MoveButton.java  ← Rock/Paper/Scissors hover buttons
            ├── GlowButton.java  ← General-purpose glowing action button
            └── RoundBorder.java ← Rounded rectangle border utility

SEPARATION OF CONCERNS
─────────────────────────────────────────────────────────────
  server/     → Pure networking & game rules. No Swing imports.
  client/     → Connects to server, parses messages, updates view.
  ui/         → All Swing painting, layout, and components.
  ui/theme/   → Single source of truth for all colors & fonts.
              → To restyle the app: only edit AppTheme.java

REQUIREMENTS
─────────────────────────────────────────────────────────────
  Java 17 or higher (uses switch expressions & pattern matching)

COMPILE(everything at once:) project command:
─────────────────────────────────────────────────────────────
   javac -d out src\server\*.java src\client\*.java src\ui\*.java src\ui\theme\*.java src\ui\components\*.java

RUN Project Command:
─────────────────────────────────────────────────────────────
Run server after compile: java -cp out server.RPSServer

Run client
Open new terminal: java -cp out client.RPSClientGUI
Open another terminal for second player: java -cp out client.RPSClientGUI

PLAY ON LAN (Different Machines)
─────────────────────────────────────────────────────────────
  1. Start RPSServer on host machine
  2. Get host IP: ipconfig (Windows) or ifconfig (Mac/Linux)
  3. In client/RPSClientGUI.java, change:
       new Socket("localhost", 5000)
     to:
       new Socket("192.168.x.x", 5000)
  4. Recompile & run the client on two other machines