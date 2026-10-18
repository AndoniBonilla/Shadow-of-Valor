package FightingGame;

// Import statements for all required utilities and libraries

import java.util.ArrayList; // G4: collects projectile-vs-projectile losers before bulk removal.
import java.util.HashSet; // For tracking active keys and one-shot key events.
import java.util.Iterator; // For safe projectile list iteration with removal.
import java.util.List; // Used for the live projectile list in the G4 two-pass loop.
import java.util.Optional; // U6: receives the user's button choice from the end-of-match dialog.
import java.util.Set; // For activeKeys, justPressed, justReleased sets.

import javafx.animation.AnimationTimer; // Main game loop driven at screen refresh rate.
import javafx.application.Platform; // Used to show the end-game Alert on the FX thread.
import javafx.scene.Scene; // The game window's content.
import javafx.scene.control.Alert; // Pop-up for the end-of-match message. (U6)
import javafx.scene.control.ButtonType; // Rematch and Main Menu button types. (U6)
import javafx.scene.control.Label; // Timer label displayed in the arena.
import javafx.scene.control.ProgressBar; // HUD health and shield bars.
import javafx.scene.input.KeyCode; // Key constants used by the Controls record.
import javafx.scene.layout.Pane; // Root container for all game elements.
import javafx.stage.Stage; // The primary application window.

/**
 * Owns one complete match: root pane, HUD, timer, key sets, game loop,
 * and ProjectileManager. Created fresh for each match; no shared mutable
 * static state except the arena constants. (U3)
 */
public class GameSession
{

    // -----------------------------------------------------------------
    // Static arena constants (used by Character.moveBy and AbilityManager.teleport)
    // -----------------------------------------------------------------

    public static final int    ARENA_WIDTH    = 610; // Total arena width in pixels.
    public static final int    ARENA_HEIGHT   = 450; // Total arena height in pixels.
    public static final double BOUNDARY_TOP   = 100; // Upper movement limit.
    public static final double BOUNDARY_BOTTOM= 350; // Lower movement limit.
    public static final double BOUNDARY_LEFT  = 50;  // Left movement limit.
    public static final double BOUNDARY_RIGHT = 550; // Right movement limit.

    // -----------------------------------------------------------------
    // Controls record — one instance per player. (U5)
    // All eight key bindings for a single fighter.
    // -----------------------------------------------------------------

    record Controls(
        KeyCode up, KeyCode down, KeyCode left, KeyCode right,
        KeyCode attack, KeyCode special, KeyCode shield, KeyCode projectile
    ) {}

    // P1: WASD / X / C / Z / V
    private static final Controls P1_CONTROLS = new Controls(
        KeyCode.W, KeyCode.S, KeyCode.A, KeyCode.D,
        KeyCode.X, KeyCode.C, KeyCode.Z, KeyCode.V);

    // P2: Arrow keys / J / K / M / L
    private static final Controls P2_CONTROLS = new Controls(
        KeyCode.UP, KeyCode.DOWN, KeyCode.LEFT, KeyCode.RIGHT,
        KeyCode.J, KeyCode.K, KeyCode.M, KeyCode.L);

    // -----------------------------------------------------------------
    // Static bridge for backward compatibility (FightingGameApp still calls GameApp.*)
    // -----------------------------------------------------------------

    private static Stage sharedPrimaryStage; // Set by the static adapter setPrimaryStage().

    /**
     * Static adapter: stores the stage so the static startGameWithCharacters can use it.
     * FightingGameApp calls GameApp.setPrimaryStage → GameApp delegates here.
     */
    public static void setPrimaryStage(Stage stage)
    {
        sharedPrimaryStage = stage;
        stage.show(); // Make the stage visible (original behaviour).
    }

    /**
     * Static adapter: builds fresh Character instances from specs and starts a new match. (U2, U6)
     * FightingGameApp calls GameApp.startGameWithSpecs → GameApp delegates here.
     */
    public static void startGameWithSpecs(CharacterRoster.CharacterSpec spec1,
                                          CharacterRoster.CharacterSpec spec2,
                                          Runnable rematch, Runnable mainMenu)
    {
        Character player1 = spec1.createCharacter(); // Fresh instance; full health and no buffs.
        Character player2 = spec2.createCharacter();
        new GameSession(sharedPrimaryStage, rematch, mainMenu).start(player1, player2);
    }

    // -----------------------------------------------------------------
    // Instance state (one per match)
    // -----------------------------------------------------------------

    private final Stage primaryStage; // The window this session uses.
    private Pane root; // Root pane for all in-game visual elements.

    private ProgressBar player1HealthBar; // HUD health bar for Player 1 (top of screen).
    private ProgressBar player2HealthBar; // HUD health bar for Player 2.
    private ProgressBar player1ShieldBar; // HUD shield bar for Player 1.
    private ProgressBar player2ShieldBar; // HUD shield bar for Player 2.
    private Label timerLabel; // Countdown timer displayed at the top-centre.

    private double timeRemaining; // Seconds left in the match.
    private double currentDt = 0; // dt for the current frame; available to processInput.

    // U5: Three sets drive the input model.
    // activeKeys    — all keys currently held down (populated on press, cleared on release).
    // justPressed   — keys pressed this frame for the first time (cleared each frame end).
    // justReleased  — keys released this frame (cleared each frame end).
    private final Set<KeyCode> activeKeys   = new HashSet<>();
    private final Set<KeyCode> justPressed  = new HashSet<>();
    private final Set<KeyCode> justReleased = new HashSet<>();

    private ProjectileManager projectileManager; // Owns projectile list and sprites.
    private AnimationTimer gameLoop; // Drives the match at screen-refresh rate.

    // U6: called when the match ends; provided by FightingGameApp so GameSession stays decoupled.
    private final Runnable rematchAction;   // Starts a fresh match with the same character specs.
    private final Runnable mainMenuAction;  // Returns the user to the main navigation screen.

    // -----------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------

    public GameSession(Stage primaryStage, Runnable rematchAction, Runnable mainMenuAction)
    {
        this.primaryStage = primaryStage;
        this.rematchAction = rematchAction;     // Store callback for end-of-match rematch. (U6)
        this.mainMenuAction = mainMenuAction;   // Store callback for returning to main menu. (U6)
    }

    // -----------------------------------------------------------------
    // Match entry point
    // -----------------------------------------------------------------

    /**
     * Initialises the arena and starts the game loop for the given fighters.
     *
     * @param player1 The character selected by Player 1.
     * @param player2 The character selected by Player 2.
     */
    public void start(Character player1, Character player2)
    {
        root = new Pane();
        root.setStyle("-fx-background-color: lightgray;"); // Arena background colour.

        // Position sprites first so addBarsToPane reads the correct coordinates.
        player1.getCharacterSprite().setLayoutX(100);
        player1.getCharacterSprite().setLayoutY(200);
        player2.getCharacterSprite().setLayoutX(400);
        player2.getCharacterSprite().setLayoutY(200);

        player1.setOpponent(player2); // Wire up mutual opponent references.
        player2.setOpponent(player1);

        setupHealthBars(); // Create and add the top-of-screen HUD health bars.
        setupShieldBars(); // Create and add the top-of-screen HUD shield bars.
        setupTimerLabel(); // Create the timer label (added to root below).

        root.getChildren().addAll(
            player1.getCharacterSprite(),
            player2.getCharacterSprite(),
            timerLabel
        );

        projectileManager = new ProjectileManager(root); // Instance-bound to this root. (U3)

        Scene scene = new Scene(root, ARENA_WIDTH, ARENA_HEIGHT);
        setupKeyHandlers(scene); // Wire up the U5 key-set handlers.
        startGameLoop(player1, player2); // Begin the AnimationTimer.

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // -----------------------------------------------------------------
    // Setup helpers
    // -----------------------------------------------------------------

    // Creates the HUD health bars at the top of the arena.
    private void setupHealthBars()
    {
        player1HealthBar = new ProgressBar(1.0); // Fully filled at the start.
        player1HealthBar.setStyle("-fx-accent: green;");
        player1HealthBar.setLayoutX(50);
        player1HealthBar.setLayoutY(20);
        player1HealthBar.setPrefWidth(200);

        player2HealthBar = new ProgressBar(1.0);
        player2HealthBar.setStyle("-fx-accent: green;");
        player2HealthBar.setLayoutX(350);
        player2HealthBar.setLayoutY(20);
        player2HealthBar.setPrefWidth(200);

        root.getChildren().addAll(player1HealthBar, player2HealthBar);
    }

    // Creates the HUD shield bars at the top of the arena.
    private void setupShieldBars()
    {
        player1ShieldBar = new ProgressBar(1.0);
        player1ShieldBar.setPrefWidth(150);
        player1ShieldBar.setLayoutX(50);
        player1ShieldBar.setLayoutY(80);
        player1ShieldBar.setStyle("-fx-accent: blue;");
        root.getChildren().add(player1ShieldBar);

        player2ShieldBar = new ProgressBar(1.0);
        player2ShieldBar.setPrefWidth(150);
        player2ShieldBar.setLayoutX(400);
        player2ShieldBar.setLayoutY(80);
        player2ShieldBar.setStyle("-fx-accent: blue;");
        root.getChildren().add(player2ShieldBar);
    }

    // Creates the countdown timer label positioned near the top-centre.
    private void setupTimerLabel()
    {
        timerLabel = new Label("Time: 200");
        timerLabel.setLayoutX(270);
        timerLabel.setLayoutY(10);
        timerLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: black;");
    }

    // -----------------------------------------------------------------
    // Key handlers (U5)
    // -----------------------------------------------------------------

    /**
     * Wires the scene's key-press and key-release handlers to the three input sets. (U5)
     * OS key-repeat events are ignored by only adding to justPressed on the first press.
     */
    private void setupKeyHandlers(Scene scene)
    {
        scene.setOnKeyPressed(event ->
        {
            KeyCode code = event.getCode();
            if (!activeKeys.contains(code))
            {
                justPressed.add(code); // First press only; repeated events from OS are skipped.
            }
            activeKeys.add(code); // Always keep the held-key set current.
        });

        scene.setOnKeyReleased(event ->
        {
            KeyCode code = event.getCode();
            activeKeys.remove(code); // Remove from the held set immediately.
            justReleased.add(code); // Record for this frame's one-shot release handling.
        });
    }

    // -----------------------------------------------------------------
    // Game loop
    // -----------------------------------------------------------------

    /**
     * Creates and starts the AnimationTimer that drives the match. (U3, U4, U5, T2)
     * Delta-time is capped at 0.05 s to prevent tunnelling after focus loss. (U4)
     */
    private void startGameLoop(Character player1, Character player2)
    {
        timeRemaining = 200; // 200-second match countdown.

        gameLoop = new AnimationTimer()
        {
            private long previousNanos = System.nanoTime(); // Last frame's nanosecond timestamp.

            @Override
            public void handle(long now)
            {
                double rawDt = (now - previousNanos) / 1_000_000_000.0; // Convert ns to seconds.
                previousNanos = now;
                double dt = Math.min(rawDt, 0.05); // U4: cap so a long pause doesn't cause a huge jump.
                currentDt = dt;
                long nowMillis = System.currentTimeMillis(); // Epoch ms for timestamp comparisons.

                // Countdown timer.
                timeRemaining -= dt;
                if (timeRemaining <= 0)
                {
                    endGame(player1, player2);
                    stop();
                    return;
                }
                timerLabel.setText("Time: " + (int) timeRemaining);

                // T2: per-frame attack wind-up / end-lag state machine (no threads).
                player1.update(nowMillis, dt);
                player2.update(nowMillis, dt);

                // U5: handle one frame of input from both players.
                processInput(player1, player2, P1_CONTROLS, dt);
                processInput(player2, player1, P2_CONTROLS, dt);

                // --- G4: Two-pass projectile update ---
                // Pass 1: move each projectile; remove out-of-bounds; check vs each player.
                Iterator<Projectile> it = projectileManager.getActiveProjectiles().iterator();
                while (it.hasNext())
                {
                    Projectile p = it.next();
                    p.updatePosition(dt); // Move along its direction vector.

                    if (isOutOfBounds(p))
                    {
                        root.getChildren().remove(p.getSprite()); // Remove visual.
                        it.remove(); // Remove from list (safe via iterator).
                        continue;
                    }

                    // Check whether this projectile hits the opposing player (not its owner).
                    Character hitTarget = null;
                    if (p.getOwner() != player1)
                    {
                        hitTarget = player1; // Projectile fired by P2; check P1.
                    }
                    else
                    {
                        hitTarget = player2; // Projectile fired by P1; check P2.
                    }
                    projectileManager.handleCollision(p, hitTarget, it); // Deals damage and removes.
                }

                // Pass 2: projectile-vs-projectile — collect losers first, then bulk-remove.
                // A separate loop avoids ConcurrentModificationException on the live list.
                List<Projectile> toRemove = new ArrayList<>();
                List<Projectile> live = projectileManager.getActiveProjectiles();
                for (int i = 0; i < live.size(); i++)
                {
                    for (int j = i + 1; j < live.size(); j++)
                    {
                        Projectile a = live.get(i);
                        Projectile b = live.get(j);
                        if (a.collidesWith(b))
                        {
                            if (a.getSize() > b.getSize())
                            {
                                toRemove.add(b); // Smaller loses.
                            }
                            else if (b.getSize() > a.getSize())
                            {
                                toRemove.add(a); // Smaller loses.
                            }
                            else
                            {
                                toRemove.add(a); // Equal size: both removed.
                                toRemove.add(b);
                            }
                        }
                    }
                }
                for (Projectile loser : toRemove)
                {
                    projectileManager.removeProjectile(loser); // Removes sprite and list entry.
                }

                updateBars(player1, player2); // Refresh HUD bars.

                // Victory check.
                if (player1.getHealth() <= 0 || player2.getHealth() <= 0)
                {
                    endGame(player1, player2);
                    stop();
                }

                // U5: clear one-shot sets so each key event is processed exactly once per frame.
                justPressed.clear();
                justReleased.clear();
            }
        };
        gameLoop.start();
    }

    // -----------------------------------------------------------------
    // Per-player input processing (U4, U5)
    // -----------------------------------------------------------------

    /**
     * Reads this frame's input sets and dispatches movement, attack, special,
     * shield, and projectile actions for one player. (U4, U5)
     *
     * @param player   The player whose controls are being processed.
     * @param opponent The other player (target for attacks and projectiles).
     * @param controls The key bindings for this player.
     * @param dt       Frame delta-time in seconds.
     */
    private void processInput(Character player, Character opponent, Controls controls, double dt)
    {
        // --- Movement: held keys with diagonal normalization. (U4, U5) ---
        double dx = 0, dy = 0;
        if (activeKeys.contains(controls.left()))  dx -= 1;
        if (activeKeys.contains(controls.right())) dx += 1;
        if (activeKeys.contains(controls.up()))    dy -= 1;
        if (activeKeys.contains(controls.down()))  dy += 1;
        if (dx != 0 || dy != 0)
        {
            double len = Math.sqrt(dx * dx + dy * dy);
            player.moveBy(dx / len, dy / len, dt); // Normalized so diagonal is not faster. (U4)
        }

        // --- Attack: held key; Character gates on isAttacking / isInLag. (U5) ---
        if (activeKeys.contains(controls.attack()))
        {
            if (player.isShieldActive()) player.releaseShieldKey(); // Shield and attack are mutually exclusive.
            player.performAttack(opponent);
        }

        // --- Special: just-pressed so a held key never spams it. (U5) ---
        // Cooldown message is printed inside performSpecialAbility; justPressed ensures it fires once.
        if (justPressed.contains(controls.special()))
        {
            player.performSpecialAbility(opponent);
        }

        // --- Shield: held to activate; one-shot release to deactivate. (U5) ---
        if (activeKeys.contains(controls.shield()))
        {
            player.activateShield();
        }
        if (justReleased.contains(controls.shield()))
        {
            player.releaseShieldKey();
        }

        // --- Projectile: held to charge; fire on the first release. (U5) ---
        if (activeKeys.contains(controls.projectile()) && !player.isProjectileKeyPressed())
        {
            player.startCharging(); // Begin tracking charge duration.
            player.setProjectileKeyPressed(true); // Guard: only startCharging once per hold.
        }
        if (justReleased.contains(controls.projectile()) && player.isProjectileKeyPressed())
        {
            Projectile proj = player.launchChargedProjectile(opponent); // Size/speed from charge time. (G5)
            player.setProjectileKeyPressed(false); // Reset so next hold starts fresh.
            if (proj != null)
            {
                projectileManager.addProjectile(proj); // Add to manager and scene.
            }
        }
    }

    // -----------------------------------------------------------------
    // HUD update
    // -----------------------------------------------------------------

    /**
     * Updates both players' HUD bars each frame.
     * Called from the game loop; always on the FX thread.
     */
    public void updateBars(Character player1, Character player2)
    {
        player1.updateShieldState(System.currentTimeMillis()); // Handles depletion and regeneration.
        player2.updateShieldState(System.currentTimeMillis());

        if (player1ShieldBar != null)
        {
            player1ShieldBar.setProgress(player1.getShieldLevel());
            // U2: blue = shield available; red = on cooldown after a break.
            player1ShieldBar.setStyle(player1.isShieldAvailable() ? "-fx-accent: blue;" : "-fx-accent: red;");
        }
        if (player2ShieldBar != null)
        {
            player2ShieldBar.setProgress(player2.getShieldLevel());
            player2ShieldBar.setStyle(player2.isShieldAvailable() ? "-fx-accent: blue;" : "-fx-accent: red;");
        }
        if (player1HealthBar != null)
        {
            player1HealthBar.setProgress(player1.getHealthPercentage());
            player1HealthBar.setStyle(player1.getHealthPercentage() > 0 ? "-fx-accent: green;" : "-fx-accent: red;");
        }
        if (player2HealthBar != null)
        {
            player2HealthBar.setProgress(player2.getHealthPercentage());
            player2HealthBar.setStyle(player2.getHealthPercentage() > 0 ? "-fx-accent: green;" : "-fx-accent: red;");
        }
    }

    // -----------------------------------------------------------------
    // Utility
    // -----------------------------------------------------------------

    // Returns true when a projectile's sprite has left the arena boundaries.
    private boolean isOutOfBounds(Projectile projectile)
    {
        double x = projectile.getSprite().getLayoutX();
        double y = projectile.getSprite().getLayoutY();
        return x < 0 || x > ARENA_WIDTH || y < 0 || y > ARENA_HEIGHT;
    }

    /**
     * Ends the match: determines the winner by health percentage and shows the end-of-match
     * dialog with Rematch and Main Menu options. (U6)
     * Winner is whoever has the higher health percentage; equal percentages (including both
     * reaching 0 on the same frame) result in a Draw.
     */
    private void endGame(Character player1, Character player2)
    {
        Platform.runLater(() ->
        {
            // U6: compare health percentages so characters with different max-HP are treated fairly.
            double p1pct = player1.getHealth() / player1.getMaxHealth();
            double p2pct = player2.getHealth() / player2.getMaxHealth();

            String message;
            if (Math.abs(p1pct - p2pct) < 1e-9)
            {
                message = "Draw!"; // Equal percentages (including both reaching 0 together).
            }
            else if (p1pct > p2pct)
            {
                message = player1.getName() + " Wins!";
            }
            else
            {
                message = player2.getName() + " Wins!";
            }

            // U6: replace primaryStage.close() with Rematch / Main Menu dialog.
            ButtonType rematchBtn  = new ButtonType("Rematch");
            ButtonType mainMenuBtn = new ButtonType("Main Menu");
            Alert alert = new Alert(Alert.AlertType.NONE, "Game Over! " + message,
                                    rematchBtn, mainMenuBtn);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == rematchBtn)
            {
                rematchAction.run(); // U6: fresh session with the same character specs.
            }
            else
            {
                mainMenuAction.run(); // U6: return to the main navigation screen.
            }
        });
    }

}
