A 2D Java based fighting game
A JavaFX-based fighting game that features unique characters, abilities and projectile mechanics.


This game is a 1v1 based fighting game with a color-cycling logo, character move list, and character selection screen. 

- Multiple playable characters with unique abilities
  
- Combat mechanics such as attacking, blocking, and projectiles
  
- Single round matches with win/loss/draw conditions and a Rematch option
- Installation/Setup
  
- Java 22.0.2
  
- JavaFX 23.0.1 (Zip file linked for MacOS 64x SDK) 

- Download JavaFX 23.0.1 from Gluon
  
- Below are the different ones
  
- <img width="1208" alt="Screenshot 2025-02-01 at 4 11 09 PM" src="https://github.com/user-attachments/assets/0fd80669-98eb-4203-b206-e60f8d392f37" />

1. Linux (x64) SDK
Download: This version is a JavaFX library compiled for 64 bit Linux systems.
File: openjfx-23.0.1_linux-x64_bin-sdk.zip
   
2. Linux (x64) jmods
This version is for Linux systems but for JMOD files instead of SDK. JMOD files are used for modular development and focused on advanced JavaFX usage. 
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
Download: This version is for Windows systems with 64-bit architecture (e.g., most modern Windows computers). Use if you are using Windows 10 or 11 (with an Intel or AMD 64-bit processor).
File: javafx-sdk-23.0.1_windows-x64_sdk.zip

8. Windows (x64) jmods
Download: This is for Windows systems with 64-bit architecture but specifically contains JMOD files for modular JavaFX development.
File: javafx-sdk-23.0.1_windows-x64_jmods.zip

9. Javadocs
Download: This download is the JavaFX documentation in a compressed format. 
File: javafx-javadoc-23.0.1.zip

### Steps to Run 
1. Download Eclipse IDE for Java Developers

<img width="830" alt="Screenshot 2025-02-26 at 2 22 05 PM" src="https://github.com/user-attachments/assets/90ee2ab2-d483-4c41-a5d9-c556ff1f7b25" />

Windows:
- Download x86_64 for window computers with a 64-bit Intel and AMD processors 
File: eclipse-java-2024-12-R-win32-x86_64.zip

- Download AArch64 for windows on ARM (WoA) devices like Microsoft Surface Pro or other ARM-based Windows systems.
File: eclipse-java-2024-12-R-win32-aarch64.zip

MacOS:
- Download x86_64 for macOS computers with Intel processors
File: eclipse-java-2024-12-R-macosx-cocoa-x86_64.dmg

- Download AArch64 for macOS computers with M1 and M2 chips
File: eclipse-java-2024-12-R-macosx-cocoa-aarch64.dmg

Linux: 
- Download x86_64 for Linux computers with 64 bit Intel and AMD processors
File: eclipse-java-2024-12-R-linux-gtk-x86_64.tar.gz

- Download AArch64 for ARM-based Linux systems such as Raspberry Pi or ARM servers
File: eclipse-java-2024-12-R-linux-gtk-aarch64.tar.gz

- Download riscv64 for RISC-V 64-bit processors 
File: eclipse-java-2024-12-R-linux-gtk-riscv64.tar.gz

2. Open a new Java Project

3. Make a package to hold all of the classes


4. Create the classes in the package: AbilityManager.java, Character.java, CharacterRoster.java, CharacterSelectScreen.java, FightingGameApp.java, GameApp.java, GameSession.java, Projectile.java, ProjectileManager.java
<img width="269" alt="Screenshot 2025-02-26 at 3 09 21 PM" src="https://github.com/user-attachments/assets/0f487a89-8c66-4845-9067-a51fd43f55cd" />

5. Create a folder called Jar files for "Name of Project Files" from src folder 

6. Then create a folder called "Jar files for <Project Name>" for the highlighted jar files:

7. Find the from the javafx-sdk: 23.0.1 folder (the example has 23.0.1 2 due to having javafx-sdk: 23.0.1 folders) to the folder "Jar files for the project":


8. Select the following jar files from the lib folder in javafx-sdk: 23.0.1 folder
<img width="535" alt="Screenshot 2025-02-26 at 3 59 35 PM" src="https://github.com/user-attachments/assets/66678913-6461-4712-a853-98239b046b76" />

9. Right Click your project in the project Manager < Go to Build Path < Configure Build Path
    
<img width="416" alt="Screenshot 2025-02-26 at 4 03 31 PM" src="https://github.com/user-attachments/assets/4b46442e-0e6f-4681-8803-8730570b4f7d" />

10. Then, press the module path and press the "Add JAR button"
<img width="1016" alt="Screenshot 2025-02-26 at 4 05 22 PM" src="https://github.com/user-attachments/assets/22e405b4-da35-4f9b-9564-7ce09b67ca15" />

11. Press Apply, and then "Apply and Close"
  
12. In Eclipse, go to the Run drop down and find Run < Run Configurations

<img width="900" alt="Screenshot 2025-02-26 at 4 24 56 PM" src="https://github.com/user-attachments/assets/9a53afe4-503b-412d-a46a-dd96d2b1a68b" />

13. In "Main" check that the main class is the FightingGameApp file or the file that contains and that the project is the name of the project
" public static void main(String[] args) 
{
    launch(args); 
}

<img width="760" alt="Screenshot 2025-02-26 at 4 23 33 PM" src="https://github.com/user-attachments/assets/02261315-ba9b-4491-83c4-42a38e12341a" />

14. Move to "Arguments" and locate the VM arguments box

<img width="956" alt="Screenshot 2025-02-26 at 4 41 51 PM" src="https://github.com/user-attachments/assets/adb04ba4-f098-4c18-a9dd-4e8d99c7ffea" />

15. Find the JavaFX SDK 23.0.1 and locate the lib folder (this will be used in the VM arguments)

16. Based on your system use these configurations:

MacOS/Linux:

```
--module-path /Users/YOUR_USERNAME/Downloads/javafx-sdk-23.0.1/lib
--add-modules javafx.controls
-Djava.library.path=/Users/YOUR_USERNAME/Downloads/javafx-sdk-23.0.1/lib
```

Windows:
```
--module-path C:\javafx-sdk-23.0.1\lib
--add-modules javafx.controls
-Djava.library.path=C:\javafx-sdk-23.0.1\lib
```

17. Press Apply and then run

### Troubleshooting

If the window does not appear or rendering looks incorrect, add these extra VM arguments:

```
-Dprism.order=sw
-Djavafx.platform=macos
```

(The macOS flag only applies on macOS; the software renderer flag can help on any platform if hardware acceleration fails.)




Gameplay Instructions: 

Character Selection:

Choose a fighter from the character selection screen. Select Ready up when you are sure you select a character, you cannot undo this. 
If you want to switch character, before you ready up, press the character deselect. 
Both players may pick the same character (mirror matches are supported).
Each character has a unique special ability, or at least some similar ones with augmentations. 

Movement: WASD(Player 1) Arrow Keys(Player 2)

Basic Attack: X(P1) / J(P2)

Special Ability: C(P1) / K(P2)

Block: Z(P1) / M(P2)

Projectile: V(P1) / L(P2)

Game Mechanics: 
Attacks: Each character has a basic attack with a 10% chance to miss an attack and a 30% chance to land a critical hit. 

Blocking: Reduces damage but depletes your shield. Shield recharges over time; after it breaks it cannot be reactivated for 5 seconds.

Projectiles: Charged by holding V(P1)/L(P2) and released to fire. Larger projectiles deal more damage. Projectiles from opposing players cancel each other out (the larger one survives).

Abilities: Cooldowns apply to special abilities.

Class Structure: 
/src

 ├── FightingGame Project:
 
 │   ├── AbilityManager.java         # Special abilities logic and cooldowns
 
 │   ├── Character.java              # Character logic, attributes, and buff system
 
 │   ├── CharacterRoster.java        # Character specs (name, stats, ability, color)
 
 │   ├── CharacterSelectScreen.java  # UI for character selection
 
 │   ├── FightingGameApp.java        # Entry point: logo, main menu, character select
 
 │   ├── GameApp.java                # Thin static adapter for FightingGameApp → GameSession
  
 │   ├── GameSession.java            # One complete match: HUD, input, game loop, end-of-match
  
 │   ├── Projectile.java             # Projectile creation and properties
 
 │   ├── ProjectileManager.java      # Manages active projectiles and collision detection
   
 │   ├── Fighting Game Files 
         ├── Jar files for FightingGame
             ├──javafx.base.jar
             ├──javafx.controls.jar
             ├──javafx.fxml.jar
             ├──javafx.graphics.jar
             ├──javafx.media.jar
             ├──javafx.web.jar
