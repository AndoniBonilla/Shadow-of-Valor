package FightingGame;

// Import JavaFX classes for layout and UI elements
import javafx.geometry.Pos; // For aligning elements within layouts       
import javafx.scene.Scene; // Represents a JavaFX scene
import javafx.scene.control.Button; // Button control for user interaction
import javafx.scene.control.Label; // Label control for displaying text
import javafx.scene.layout.GridPane; // A grid-based layout for arranging nodes
import javafx.scene.layout.HBox; // A horizontal box layout
import javafx.scene.layout.VBox; // A vertical box layout

import java.util.List; // List interface for managing characters
import java.util.function.BiConsumer; // Functional interface for handling two inputs (player1, player2)
 
/**
 * This class represents the character selection screen in the game.
 * Players can select characters, view their passive abilities, and mark readiness.
 */
public class CharacterSelectScreen 
{
    // Fields for managing the scene and player selections
    private Scene scene; // The main scene for the character selection screen
    private Label player1Selection; // Label to display Player 1's character selection
    private Label player2Selection; // Label to display Player 2's character selection
    private boolean player1Ready = false; // Tracks if Player 1 is ready
    private boolean player2Ready = false; // Tracks if Player 2 is ready
    private Character player1Character; // Player 1's selected character
    private Character player2Character; // Player 2's selected character
    private Button player1ReadyButton; // Button to mark Player 1 as ready
    private Button player2ReadyButton; // Button to mark Player 2 as ready
    private BiConsumer<Character, Character> onReady; // Callback function for when both players are ready

    /**
     * Sets the callback function to execute when both players are ready.
     * @param onReady A BiConsumer that accepts Player 1 and Player 2's characters.
     */
    public void setOnReady(BiConsumer<Character, Character> onReady)
    {
        this.onReady = onReady;
    }

 
    /**
     * Builds and returns the character selection screen as a JavaFX `Scene`.
     * The scene includes:
     * 1. A grid for displaying characters with their passive abilities.
     * 2. Controls for Player 1 and Player 2 to select, ready up, or deselect characters.
     * 3. A "Start Game" button to transition to gameplay once both players are ready.
     * 
     * @return The constructed Scene object for character selection.
     */
    public Scene getScene()
    {
        // Create the main vertical layout for the character selection screen
        VBox mainLayout = new VBox(15); // Vertical layout with 15px spacing
        mainLayout.setAlignment(Pos.CENTER); // Center all elements horizontally and vertically

        // Grid to display characters and their passive abilities
        GridPane characterGrid = new GridPane(); // Grid layout for character buttons
        characterGrid.setHgap(10); // 10px horizontal spacing between cells
        characterGrid.setVgap(10); // 10px vertical spacing between cells

        // Retrieve the list of characters and ensure all passive abilities are defined
        List<Character> characters = CharacterRoster.getCharacters(); // Load all available characters

        // Populate the grid with character buttons and passive labels
        for (int i = 0; i < characters.size(); i++)
        {
            Character character = characters.get(i);

            // Create a vertical box for each character's button and passive label
            VBox characterBox = new VBox(5); // Vertical layout with 5px spacing
            characterBox.setAlignment(Pos.CENTER); // Center-align elements within the box

            // Button for selecting the character
            Button characterButton = new Button(character.getName()); // Button labeled with the character's name
            characterButton.setOnAction(e -> selectCharacter(character)); // Assign an action to handle selection

            // Label to display the game's characters
            Label newLabel = new Label(); // Label showing the passive
            newLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;"); // Set font size and color for the label

            // Add the button and label to the character's vertical box
            characterBox.getChildren().addAll(characterButton, newLabel);

            // Add the character box to the grid (arranged in rows of 4)
            characterGrid.add(characterBox, i % 4, i / 4); // Columns determined by index mod 4
        }

        // Player 1 selection 
        player1Selection = new Label("Player 1: Not selected"); // Label showing Player 1's selection status
        player1ReadyButton = new Button("Player 1 Ready"); // Button to mark Player 1 as ready
        player1ReadyButton.setDisable(true); // Initially disabled until a character is selected
        player1ReadyButton.setOnAction(e -> 
        {
            player1Ready = true; // Mark Player 1 as ready
            player1ReadyButton.setDisable(true); // Disable the button to prevent multiple clicks
        });

        // Button to allow Player 1 to deselect their chosen character
        Button player1DeselectButton = new Button("Deselect Player 1");
        player1DeselectButton.setOnAction(e -> 
        {
            if (!player1Ready) 
            { // Only allow deselection if Player 1 is not ready
                player1Character = null; // Clear Player 1's selected character
                player1Selection.setText("Player 1: Not selected"); // Update the selection label
                player1ReadyButton.setDisable(true); // Disable the ready button
            }
        });

        // Layout for Player 1
        VBox player1Controls = new VBox(10, player1Selection, player1ReadyButton, player1DeselectButton);
        player1Controls.setAlignment(Pos.CENTER); // Center-align Player 1's controls

        // Button to allow Player 2 to select a different character 
        player2Selection = new Label("Player 2: Not selected"); // Label showing Player 2's selection status
        player2ReadyButton = new Button("Player 2 Ready"); // Button to mark Player 2 as ready
        player2ReadyButton.setDisable(true); // Initially disabled until a character is selected
        player2ReadyButton.setOnAction(e -> 
        {
            player2Ready = true; // Mark Player 2 as ready
            player2ReadyButton.setDisable(true); // Disable the button to prevent multiple clicks
        });

        // Button to allow Player 2 to deselect their chosen character
        Button player2DeselectButton = new Button("Deselect Player 2");
        player2DeselectButton.setOnAction(e -> 
        {
            if (!player2Ready)
            { // Only allow deselection if Player 2 is not ready
                player2Character = null; // Clear Player 2's selected character
                player2Selection.setText("Player 2: Not selected"); // Update the selection label
                player2ReadyButton.setDisable(true); // Disable the ready button
            }
        });

        // Layout for Player 2's controls
        VBox player2Controls = new VBox(10, player2Selection, player2ReadyButton, player2DeselectButton);
        player2Controls.setAlignment(Pos.CENTER); // Center-align Player 2's controls

        // Button to start the game once both players are ready
        Button startGameButton = new Button("Start Game");
        startGameButton.setOnAction(e -> 
        {
            if (player1Ready && player2Ready) 
            { // Check if both players are marked as ready
                if (player1Character != null && player2Character != null) 
                { // Ensure both characters are selected
                    // Log the selected characters and their passives
                    System.out.println("Starting game with:");
                    System.out.println("Player 1: " + player1Character.getName());
                    System.out.println("Player 2: " + player2Character.getName());
                    if (onReady != null) { // If a callback for readiness is defined
                        onReady.accept(player1Character, player2Character); // Pass selected characters to the callback
                    }
                } 
                else 
                {
                    System.err.println("Both characters must be selected before starting the game.");
                }
            } 
            else 
            {
                System.out.println("Both players need to be ready."); // Prompt readiness if not fulfilled
            }
        });

        // Layout combining Player 1 and Player 2 controls horizontally
        HBox playerControls = new HBox(50, player1Controls, player2Controls); // Horizontal layout with 50px spacing
        playerControls.setAlignment(Pos.CENTER); // Center-align the controls

        // Add all elements to the main vertical layout
        mainLayout.getChildren().addAll(characterGrid, playerControls, startGameButton);

        // Create a scene with the main layout and set its dimensions
        scene = new Scene(mainLayout, 600, 400);
        return scene; // Return the constructed scene
    }

    /**
     * Handles character selection for Player 1 or Player 2.
     * This method ensures that:
     * 1. Characters are not selected by both players simultaneously.
     * 2. Player 1 and Player 2 can select their characters independently.
     * 3. Once selected, the appropriate buttons and labels are updated to reflect the player's choice.
     *
     * @param character The selected character that the player is attempting to choose.
     */
    private void selectCharacter(Character character) 
    {
        // Check if the character is already selected by either player
        if ((player1Character == character) || (player2Character == character))
        {
            System.out.println("Character " + character.getName() + " is already selected.");
            return; // Prevent duplicate selection and exit the method
        }

        // Check if Player 1 is not ready and hasn't selected a character yet
        if (!player1Ready && player1Character == null) 
        {
            // Update Player 1's selection label and assign the character
            player1Selection.setText("Player 1: " + character.getName());
            player1Character = character;
            player1ReadyButton.setDisable(false); // Enable the "Ready" button for Player 1

            // Log Player 1's selection and the chosen character's passive ability
            System.out.println("Player 1 selected: " + player1Character.getName());  
                              
        } 
        // Check if Player 2 is not ready and hasn't selected a character yet
        else if (!player2Ready && player2Character == null) 
        {
            // Update Player 2's selection label and assign the character
            player2Selection.setText("Player 2: " + character.getName());
            player2Character = character;
            player2ReadyButton.setDisable(false); // Enable the "Ready" button for Player 2

            // Log Player 2's selection and the chosen character's passive ability
            System.out.println("Player 2 selected: " + player2Character.getName());
        }
    }
   
}
