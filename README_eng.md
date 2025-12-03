
---

## 🇬🇧 `README_EN.md`

```markdown
# SnovyMafia

**SnovyMafia** is a full-featured plugin for **Minecraft (Paper/Purpur 1.21.8)** that implements the **"Mafia"** game with extended features, customizable roles, GUI menu, and a host system.

---

## ✅ Features

- 🎭 **20 roles** with unique abilities (mafia, doctor, sheriff, maniac, stripper, etc.)
- 🎮 **Host system** — manage the game via GUI
- 📜 **Role books** and instructions for players
- ⏱ **Time settings** (night, voting, start countdown)
- 📊 **Scoreboard** with phase and time display
- 🎯 **Chat voting** (`/vote <player>`)
- 🧠 **Click on players** to perform night actions
- 🌙 **Minecraft time sync** with game phases
- 👻 **Night role invisibility** and **silent actions**
- 🔄 **Player queue** (`/mafia join`, `/mafia leave`)
- ⚙️ **GUI settings** for roles and time
- 📦 **Gradle (Groovy DSL) build**

---

## 🛠 Installation

1. Build the plugin:
   ```bash
   ./gradlew clean build

2. Copy build/libs/SnovyMafia-1.0.jar to your server's plugins folder.
3. Restart the server.
4. Configure config.yml to your needs.

📜 Commands
Command
Description
/mafia
Open management GUI (host only)
/mafia join
Join the game
/mafia leave
Leave the queue
/mafia leading add <name>
Add a host
/mafia leading remove <name>
Remove a host
/vote <name>
Vote for a player (during day)

⚙️ Configuration
All settings are in config.yml:

1 night-duration: 120              # Night duration in seconds
2 day-vote-duration: 60            # Voting duration in seconds
3 start-countdown-duration: 60     # Countdown before start in seconds
4 leaders: []                      # List of hosts
5 enabled-roles:                   # Enabled roles
6 villager: true
7 mafia: true
8 don: false
9 sheriff: true
10 doctor: true
11 # ... etc.

🎨 Features
Night role invisibility — mafia can move unseen
Silent doors — no sound when opening
Animated prefix — 🎭 in chat
Scoreboard with phase and timer
Instruction books — for all roles
Custom titles — HUD notifications at night

🧩 Project Structure
SnovyMafia/
├── build.gradle
├── src/
│   └── main/
│       ├── java/
│       │   └── dev/fassykite/snovymafia/
│       │       ├── SnovyMafia.java
│       │       ├── commands/
│       │       ├── game/
│       │       ├── gui/
│       │       └── listeners/
│       └── resources/
│           ├── plugin.yml
│           └── config.yml

🚀 Starting the Game
Become a host: /mafia leading add <your_nick>
Open GUI: /mafia
Click "▶ Start Game (60s)" or "⚡ Start Game (immediate)"
Players can join: /mafia join

📦 Dependencies
Java 21
Paper/Purpur 1.21.8
Gradle (Groovy DSL)

📞 Support
If you have questions or bugs — create an Issue in the repository.

Author: Fassykite
License: MIT