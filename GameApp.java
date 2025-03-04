package FightingGame;

import java.util.HashSet; // Used for tracking active keys.       
import java.util.Iterator; // For iterating through lists of projectiles.
import java.util.List; // For managing lists of objects.
import java.util.Set; // For handling unique key inputs.

import javafx.animation.AnimationTimer; // For creating a game loop.
import javafx.application.Platform; // Ensures UI updates occur on the JavaFX application thread.
import javafx.scene.Scene; // Represents the game window's content.
import javafx.scene.control.Alert; // Displays pop-up alerts.
import javafx.scene.control.Label; // For displaying text (e.g., timer).
import javafx.scene.control.ProgressBar; // For health and shield visuals.
import javafx.scene.input.KeyCode; // Represents keys pressed by the player.
import javafx.scene.layout.Pane; // Root container for the game scene.
import javafx.stage.Stage; // Represents the main game window.

/**
 * The GameApp class sets up and runs the main game logic,
 * including the arena, characters, and input handling.
 */

public class GameApp 
{
	
	// Constants for arena dimensions
    public static final int ARENA_WIDTH = 610;
    public static final int ARENA_HEIGHT = 450;

    // Static fields for the primary stage and UI components
    private static Stage primaryStage; // The main game window
    static Pane root = new Pane(); // Root container for all game elements
    private static ProgressBar player1HealthBar; // Health bar for Player 1
    private static ProgressBar player2HealthBar; // Health bar for Player 2
    private static ProgressBar player1ShieldBar; // Shield bar for Player 1
    private static ProgressBar player2ShieldBar; // Shield bar for Player 2
    private static Label timerLabel; // Label to display the game timer

    // Constants for character boundaries
    public static final double BOUNDARY_TOP = 100;
    public static final double BOUNDARY_BOTTOM = 350;    
    public static final double BOUNDARY_LEFT = 50;
    public static final double BOUNDARY_RIGHT = 550;

    // Fields for tracking active keys and game state
    private static Set<KeyCode> activeKeys = new HashSet<>(); // Tracks currently pressed keys
    private static double timeRemaining = 200; // 300 seconds

    /**
     * Sets the primary stage for the application.
     *
     * @param stage The main game window.
     */
    public static void setPrimaryStage(Stage stage) 
    {
        primaryStage = stage; // Assign the provided stage to the static field
        primaryStage.show(); // Make the stage visible
    }

    /**
     * Initializes and starts the game with the selected characters.
     * This method sets up the game scene, initializes character positions,
     * assigns opponents, sets up UI elements, and starts the game loop.
     *
     * @param player1 The first player's character.
     * @param player2 The second player's character.
     */
    public static void startGameWithCharacters(Character player1, Character player2) 
    {
        // Create a new root pane for the game elements
        root = new Pane();
        root.setStyle("-fx-background-color: lightgray;"); // Set the background color of the game

        // Set initial positions of Player 1 and Player 2 on the game screen
        player1.getCharacterSprite().setLayoutX(100); // Position Player 1 towards the left
        player1.getCharacterSprite().setLayoutY(200); // Position Player 1 vertically centered
        player2.getCharacterSprite().setLayoutX(400); // Position Player 2 towards the right
        player2.getCharacterSprite().setLayoutY(200); // Position Player 2 vertically centered

        // Assign each player an opponent for attack and ability interactions
        player1.setOpponent(player2);
        player2.setOpponent(player1);

        // Initialize the health bars, shield bars, and game timer
        setupHealthBars(player1, player2);
        setupShieldBars(player1, player2);
        setupTimerLabel();

        // Add game elements (characters and timer) to the UI
        root.getChildren().addAll(
            player1.getCharacterSprite(),
            player2.getCharacterSprite(),
            timerLabel
        );

        // Create the game scene and configure key handlers for controls
        Scene scene = new Scene(root, ARENA_WIDTH, ARENA_HEIGHT);
        setupKeyHandlers(scene, player1, player2);

        // Start the game loop and initialize the round manager
        startGameLoop(player1, player2, new RoundManager(3, List.of(player1, player2)));

        // Assign the scene to the primary stage and display the game window
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * Updates the health and shield bars for both players.
     * This ensures that UI elements accurately reflect each player's current status.
     *
     * @param player1 The first player's character.
     * @param player2 The second player's character.
     */
    public static void updateBars(Character player1, Character player2) 
    {
        // Update each player's shield status based on the current time
        player1.updateShieldState(System.currentTimeMillis());
        player2.updateShieldState(System.currentTimeMillis());

        // Update Player 1's shield bar if it exists
        if (player1ShieldBar != null) 
        {
            player1ShieldBar.setProgress(player1.getShieldLevel()); // Set the shield bar value
            player1ShieldBar.setStyle(player1.getShieldLevel() > 0 ? "-fx-accent: blue;" : "-fx-accent: gray;"); // Update color based on shield status
        }

        // Update Player 2's shield bar if it exists
        if (player2ShieldBar != null) 
        {
            player2ShieldBar.setProgress(player2.getShieldLevel()); // Set the shield bar value
            player2ShieldBar.setStyle(player2.getShieldLevel() > 0 ? "-fx-accent: blue;" : "-fx-accent: gray;"); // Update color based on shield status
        }

        // Update Player 1's health bar if it exists
        if (player1HealthBar != null) 
        {
            player1HealthBar.setProgress(player1.getHealthPercentage()); // Set the health bar value
            player1HealthBar.setStyle(player1.getHealthPercentage() > 0 ? "-fx-accent: green;" : "-fx-accent: red;"); // Change color based on health status
        }

        // Update Player 2's health bar if it exists
        if (player2HealthBar != null)
        {
            player2HealthBar.setProgress(player2.getHealthPercentage()); // Set the health bar value
            player2HealthBar.setStyle(player2.getHealthPercentage() > 0 ? "-fx-accent: green;" : "-fx-accent: red;"); // Change color based on health status
        }
    }


 // Initializes and positions shield bars for Player 1 and Player 2
    private static void setupShieldBars(Character player1, Character player2) 
    {
        // Check if Player 1's shield bar is uninitialized
        if (player1ShieldBar == null) 
        {
            player1ShieldBar = new ProgressBar(1.0); // Initialize Player 1's shield bar at full (1.0)
            player1ShieldBar.setPrefWidth(150); // Set the width of the shield bar
            player1ShieldBar.setLayoutX(50); // Position the shield bar near the left side of the screen
            player1ShieldBar.setLayoutY(80); // Position the shield bar below Player 1's health bar
            player1ShieldBar.setStyle("-fx-accent: blue;"); // Set the color of the shield bar to blue
            root.getChildren().add(player1ShieldBar); // Add Player 1's shield bar to the root pane
        }

        // Check if Player 2's shield bar is uninitialized
        if (player2ShieldBar == null) 
        {
            player2ShieldBar = new ProgressBar(1.0); // Initialize Player 2's shield bar at full (1.0)
            player2ShieldBar.setPrefWidth(150); // Set the width of the shield bar
            player2ShieldBar.setLayoutX(400); // Position the shield bar near the right side of the screen
            player2ShieldBar.setLayoutY(80); // Position the shield bar below Player 2's health bar
            player2ShieldBar.setStyle("-fx-accent: blue;"); // Set the color of the shield bar to blue
            root.getChildren().add(player2ShieldBar); // Add Player 2's shield bar to the root pane
        }
    }

    // Sets up the game timer label
    private static void setupTimerLabel() 
    {
        // Create a label to display the remaining game time
        timerLabel = new Label("Time: 200"); // Initialize the timer label with "Time: 200" as default text

        // Position the timer label in the center-top of the screen
        timerLabel.setLayoutX(270); // Horizontal position near the center of the screen
        timerLabel.setLayoutY(10); // Vertical position near the top of the screen

        // Style the label for better visibility
        timerLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: black;"); // Set font size and color
    }

    // Handles movement inputs for Player 1 and Player 2 based on active keys
    private static void handlePlayerMovement(Character player1, Character player2)
    {
        // Check if Player 1 is valid
        if (player1 != null) 
        {
            // If 'W' key is pressed, move Player 1 up
            if (activeKeys.contains(KeyCode.W)) 
            {
                player1.handleInput("UP", ARENA_WIDTH, ARENA_HEIGHT);
            }
            // If 'S' key is pressed, move Player 1 down
            if (activeKeys.contains(KeyCode.S)) 
            {
                player1.handleInput("DOWN", ARENA_WIDTH, ARENA_HEIGHT);
            }
            // If 'A' key is pressed, move Player 1 left
            if (activeKeys.contains(KeyCode.A)) 
            {
                player1.handleInput("LEFT", ARENA_WIDTH, ARENA_HEIGHT);
            }
            // If 'D' key is pressed, move Player 1 right
            if (activeKeys.contains(KeyCode.D)) 
            {
                player1.handleInput("RIGHT", ARENA_WIDTH, ARENA_HEIGHT);
            }
        }

        // Check if Player 2 is valid
        if (player2 != null) 
        {
            // If the 'UP' arrow key is pressed, move Player 2 up
            if (activeKeys.contains(KeyCode.UP)) 
            {
                player2.handleInput("UP", ARENA_WIDTH, ARENA_HEIGHT);
            }
            // If the 'DOWN' arrow key is pressed, move Player 2 down
            if (activeKeys.contains(KeyCode.DOWN)) 
            {
                player2.handleInput("DOWN", ARENA_WIDTH, ARENA_HEIGHT);
            }
            // If the 'LEFT' arrow key is pressed, move Player 2 left
            if (activeKeys.contains(KeyCode.LEFT)) 
            {
                player2.handleInput("LEFT", ARENA_WIDTH, ARENA_HEIGHT);
            }
            // If the 'RIGHT' arrow key is pressed, move Player 2 right
            if (activeKeys.contains(KeyCode.RIGHT)) 
            {
                player2.handleInput("RIGHT", ARENA_WIDTH, ARENA_HEIGHT);
            }
        }
    }

    // Sets up key handlers for the game scene
    private static void setupKeyHandlers(Scene scene, Character player1, Character player2) 
    {
        // Key pressed handler
        scene.setOnKeyPressed(event -> 
        {
            activeKeys.add(event.getCode()); // Add the pressed key to the activeKeys set

            // Handle shield activation
            if (event.getCode() == KeyCode.Z) 
            { // If 'Z' key is pressed, activate Player 1's shield
                player1.activateShield();
            }
            if (event.getCode() == KeyCode.M) 
            { // If 'M' key is pressed, activate Player 2's shield
                player2.activateShield();
            }

            handlePlayerActions(player1, player2); // Handle player-specific actions (attack, special ability, etc.)
        });

        // Key released handler
        scene.setOnKeyReleased(event -> 
        {
            activeKeys.remove(event.getCode()); // Remove the released key from the activeKeys set

            // Handle shield deactivation
            if (event.getCode() == KeyCode.Z) 
            { // If 'Z' key is released, deactivate Player 1's shield
                player1.releaseShieldKey();
            }
            if (event.getCode() == KeyCode.M) 
            { // If 'M' key is released, deactivate Player 2's shield
                player2.releaseShieldKey();
            }

            // Handle projectile release for Player 1
            if (event.getCode() == KeyCode.V && player1.isProjectileKeyPressed())
            {
                Projectile projectile = player1.launchChargedProjectile(player2); // Launch the projectile at Player 2
                player1.setProjectileKeyPressed(false); // Reset the key press state
                if (projectile != null)
                {
                    ProjectileManager.addProjectile(projectile); // Add the projectile to the game
                }
            }

            // Handle projectile release for Player 2
            if (event.getCode() == KeyCode.L && player2.isProjectileKeyPressed()) 
            {
                Projectile projectile = player2.launchChargedProjectile(player1); // Launch the projectile at Player 1
                player2.setProjectileKeyPressed(false); // Reset the key press state
                if (projectile != null) 
                {
                    ProjectileManager.addProjectile(projectile); // Add the projectile to the game
                }
            }
        });
    }

    /**
     * Starts the main game loop to handle animations, movement, and collisions.
     * 
     * @param player1 The first player in the game.
     * @param player2 The second player in the game.
     * @param roundManager The manager responsible for handling rounds.
     */
    private static void startGameLoop(Character player1, Character player2, RoundManager roundManager) 
    {
        AnimationTimer gameLoop = new AnimationTimer() 
        {
            private long previousTime = System.nanoTime(); // Tracks the last update time.

            @Override
            public void handle(long now)
            {
                double deltaTime = (now - previousTime) / 1e9; // Time difference in seconds.
                previousTime = now;

                timeRemaining -= deltaTime; // Decrease the remaining time for the round.
                if (timeRemaining <= 0)
                {
                    endGame(player1, player2, roundManager); // End the game if time runs out.
                    stop(); // Stop the game loop.
                    return;
                }

                timerLabel.setText("Time: " + (int) timeRemaining); // Update the timer label.

                // Safely update projectiles.
                synchronized (ProjectileManager.getActiveProjectiles())
                {
                    Iterator<Projectile> iterator = ProjectileManager.getActiveProjectiles().iterator();
                    while (iterator.hasNext()) 
                    {
                        Projectile projectile = iterator.next();
                        projectile.updatePosition(deltaTime); // Update the projectile's position.

                        // Remove projectiles that are out of bounds.
                        if (isOutOfBounds(projectile)) 
                        {
                            iterator.remove(); // Remove the projectile from the list.
                            ProjectileManager.removeProjectile(projectile); // Remove the projectile from the manager.
                        }
                    }
                }

                handlePlayerMovement(player1, player2); // Process player movement.
                updateBars(player1, player2); // Update health and shield bars.

                // Check for victory conditions.
                if (player1.getHealth() <= 0 || player2.getHealth() <= 0)
                {
                    endGame(player1, player2, roundManager); // End the game if a player is defeated.
                    stop(); // Stop the game loop.
                }
            }
        };

        timeRemaining = 200; // Initialize the game timer to 200 seconds.
        gameLoop.start(); // Start the game loop.
    }
    

    /**
     * Checks if a projectile is out of the game arena's bounds.
     * 
     * @param projectile The projectile to check.
     * @return True if the projectile is out of bounds, false otherwise.
     */
    private static boolean isOutOfBounds(Projectile projectile)
    {
        double x = projectile.getSprite().getLayoutX(); // Get the X position of the projectile.
        double y = projectile.getSprite().getLayoutY(); // Get the Y position of the projectile.
        // Check if the projectile is outside the defined arena boundaries.
        return x < 0 || x > ARENA_WIDTH || y < 0 || y > ARENA_HEIGHT;
    }


 // Handles the actions for Player 1 and Player 2 based on key inputs
    private static void handlePlayerActions(Character player1, Character player2) 
    {
        // Process actions for Player 1 using their specific key bindings
        processPlayerActions(player1, player2, KeyCode.X, KeyCode.C, KeyCode.Z, KeyCode.V, activeKeys);

        // Process actions for Player 2 using their specific key bindings
        processPlayerActions(player2, player1, KeyCode.J, KeyCode.K, KeyCode.M, KeyCode.L, activeKeys);
    }
    

    /**
     * Processes actions for a single player based on key inputs.
     *
     * @param player        The player performing the actions.
     * @param opponent      The opponent being targeted by actions.
     * @param attackKey     The key for performing an attack.
     * @param specialKey    The key for performing a special ability.
     * @param shieldKey     The key for activating or deactivating the shield.
     * @param projectileKey The key for charging and launching a projectile.
     * @param activeKeys    The set of currently active keys.
     */
    private static void processPlayerActions(Character player, Character opponent, KeyCode attackKey, KeyCode specialKey,KeyCode shieldKey, KeyCode projectileKey, Set<KeyCode> activeKeys)
    {
        // Handle attack action
        if (activeKeys.contains(attackKey)) 
        {
            if (player.isShieldActive()) 
            {
                player.releaseShieldKey(); // Automatically disable the shield if attacking
            }
            player.performAttack(opponent); // Execute an attack on the opponent
        }

        // Handle special ability action
        if (activeKeys.contains(specialKey)) 
        {
            player.performSpecialAbility(opponent); // Execute the player's special ability
        }

        // Handle shield activation or deactivation
        if (activeKeys.contains(shieldKey)) 
        {
            player.activateShield(); // Activate the shield
        } 
        else if (player.isShieldActive()) 
        {
            player.releaseShieldKey(); // Deactivate the shield when the key is released
        }

        // Handle projectile charging and launching
        if (activeKeys.contains(projectileKey))
        {
            if (!player.isProjectileKeyPressed())
            {
                player.startCharging(); // Begin charging the projectile
                player.setProjectileKeyPressed(true); // Mark the projectile key as pressed
            }
        } 
        else 
        {
            if (player.isProjectileKeyPressed()) 
            {
                // Launch the projectile when the key is released
                Projectile projectile = player.launchChargedProjectile(opponent);
                player.setProjectileKeyPressed(false); // Reset the projectile key press state

                if (projectile != null) 
                {
                    ProjectileManager.addProjectile(projectile); // Add the launched projectile to the manager
                }
            }
        }
    }

    /**
     * Ends the game, displaying the winner and closing the game window.
     *
     * @param player1       Player 1 in the game.
     * @param player2       Player 2 in the game.
     * @param roundManager  The manager responsible for game rounds.
     */
    private static void endGame(Character player1, Character player2, RoundManager roundManager) 
    {
        Platform.runLater(() -> 
        { // Ensure UI updates are performed on the JavaFX Application Thread
            Alert alert = new Alert(Alert.AlertType.INFORMATION); // Create an informational alert
            alert.setTitle("Game Over"); // Set the title of the alert
            alert.setHeaderText(null); // No header text for the alert

            // Determine the winner based on remaining health
            String winner = player1.getHealth() > player2.getHealth() ? player1.getName() : player2.getName();

            // Set the alert message and display it
            alert.setContentText("Game Over! " + winner + " Wins!");
            alert.showAndWait(); // Wait for user confirmation

            primaryStage.close(); // Close the primary stage, ending the game
        });
    }



    /**
     * Sets up health bars for Player 1 and Player 2.
     *
     * @param player1 Player 1 in the game.
     * @param player2 Player 2 in the game.
     */
    private static void setupHealthBars(Character player1, Character player2) 
    {
        // Initialize Player 1's health bar
        player1HealthBar = new ProgressBar(1.0); // Fully filled at the start
        player1HealthBar.setStyle("-fx-accent: green;"); // Style the health bar with a blue color
        player1HealthBar.setLayoutX(50); // Position the health bar
        player1HealthBar.setLayoutY(20); // Set the vertical position
        player1HealthBar.setPrefWidth(200); // Set the width of the health bar

        // Initialize Player 2's health bar
        player2HealthBar = new ProgressBar(1.0); // Fully filled at the start
        player2HealthBar.setStyle("-fx-accent: green;"); // Style the health bar with a green color
        player2HealthBar.setLayoutX(350); // Position the health bar on the right
        player2HealthBar.setLayoutY(20); // Set the vertical position
        player2HealthBar.setPrefWidth(200); // Set the width of the health bar

        // Add both health bars to the game's root pane
        root.getChildren().addAll(player1HealthBar, player2HealthBar);
    }

 
}
