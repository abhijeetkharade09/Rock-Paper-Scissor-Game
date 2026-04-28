🎮 Rock Paper Scissors Multiplayer v2.0

📁 Project Setup Command
mkdir RPS_Game\src\server RPS_Game\src\client RPS_Game\src\ui\theme RPS_Game\src\ui\components
ni RPS_Game\src\server\RPSServer.java
ni RPS_Game\src\server\GameSession.java
ni RPS_Game\src\client\RPSClientGUI.java
ni RPS_Game\src\ui\GameWindow.java
ni RPS_Game\src\ui\NameDialog.java
ni RPS_Game\src\ui\theme\AppTheme.java
ni RPS_Game\src\ui\components\GlowPanel.java
ni RPS_Game\src\ui\components\MoveButton.java
ni RPS_Game\src\ui\components\GlowButton.java
ni RPS_Game\src\ui\components\RoundBorder.java

📂 Project Structure
RPS_Game/
└── src/
    ├── server/
    │   ├── RPSServer.java        // Entry point: starts server on port 5000
    │   └── GameSession.java      // Handles one game between two players
    │
    ├── client/
    │   └── RPSClientGUI.java     // Network logic + game state (no UI code)
    │
    └── ui/
        ├── GameWindow.java       // Builds the main window layout
        ├── NameDialog.java       // Startup name entry popup
        │
        ├── theme/
        │   └── AppTheme.java     // All colors, fonts, UI settings
        │
        └── components/
            ├── GlowPanel.java    // Rounded panel with border
            ├── MoveButton.java   // Rock/Paper/Scissors buttons
            ├── GlowButton.java   // General-purpose button
            └── RoundBorder.java  // Rounded border utility
            
🧠 Separation of Concerns
Folder	Responsibility
server/	Networking + game rules (No Swing code)
client/	Server connection + message handling
ui/	GUI layout and rendering
ui/theme/	Centralized colors and fonts
components/	Reusable UI components

⚙️ Requirements
Java 17 or higher
Supports modern Java features (switch expressions, pattern matching)

🛠️ Compile Project
javac -d out src\server\*.java src\client\*.java src\ui\*.java src\ui\theme\*.java src\ui\components\*.java

▶️ Run Application
Start Server
java -cp out server.RPSServer
Start Client (Player 1)
java -cp out client.RPSClientGUI
Start Client (Player 2)
java -cp out client.RPSClientGUI
🌐 Play Over LAN (Different Machines)
Start server on host machine
