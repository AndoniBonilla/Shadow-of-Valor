A 2D Java based fighting game
A JavaFX-based fighting game that features unique characters, abilties and projectile mechanics.



This game is a 1v1 based fighting game with a pixelated logo, character move list, and character selection screen. 

- Multiple playable characters with unique abilties
  
- Combat mechanics such as attacking, blocking, and projectiles
  
- Single Round matches with win/loss conditions
- Intallation/Setup
  
- Java 22.0.2
  
- JavaFX 23.0.1 (Zip file linked for MacOS 64x SDK) 
  
- Download JavaFX 23.01 from Gloun
  
- Below are the different ones
  
- <img width="1208" alt="Screenshot 2025-02-01 at 4 11 09 PM" src="https://github.com/user-attachments/assets/0fd80669-98eb-4203-b206-e60f8d392f37" />
1. Linux (x64) SDK
Download: This version is a JavaFX libary complied for 64 bit Linux systems.
File: openjfx-23.0.1_linux-x64_bin-sdk.zip
   
2. Linux (x64) jmods
This version is for Linux systems but for JMOD files files instead of SDK. JMOD files are used for modular devlopement and focused on advaanced JavaFX usage. 
File: openjfx-23.0.1_linux-x64_bin-jmods.zip

3. macOS (x64) SDK
Download: This version is suitable for macOS computers with Intel processors. 
File: javafx-sdk-23.0.1_macos-x64_sdk.zip

4. macOS (x64) jmods
Download: This version is for macOS systems with Intel processors (64-bit architecture) but includes JMOD files instead of the SDK. JMOD files are typically used for modular development and are more focused on advanced JavaFX usage.
File: javafx-sdk-23.0.1_macos-x64_jmods.zip

5. macOS (aarch64) SDK
Download: This version is designed for macOS systems with Apple Silicon processors (M1, M2). 
File: javafx-sdk-23.0.1_macos-aarch64_sdk.zip

6. macOS (aarch64) jmods
Download: Similar to the x64 jmods version, this is for Apple Silicon macOS systems and includes JMOD files.
File: javafx-sdk-23.0.1_macos-aarch64_jmods.zip

7. Windows (x64) SDK
Download: This version is for Windows systems with 64-bit architecture (e.g., most modern Windows computers). Use if you are  are using Windows 10 or 11 (with an Intel or AMD 64-bit processor).
File: javafx-sdk-23.0.1_windows-x64_sdk.zip

8. Windows (x64) jmods
Download: This is for Windows systems with 64-bit architecture but specifically contains JMOD files for modular JavaFX development.
File: javafx-sdk-23.0.1_windows-x64_jmods.zip

9. Javadocs
Download: This download is the JavaFX documentation in a compressed format. 
File: javafx-javadoc-23.0.1.zip

### Steps to Run 
1. Download Eclipse 
2. Open a new Java Project
3. Make a package
4. Create the classes: 
5. Connect your project files to this package
6. Go to Build Path < Configure Build Path< Add External JARs 
7. Downlad the following jar files for javaFX(this should be found fromt the download from Gluon):
<img width="464" alt="Screenshot 2025-01-30 at 12 03 09 AM" src="https://github.com/user-attachments/assets/8b17d925-b747-4fcd-b5b0-e0d7b99dc533" />

8. This is found in the lib folder on your javafx-sdk 23.01.
9. <img width="473" alt="Screenshot 2025-02-01 at 4 14 40 PM" src="https://github.com/user-attachments/assets/4668e6ba-f6e8-4397-ba5c-28cd9c75bbf1" />

10. Select the files seen in step 6:
<img width="1302" alt="Screenshot 2025-02-01 at 4 15 13 PM" src="https://github.com/user-attachments/assets/3d171111-461c-4757-a0ab-efb814dbe394" />

11. In eclipse go to the Run drop down and find the Run configurations.
12. Make sure your Java SE is 22.0.1 and that your JavaFX SDK is 23.0.1(This may be differnet on windows, has worked before with Jave 23.01, try both options)
13. In run configurations, for VM argument put this except your user/User/place where your javaFX jar files are(mine were in(Downloads)//javafx-sdk-23.0.1/lib --add-modules javafx.controls,javafx.fxml
14. Example:. <img width="689" alt="Screenshot 2025-01-30 at 12 08 46 AM" src="https://github.com/user-attachments/assets/a929046d-35cd-4a71-ad95-46c23ba3fe32" />

15. Press Apply and then run

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
Attacks: Each character has a basic attack with a 10% chance to miss an attack and a 30% chance to hit a critical attack. 
Blocking: Reduces damage but you lose some of your shield 
Projectiles: Mostly for fun, they don't do damage(unfortunatly this had to be sacrficed for gameplay functionality)
Abilties: Cooldowns apply to attacks and special abilities

Class Structure: 
/src

 ├── FightingGame Project:
 
 │   ├── AbilityManager.java         # Special abilities logic and cooldowns
 
 │   ├── Character.java              # Character logic and attributes
 
 │   ├── CharacterRoster.java        # Character stats such as health, special ability cooldowns
 
 │   ├── CharacterSelectScreen.java  # UI for character selection

 │   ├── FightingGameApp.java        # Main file that is used to show the logo screen, character select screen, and move screen to users
 
 │   ├── GameApp.java                # Keyboard input and tracking
  
 │   ├── Projectile.java             # Projectile creation and properites of a projectile

 │   ├── ProjectileManager.java      # Manages active projectiles and the removal of these projectiles

 │   ├── RoundManager.java            # Makes sure that there are only 2 players at one time
   
