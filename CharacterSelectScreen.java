package FightingGame;

// Import JavaFX classes for layout and UI elements
import javafx.geometry.Pos; // For aligning elements within layouts
import javafx.scene.Scene; // Represents a JavaFX scene
import javafx.scene.control.Button; // Button control for user interaction
import javafx.scene.control.Label; // Label control for displaying text
import javafx.scene.layout.GridPane; // A grid-based layout for arranging nodes
import javafx.scene.layout.HBox; // A horizontal box layout
import javafx.scene.layout.VBox; // A vertical box layout

import java.util.List; // List interface for managing specs
import java.util.function.BiConsumer; // Functional interface for handling two inputs (player1, player2)

/**
 * This class represents the character selection screen in the game.
 * Players can select characters, view their stats, and mark readiness.
 * Mirror matches are allowed: both players may choose the same character. (U2)
 */
public class CharacterSelectScreen
{
    // Fields for managing the scene and player selections
    private Scene scene; // The main scene for the character selection screen
    private Label player1Selection; // Label to display Player 1's character selection
    private Label player2Selection; // Label to display Player 2's character selection
    private boolean player1Ready = false; // Tracks if Player 1 is ready
    private boolean player2Ready = false; // Tracks if Player 2 is ready
    private CharacterRoster.CharacterSpec player1Spec; // Spec selected by Player 1. (U2)
    private CharacterRoster.CharacterSpec player2Spec; // Spec selected by Player 2. (U2)
    private Button player1ReadyButton; // Button to mark Player 1 as ready
    private Button player2ReadyButton; // Button to mark Player 2 as ready

    // U2: callback receives CharacterSpecs; fresh Character instances are built from them.
    private BiConsumer<CharacterRoster.CharacterSpec, CharacterRoster.CharacterSpec> onReady;

    /**
     * Sets the callback function to execute when both players are ready.
     * @param onReady A BiConsumer that accepts Player 1 and Player 2's character specs.
     */
    public void setOnReady(BiConsumer<CharacterRoster.CharacterSpec, CharacterRoster.CharacterSpec> onReady)
    {
        this.onReady = onReady;
    }


    /**
     * Builds and returns the character selection screen as a JavaFX Scene.
     *
     * @return The constructed Scene object for character selection.
     */
    public Scene getScene()
    {
        // Create the main vertical layout for the character selection screen
        VBox mainLayout = new VBox(15); // Vertical layout with 15px spacing
        mainLayout.setAlignment(Pos.CENTER); // Center all elements horizontally and vertically

        // Grid to display characters
        GridPane characterGrid = new GridPane(); // Grid layout for character buttons
        characterGrid.setHgap(10); // 10px horizontal spacing between cells
        characterGrid.setVgap(10); // 10px vertical spacing between cells

        // Retrieve the list of specs; no Character instances are created here. (U2)
        List<CharacterRoster.CharacterSpec> specs = CharacterRoster.getCharacters();

        // Populate the grid with one button per spec.
        for (int i = 0; i < specs.size(); i++)
        {
            CharacterRoster.CharacterSpec spec = specs.get(i);

            // Q1: removed the dead empty newLabel that was here before.
            Button characterButton = new Button(spec.name()); // Button labeled with the spec's name.
            characterButton.setOnAction(e -> selectCharacter(spec)); // Assign selection handler.

            // Add the button to the grid (arranged in rows of 4).
            characterGrid.add(characterButton, i % 4, i / 4);
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
                player1Spec = null; // Clear Player 1's selected spec.
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
                player2Spec = null; // Clear Player 2's selected spec.
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
                if (player1Spec != null && player2Spec != null)
                { // Ensure both specs are selected
                    System.out.println("Starting game with:");
                    System.out.println("Player 1: " + player1Spec.name());
                    System.out.println("Player 2: " + player2Spec.name());
                    if (onReady != null)
                    {
                        onReady.accept(player1Spec, player2Spec); // Pass specs; instances built by caller.
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
     * Handles character spec selection for Player 1 or Player 2.
     * Mirror matches are allowed: both players may pick the same spec. (U2)
     * A click fills the first available (unselected, not-yet-ready) slot.
     *
     * @param spec The selected character spec that the player is choosing.
     */
    private void selectCharacter(CharacterRoster.CharacterSpec spec)
    {
        // Assign to Player 1's slot if it is empty and Player 1 is not yet ready.
        if (!player1Ready && player1Spec == null)
        {
            player1Spec = spec;
            player1Selection.setText("Player 1: " + spec.name());
            player1ReadyButton.setDisable(false); // Enable the "Ready" button for Player 1.
            System.out.println("Player 1 selected: " + spec.name());
        }
        // Otherwise assign to Player 2's slot if it is empty and Player 2 is not yet ready.
        else if (!player2Ready && player2Spec == null)
        {
            player2Spec = spec;
            player2Selection.setText("Player 2: " + spec.name());
            player2ReadyButton.setDisable(false); // Enable the "Ready" button for Player 2.
            System.out.println("Player 2 selected: " + spec.name());
        }
    }

}
