package FightingGame;
// Defines the `FightingGame` package. Groups related game files, such as `Character` and `GameApp`.

import javafx.scene.Group;    
// Used to group multiple visual elements (e.g., body parts of the character) into one sprite.

import java.util.ArrayList;
// A resizable array for storing data, such as projectiles or character attributes.

import java.util.Iterator;
// Allows safe traversal and removal of items from lists during iteration.

import java.util.List;
// Interface for working with lists in Java, implemented by `ArrayList`.

import javafx.animation.KeyFrame;
// Represents a single frame in an animation timeline.

import javafx.animation.Timeline;
// Used for creating animations that cycle through multiple `KeyFrame` objects.

import javafx.application.Platform;
// Ensures thread-safe operations when updating JavaFX UI elements.
import javafx.geometry.Bounds;
import javafx.util.Duration;
// Represents time intervals, used for animations and effects.

import javafx.scene.effect.ColorAdjust;
// Modifies color properties (brightness, contrast) for visual effects like glowing or dimming.

import javafx.scene.effect.DropShadow;
// Creates a shadow effect, often used to indicate shield activation or special abilities.

import javafx.scene.effect.Lighting;
// Simulates a light source on the character, making them appear illuminated.

import javafx.scene.control.ProgressBar;
// Visual representation of health or shield levels above the character.

import javafx.scene.paint.Color;
// Defines colors for elements like the character sprite or effects.

import javafx.scene.paint.Paint;
// A more generic version of `Color`, allows for gradient or pattern fills.

import javafx.scene.shape.Circle;
// Represents the head of the character as a circular node.

import javafx.scene.shape.Line;
// Represents parts of the character sprite (e.g., arms, legs) as straight lines.

public class Character {
    private Timeline glowTimeline; // Animation timeline for glowing effects.

    private String name; // Name of the character (e.g., "Blaze", "Aqua").
    private String passiveAbility; // Passive ability that enhances gameplay (e.g., "burning", "lifesteal").
    private String specialAbilityName; // Name of the character's special active ability.
    private Color color; // Primary color of the character for visual differentiation.
    private Timeline shieldPulseAnimation;

    private Character opponent; // Reference to the character's current opponent.
    private Character owner; // Tracks the entity controlling the character or launching a projectile.

    private ProgressBar shieldBar; // Visual representation of the shield's current strength.
    private Group characterSprite; // The character's visual representation, composed of shapes.
    private ProgressBar healthBar; // Displays the character's current health level.

    private Projectile lastProjectile; // The last projectile launched, used for abilities like teleportation.
    private Runnable onHealthChange; // Defines an action to execute when health changes (e.g., update visuals).

    private double health; // Current health of the character.
    private int maxHealth; // Maximum possible health of the character.
    private int attackPower; // Damage dealt during an attack.
    private int strength; // Additional attribute for future gameplay mechanics.

    private boolean isAttacking = false; // Indicates if the character is currently attacking.
    private boolean isInLag = false; // Indicates if the character is in attack recovery lag.
    private boolean stunned; // Indicates if the character is stunned and unable to act.
    private boolean isShieldAvailable; // Tracks if the shield can be activated.
    private boolean isInvisible = false; // Tracks if the character is invisible.
    private boolean isDisabled = false; // Indicates if the character is disabled (e.g., defeated).
    private boolean burningEffect; // Tracks if the character is under a burning effect.
    private boolean lifestealEffect; // Tracks if the character has a lifesteal effect on attacks.
    private boolean isCharging = false; // Tracks if the character is charging a projectile.
    private boolean isShieldActive = false; // Indicates if the shield is currently active.
    private boolean projectileKeyPressed = false; // Tracks if the projectile key was recently pressed.

    private double shieldLevel = 1.0; // Current shield strength (from 0.0 to 1.0).
    private double movementMultiplier = 1.0; // Adjusts movement speed (default is normal speed).
    private double startX; // Starting X-coordinate for projectile launches.
    private double defensePower; // Defense attribute for reducing incoming damage.
    private double defense; // or appropriate data type


    private double startY; // Starting Y-coordinate for projectile launches
    private double targetX; // Target X-coordinate for projectiles or abilities.
    private double targetY; // Target Y-coordinate for projectiles or abilities.
    private double defenseMultiplier = 1.0; // Multiplier for adjusting defense power.
    
    private double movementSpeed = 3.0; // Default movement speed.

    private long shieldLastUsedTime; // Tracks the last time the shield was used.
    private long lastAttackTime = 0; // Tracks the time of the last attack for combo timing.
    private long lastSpecialUsedTime = 0;
    private long chargingStartTime = -1; // Tracks when projectile charging began.
    private long projectilePressStartTime = -1; // Tracks when the projectile key was pressed.
    private long specialAbilityCooldown; // Cooldown duration for special abilities in milliseconds.

    private static final long SHIELD_COOLDOWN = 5000; // Cooldown for reactivating the shield.
    private static long ATTACK_DELAY_MS = 500; // Wind-up time for attacks in milliseconds.
    private static long END_LAG_MS = 1350; // Recovery lag after attacks in milliseconds.
    private static final int MAX_PROJECTILES = 6; // Maximum number of projectiles allowed at a time.
    private static final double SHIELD_REGEN_RATE = 0.0035; // Rate of shield regeneration per frame.
    private static final double SHIELD_DEPLETION_RATE = 0.0058; // Rate of shield depletion while active.

    long chargeDuration = System.currentTimeMillis() - chargingStartTime; // Tracks the duration of projectile charging.
    double size = Math.min(5.0 + chargeDuration / 100.0, 30.0); // Max size 30
    double speed = Math.max(10.0 - chargeDuration / 500.0, 2.0); // Min speed 2

    
    // Represents a projectile launched by the character.
 // - `this`: Refers to the current character launching the projectile.
 // - `startX` and `startY`: The starting coordinates of the projectile.
 // - `targetX` and `targetY`: The target coordinates the projectile will travel toward.
 // - `chargeDuration`: The time the projectile has been charged, which affects size and speed.
 // - `defenseMultiplier`: Adjusts the projectile's damage reduction or effect based on the character's defense.
    
    Projectile projectile = new Projectile(this, startX, startY, targetX, targetY, chargeDuration, defenseMultiplier);
 

    public Character(String name, int maxHealth, int attackPower, int defensePower, String specialAbilityName, Color color, int x, int y, String passiveAbility)
    {
        // Assigns the character's name.
        this.name = name;

        // Sets the maximum and current health to the same initial value.
        this.maxHealth = maxHealth;
        this.health = maxHealth;

        // Assigns the attack power and defense attributes.
        this.attackPower = attackPower;
        this.defensePower = defensePower;

        // Sets the name of the special ability and the passive ability for this character.
        this.specialAbilityName = specialAbilityName;
        this.passiveAbility = passiveAbility;

        // Sets the character's color (used for the sprite and effects).
        this.color = color;

        // Creates a shield bar above the character.
        this.shieldBar = new ProgressBar(1.0); // Initializes the shield bar at full strength (1.0 = 100%).
        this.shieldBar.setPrefWidth(100); // Sets the shield bar's visual width.
        this.shieldBar.setStyle("-fx-accent: blue;"); // Colors the shield bar blue.
        this.shieldBar.setLayoutX(x); // Positions the shield bar horizontally.
        this.shieldBar.setLayoutY(y - 30); // Positions the shield bar above the character.

        // Adds the shield bar to the game's root node (displaying it in the game).
        GameApp.root.getChildren().add(this.shieldBar);

        
        // Creates the visual representation (sprite) of the character.
        Circle head = new Circle(15, color); // Defines the character's head as a circle.
        Line body = new Line(0, -20, 0, 20); // Defines the character's body as a vertical line.
        Line leftArm = new Line(-20, 0, 0, 0); // Defines the left arm as a horizontal line.
        Line rightArm = new Line(0, 0, 20, 0); // Defines the right arm as a horizontal line.
        Line leftLeg = new Line(-10, 20, 0, 30); // Defines the left leg as a diagonal line.
        Line rightLeg = new Line(10, 20, 0, 30); // Defines the right leg as a diagonal line.

        body.setStroke(Color.BLACK); // Sets the body outline color to black.
        leftArm.setStroke(Color.BLACK); // Sets the left arm outline color to black.
        rightArm.setStroke(Color.BLACK); // Sets the right arm outline color to black.
        leftLeg.setStroke(Color.BLACK); // Sets the left leg outline color to black.
        rightLeg.setStroke(Color.BLACK); // Sets the right leg outline color to black.

        this.characterSprite = new Group(head, body, leftArm, rightArm, leftLeg, rightLeg); // Groups all parts.
        this.characterSprite.setLayoutX(x); // Sets the initial X-coordinate of the character sprite.
        this.characterSprite.setLayoutY(y); // Sets the initial Y-coordinate of the character sprite.

        this.healthBar = new ProgressBar(1.0); // Initializes the health bar to full.
        this.healthBar.setPrefWidth(100); // Sets the width of the health bar.
        this.healthBar.setStyle("-fx-accent: green;"); // Styles the health bar as green.
        this.healthBar.setLayoutX(x); // Positions the health bar relative to the character.
        this.healthBar.setLayoutY(y - 20); // Places the health bar slightly above the character.

        GameApp.root.getChildren().add(this.healthBar); // Adds the health bar to the game scene.
    }

    
    
    public Group getCharacterSprite() 
    {
        // Returns the graphical representation (sprite) of the character.
        // This sprite is used for rendering the character on the game screen.
        return characterSprite;
    }

    public void setProjectileKeyPressed(boolean pressed) {
        // Sets whether the projectile key is currently being pressed.
        // This is used to track if the player is holding down the key to charge a projectile.
        this.projectileKeyPressed = pressed;
    }

    public void setOpponent(Character opponent) 
    {
        // Assigns an opponent to this character.
        this.opponent = opponent;

        // Logs the name of the assigned opponent for debugging purposes.
        System.out.println(name + " is assigned opponent: " + (opponent != null ? opponent.name : "None"));
    }

    public int getStrength() 
    {
        // Returns the character's strength value.
        // This attribute can be used for specific mechanics like determining attack power.
        return strength;
    }

    public double getDefense() {
        return this.defense; // Assuming `defense` is a field in the Character class
    }

    
    public boolean isInvisible() 
    {
        // Returns whether the character is currently invisible.
        // Used to check the character's visibility status during gameplay.
        return isInvisible;    
    }

    public int getAttackPower() {
        // Returns the character's current attack power.
        // This value is used to calculate the damage dealt during attacks.
        return attackPower;
    }

    public double getMaxHealth() 
    {
        // Returns the maximum health of the character.
        // This is used to calculate health percentages or reset health.
        return this.maxHealth;
    }

    public Character getOpponent() 
    {
        // Returns the opponent assigned to this character.
        // This is used for targeting or applying effects.
        return opponent;
    }

    public double getMovementSpeed() {
        // Returns the character's movement speed.
        // This determines how fast the character can move during gameplay.
        return movementSpeed;
    }

    public void setMovementSpeed(double movementSpeed) {
        // Sets the character's movement speed to a new value.
        // This can be modified by abilities or passive effects.
        this.movementSpeed = movementSpeed;
    }

    public void setAttackPower(int attackPower) {
        // Updates the character's attack power to a new value.
        // This is useful for temporary boosts or power adjustments.
        this.attackPower = attackPower;
    }

    public String getSpecialAbilityName() 
    {
        // Returns the name of the character's special ability.
        // This is used to identify which ability to execute when activated.
        return this.specialAbilityName;
    }

    public boolean canActivateShield() {
        // Returns whether the shield can be activated.
        // This checks if the shield is available and not currently active.
        return isShieldAvailable && !isShieldActive;
    }

    public double getDefensePower() {
        // Returns the character's defense power.
        // This value is used to calculate how much incoming damage is reduced.
        return defensePower;
    }

    public void setDefensePower(double defensePower) {
        // Sets the character's defense power to a new value.
        // This allows for temporary boosts or adjustments during gameplay.
        this.defensePower = defensePower;
    }

    public double getShieldLevel() 
    {
        // Returns the current shield level (from 0.0 to 1.0).
        // This is used to determine how much shield remains during combat.
        return shieldLevel;
    }

    public double getHealth() 
    {
        // Returns the character's current health value.
        // This is used to determine if the character is alive or defeated.
        return health;
    }
 // Calculate projectile size based on charge duration
    public double calculateProjectileSize(long chargeDuration) {
        return Math.min(5.0 + chargeDuration / 100.0, 30.0); // Max size 30
    }

    // Calculate projectile speed based on charge duration
    public double calculateProjectileSpeed(long chargeDuration) {
        return Math.max(10.0 - chargeDuration / 500.0, 2.0); // Min speed 2
    }

    // Track whether the character is charging
    public boolean isCharging() {
        return this.isCharging;
    }

    // Getters and setters for effects
    public boolean hasBurningEffect() {
        // Returns whether the character has a burning effect applied.
        // Used to check for status effects during gameplay.
        return burningEffect;
    }

    public void setBurningEffect(boolean burningEffect) {
        // Sets whether the character is affected by a burning status effect.
        this.burningEffect = burningEffect;
    }

    public boolean hasLifestealEffect() {
        // Returns whether the character has a lifesteal effect on attacks.
        return lifestealEffect;
    }

    public void setLifestealEffect(boolean lifestealEffect) {
        // Sets whether the character has a lifesteal effect.
        this.lifestealEffect = lifestealEffect;
    }

    // Static List to manage projectiles
    private static List<Projectile> activeProjectiles = new ArrayList<>();

    public static boolean canLaunchProjectile() {
        // Returns whether the character can launch another projectile.
        // Ensures the total number of active projectiles is within the allowed limit.
        return activeProjectiles.size() < MAX_PROJECTILES;
    }

    public static synchronized void addProjectile(Projectile projectile) {
        // Adds a projectile to the list of active projectiles.
        // Ensures no duplicate projectiles and renders it in the game scene.
        if (!activeProjectiles.contains(projectile)) {
            activeProjectiles.add(projectile);
            GameApp.addProjectileToScene(projectile.getSprite());
        }
    }

    public static synchronized void removeProjectile(Projectile projectile) {
        // Removes a projectile from the list of active projectiles.
        // Ensures the projectile is no longer rendered in the game scene.
        activeProjectiles.remove(projectile);
        GameApp.removeProjectileFromScene(projectile.getSprite());
    }

    public ProgressBar getShieldBar() {
        if (shieldBar == null) { // Check if the shield bar has not been created yet
            shieldBar = new ProgressBar(1.0); // Initialize the shield bar at full strength (1.0 = 100%)
            shieldBar.setPrefWidth(100); // Set the default width of the shield bar
            shieldBar.setStyle("-fx-accent: blue;"); // Style the shield bar with a blue color
        }
        return shieldBar; // Return the shield bar (either newly created or already existing)
    }


    public String getName() {
        // Returns the name of the character.
        // Used for display, logging, or identification purposes.
        return name;
    }

    public double getHealthPercentage() {
        // Returns the character's current health as a percentage of the maximum health.
        // Useful for updating the visual representation of the health bar.
        return (double) health / maxHealth;
    }

    public void setOnHealthChange(Runnable onHealthChange) {
        // Assigns an action to be executed whenever the character's health changes.
        this.onHealthChange = onHealthChange;
    }
    


    public boolean isShieldActive() {
        // Returns whether the shield is currently active.
        return isShieldActive;
    }

    public Character getOwner() 
    {
        // Returns the owner of this character (e.g., player or AI controlling it).
        return owner;
    }

    public boolean isProjectileKeyPressed() {
        // Returns whether the projectile key is currently pressed.
        return projectileKeyPressed;
    }

    public Paint getColor() 
    {
        // Returns the primary color of the character's sprite.
        return color;
    }

    
 // Method to move the character up while respecting the arena's top boundary
    public void moveUp(double boundaryTop) {
        // Calculate the new Y-coordinate by subtracting movement speed (adjusted by the movement multiplier) from the current Y-coordinate.
        double newY = this.characterSprite.getLayoutY() - movementSpeed * movementMultiplier;

        // Check if the new Y-coordinate is within the bounds of the arena (not above the top boundary).
        if (newY >= boundaryTop) {
            // Update the character's Y-coordinate to move them upward.
            this.characterSprite.setLayoutY(newY);
        }
    }

    // Method to move the character down while respecting the arena's bottom boundary
    public void moveDown(double boundaryBottom) {
        // Calculate the new Y-coordinate by adding movement speed (adjusted by the movement multiplier) to the current Y-coordinate.
        double newY = this.characterSprite.getLayoutY() + movementSpeed * movementMultiplier;

        // Check if the new Y-coordinate is within the bounds of the arena (not below the bottom boundary).
        if (newY <= boundaryBottom) {
            // Update the character's Y-coordinate to move them downward.
            this.characterSprite.setLayoutY(newY);
        }
    }

    // Method to move the character left while respecting the arena's left boundary
    public void moveLeft(double boundaryLeft) {
        // Calculate the new X-coordinate by subtracting movement speed (adjusted by the movement multiplier) from the current X-coordinate.
        double newX = this.characterSprite.getLayoutX() - movementSpeed * movementMultiplier;

        // Check if the new X-coordinate is within the bounds of the arena (not left of the left boundary).
        if (newX >= boundaryLeft) {
            // Update the character's X-coordinate to move them leftward.
            this.characterSprite.setLayoutX(newX);
        }
    }

    // Method to move the character right while respecting the arena's right boundary
    public void moveRight(double boundaryRight) {
        // Calculate the new X-coordinate by adding movement speed (adjusted by the movement multiplier) to the current X-coordinate.
        double newX = this.characterSprite.getLayoutX() + movementSpeed * movementMultiplier;

        // Check if the new X-coordinate is within the bounds of the arena (not right of the right boundary).
        if (newX <= boundaryRight) {
            // Update the character's X-coordinate to move them rightward.
            this.characterSprite.setLayoutX(newX);
        }
    }

    // Method to reset the character to their initial state
    public void resetCharacter() {
        // Reset the character's health to the maximum health value.
        this.health = this.maxHealth;

        // Update the visual health bar to reflect full health.
        this.healthBar.setProgress(1.0);

        // Ensure the health bar is added back to the game's root pane, in case it was removed earlier.
        GameApp.root.getChildren().add(this.healthBar);
    }

    // Method to start charging a projectile
    public void startProjectilePress() {
        // Record the current system time to track when the charging began.
        chargingStartTime = System.currentTimeMillis();

        // Set the `isCharging` flag to true, indicating that the character is actively charging a projectile.
        isCharging = true;
    }
    // Method to release a charged projectile, targeting an opponent
    public void releaseProjectile(Character opponent) {
        // Check if the character is charging a projectile
        if (!isCharging) {
            System.out.println(name + " is not charging a projectile."); // Inform the user that no charge is active
            return; // Exit the method early as no projectile can be launched
        }

        // Stop charging the projectile
        isCharging = false;
        long chargeDuration = System.currentTimeMillis() - chargingStartTime; // Calculate how long the projectile was charged
        chargingStartTime = -1; // Reset the charging start time for future charges

        // Check if the character can launch another projectile (based on the global projectile limit)
        if (!ProjectileManager.canLaunchProjectile(this)) {
            System.out.println(name + " cannot launch: Maximum projectiles reached."); // Inform the user that no more projectiles can be launched
            return; // Exit the method early as launching is not allowed
        }

        // Calculate the size of the projectile based on charge duration
        double size = Math.min(5.0 + chargeDuration / 100.0, 30.0); // Size increases with charge but caps at 30.0
        // Calculate the speed of the projectile based on charge duration
        double speed = Math.max(2.0, Math.min(10.0, 10.0 - chargeDuration / 500.0)); // Speed decreases with charge but has a minimum of 2.0

        // Determine the starting position of the projectile (the character's current position)
        double startX = this.getCharacterSprite().getLayoutX(); // X-coordinate of the character
        double startY = this.getCharacterSprite().getLayoutY(); // Y-coordinate of the character

        // Determine the target position of the projectile (the opponent's current position)
        double targetX = opponent.getCharacterSprite().getLayoutX(); // X-coordinate of the opponent
        double targetY = opponent.getCharacterSprite().getLayoutY(); // Y-coordinate of the opponent

        // Create a new projectile with the calculated properties
        Projectile projectile = new Projectile(this, startX, startY, targetX, targetY, chargeDuration, targetY);

        // Add the projectile to the ProjectileManager for tracking and updates
        ProjectileManager.addProjectile(projectile);

        // Print debug information about the launched projectile
        System.out.printf("%s launched a projectile! Size = %.2f, Speed = %.2f%n", name, size, speed);
    }



 // Returns the time in milliseconds when the projectile key was last pressed.
    public long getProjectilePressStartTime() 
    {
        return projectilePressStartTime; // Allows other methods to access the stored timestamp for projectile-related logic.
    }

    // Sets the time in milliseconds when the projectile key was pressed.
    public void setProjectilePressStartTime(long timeMillis) 
    {
        this.projectilePressStartTime = timeMillis; // Updates the timestamp, typically when the projectile charging starts.
    }

    // Initializes the starting and target coordinates for the character's projectile.
    public void initializeCoordinates() 
    {
        if (opponent != null) { // Ensure the opponent is assigned.
            // Set the starting position (current character's location).
            this.startX = this.getCharacterSprite().getLayoutX();
            this.startY = this.getCharacterSprite().getLayoutY();

            // Set the target position (opponent's location).
            this.targetX = opponent.getCharacterSprite().getLayoutX();
            this.targetY = opponent.getCharacterSprite().getLayoutY();
        } 
        else 
        {
            // Log an error if the opponent is not set.
            System.err.println("Error: Opponent is not set.");
        }
    }

    // Applies a burning effect to the opponent, dealing 3 damage per second for 3 seconds.
    public void applyBurningEffect(Character opponent) {
        // Create a new thread to handle the burning effect over time without blocking the main game loop.
        new Thread(() -> {
            for (int i = 0; i < 3; i++) { // Loop for 3 iterations (3 seconds of burning).
                try {
                    Thread.sleep(1000); // Pause for 1 second between damage ticks.
                } catch (InterruptedException e) {
                    e.printStackTrace(); // Log any interruptions during the sleep.
                }
                opponent.takeDamage(3, lastProjectile); // Apply 3 damage to the opponent.
                // Log the opponent's remaining health after applying the burn.
                System.out.println(opponent.getName() + " is burning! Remaining health: " + opponent.getHealth());
            }
        }).start(); // Start the thread to execute the burning effect.
    }

    // Applies a burning effect to the character itself, dealing 3 damage per second for 3 seconds.
    public void applyBurningEffect() 
    {
        // Create a new thread to handle self-inflicted burning damage over time.
        new Thread(() -> 
        {
            try {
                for (int i = 0; i < 3; i++)
                { // Loop for 3 iterations (3 seconds of burning).
                    Thread.sleep(1000); // Pause for 1 second between damage ticks.
                    takeDamage(3, lastProjectile); // Apply 3 damage to the character.
                    // Log the character's name and indicate they are taking burning damage.
                    System.out.println(name + " is taking burning damage!");
                }
            } 
            catch (InterruptedException e)
            {
                e.printStackTrace(); // Log any interruptions during the sleep.
            }
        }).start(); // Start the thread to execute the self-burning effect.
    }

 // Launches a projectile toward the opponent, applying size and passive effects.
    public void launchProjectile(Character opponent) {
        // Ensure both characters and their sprites are valid
        if (this.characterSprite == null || opponent.getCharacterSprite() == null) {
            System.err.println("Error: Character or opponent sprite is not initialized."); 
            return; // Exit the method if sprites are not properly initialized
        }

        // Get the starting position (current position of this character)
        double startX = this.characterSprite.getLayoutX();
        double startY = this.characterSprite.getLayoutY();

        // Get the target position (current position of the opponent)
        double targetX = opponent.getCharacterSprite().getLayoutX();
        double targetY = opponent.getCharacterSprite().getLayoutY();

        // Calculate the duration the projectile was charged
        long chargeDuration = System.currentTimeMillis() - chargingStartTime;
        chargingStartTime = -1; // Reset the charging state
        isCharging = false; // Mark charging as complete

        // Dynamically calculate the projectile's speed based on charge duration
        double speed = Math.max(5.0, 10.0 - Math.min(chargeDuration / 500.0, 5.0)); // Minimum speed = 5.0

        // Dynamically calculate the projectile's size based on charge duration
        double size = Math.min(10.0 + chargeDuration / 100.0, 50.0); // Maximum size = 50.0

        // Create a new projectile object with the calculated properties
        Projectile projectile = new Projectile(this, startX, startY, targetX, targetY, chargeDuration, size);

        // Add the projectile to the active projectiles in the ProjectileManager
        ProjectileManager.addToActiveProjectiles(projectile);

        // Print debug information about the launched projectile
        System.out.println(getName() + " launched a projectile! Size: " + size + ", Speed: " + speed);
    }

    /**
     * Temporarily boosts the character's attack speed by reducing the attack delay 
     * and end lag for a specified duration.
     *
     * @param speedMultiplier The factor by which the attack speed is increased. 
     *                        Higher values result in faster attacks.
     * @param duration        The duration (in milliseconds) for which the boost lasts.
     */
    public void boostAttackSpeed(double speedMultiplier, int duration) {
        // Store the original attack delay and end lag values to restore later
        long originalAttackDelay = ATTACK_DELAY_MS; // Current time delay before attacks start
        long originalEndLag = END_LAG_MS; // Current time delay after an attack (recovery period)

        // Modify the attack delay and end lag by dividing them by the speed multiplier
        ATTACK_DELAY_MS = (long) (ATTACK_DELAY_MS / speedMultiplier); // Reduce attack wind-up time
        END_LAG_MS = (long) (END_LAG_MS / speedMultiplier); // Reduce recovery time after attacking

        // Print debug statements to confirm the attack speed boost
        System.out.println(name + "'s attack speed is boosted!"); // Indicates the ability is active
        System.out.println("New Attack Delay: " + ATTACK_DELAY_MS); // Prints the modified attack delay
        System.out.println("New End Lag: " + END_LAG_MS); // Prints the modified end lag

        // Create a new thread to handle reverting the speed boost after the duration ends
        new Thread(() -> {
            try {
                // Pause the thread for the specified duration (how long the boost lasts)
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace(); // Log any thread interruption errors
            }
            // Revert the attack delay and end lag back to their original values
            ATTACK_DELAY_MS = originalAttackDelay; // Restore original attack delay
            END_LAG_MS = originalEndLag; // Restore original end lag
            System.out.println(name + "'s attack speed boost has ended."); // Confirm that the boost ended
        }).start(); // Start the thread to manage the timing of the boost
    }




 // Executes a combo attack on the opponent.
    private void executeComboAttack(Character opponent) {
        int comboDamage = 10; // Fixed damage dealt by the combo attack

        // Inflict the combo damage on the opponent
        opponent.takeDamage(comboDamage, lastProjectile); // Use the last projectile (if any) for effects


        // Optionally apply a visual effect to represent the combo attack
        applyAttackHitEffect(opponent);
    }

 // Begins the charging process for a projectile.
    public void startCharging() {
        if (!isCharging) { // Check if the character is not already charging
            chargingStartTime = System.currentTimeMillis(); // Record the time charging began
            isCharging = true; // Mark charging as active
            System.out.println(name + " started charging a projectile!");
        }
    }


 // Launches a charged projectile toward the opponent after charging.
    public Projectile launchChargedProjectile(Character opponent) {
        // Ensure the character is charging before launching
        if (!isCharging) {
            System.out.println(name + " is not charging and cannot launch a projectile.");
            return null; // Exit if the character is not charging
        }

        // Ensure the opponent is valid
        if (opponent == null) {
            System.err.println(name + " cannot launch a projectile because the opponent is null.");
            return null; // Exit if no valid opponent is specified
        }

        // Ensure charging has a valid start time
        if (chargingStartTime == -1) {
            System.err.println(name + " attempted to launch a projectile without a valid charging state.");
            return null; // Exit if charging state is invalid
        }

        // Calculate the charge duration
        long chargeDuration = System.currentTimeMillis() - chargingStartTime;
        chargingStartTime = -1; // Reset the charging state
        isCharging = false; // Mark charging as complete

        // Dynamically calculate the size and speed of the projectile
        double speed = Math.max(5.0, 10.0 - chargeDuration / 500.0); // Minimum speed = 5.0
        double size = Math.min(10.0 + chargeDuration / 100.0, 50.0); // Maximum size = 50.0

        // Create the projectile with calculated properties
        Projectile projectile = new Projectile(this, 
            getCharacterSprite().getLayoutX(), // Start X-coordinate
            getCharacterSprite().getLayoutY(), // Start Y-coordinate
            opponent.getCharacterSprite().getLayoutX(), // Target X-coordinate
            opponent.getCharacterSprite().getLayoutY(), // Target Y-coordinate
            size, speed);

        // Check if the projectile has a valid owner
        if (projectile.getOwner() == null) {
            System.err.println("Error: Projectile created without a valid owner: " + name);
        }

        // Log details about the launched projectile
        System.out.println(name + " launched a projectile! Charge Duration: " + chargeDuration 
            + "ms, Size: " + size + ", Speed: " + speed);
        return projectile; // Return the created projectile
    }


 // Returns the bounds of the character's sprite for collision detection.
    public Bounds getBounds() {
        return this.getCharacterSprite().getBoundsInParent(); // Get the visual bounds of the character
    }


 // Starts a special visual effect for the character.
    public void startSpecialEffect() {
        if (characterSprite != null) { // Ensure the sprite is initialized.
            ColorAdjust colorAdjust = new ColorAdjust(); // Create a color adjustment effect.
            colorAdjust.setBrightness(0.5); // Set brightness to create a glow effect.
            characterSprite.setEffect(new Lighting()); // Apply a lighting effect to enhance the glow.
            System.out.println(name + " is using a special ability!"); // Log the activation.
        }
    }

    // Clears the special visual effect from the character.
    public void clearSpecialEffect() {
        if (characterSprite != null) { // Ensure the sprite is initialized.
            characterSprite.setEffect(null); // Remove any applied visual effects.
            System.out.println(name + "'s special ability effect cleared!"); // Log the effect removal.
        }
    }

  


    // Restores the character's health to its maximum value.
    public void restoreHealthToFull() {
        this.health = this.maxHealth; // Set health to the maximum value.
        System.out.println(this.name + "'s health has been fully restored!"); // Log the restoration.
        if (onHealthChange != null) { // If a listener is defined for health changes:
            onHealthChange.run(); // Trigger the listener to update visuals or effects.
        }
    }

 // Updates the shield state, including regeneration and depletion.
    public void updateShieldState(long currentTime) {
        // Ensure the shield bar is initialized before performing operations.
        if (shieldBar == null) {
            System.err.println("Error: shieldBar is not initialized for " + name);
            return; // Exit the method if the shield bar is not initialized.
        }

        // Handle shield depletion if it is active.
        if (isShieldActive) {
            shieldLevel -= SHIELD_DEPLETION_RATE; // Deplete the shield level at a constant rate.
            if (shieldLevel <= 0) { // If the shield is depleted, deactivate it.
                deactivateShield(); // Turn off the shield.
                shieldLevel = 0; // Ensure the shield level doesn't go negative.
            }
        } 
        // Regenerate the shield if it is not active and not full.
        else if (shieldLevel < 1.0) {
            double regenerationRate = SHIELD_REGEN_RATE; // Default regeneration rate.
            // Check if the character has a passive ability to regenerate faster.
            if ("fast shield".equals(passiveAbility)) {
                regenerationRate *= 1.5; // Increase regeneration rate by 50%.
                System.out.println(name + "'s shield regenerates faster due to passive ability!");
            }
            shieldLevel = Math.min(1.0, shieldLevel + regenerationRate); // Regenerate shield but cap at 100%.
        }

        // Check if the shield is available for activation after cooldown.
        if (!isShieldAvailable && (currentTime - shieldLastUsedTime >= SHIELD_COOLDOWN)) {
            isShieldAvailable = true; // Mark the shield as available.
        }

        // Update the shield bar's progress and color based on availability.
        shieldBar.setProgress(shieldLevel); // Update visual representation of the shield.
        shieldBar.setStyle(isShieldAvailable ? "-fx-accent: green;" : "-fx-accent: red;"); // Green when available, red otherwise.
    }

    // Applies a stun effect to the opponent with a 10% chance.
    public void applyStunEffect(Character opponent) {
        if (Math.random() < 0.1) { // 10% chance to apply the stun.
            opponent.setStunned(true); // Set the opponent's stunned state to true.
            System.out.println(opponent.getName() + " is stunned!"); // Log the stun event.
        }
    }

    // Initializes a projectile and assigns this character as its owner.
    public void initializeProjectile(Projectile projectile) {
        projectile.setOwner(this); // Assign `this` character as the owner of the projectile.
    }

    // Activates the shield, adding a glowing effect around the character sprite.
    public void activateShield() {
        if (characterSprite != null) { // Ensure the character sprite exists.
            DropShadow shieldEffect = new DropShadow(); // Create a glow effect.
            shieldEffect.setColor(this.name.equals("Aqua") ? Color.DARKBLUE : Color.BLUE); // Use a darker blue for Aqua's shield.
            shieldEffect.setRadius(20); // Set the intensity of the glow.

            // Apply the glowing effect to the sprite.
            characterSprite.setEffect(shieldEffect);

            // Create a pulsing effect for the shield glow.
            shieldPulseAnimation = new Timeline(
                new KeyFrame(Duration.millis(500), e -> shieldEffect.setRadius(20)), // Start of pulse.
                new KeyFrame(Duration.millis(1000), e -> shieldEffect.setRadius(30)) // End of pulse.
            );
            shieldPulseAnimation.setCycleCount(Timeline.INDEFINITE); // Loop the pulse animation indefinitely.
            shieldPulseAnimation.play(); // Start the animation.
        }
        isShieldActive = true; // Mark the shield as active.
    }

    // Deactivates the shield, removing the glowing effect.
    public void deactivateShield() {
        if (characterSprite != null) { // Ensure the character sprite exists.
            characterSprite.setEffect(null); // Remove any applied effect.
            if (shieldPulseAnimation != null) { // Stop the pulsing effect if it exists.
                shieldPulseAnimation.stop();
                shieldPulseAnimation = null; // Clear the reference to the animation.
            }
        }
        isShieldActive = false; // Mark the shield as inactive.
    }

    // Removes any ability-related visual effect from the character sprite.
    public void removeAbilityGlow() {
        if (characterSprite != null) { // Ensure the character sprite exists.
            characterSprite.setEffect(null); // Clear any applied effect on the sprite.
        }
    }

    // Handles character movement based on player input.
    public void handleInput(String input, double arenaWidth, double arenaHeight) {
        switch (input) {
            case "UP": // Move the character up.
                moveUp(GameApp.BOUNDARY_TOP);
                break;
            case "DOWN": // Move the character down.
                moveDown(GameApp.BOUNDARY_BOTTOM);
                break;
            case "LEFT": // Move the character left.
                moveLeft(GameApp.BOUNDARY_LEFT);
                break;
            case "RIGHT": // Move the character right.
                moveRight(GameApp.BOUNDARY_RIGHT);
                break;
            default: // Handle unrecognized input.
                System.err.println("Unknown input: " + input);
        }
    }

    // Checks whether the character's shield blocks a given projectile.
    public boolean blocksProjectile(Projectile projectile) {
        return isShieldActive && // Check if the shield is active.
               characterSprite.getBoundsInParent().intersects(projectile.getSprite().getBoundsInParent()); 
               // Check if the projectile intersects with the character's sprite.
    }

    // Iterates over all active projectiles to check for collisions or blocked projectiles.
    public void checkProjectileCollisions() {
        Iterator<Projectile> iterator = ProjectileManager.getActiveProjectiles().iterator(); // Retrieve an iterator for active projectiles.
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next(); // Get the next projectile.

            // Skip projectiles launched by this character.
            if (projectile.getOwner() == this) {
                continue;
            }

            // Check if the shield blocks the projectile.
            if (blocksProjectile(projectile)) {
                iterator.remove(); // Remove the projectile safely.
                ProjectileManager.removeProjectile(projectile); // Remove it from the manager.
                System.out.println(name + " blocked a projectile!"); // Log the block event.
                continue; // Skip further checks for this projectile.
            }

            // Check if the projectile hits the character.
            if (characterSprite.getBoundsInParent().intersects(projectile.getSprite().getBoundsInParent())) {
                takeDamage(projectile.getDamage(), projectile); // Apply damage to the character.

                // Apply additional effects if the projectile has them.
                if (projectile.isBurning()) {
                    applyBurningEffect(); // Apply a burning effect to the character.
                    System.out.println(name + " is burning due to a projectile!");
                }

                if (projectile.isLifesteal() && projectile.getOwner() != null) {
                    double lifestealAmount = projectile.getDamage() / 2.0; // Calculate the lifesteal amount.
                    projectile.getOwner().heal(lifestealAmount); // Heal the projectile's owner.
                    System.out.println(projectile.getOwner().getName() + " healed for " + lifestealAmount + " due to lifesteal!");
                }

                iterator.remove(); // Safely remove the projectile.
                ProjectileManager.removeProjectile(projectile); // Remove it from the manager.
                System.out.println(name + " was hit by a projectile!"); // Log the hit event.
            }
        }
    }

    /**
     * Deactivates the shield when the shield key is released.
     */
    public void releaseShieldKey() 
    {
        if (isShieldActive) 
        {
            deactivateShield(); // Deactivate the shield and remove visual effects.
            shieldLastUsedTime = System.currentTimeMillis(); // Record the time for cooldown tracking.
        }  
        
        
    }

    /**
     * Executes a basic attack on the opponent.
     *
     * @param opponent The opponent character being attacked.
     */
    public void performAttack(Character opponent) {
        if (isDisabled) { // Prevent attacks if the character is stunned or disabled.
            System.out.println(name + " is stunned and cannot attack!");
            return;
        }

        if (isAttacking || isInLag) { // Prevent attacks if already attacking or recovering.
            System.out.println(name + " is already attacking or in lag!");
            return;
        }

        if (this.owner == null) { // Ensure the character is the owner of the attack.
            this.owner = this;
        }

        System.out.println(this.name + " is winding up for an attack...");
        isAttacking = true; // Mark the character as attacking.

        applyAttackWindUpEffect(); // Apply a visual effect for the wind-up.

        // Execute the attack in a new thread
        new Thread(() -> {
            try {
                Thread.sleep(ATTACK_DELAY_MS); // Simulate the wind-up delay.

                executeComboAttack(opponent); // Perform a combo attack if applicable.

                int damage = calculateDamage(); // Calculate the damage to be dealt.
                opponent.takeDamage(damage, null); // Apply damage to the opponent.
                System.out.println(this.name + " attacked " + opponent.name + " for " + damage + " damage!");

                Thread.sleep(END_LAG_MS); // Simulate recovery time after the attack.
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                isAttacking = false; // Mark the character as ready for another attack.
                System.out.println(this.name + " is ready to attack again!");
            }
        }).start();
    }

    /**
     * Switches the character's position with the last launched projectile.
     */
    public void switchWithProjectile() {
        if (lastProjectile != null) { // Ensure a projectile exists to switch with.
            double tempX = this.characterSprite.getLayoutX(); // Store current X position.
            double tempY = this.characterSprite.getLayoutY(); // Store current Y position.

            // Swap the positions of the character and the projectile.
            this.characterSprite.setLayoutX(lastProjectile.getSprite().getLayoutX());
            this.characterSprite.setLayoutY(lastProjectile.getSprite().getLayoutY());
            lastProjectile.getSprite().setLayoutX(tempX);
            lastProjectile.getSprite().setLayoutY(tempY);

            System.out.println(name + " switched positions with the projectile!"); // Debug log.
        }
    }

    /**
     * Temporarily increases the character's defense multiplier for 5 seconds.
     */
    public void increaseDefenseTemporarily() {
        double originalDefenseMultiplier = defenseMultiplier; // Save the current defense multiplier.

        defenseMultiplier *= 1.5; // Increase defense multiplier by 50%.
        System.out.println(name + "'s defense is temporarily boosted!"); // Debug log.

        // Revert the defense multiplier after 5 seconds.
        new Thread(() -> {
            try {
                Thread.sleep(5000); // Wait for 5 seconds.
            } catch (InterruptedException e) {
                e.printStackTrace(); // Handle thread interruptions.
            }
            defenseMultiplier = originalDefenseMultiplier; // Restore the original multiplier.
            System.out.println(name + "'s defense boost has ended."); // Debug log.
        }).start();
    }

    /**
     * Applies a visual flash effect to the opponent's sprite when hit.
     *
     * @param opponent The character being hit by the attack.
     */
    private void applyAttackHitEffect(Character opponent) {
        if (opponent.getCharacterSprite() != null) { // Ensure the opponent's sprite exists.
            ColorAdjust hitFlash = new ColorAdjust(); // Create a brightness adjustment effect.
            hitFlash.setBrightness(1.0); // Set brightness to full for a flash effect.

            Platform.runLater(() -> opponent.getCharacterSprite().setEffect(hitFlash)); // Apply the effect.

            // Remove the flash effect after 200 milliseconds.
            new Thread(() -> {
                try {
                    Thread.sleep(200); // Flash effect duration.
                } catch (InterruptedException e) {
                    e.printStackTrace(); // Handle interruption.
                }
                Platform.runLater(() -> opponent.getCharacterSprite().setEffect(null)); // Clear the effect.
            }).start();
        }
    }

    /**
     * Tracks combo attacks based on timing of consecutive attacks.
     */
    public void registerAttack() {
        long currentTime = System.currentTimeMillis(); // Get the current time.

        if (currentTime - lastAttackTime <= 1000) { // Check if the attack is within the combo time window (1 second).
            
        } else {
        }

        lastAttackTime = currentTime; // Update the last attack time.
    }

    /**
     * Calculates the damage dealt during an attack.
     *
     * @return The amount of damage to be dealt.
     */
    private int calculateDamage() {
        if (this.owner == null) { // Ensure the character has an owner (e.g., itself).
            System.err.println("Error: Owner is null during damage calculation.");
            return 0; // Prevent damage calculation if owner is null.
        }

        Character target = this.owner.getOpponent(); // Get the opponent of the owner.
        if (target == null) { // Ensure the opponent exists.
            System.err.println("Error: Opponent is null during damage calculation.");
            return 0; // Prevent damage calculation if opponent is null.
        }

        int baseDamage = this.attackPower; // Base attack power of the character.
        double randomFactor = Math.random(); // Generate a random value for critical hits or misses.

        if (randomFactor > 0.9) { // 10% chance for a critical hit.
            System.out.println(owner.getName() + " landed a critical hit!");
            return (int) (baseDamage * 2); // Critical hits double the damage.
        }

        if (randomFactor < 0.1) { // 10% chance for a missed attack.
            System.out.println(owner.getName() + " missed the attack!");
            return 0; // Missed attacks deal no damage.
        }

        double defenseReduction = target.getDefense() * target.getDefenseMultiplier(); // Calculate defense reduction.
        int finalDamage = Math.max(1, baseDamage - (int) defenseReduction); // Ensure a minimum of 1 damage.
        System.out.println(owner.getName() + " dealt " + finalDamage + " damage to " + target.getName());
        return finalDamage; // Return the calculated damage.
    }


    /**
     * Applies a red glow effect to indicate the attack wind-up and removes it after the delay.
     */
    private void applyAttackWindUpEffect() {
        if (characterSprite != null) { // Check if the character sprite exists to apply the effect.
            DropShadow attackEffect = new DropShadow(); // Create a red glow effect.
            attackEffect.setColor(Color.RED); // Set the glow color to red.
            attackEffect.setRadius(30); // Adjust the glow radius for visibility.

            Platform.runLater(() -> characterSprite.setEffect(attackEffect)); // Apply the effect on the UI thread.

            new Thread(() -> {
                try {
                    Thread.sleep(ATTACK_DELAY_MS); // Match the wind-up delay.
                } catch (InterruptedException e) {
                    e.printStackTrace(); // Handle interruption.
                }
                Platform.runLater(() -> characterSprite.setEffect(null)); // Clear the effect on the UI thread.
            }).start();
        }
    }

    /**
     * Sets the opacity of the character sprite to indicate stunned state.
     *
     * @param isStunned Whether the character is stunned.
     */
    public void setStunnedVisualEffect(boolean isStunned) {
        if (isStunned) {
            characterSprite.setOpacity(0.5); // Dim the character sprite to indicate the stunned state.
        } else {
            characterSprite.setOpacity(1.0); // Restore full opacity to indicate normal state.
        }
    }

    /**
     * Handles damage taken by the character, considering shield logic.
     *
     * @param damage The amount of damage to apply.
     * @param projectile The projectile causing the damage (optional).
     */
    public void takeDamage(double damage, Projectile projectile) {
        if (isShieldActive) { // If the shield is active, reduce shield strength.
            double shieldReductionFactor = this.name.equals("Aqua") ? 0.5 : 1.0; // Aqua takes less shield damage.
            double shieldDamage = damage * shieldReductionFactor;

            shieldLevel -= shieldDamage / 100.0; // Reduce shield level.

            if (shieldLevel <= 0) { // If the shield breaks, deactivate it.
                shieldLevel = 0;
                deactivateShield();
                System.out.println(name + "'s shield is broken!");
            } else {
                System.out.println(name + "'s shield absorbed " + shieldDamage + " damage. Remaining shield: " + (shieldLevel * 100) + "");
            }
            return; // Skip health damage if the shield is active.
        }

        health = Math.max(0, health - damage); // Reduce health if the shield is inactive.
        System.out.println(name + " took " + damage + " damage. Remaining health: " + health);

        if (health <= 0) {
            System.out.println(name + " has been defeated!"); // Log defeat if health reaches zero.
        }
    }

    /**
     * Reduces shield strength directly, bypassing other logic.
     *
     * @param shieldDamage The amount of shield damage to apply.
     */
    public void reduceShieldStrength(double shieldDamage) {
        shieldLevel -= shieldDamage / 100.0; // Scale damage to shield level range.

        if (shieldLevel <= 0) { // If the shield breaks, deactivate it.
            shieldLevel = 0;
            deactivateShield();
            System.out.println(name + "'s shield is broken!");
        } else {
            System.out.println(name + "'s shield absorbed " + shieldDamage + " damage. Remaining shield: " + (shieldLevel * 100) + "%");
        }
    }

    /**
     * Sets the last projectile for characters with a teleport ability.
     *
     * @param projectile The projectile to associate with the character.
     */
    public void setLastProjectile(Projectile projectile) {
        if (this.passiveAbility.equals("teleport")) { // Check if the character has the teleport ability.
            this.lastProjectile = projectile; // Save the projectile.
            System.out.println(name + " has set the last projectile for teleportation."); // Log the action.
        } else {
            System.out.println(name + " does not have a teleport ability, no last projectile set."); // Log inability.
        }
    }

    /**
     * Sets the cooldown for the special ability.
     *
     * @param specialAbilityName The name of the special ability.
     */
    public void setSpecialAbilityName(String specialAbilityName) {
        this.specialAbilityName = specialAbilityName;
        this.specialAbilityCooldown = AbilityManager.getCooldown(specialAbilityName); // Set cooldown based on ability.
    }

    /**
     * Resets the cooldown timer for the character's special ability.
     */
    public void resetSpecialAbilityCooldown() {
        this.lastSpecialUsedTime = System.currentTimeMillis(); // Record the current time.
        this.specialAbilityCooldown = AbilityManager.getCooldown(this.specialAbilityName); // Set cooldown from ability manager.
        System.out.println(name + "'s special ability cooldown has been reset!"); // Log the reset.
    }

    /**
     * Executes the character's special ability.
     *
     * @param opponent The opponent affected by the special ability.
     */
    public void performSpecialAbility(Character opponent) {
        if (canUseSpecialAbility()) { // Check if the special ability is ready.
            System.out.println(name + " is using their special ability: " + specialAbilityName);
            AbilityManager.executeAbility(this, opponent); // Call AbilityManager to execute the ability.
            lastSpecialUsedTime = System.currentTimeMillis(); // Update the last used time.
        } else {
            long timeRemaining = (specialAbilityCooldown - (System.currentTimeMillis() - lastSpecialUsedTime)) / 1000;
            System.out.println(name + "'s special ability is on cooldown! Cooldown time remaining: " + timeRemaining + " seconds.");
        }
    }

    /**
     * Reduces the character's health by a specified amount.
     *
     * @param actualDamage The damage to apply to the character's health.
     */
    public void reduceHealth(double actualDamage) {
        if (actualDamage < 0) { // Ensure the damage is not negative.
            System.out.println("Invalid damage amount. Damage cannot be negative."); // Log error.
            return;
        }

        this.health -= actualDamage; // Deduct damage from health.

        if (this.health < 0) { // Prevent health from dropping below zero.
            this.health = 0;
        }

        System.out.println(name + " took " + actualDamage + " damage. Remaining health: " + this.health); // Log health status.

        if (this.health == 0) { // Check if the character is defeated.
            System.out.println(name + " has been defeated!"); // Log defeat.
            this.isDisabled = true; // Disable character actions.
        }
    }

    /**
     * Checks if the character's special ability can be used.
     *
     * @return True if the special ability can be used, false otherwise.
     */
    public boolean canUseSpecialAbility() {
        long currentTime = System.currentTimeMillis(); // Get current time.
        return currentTime - lastSpecialUsedTime >= specialAbilityCooldown; // Check cooldown.
    }

    /**
     * Displays a visual effect when the ability cooldown resets.
     */
    public void showCooldownResetEffect() {
        if (characterSprite != null) { // Ensure the sprite exists.
            DropShadow resetEffect = new DropShadow(); // Create a visual effect.
            resetEffect.setColor(Color.GOLD); // Use gold to signify readiness.
            resetEffect.setRadius(20); // Set the size of the glow effect.
            Platform.runLater(() -> characterSprite.setEffect(resetEffect)); // Apply the effect on the UI thread.

            new Thread(() -> { // Remove the effect after a short duration.
                try {
                    Thread.sleep(1000); // Effect duration (1 second).
                } catch (InterruptedException e) {
                    e.printStackTrace(); // Handle exceptions.
                }
                Platform.runLater(() -> characterSprite.setEffect(null)); // Clear the effect on the UI thread.
            }).start();
        }
    }

    /**
     * Retrieves the cooldown duration for the character's special ability.
     *
     * @return The cooldown duration in milliseconds.
     */
    public long getSpecialAbilityCooldown() {
        return specialAbilityCooldown; // Return the cooldown value.
    }

    /**
     * Displays a visual effect for using a special ability.
     */
    public void showSpecialAbilityEffect() {
        if (characterSprite != null) { // Ensure the sprite exists.
            ColorAdjust colorAdjust = new ColorAdjust(); // Create a color adjustment effect.
            colorAdjust.setBrightness(0.5); // Brighten the sprite to indicate ability usage.
            characterSprite.setEffect(colorAdjust); // Apply the effect.

            System.out.println(name + " is using a special ability!"); // Log the action.

            new Thread(() -> { // Remove the effect after a short duration.
                try {
                    Thread.sleep(2000); // Duration of the effect (2 seconds).
                } catch (InterruptedException e) {
                    e.printStackTrace(); // Handle exceptions.
                }
                Platform.runLater(() -> characterSprite.setEffect(null)); // Clear the effect on the UI thread.
            }).start();
        }
    }

    /**
     * Displays a shield visual effect when activated.
     */
    public void showShieldEffect() {
        if (characterSprite != null) { // Ensure the sprite exists.
            DropShadow blueGlow = new DropShadow(); // Create a blue glow effect.
            blueGlow.setColor(Color.BLUE); // Use blue to signify shielding.
            blueGlow.setRadius(20); // Set the size of the glow.
            characterSprite.setEffect(blueGlow); // Apply the effect.

            new Thread(() -> { // Remove the effect after the shield duration ends.
                try {
                    Thread.sleep(3000); // Shield duration (3 seconds).
                } catch (InterruptedException e) {
                    e.printStackTrace(); // Handle exceptions.
                }
                Platform.runLater(() -> characterSprite.setEffect(null)); // Clear the effect on the UI thread.
            }).start();
        }
    }

    /**
     * Calculates the reduced damage by subtracting defense and multiplier effects.
     *
     * @param incomingDamage The original damage value.
     * @return The reduced damage after applying defense.
     */
    public double calculateReducedDamage(double incomingDamage) {
        double reducedDamage = incomingDamage - (getDefense() * getDefenseMultiplier());
        return Math.max(0, reducedDamage); // Ensure damage doesn't go negative.
    }

    /**
     * Toggles the character's invisibility.
     *
     * @param isInvisible Whether the character is invisible or not.
     */
    public void setInvisible(boolean isInvisible) {
        this.isInvisible = isInvisible; // Update the invisibility state.
        if (this.characterSprite != null) {
            this.characterSprite.setVisible(!isInvisible); // Hide or show the sprite.
        }
        System.out.println(name + (isInvisible ? " is now invisible." : " is now visible.")); // Log the state change.
    }

    /**
     * Heals the character by a specified amount.
     *
     * @param d The amount of health to restore.
     */
    public void heal(double d) {
        double oldHealth = this.health; // Store the current health.
        this.health = Math.min(this.health + d, this.maxHealth); // Increase health up to the max limit.

        if (oldHealth != this.health && onHealthChange != null) { // Trigger health change actions if applicable.
            onHealthChange.run(); // Notify listeners about the health update.
        }

        System.out.println(name + " healed for " + d + " health! Current health: " + health); // Log the healing action.
    }

    /**
     * Stops the charging process for projectiles and logs the charge duration.
     *
     * @return The duration of the charge in milliseconds.
     */
    public long stopCharging() {
        if (isCharging) { // Check if the character is currently charging.
            long chargeDuration = System.currentTimeMillis() - chargingStartTime; // Calculate the charge duration.
            chargingStartTime = -1; // Reset the charging state.
            isCharging = false;
            System.out.println(name + " charged for " + chargeDuration + " milliseconds."); // Log the charge duration.
            return chargeDuration; // Return the charge duration.
        }
        return 0; // Return 0 if not charging.
    }

    /**
     * Starts a glowing visual effect for the character.
     */
    public void startGlowEffect() {
        if (glowTimeline == null) { // Ensure the effect isn't already active.
            ColorAdjust colorAdjust = new ColorAdjust(); // Create a color adjustment effect.
            Lighting lighting = new Lighting(); // Create a lighting effect.
            this.characterSprite.setEffect(lighting); // Apply the lighting effect.

            glowTimeline = new Timeline(
                new KeyFrame(Duration.millis(0), e -> colorAdjust.setBrightness(0.3)), // Dim phase.
                new KeyFrame(Duration.millis(500), e -> colorAdjust.setBrightness(0.7)) // Bright phase.
            );
            glowTimeline.setCycleCount(Timeline.INDEFINITE); // Repeat the effect indefinitely.
            glowTimeline.setAutoReverse(true); // Alternate between dim and bright phases.
            glowTimeline.play(); // Start the glow effect.
            System.out.println(name + " started glowing!"); // Log the effect activation.
        }
    }

    /**
     * Stops the glowing visual effect for the character.
     */
    public void stopGlowEffect() {
        if (glowTimeline != null) { // Check if the glow effect is active.
            glowTimeline.stop(); // Stop the glow animation.
            glowTimeline = null; // Clear the timeline reference.
            this.characterSprite.setEffect(null); // Remove the visual effect.
            System.out.println(name + " stopped glowing."); // Log the effect stop.
        }
    }

    /**
     * Releases a charged projectile when the charge key is released.
     */
    public void releaseProjectilePress() {
        if (isCharging) { // Check if the character is currently charging.
            launchChargedProjectile(opponent); // Launch the charged projectile at the opponent.
            isCharging = false; // Reset the charging state.
        }
    }

    /**
     * Sets the movement multiplier for the character.
     *
     * @param multiplier The movement speed multiplier.
     */
    public void setMovementMultiplier(double multiplier) {
        this.movementMultiplier = multiplier; // Update the movement multiplier.
    }

    /**
     * Retrieves the current movement multiplier for the character.
     *
     * @return The movement multiplier.
     */
    public double getMovementMultiplier() {
        return movementMultiplier; // Return the movement multiplier.
    }

    /**
     * Temporarily disables the character for a specified duration.
     *
     * @param duration The duration of the disable in milliseconds.
     */
    public void disableForDuration(int duration) {
        this.isDisabled = true; // Prevent the character from taking actions.
        System.out.println(this.name + " is disabled!"); // Log the state change.
        new Thread(() -> {
            try {
                Thread.sleep(duration); // Wait for the specified duration.
            } catch (InterruptedException e) {
                e.printStackTrace(); // Handle interruptions.
            }
            this.isDisabled = false; // Re-enable the character.
            System.out.println(this.name + " is no longer disabled!"); // Log the state change.
        }).start();
    }

    /**
     * Sets the stunned state for the character.
     *
     * @param stunned Whether the character is stunned.
     */
    public void setStunned(boolean stunned) {
        this.stunned = stunned; // Update the stunned state.

        if (stunned) {
            System.out.println(name + " is now stunned."); // Log the state change.
            if (characterSprite != null) {
                characterSprite.setOpacity(0.5); // Dim the sprite to indicate the stunned state.
            }
        } else {
            System.out.println(name + " is no longer stunned."); // Log the state change.
            if (characterSprite != null) {
                characterSprite.setOpacity(1.0); // Restore the sprite's opacity.
            }
        }
    }

    /**
     * Checks if the character is currently stunned.
     *
     * @return True if the character is stunned, false otherwise.
     */
    public boolean isStunned() {
        return stunned; // Return the stunned state.
    }

    /**
     * Updates the character's health.
     *
     * @param health The new health value.
     */
    public void setHealth(int health) {
        if (health < 0) {
            this.health = 0; // Prevent health from going below 0.
        } else if (health > maxHealth) {
            this.health = maxHealth; // Prevent health from exceeding the maximum.
        } else {
            this.health = health; // Set the health to the specified value.
        }
        System.out.println(name + "'s health is now: " + this.health); // Log the new health value.

        if (healthBar != null) { // Update the health bar if it exists.
            healthBar.setProgress((double) this.health / maxHealth); // Adjust the health bar to match the current health.
        }
    }

    /**
     * Retrieves the character's current defense multiplier.
     *
     * @return The defense multiplier.
     */
    public double getDefenseMultiplier() {
        return defenseMultiplier; // Return the defense multiplier.
    }

    /**
     * Updates the character's defense multiplier.
     *
     * @param multiplier The new defense multiplier.
     */
    public void setDefenseMultiplier(double multiplier) {
        if (multiplier < 0) {
            this.defenseMultiplier = 0; // Prevent negative defense multipliers.
        } else {
            this.defenseMultiplier = multiplier; // Update the defense multiplier.
        }
        System.out.println(name + "'s defense multiplier is now: " + this.defenseMultiplier); // Log the new value.
    }

    /**
     * Checks if the character's passive ability is active.
     *
     * @return The passive ability name.
     */
    public String getPassiveAbility() {
        return this.passiveAbility; // Return the passive ability.
    }

    /**
     * Deals damage to the opponent based on projectile size and speed.
     * The damage calculation considers the projectile's properties,
     * the attacker's attack power, and the opponent's defense power.
     *
     * @param opponent The character receiving the damage.
     * @param baseDamage The calculated base damage of the projectile.
     */
    public void dealDamageToOpponent(Character opponent, double baseDamage) {
        // Retrieve the opponent's defense power
        double defense = opponent.getDefensePower();

        // Subtract the opponent's defense from the base damage
        double actualDamage = baseDamage - defense;

        // Ensure that damage cannot be negative
        actualDamage = Math.max(0, actualDamage);

        // Apply the calculated damage to the opponent's health
        opponent.reduceHealth(actualDamage);

        // Log the damage details and the opponent's remaining health
        System.out.println(opponent.getName() + " took " + actualDamage + " damage. Remaining health: " + opponent.getHealth());
    }

    /**
     * Sets the cooldown for the character's special ability.
     *
     * @param cooldown The cooldown duration in milliseconds.
     */
    public void setSpecialAbilityCooldown(int cooldown) {
        // Set the cooldown duration for the special ability
        this.specialAbilityCooldown = cooldown;

        // Record the current time as the last time the ability was used
        this.lastSpecialUsedTime = System.currentTimeMillis();

        // Log the cooldown setup
        System.out.println(name + "'s special ability cooldown is set to " + cooldown + " milliseconds.");
    }

    public void setAbilityReadyVisual() {
        if (characterSprite != null) { // Check if the sprite exists
            characterSprite.setOpacity(1.0); // Restore the sprite's opacity
        }
    }




}
