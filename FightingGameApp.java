package FightingGame;

// Import necessary Java utilities and JavaFX components for the graphical user interface
import java.util.Scanner;  // Import Scanner for text-based input

import javafx.application.Application; // Base class for JavaFX applications
import javafx.scene.Scene; // Represents the contents of a JavaFX stage
import javafx.scene.control.Button; // Button UI component for interactions
import javafx.scene.control.Label; // Label UI component for displaying text
import javafx.scene.layout.VBox; // Layout container that arranges children vertically
import javafx.stage.Stage; // The primary window container for JavaFX applications

/**
 * Main application class for the Fighting Game.
 * Handles initialization, user navigation, and transitions between scenes.
 */
public class FightingGameApp extends Application {

    private static Stage primaryStage; // The main stage for displaying scenes

    /**
     * Entry point for JavaFX applications.
     * Initializes the main stage and starts the game by displaying the logo.
     *
     * @param primaryStage The primary stage provided by JavaFX.
     */
    @Override
    public void start(Stage primaryStage) {
        FightingGameApp.primaryStage = primaryStage; // Set the primary stage for the application

        // Display the ASCII logo, then prompt the user to navigate to the next screen
        displayLogoWithColorChange(() -> {
            // After the logo display is complete, prompt the user for the next action
            promptUserChoice();
        });
    }

    /**
     * Main method to launch the JavaFX application.
     *
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }

    /**
     * Displays an ASCII logo with a color-changing effect for visual flair.
     * Once complete, it runs the provided onLogoComplete Runnable.
     *
     * @param onLogoComplete Callback to execute after the logo display completes.
     */
    private void displayLogoWithColorChange(Runnable onLogoComplete) {
        // Define an array of ANSI color codes for the logo text
        String[] colors = {
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

        // Loop through each color code to display the logo in alternating colors
        for (String color : colors) {
            // Clear the console by printing multiple blank lines
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }

            // Print the logo text in the current color
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
            System.out.println("\033[0m"); // Reset to the default color

            try {
                Thread.sleep(800); // Pause for 1 second before switching colors
            } catch (InterruptedException e) {
                e.printStackTrace(); // Handle any interruption errors
            }
        }

        // Execute the next action after the logo display completes
        onLogoComplete.run();
    }

    /**
     * Prompts the user to choose their next action from a list of options.
     * Options include navigating to the character selection screen, move list, or backstory.
     */
    public void promptUserChoice() {
        try (Scanner scanner = new Scanner(System.in)) {
            int choice = 0;

            // Loop until the user enters a valid choice
            while (true) {
                System.out.println("\nWhat would you like to do next?");
                System.out.println("1. Go to Character Selection Screen");
                System.out.println("2. View Move List");
                System.out.println("3. Exit the Game");
                System.out.print("Enter your choice (1, 2, or 3): ");

                // Validate the user's input
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();

                    // Execute the corresponding action based on the user's choice
                    switch (choice) {
                        case 1:
                            showCharacterSelectionScreen(); // Navigate to the character selection screen
                            return; // Exit the method
                        case 2:
                            showMoveListScreen(); // Navigate to the move list screen
                            return; // Exit the method
                      
                        case 3:
                            System.out.println("Exiting the game. Goodbye!"); // Exit the game
                            System.exit(0); // Terminate the application
                        default:
                            System.out.println("Invalid choice. Please enter 1, 2, or 3"); // Invalid choice handler
                    }
                } else {
                    System.out.println("Invalid input. Please enter a number (1, 2, or 3)"); // Handle non-integer input
                    scanner.next(); // Clear invalid input
                }
            }
        }
    }


    /**
     * Displays the character selection screen.
     * Sets a callback to start the game once both players are ready.
     */
    private void showCharacterSelectionScreen() {
        CharacterSelectScreen characterSelectScreen = new CharacterSelectScreen();

        // Set the callback to start the game once both players are ready
        characterSelectScreen.setOnReady((player1, player2) -> startGameWithCharacters(player1, player2));

        // Set the character selection scene on the primary stage
        primaryStage.setScene(characterSelectScreen.getScene());
        primaryStage.show();
    }

    /**
     * Starts the game with the selected characters.
     *
     * @param player1Character The character selected by Player 1.
     * @param player2Character The character selected by Player 2.
     */
    private void startGameWithCharacters(Character player1Character, Character player2Character) {
        // Pass the characters to the game logic and begin the match
        GameApp.setPrimaryStage(primaryStage);
        GameApp.startGameWithCharacters(player1Character, player2Character);
    }


    /**
     * Displays the move list for the characters in a new window.
     */
    private void showMoveListScreen() {
        // Setup and display the move list screen
        Stage moveListStage = new Stage(); // Create a new stage for the move list screen
        VBox root = new VBox(10); // Use a vertical layout with 10px spacing between elements
        root.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: white;"); // Apply padding, alignment, and background color styles

        // Add a title label for the move list
        Label title = new Label("Character Move List");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;"); // Style the title to be large and bold

        // Add the move list details in a label
        Label moveList = new Label(getMoveListDetails()); // Fetch the formatted move list details
        moveList.setStyle("-fx-font-size: 14px;"); // Style the text for readability
        moveList.setWrapText(true); // Allow the text to wrap if it exceeds the label's width

        // Add a back button to close the move list screen
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> moveListStage.close()); // Close the stage when the button is clicked

        // Add the title, move list, and back button to the layout
        root.getChildren().addAll(title, moveList, backButton);

        // Create a new scene with the root layout and set it on the stage
        Scene scene = new Scene(root, 600, 400); // Set dimensions for the move list screen
        moveListStage.setScene(scene);
        moveListStage.setTitle("Move List"); // Set the title of the stage
        moveListStage.show(); // Show the stage to the user
    }


    /**
     * Helper method to return the move list details as a formatted string.
     *
     * @return A string containing the move list for the game characters.
     */
    private String getMoveListDetails() {
        // Return a formatted string containing the move list details for both players
        return """
                Character Move List:
                Player 1:
                - Move: W (Up), A (Left), S (Down), D (Right): Movement keys for Player 1
                - Basic Attack: X: Basic attack key for Player 1
                - Special Ability: C: Special ability key for Player 1
                - Shield: Z:  Shield key for Player 1
                - Projectile: V:  Projectile key for Player 1
                - Readying up will not let you deselect your character, once you ready up, you cannot go back

                Player 2:
                - Move: Up Arrow (Up), Left Arrow (Left), Down Arrow (Down), Right Arrow (Right): Movement keys for Player 2
                - Basic Attack: J: Basic attack key for Player 2
                - Special Ability: K: Special ability key for Player 2
                - Shield: Z:  Shield key for Player 2
                - Projectile: V:  Projectile key for Player 2
                - Readying up will not let you deselect your character, once you ready up, you cannot go back
                
               """ ; 
    }

}
