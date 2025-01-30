A 2D Java based fighting game
A JavaFX-based fighting game that features unique characters, abilties and projectile mechanics.



This game is a 1v1 based fighting game with a pixelated logo, character move list, and character selection screen. 
- Multiple playable characters with unique abilties
- Combat mechanics such as attacking, blocking, and projectiles
- Single Round matches with win/loss conditions
- Intallation/Setup
- Java 22.01
- JavaFX 22 (Make sure you have the correct JavaFX SDK)
* This project was done on Mac, so no guarantee that it will work on Windows)
* Windows may take a different approach

### Steps to Run 
1. Download Eclipse<img 
2. Open a new Java Project
3. Make a package
4. Connect your project files to this package
5. Go to Build Path < Donfigure Build Path< Add External JARs 
6. Downlad the following jar files for javaFX: <img width="464" alt="Screenshot 2025-01-30 at 12 03 09 AM" src="https://github.com/user-attachments/assets/8b17d925-b747-4fcd-b5b0-e0d7b99dc533" />
7. In eclipse go to the Run drop down and find the Run configurations.
8. Make sure your Java SE is 22.0.1 and that your JavaFX SDK is 23.0.1
9. In run configurations, for VM argument put this except your user/User/place where your javaFX jar files are(mine were in(Downloads)//javafx-sdk-23.0.1/lib --add-modules javafx.controls,javafx.fxml
10. Example: <img width="689" alt="Screenshot 2025-01-30 at 12 08 46 AM" src="https://github.com/user-attachments/assets/a929046d-35cd-4a71-ad95-46c23ba3fe32" />
11. Press Apply and then run

Preferably use Eclipse(I don't have knowledge of other IDE's)
Another way(might work, might not) 
git clone https://github.com/YourUsername/JavaFightingGame.git
cd JavaFightingGame
javac --module-path "path_to_javafx_lib" --add-modules javafx.controls,javafx.fxml -d bin src/FightingGame/*.java
For refernece: 
java --module-path "path_to_javafx_lib" --add-modules javafx.controls,javafx.fxml -cp bin FightingGame.GameApp



Gameplay Instructions: 
Character Selection:
Choose a fighter from the character selection screen. Select Ready up when you are sure you select a character, cannot undo this. 
If you want to switch character, before you ready up, press the character deselct. 
Each character has unique a special ability, or at least some similar ones just slightly augmented. 
Movement: WASD(Player 1) Arrow Keys(Player 2)
Basic Attack: X(P1) / J(P2)
Special Ability: C(P1) / K(P2)
Block: Z(P1) / M(P2)
Projectile: V(P1) / L(P2)

Game Mechanics: 
Attacks: Each character has a basic attack and special ability
Blocking: Reduces damage but you lose some of your shield 
Projectiles: Mostly for fun, they don't do damage(unfortunatly this had to be sacrficed for gameplay functionality)
Abilties: Cooldowns apply to attacks and special abilities

Projectile Structure: 
/src
 ├── FightingGame/
 │   ├── GameApp.java           # Main game loop
 │   ├── Character.java         # Character logic and attributes
 │   ├── CharacterRoster.java   # Character selection
 │   ├── CharacterSelectScreen.java  # UI for character selection
 │   ├── FightArenaFrame.java   # Game window container
 │   ├── FightArenaPanel.java   # Renders characters, health bars, and round updates
 │   ├── RoundManager.java      # Handles rounds and win conditions
 │   ├── Projectile.java        # Projectile behavior and hit detection
 │   ├── ProjectileManager.java # Manages active projectiles and collisions
 │   ├── AbilityManager.java    # Special abilities logic and cooldowns
 ├── assets/                    # Sprites and sounds
 ├── README.md                   # This file



If you do use this, I hope you enjoy and if you make it better, send me a copy (:

