A 2D Java based fighting game
A JavaFX-based fighting game that features unique characters, abilties and projectile mechanics.



This game is a 1v1 based fighting game with a pixelated logo, character move list, and character selection screen. 

- Multiple playable characters with unique abilties
  
- Combat mechanics such as attacking, blocking, and projectiles
  
- Single Round matches with win/loss conditions
- Intallation/Setup
  
- Java 22.01
  
- JavaFX 23.01 (Make sure you have the correct JavaFX SDK based on your system)
  
- Downlad JavaFX 23.01 from Gloun
  
- Below are the differnt ones
  
- <img width="1208" alt="Screenshot 2025-02-01 at 4 11 09 PM" src="https://github.com/user-attachments/assets/0fd80669-98eb-4203-b206-e60f8d392f37" />
1. macOS (x64) SDK
Download: This version is suitable for macOS computers with Intel processors (64-bit architecture). If you have a Mac with an Intel-based processor (like a MacBook Pro with Intel Core i7), they should download this version.
File: javafx-sdk-23.0.1_macos-x64_sdk.zip
Use: This download contains the full JavaFX SDK, which includes the libraries and documentation required to run JavaFX applications.
2. macOS (x64) jmods
Download: This version is for macOS systems with Intel processors (64-bit architecture) but includes JMOD files instead of the SDK. JMOD files are typically used for modular development and are more focused on advanced JavaFX usage.
File: javafx-sdk-23.0.1_macos-x64_jmods.zip
Use: Only download this version if you're specifically working with Java modules, but in most cases, the SDK version (above) should suffice.
3. macOS (aarch64) SDK
Download: This version is designed for macOS systems with Apple Silicon processors (M1, M2). If your client has a Mac with Apple Silicon (like a MacBook Air or MacBook Pro with an M1 or M2 chip), they should download this version.
File: javafx-sdk-23.0.1_macos-aarch64_sdk.zip
Use: This download includes the JavaFX SDK for ARM-based macOS systems.
4. macOS (aarch64) jmods
Download: Similar to the x64 jmods version, this is for Apple Silicon macOS systems and includes JMOD files.
File: javafx-sdk-23.0.1_macos-aarch64_jmods.zip
Use: Again, use this only if you need to work with modular JavaFX development. For most users, the SDK is sufficient.
5. Windows (x64) SDK
Download: This version is for Windows systems with 64-bit architecture (e.g., most modern Windows computers). Your client should download this if they are using Windows 10 or 11 (with an Intel or AMD 64-bit processor).
File: javafx-sdk-23.0.1_windows-x64_sdk.zip
Use: This is the standard JavaFX SDK for Windows, containing all the libraries required to run JavaFX apps.
6. Windows (x64) jmods
Download: This is for Windows systems with 64-bit architecture but specifically contains JMOD files for modular JavaFX development.
File: javafx-sdk-23.0.1_windows-x64_jmods.zip
Use: Use this if you need to work with modular development; otherwise, the SDK version (above) is the standard choice.
7. Javadocs
Download: This download is simply the JavaFX documentation in a compressed format and is not necessary for running applications. It’s useful for developers who want to refer to JavaFX documentation offline.
File: javafx-javadoc-23.0.1.zip
Use: This is only needed if you want to access detailed JavaFX API documentation offline.
For the downloads: Choose the download that goes with your computer and has the SDK download

* This project was done on Mac, so no guarantee that it will work on Windows)
* Windows may take a different approach

### Steps to Run 
1. Download Eclipse<img 
2. Open a new Java Project
3. Make a package
4. Connect your project files to this package
5. Go to Build Path < Donfigure Build Path< Add External JARs 
6. Downlad the following jar files for javaFX(this should be found fromt the download from Gluon):
<img width="464" alt="Screenshot 2025-01-30 at 12 03 09 AM" src="https://github.com/user-attachments/assets/8b17d925-b747-4fcd-b5b0-e0d7b99dc533" />

7. This is found in the lib folder on your javafx-sdk 23.01.
8. <img width="473" alt="Screenshot 2025-02-01 at 4 14 40 PM" src="https://github.com/user-attachments/assets/4668e6ba-f6e8-4397-ba5c-28cd9c75bbf1" />

9. Select the files seen in step 6:
<img width="1302" alt="Screenshot 2025-02-01 at 4 15 13 PM" src="https://github.com/user-attachments/assets/3d171111-461c-4757-a0ab-efb814dbe394" />

10. In eclipse go to the Run drop down and find the Run configurations.
11. Make sure your Java SE is 22.0.1 and that your JavaFX SDK is 23.0.1(This may be differnet on windows, has worked before with Jave 23.01, try both options)
12. In run configurations, for VM argument put this except your user/User/place where your javaFX jar files are(mine were in(Downloads)//javafx-sdk-23.0.1/lib --add-modules javafx.controls,javafx.fxml
13. Example:. <img width="689" alt="Screenshot 2025-01-30 at 12 08 46 AM" src="https://github.com/user-attachments/assets/a929046d-35cd-4a71-ad95-46c23ba3fe32" />

14. Press Apply and then run

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

Class Structure: 
/src

 ├── FightingGame Project:
 
 │   ├── GameApp.java             # Main game loop
 
 │   ├── Character.java            # Character logic and attributes
 
 │   ├── CharacterRoster.java      # Character selection
 
 │   ├── CharacterSelectScreen.java    # UI for character selection
 
 │   ├── FightArenaFrame.java     # Game window container
 
 │   ├── FightArenaPanel.java     # Renders characters, health bars, and round updates
 
 │   ├── RoundManager.java        # Handles rounds and win conditions
 
 │   ├── Projectile.java          # Projectile behavior and hit detection
 
 │   ├── ProjectileManager.java   # Manages active projectiles and collisions
 
 │   ├── AbilityManager.java      # Special abilities logic and cooldowns
 
 ├── assets/                     # Sprites and sounds
 
 
 



If you do use this, I hope you enjoy and if you make it better, send me a copy (:

