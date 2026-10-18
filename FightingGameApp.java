package FightingGame;

import javafx.animation.KeyFrame; // Single frame in the logo color-cycling timeline.
import javafx.animation.Timeline; // Drives the logo color-cycling animation. (U1)
import javafx.application.Application; // Base class for JavaFX applications
import javafx.geometry.Pos;
import javafx.scene.Scene; // Represents the contents of a JavaFX stage
import javafx.scene.control.Button; // Button UI component for interactions
import javafx.scene.control.Label; // Label control for displaying text
import javafx.scene.layout.VBox; // Layout container that arranges children vertically
import javafx.scene.paint.Color; // Used for the 12-color logo cycle. (U1)
import javafx.stage.Stage; // The primary window container for JavaFX applications
import javafx.util.Duration; // Time intervals for the logo Timeline. (U1)

/**
 * Main application class for the Fighting Game.
 * Handles initialization, user navigation, and transitions between scenes.
 */
public class FightingGameApp extends Application
{

    private static Stage primaryStage; // The main stage for displaying scenes.
    // U6: kept as static so GameSession callbacks can call showMainMenu().
    private static FightingGameApp instance; // Singleton reference for static callbacks.

    /**
     * Entry point for JavaFX applications.
     * Shows the stage immediately and displays the animated logo. (U1)
     *
     * @param primaryStage The primary stage provided by JavaFX.
     */
    @Override
    public void start(Stage primaryStage)
    {
        FightingGameApp.primaryStage = primaryStage;
        FightingGameApp.instance = this; // Store for static callbacks (U6).
        displayLogoWithColorChange(this::promptUserChoice); // U1: no Thread.sleep.
    }

    /**
     * Main method to launch the JavaFX application.
     *
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args)
    {
        launch(args); // Launch the JavaFX application
    }

    /**
     * Displays the ASCII logo in a JavaFX window, cycling through 12 text colors
     * with a Timeline (12 frames × 300 ms ≈ 3.6 s). (U1)
     * The stage is shown immediately; no Thread.sleep is used anywhere.
     * Goes to the menu when the animation finishes, or on any key press or click.
     *
     * @param onLogoComplete Callback to execute after the logo display completes.
     */
    private void displayLogoWithColorChange(Runnable onLogoComplete)
    {
        // Multi-line ASCII logo rendered in a monospace label.
        String logoText =
              "  ███████╗██╗  ██╗ █████╗  ██████╗   ██╗   ██╗ █████╗ ██╗\n"
            + "  ██╔════╝██║  ██║██╔══██╗ ██╔══██║  ██║   ██║██╔══██╗██║\n"
            + "  ███████╗███████║███████║ ██║  ██║  ██║   ██║███████║██║\n"
            + "  ╚════██║██╔══██║██╔══██║ ██║  ██║  ╚██╗ ██╔╝██╔══██║██║\n"
            + "  ███████║██║  ██║██║  ██║ █████╔═╝   ╚████╔╝ ██║  ██║███████╗\n"
            + "  ╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝╚════╝      ╚═══╝  ╚═╝  ╚═╝╚══════╝\n"
            + "\n"
            + "        ██████╗ ███████╗    ██╗   ██╗ █████╗ ██╗      ██████╗ ██████╗\n"
            + "        ██╔═══╝ ██╔════╝    ██║   ██║██╔══██╗██║     ██╔═══██╗██╔══██╗\n"
            + "        ██████╗ █████╗      ██║   ██║███████║██║     ██║   ██║██████╔╝\n"
            + "             ██╗██╔══╝      ╚██╗ ██╔╝██╔══██║██║     ██║   ██║██╔══██╗\n"
            + "        ██████╔╝██║          ╚████╔╝ ██║  ██║███████╗╚██████╔╝██║  ██║\n"
            + "        ╚═════╝ ╚═╝           ╚═══╝  ╚═╝  ╚═╝╚══════╝╚═════╝ ╚═╝  ╚═╝\n"
            + "\n"
            + "               Press any key or click to continue...";

        Label logoLabel = new Label(logoText); // Monospace label renders box-drawing characters.
        logoLabel.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");

        VBox logoBox = new VBox(logoLabel);
        logoBox.setAlignment(Pos.CENTER);
        logoBox.setStyle("-fx-background-color: black;"); // Dark background for contrast.

        Scene logoScene = new Scene(logoBox, 700, 420);
        primaryStage.setScene(logoScene);
        primaryStage.setTitle("Shadow of Valor");
        primaryStage.show(); // U1: show the stage immediately; no blocking sleep.

        // 12-color cycle (one color per KeyFrame, 300 ms apart). (U1)
        Color[] colors = {
            Color.RED, Color.LIMEGREEN, Color.YELLOW, Color.DODGERBLUE, Color.MEDIUMPURPLE,
            Color.CYAN, Color.WHITE, Color.ORANGERED, Color.SPRINGGREEN,
            Color.GOLD, Color.CORNFLOWERBLUE, Color.ORCHID
        };

        Timeline timeline = new Timeline();
        for (int i = 0; i < colors.length; i++)
        {
            final Color c = colors[i];
            timeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(300 * (i + 1)), e -> logoLabel.setTextFill(c)));
        }

        // Guard against double-firing if both the timeline and a key/click fire close together.
        final boolean[] done = {false};
        Runnable goToMenu = () ->
        {
            if (!done[0])
            {
                done[0] = true;
                onLogoComplete.run();
            }
        };

        timeline.setOnFinished(e -> goToMenu.run()); // Go to menu when animation ends.
        logoScene.setOnKeyPressed(e -> { timeline.stop(); goToMenu.run(); }); // Skip on key press.
        logoScene.setOnMouseClicked(e -> { timeline.stop(); goToMenu.run(); }); // Skip on click.

        timeline.play(); // Start cycling colors; no Thread.sleep anywhere. (U1)
    }

    /**
     * Displays the main menu with navigation buttons.
     * Also exposed as a static method so GameSession can return here. (U6)
     */
    public static void showMainMenu()
    {
        instance.promptUserChoice();
    }

    /**
     * Displays the main menu with buttons instead of Scanner input.
     */
    public void promptUserChoice()
    {
        VBox mainMenu = new VBox(15); // Vertical layout for buttons
        mainMenu.setAlignment(Pos.CENTER);

        Label title = new Label("What would you like to do next?");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button characterSelectionButton = new Button("Go to Character Selection Screen");
        characterSelectionButton.setOnAction(e -> showCharacterSelectionScreen());

        Button moveListButton = new Button("View Move List");
        moveListButton.setOnAction(e -> showMoveListScreen());

        Button exitButton = new Button("Exit the Game");
        exitButton.setOnAction(e ->
        {
            System.out.println("Exiting the game. Goodbye!");
            System.exit(0);
        });

        mainMenu.getChildren().addAll(title, characterSelectionButton, moveListButton, exitButton);

        Scene menuScene = new Scene(mainMenu, 600, 400);
        primaryStage.setScene(menuScene);
        primaryStage.show();
    }

    /**
     * Displays the character selection screen.
     * Sets a callback to start the game once both players are ready.
     */
    private void showCharacterSelectionScreen()
    {
        CharacterSelectScreen characterSelectScreen = new CharacterSelectScreen();

        // U2: callback receives specs; fresh Character instances are built in startGameWithSpecs.
        characterSelectScreen.setOnReady((spec1, spec2) -> startGameWithSpecs(spec1, spec2));

        primaryStage.setScene(characterSelectScreen.getScene());
        primaryStage.show();
    }

    /**
     * Starts a match from two CharacterSpecs. (U2, U6)
     * Creates fresh Character instances and passes rematch / main-menu callbacks to GameSession.
     *
     * @param spec1 Spec for Player 1's character.
     * @param spec2 Spec for Player 2's character.
     */
    private void startGameWithSpecs(CharacterRoster.CharacterSpec spec1, CharacterRoster.CharacterSpec spec2)
    {
        // Rematch restarts the match from the same specs; both players get fresh instances. (U6)
        Runnable rematch = () -> startGameWithSpecs(spec1, spec2);
        Runnable mainMenu = this::promptUserChoice; // Main Menu returns to the navigation screen. (U6)

        GameApp.setPrimaryStage(primaryStage);
        GameApp.startGameWithSpecs(spec1, spec2, rematch, mainMenu); // Build characters and start.
    }

    /**
     * Displays the move list for the characters in a new window.
     */
    private void showMoveListScreen()
    {
        Stage moveListStage = new Stage(); // Create a new stage for the move list screen
        VBox root = new VBox(10); // Use a vertical layout with 10px spacing between elements
        root.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: white;");

        Label title = new Label("Character Move List");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label moveList = new Label(getMoveListDetails()); // Fetch the formatted move list details
        moveList.setStyle("-fx-font-size: 14px;");
        moveList.setWrapText(true); // Allow the text to wrap if it exceeds the label's width

        Button backButton = new Button("Back");
        backButton.setOnAction(event ->
        {
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.close();
            promptUserChoice(); // Show the menu.
        });

        root.getChildren().addAll(title, moveList, backButton);

        Scene scene = new Scene(root, 600, 400);
        moveListStage.setScene(scene);
        moveListStage.setTitle("Move List");
        moveListStage.show();
    }

    /**
     * Helper method to return the move list details as a formatted string.
     *
     * @return A string containing the move list for the game characters.
     */
    private String getMoveListDetails()
    {
        return """
                Character Move List:
                Player 1:
                - Move: W (Up), A (Left), S (Down), D (Right)
                - Basic Attack: X
                - Special Ability: C
                - Shield: Z
                - Projectile: V (hold to charge, release to fire)

                Player 2:
                - Move: Up Arrow, Left Arrow, Down Arrow, Right Arrow
                - Basic Attack: J
                - Special Ability: K
                - Shield: M
                - Projectile: L (hold to charge, release to fire)
               """;
    }

}
