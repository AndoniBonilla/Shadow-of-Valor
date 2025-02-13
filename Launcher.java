package FightingGame; 
// Declares the package `FightingGame`, grouping related game classes together.

public class Launcher 
{
    // The Launcher class serves as the entry point for the application.

    public static void main(String[] args) 
    {
        // The main method is the entry point of a Java application.
        
        // Launch the FightingGameApp JavaFX application
        FightingGameApp.main(args);
        // Calls the `main` method in `FightingGameApp` to start the game.
        // This delegates the responsibility of launching the JavaFX application
        // and setting up the game to the FightingGameApp class.
    }
}
