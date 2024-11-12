package FightingGame;

import java.util.Scanner;
import javafx.application.Application;
import javafx.stage.Stage;

public class FightingGameApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Display the ASCII logo, then prompt the user to go to the character selection screen
        displayLogoWithColorChange(() -> {
            // After logo, go to character selection
        	promptUserChoice();
        });
        
    }

    public static void main(String[] args) 
    {
        launch(args);
    }


    private void displayLogoWithColorChange(Runnable onLogoComplete)
    {
        String[] colors = 
        	{
            "\033[0;31m", // Red
            "\033[0;32m", // Green
            "\033[0;33m", // Yellow
            "\033[0;34m", // Blue
            "\033[0;35m", // Purple
            "\033[0;36m", // Cyan
            "\033[0;37m", // White
            "\033[1;31m", // Bright Red
            "\033[1;32m", // Bright Green
            "\033[1;33m", // Bright Yellow
            "\033[1;34m", // Bright Blue
            "\033[1;35m"  // Bright Purple
        };
        

        // Display the logo in each color sequentially
        for (String color : colors) {
            // Clear the console by printing blank lines
            for (int i = 0; i < 25; i++) {
                System.out.println();
            }
            System.out.println(color +
            				   " ███████╗██╗  ██╗ █████╗  ██████╗   ███████  ██╗   ╔██ 	  ███████  ██████        ");
            System.out.println(" ██╔════╝██║  ██║██╔══██╗ ██╔══██║ ██╔═══██╗ ██║   ║██ 	  ██╔══██╗ ██╔══╗   ");
            System.out.println(" ███████╗███████║███████║ ██║  ██║ ██║   ██║ ██║╔█╗║██ 	  ██║  ██║ ██████╔");
            System.out.println(" ╚════██║██╔══██║██╔══██║ ██║  ██║ ██║   ██║ ██║███║██ 	  ██║  ██║ ██╔═══╝ ");
            System.out.println(" ███████║██║  ██║██║  ██║ █████╔═╝ ╚██████╔╝  ███ ███     ███████  ██║     ");
            System.out.println(" ╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝ ╚════╝        				    	                     ");
            System.out.println("                 ██╗   ██╗ █████╗ ██╗       █████╗  ██████╗ ");
            System.out.println("                 ██║   ██║██╔══██╗██║     ██╔═══██╗ ██╔══██╗");
            System.out.println("                 ██║   ██║███████║██║     ██║   ██║ ██████╔╝");
            System.out.println("                 ╚██╗ ██╔╝██╔══██║██║     ██║   ██║ ██╔═╗██   ");
            System.out.println("                  ╚████╔╝ ██║  ██║███████╗ ██████╔╝ ██║ ╚═██ ");
            System.out.println("                   ╚═══╝  ╚═╝  ╚═╝╚══════╝ ╚═════╝  ╚═╝    ██   ");
            System.out.println("\033[0m");
            try 
            {
                Thread.sleep(100);
            } 
            catch (InterruptedException e) 
            {
                e.printStackTrace();
            }
        }
        onLogoComplete.run();
    }

    private void promptUserChoice() 
    {
        try (Scanner scanner = new Scanner(System.in)) 
        {
            System.out.println("\nWhat would you like to do next?");
            System.out.println("1. Go to Character Selection Screen");
            System.out.println("2. Exit the Game");

            System.out.print("Enter your choice (1 or 2): ");
            int choice = scanner.nextInt();

            if (choice == 1) 
            {
                showCharacterSelectionScreen();
            } 
            else 
            {
                System.out.println("Exiting the game. Goodbye!");
                System.exit(0);
            }
        }
    }

    private void showCharacterSelectionScreen() {
        CharacterSelectScreen characterSelectScreen = new CharacterSelectScreen();

        characterSelectScreen.setOnReady((player1, player2) -> startGameWithCharacters(player1, player2));

        primaryStage.setScene(characterSelectScreen.getScene());
        primaryStage.show();
    }

    private void startGameWithCharacters(Character player1Character, Character player2Character) 
    {
        GameApp.setPrimaryStage(primaryStage);
        GameApp.startGameWithCharacters(player1Character, player2Character);
    }
}