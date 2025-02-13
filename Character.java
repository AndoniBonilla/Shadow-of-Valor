package FightingGame;
// Defines the `FightingGame` package. Groups related game files, such as `Character` and `GameApp`.

import javafx.animation.KeyFrame;
// Represents a single frame in an animation timeline.
import javafx.animation.Timeline;
// Used for creating animations that cycle through multiple `KeyFrame` objects.
import javafx.application.Platform;
//Importing Platform ensures thread-safe UI updates by running code on the JavaFX Application Thread.
import javafx.scene.Group;
// Used to group multiple visual elements (e.g., body parts of the character) into one sprite.
import javafx.scene.control.ProgressBar;
// Visual representation of health or shield levels above the character.
import javafx.scene.effect.DropShadow;
// Simulates a light source on the character, making them appear illuminated.
import javafx.scene.paint.Color;
// A more generic version of `Color`, allows for gradient or pattern fills.
import javafx.scene.shape.Circle;
// Represents the head of the character as a circular node.
import javafx.scene.shape.Line;
// Represents parts of the character sprite (Ex. arms, legs) as straight lines.
import javafx.util.Duration;
// Represents time intervals, used for animations and effects.

public class Character 

{
    private String name; // Name of the character (e.g., "Blaze", "Aqua").
    private String specialAbilityName; // Name of the character's special active ability.
    private Color color; // Primary color of the character for visual differentiation.
    private Timeline shieldPulseAnimation;

    private Character opponent; // Reference to the character's current opponent.
    private Character owner; // Tracks the entity controlling the character or launching a projectile.

    private ProgressBar shieldBar; // Visual representation of the shield's current strength.
    private Group characterSprite; // The character's visual representation, composed of shapes.
    private ProgressBar healthBar; // Displays the character's current health level.

    
    private double health; // Current health of the character.
    private int maxHealth; // Maximum possible health of the character.
    private int attackPower; // Damage dealt during an attack.

    private boolean isAttacking = false; // Indicates if the character is currently attacking.
    private boolean isInLag = false; // Indicates if the character is in attack recovery lag.
    private boolean isShieldAvailable; // Tracks if the shield can be activated.
    private boolean isDisabled = false; // Indicates if the character is disabled (e.g., defeated).
    private boolean isCharging = false; // Tracks if the character is charging a projectile.
    private boolean isShieldActive = false; // Indicates if the shield is currently active.
    private boolean projectileKeyPressed = false; // Tracks if the projectile key was recently pressed.

    private double shieldLevel = 1.0; // Current shield strength (from 0.0 to 1.0).
    private double movementMultiplier = 1.0; // Adjusts movement speed (default is normal speed).
    private double startX; // Starting X-coordinate for projectile launches.
    private double defensePower; // Defense attribute for reducing incoming damage.


    private double startY; // Starting Y-coordinate for projectile launches
    private double targetX; // Target X-coordinate for projectiles or abilities.
    private double targetY; // Target Y-coordinate for projectiles or abilities.
    private double defenseMultiplier = 1.0; // Multiplier for adjusting defense power.
    
    private double movementSpeed = 3.0; // Default movement speed.

    private long shieldLastUsedTime; // Tracks the last time the shield was used.
    private long lastSpecialUsedTime = 0;
    private long chargingStartTime = -1; // Tracks when projectile charging began.
    private long specialAbilityCooldown; // Cooldown duration for special abilities in milliseconds.

    private static final long SHIELD_COOLDOWN = 5000; // Cooldown for reactivating the shield.
    private static long ATTACK_DELAY_MS = 500; // Wind-up time for attacks in milliseconds.
    private static long END_LAG_MS = 1350; // Recovery lag after attacks in milliseconds.
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
 

    public Character(String name, int maxHealth, int attackPower, int defensePower, String specialAbilityName, Color color, int x, int y)
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

    }
    
    public int getAttackPower() 
    {
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

    public double getMovementSpeed()
    {
        // Returns the character's movement speed.
        // This determines how fast the character can move during gameplay.
        return movementSpeed;
    }

    public void setMovementSpeed(double movementSpeed) 
    {
        // Sets the character's movement speed to a new value.
        // This can be modified by abilities or passive effects.
        this.movementSpeed = movementSpeed;
    }

    public void setAttackPower(int attackPower) 
    {
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

    public double getDefensePower() 
    {
        // Returns the character's defense power.
        // This value is used to calculate how much incoming damage is reduced.
        return defensePower;
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

    
    public String getName() 
    {
        // Returns the name of the character.
        // Used for display, logging, or identification purposes.
        return name;
    }

    public double getHealthPercentage() 
    {
        // Returns the character's current health as a percentage of the maximum health.
        // Useful for updating the visual representation of the health bar.
        return (double) health / maxHealth;
    }
    
    public boolean isShieldActive() 
    {
        // Returns whether the shield is currently active.
        return isShieldActive;
    } 

    public boolean isProjectileKeyPressed() 
    {
        // Returns whether the projectile key is currently pressed.
        return projectileKeyPressed;
    }

    public Color getColor() 
    {
        // Returns the primary color of the character's sprite.
        return color;
    }

    
 // Method to move the character up while respecting the arena's top boundary
    public void moveUp(double boundaryTop) 
    {
        // Calculate the new Y-coordinate by subtracting movement speed (adjusted by the movement multiplier) from the current Y-coordinate.
        double newY = this.characterSprite.getLayoutY() - movementSpeed * movementMultiplier;

        // Check if the new Y-coordinate is within the bounds of the arena (not above the top boundary).
        if (newY >= boundaryTop) 
        {
            // Update the character's Y-coordinate to move them upward.
            this.characterSprite.setLayoutY(newY);
        }
    }

    // Method to move the character down while respecting the arena's bottom boundary
    public void moveDown(double boundaryBottom)
    {
        // Calculate the new Y-coordinate by adding movement speed (adjusted by the movement multiplier) to the current Y-coordinate.
        double newY = this.characterSprite.getLayoutY() + movementSpeed * movementMultiplier;

        // Check if the new Y-coordinate is within the bounds of the arena (not below the bottom boundary).
        if (newY <= boundaryBottom) 
        {
            // Update the character's Y-coordinate to move them downward.
            this.characterSprite.setLayoutY(newY);
        }
    }

    // Method to move the character left while respecting the arena's left boundary
    public void moveLeft(double boundaryLeft) 
    {
        // Calculate the new X-coordinate by subtracting movement speed (adjusted by the movement multiplier) from the current X-coordinate.
        double newX = this.characterSprite.getLayoutX() - movementSpeed * movementMultiplier;

        // Check if the new X-coordinate is within the bounds of the arena (not left of the left boundary).
        if (newX >= boundaryLeft)
        {
            // Update the character's X-coordinate to move them leftward.
            this.characterSprite.setLayoutX(newX);
        }
    }

    // Method to move the character right while respecting the arena's right boundary
    public void moveRight(double boundaryRight)
    {
        // Calculate the new X-coordinate by adding movement speed (adjusted by the movement multiplier) to the current X-coordinate.
        double newX = this.characterSprite.getLayoutX() + movementSpeed * movementMultiplier;

        // Check if the new X-coordinate is within the bounds of the arena (not right of the right boundary).
        if (newX <= boundaryRight) 
        {
            // Update the character's X-coordinate to move them rightward.
            this.characterSprite.setLayoutX(newX);
        }
    }
       
    /**
     * Temporarily boosts the character's attack speed by reducing the attack delay 
     * and end lag for a specified duration.
     *
     * @param speedMultiplier The factor by which the attack speed is increased. 
     *                        Higher values result in faster attacks.
     * @param duration        The duration (in milliseconds) for which the boost lasts.
     */
    public void boostAttackSpeed(double speedMultiplier, int duration) 
    {
        // Store the original attack delay and end lag values to restore later
        long originalAttackDelay = ATTACK_DELAY_MS; // Current time delay before attacks start
        long originalEndLag = END_LAG_MS; // Current time delay after an attack (recovery period)

        // Modify the attack delay and end lag by dividing them by the speed multiplier
        ATTACK_DELAY_MS = (long) (ATTACK_DELAY_MS / speedMultiplier); // Reduce attack wind-up time
        END_LAG_MS = (long) (END_LAG_MS / speedMultiplier); // Reduce recovery time after attacking

      
        // Create a new thread to handle reverting the speed boost after the duration ends
        new Thread(() -> 
        {
            try
            {
                // Pause the thread for the specified duration (how long the boost lasts)
                Thread.sleep(duration);
            } 
            catch (InterruptedException e)
            {
                e.printStackTrace(); // Log any thread interruption errors
            }
            // Revert the attack delay and end lag back to their original values
            ATTACK_DELAY_MS = originalAttackDelay; // Restore original attack delay
            END_LAG_MS = originalEndLag; // Restore original end lag
            System.out.println(name + "'s attack speed boost has ended."); // Confirm that the boost ended
        }).start(); // Start the thread to manage the timing of the boost
    }
    

 // Begins the charging process for a projectile.
    public void startCharging()
    {
        if (!isCharging) 
        { // Check if the character is not already charging
            chargingStartTime = System.currentTimeMillis(); // Record the time charging began
            isCharging = true; // Mark charging as active
        }
    }


 // Launches a charged projectile toward the opponent after charging.
    public Projectile launchChargedProjectile(Character opponent)
    {
        // Ensure the character is charging before launching
        if (!isCharging)
        {
            System.out.println(name + " is not charging and cannot launch a projectile.");
            return null; // Exit if the character is not charging
        }

        // Ensure the opponent is valid
        if (opponent == null) 
        {
            System.err.println(name + " cannot launch a projectile because the opponent is null.");
            return null; // Exit if no valid opponent is specified
        }

        // Ensure charging has a valid start time
        if (chargingStartTime == -1) 
        {
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
        if (projectile.getOwner() == null) 
        {
            System.err.println("Error: Projectile created without a valid owner: " + name);
        }

        // Log details about the launched projectile
        return projectile; // Return the created projectile
        
    }

 // Updates the shield state, including regeneration and depletion.
    public void updateShieldState(long currentTime) 
    {
        // Ensure the shield bar is initialized before performing operations.
        if (shieldBar == null) 
        {
            System.err.println("Error: shieldBar is not initialized for " + name);
            return; // Exit the method if the shield bar is not initialized.
        }

        // Handle shield depletion if it is active.
        if (isShieldActive) 
        {
            shieldLevel -= SHIELD_DEPLETION_RATE; // Deplete the shield level at a constant rate.
            if (shieldLevel <= 0) 
            { // If the shield is depleted, deactivate it.
                deactivateShield(); // Turn off the shield.
                shieldLevel = 0; // Ensure the shield level doesn't go negative.
            }
        } 
        // Regenerate the shield if it is not active and not full.
        else if (shieldLevel < 1.0)
        {
            double regenerationRate = SHIELD_REGEN_RATE; // Default regeneration rate.
            
            shieldLevel = Math.min(1.0, shieldLevel + regenerationRate); // Regenerate shield but cap at 100%.
        }

        // Check if the shield is available for activation after cooldown.
        if (!isShieldAvailable && (currentTime - shieldLastUsedTime >= SHIELD_COOLDOWN))
        {
            isShieldAvailable = true; // Mark the shield as available.
        }

        // Update the shield bar's progress and color based on availability.
        shieldBar.setProgress(shieldLevel); // Update visual representation of the shield.
        shieldBar.setStyle(isShieldAvailable ? "-fx-accent: green;" : "-fx-accent: red;"); // Green when available, red otherwise.
    }
    
    // Activates the shield, adding a glowing effect around the character sprite.
    public void activateShield() 
    {
        if (characterSprite != null) 
        { // Ensure the character sprite exists.
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
    public void deactivateShield() 
    {
        if (characterSprite != null) 
        { // Ensure the character sprite exists.
            characterSprite.setEffect(null); // Remove any applied effect.
            if (shieldPulseAnimation != null) 
            { // Stop the pulsing effect if it exists.
                shieldPulseAnimation.stop();
                shieldPulseAnimation = null; // Clear the reference to the animation.
            }
        }
        isShieldActive = false; // Mark the shield as inactive.
    }
    
    // Handles character movement based on player input.
    public void handleInput(String input, double arenaWidth, double arenaHeight) 
    {
        switch (input) 
        {
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
    public void performAttack(Character opponent) 
    {
        if (isDisabled) 
        { // Prevent attacks if the character is stunned or disabled.
            System.out.println(name + " is stunned and cannot attack!");
            return;
        }

        if (isAttacking || isInLag) 
        { // Prevent attacks if already attacking or recovering.
            return;
        }

        if (this.owner == null)
        { // Ensure the character is the owner of the attack.
            this.owner = this;
        }

        System.out.println(this.name + " is winding up for an attack...");
        isAttacking = true; // Mark the character as attacking.

        applyAttackWindUpEffect(); // Apply a visual effect for the wind-up.

        // Execute the attack in a new thread
        new Thread(() -> 
        {
            try
            {
                Thread.sleep(ATTACK_DELAY_MS); // Simulate the wind-up delay.


                int damage = calculateDamage(); // Calculate the damage to be dealt.
                opponent.takeDamage(damage, null); // Apply damage to the opponent.
                System.out.println(this.name + " attacked " + opponent.name + " for " + damage + " damage!");

                Thread.sleep(END_LAG_MS); // Simulate recovery time after the attack.
            } 
            catch (InterruptedException e) 
            {
                e.printStackTrace();
            } 
            finally 
            {
                isAttacking = false; // Mark the character as ready for another attack.
                System.out.println(this.name + " is ready to attack again!");
            }
        }).start();
    }

    /**
     * Calculates the damage dealt during an attack.
     *
     * @return The amount of damage to be dealt.
     */
    private int calculateDamage() 
    {
        if (this.owner == null) 
        { // Ensure the character has an owner (e.g., itself).
            System.err.println("Error: Owner is null during damage calculation.");
            return 0; // Prevent damage calculation if owner is null.
        }

        Character target = this.owner.getOpponent(); // Get the opponent of the owner.
        if (target == null) 
        { // Ensure the opponent exists.
            System.err.println("Error: Opponent is null during damage calculation.");
            return 0; // Prevent damage calculation if opponent is null.
        }

        int baseDamage = this.attackPower; // Base attack power of the character.
        double randomFactor = Math.random(); // Generate a random value for critical hits or misses.

        if (randomFactor > 0.7) 
        { // 30% chance for a critical hit.
            System.out.println(owner.getName() + " landed a critical hit!");
            return (int) (baseDamage * 1.15); // Critical hits 115% of the damage.
        }

        if (randomFactor < 0.1) 
        { // 10% chance for a missed attack.
            System.out.println(owner.getName() + " missed the attack!");
            return 0; // Missed attacks deal no damage.
        }

     // Calculate the reduction in damage based on the target's defense and defense multiplier
        double defenseReduction = target.getDefensePower() * target.getDefenseMultiplier(); 

        // Ensure that the final damage is at least 18, even if the defense is higher than the base damage
        int finalDamage = Math.max(18, baseDamage - (int) defenseReduction); 

        // Output the damage dealt by the attacking character, including the target's name
        System.out.println(owner.getName() + " dealt " + finalDamage + " damage to " + target.getName());

        // Return the calculated final damage, which is used to update the target's health
        return finalDamage; 

    }

    /**
     * Applies a red glow effect to indicate the attack wind-up and removes it after the delay.
     */
    private void applyAttackWindUpEffect() 
    {
        if (characterSprite != null) 
        { // Check if the character sprite exists to apply the effect.
            DropShadow attackEffect = new DropShadow(); // Create a red glow effect.
            attackEffect.setColor(Color.RED); // Set the glow color to red.
            attackEffect.setRadius(30); // Adjust the glow radius for visibility.

            Platform.runLater(() -> characterSprite.setEffect(attackEffect)); // Apply the effect on the UI thread.

            new Thread(() -> 
            {
                try 
                {
                    Thread.sleep(ATTACK_DELAY_MS); // Match the wind-up delay.
                } 
                catch (InterruptedException e) 
                {
                    e.printStackTrace(); // Handle interruption.
                }
                Platform.runLater(() -> characterSprite.setEffect(null)); // Clear the effect on the UI thread.
            }).start();
        }
    }


    /**
     * Handles damage taken by the character, considering shield logic.
     *
     * @param damage The amount of damage to apply.
     * @param projectile The projectile causing the damage (optional).
     */
    public void takeDamage(double damage, Projectile projectile) 
    {
        if (isShieldActive) 
        { // If the shield is active, reduce shield strength.
            double shieldReductionFactor = 1.0; // 
            double shieldDamage = damage * shieldReductionFactor;

            shieldLevel -= shieldDamage / 100.0; // Reduce shield level.

            if (shieldLevel <= 0)
            { // If the shield breaks, deactivate it.
                shieldLevel = 0;
                deactivateShield();
                System.out.println(name + "'s shield is broken!");
            } 
            else 
            {
                System.out.println(name + "'s shield absorbed " + shieldDamage + " damage. Remaining shield: " + (shieldLevel * 100) + "");
            }
            return; // Skip health damage if the shield is active.
        }

        health = Math.max(0, health - damage); // Reduce health if the shield is inactive.
        System.out.println(name + " took " + damage + " damage. Remaining health: " + health);

        if (health <= 0)
        {
            System.out.println(name + " has been defeated!"); // Log defeat if health reaches zero.
        }
    }

    /**
     * Executes the character's special ability.
     *
     * @param opponent The opponent affected by the special ability.
     */
    public void performSpecialAbility(Character opponent) 
    {
        if (canUseSpecialAbility())
        { // Check if the special ability is ready.
            System.out.println(name + " is using their special ability: " + specialAbilityName);
            AbilityManager.executeAbility(this, opponent); // Call AbilityManager to execute the ability.
            lastSpecialUsedTime = System.currentTimeMillis(); // Update the last used time.
        } 
        else 
        {
            long timeRemaining = (specialAbilityCooldown - (System.currentTimeMillis() - lastSpecialUsedTime)) / 1000;
            System.out.println(name + "'s special ability is on cooldown! Cooldown time remaining: " + timeRemaining + " seconds.");
        }
    }

    /**
     * Reduces the character's health by a specified amount.
     *
     * @param actualDamage The damage to apply to the character's health.
     */
    public void reduceHealth(double actualDamage) 
    {
        if (actualDamage < 0) 
        { // Ensure the damage is not negative.
            System.out.println("Invalid damage amount. Damage cannot be negative."); // Log error.
            return;
        }

        this.health -= actualDamage; // Deduct damage from health.

        if (this.health < 0)  // Prevent health from dropping below zero.
        {
            this.health = 0;
        }

        System.out.println(name + " took " + actualDamage + " damage. Remaining health: " + this.health); // Log health status.

        if (this.health == 0) 
        { // Check if the character is defeated.
            System.out.println(name + " has been defeated!"); // Log defeat.
            this.isDisabled = true; // Disable character actions.
        }
    }

    /**
     * Checks if the character's special ability can be used.
     *
     * @return True if the special ability can be used, false otherwise.
     */
    public boolean canUseSpecialAbility() 
    {
        long currentTime = System.currentTimeMillis(); // Get current time.
        return currentTime - lastSpecialUsedTime >= specialAbilityCooldown; // Check cooldown.
    }
   
    /**
     * Sets the stunned state for the character.
     *
     * @param stunned Whether the character is stunned.
     */
    public void setStunned(boolean stunned) 
    {
        if (stunned)
        {
            System.out.println(name + " is now stunned."); // Log the state change.
            if (characterSprite != null)
            {
                characterSprite.setOpacity(0.5); // Dim the sprite to indicate the stunned state.
            }
        } 
        else 
        {
            if (characterSprite != null) 
            {
                characterSprite.setOpacity(1.0); // Restore the sprite's opacity.
            }
        }
    }

    /**
     * Updates the character's health.
     *
     * @param health The new health value.
     */
    public void setHealth(int health) 
    {
        if (health < 0) 
        {
            this.health = 0; // Prevent health from going below 0.
        } 
        else if (health > maxHealth) 
        {
            this.health = maxHealth; // Prevent health from exceeding the maximum.
        } 
        else 
        {
            this.health = health; // Set the health to the specified value.
        }
        System.out.println(name + "'s health is now: " + this.health); // Log the new health value.

        if (healthBar != null) 
        { // Update the health bar if it exists.
            healthBar.setProgress((double) this.health / maxHealth); // Adjust the health bar to match the current health.
        }
    }

    /**
     * Retrieves the character's current defense multiplier.
     *
     * @return The defense multiplier.
     */
    public double getDefenseMultiplier() 
    {
        return defenseMultiplier; // Return the defense multiplier.
    }
  
    /**
     * Sets the cooldown for the character's special ability.
     *
     * @param cooldown The cooldown duration in milliseconds.
     */
    public void setSpecialAbilityCooldown(int cooldown) 
    {
        // Set the cooldown duration for the special ability
        this.specialAbilityCooldown = cooldown;

        // Record the current time as the last time the ability was used
        this.lastSpecialUsedTime = System.currentTimeMillis();
    }

    public void setAbilityReadyVisual()
    {
        if (characterSprite != null)
        { // Check if the sprite exists
            characterSprite.setOpacity(1.0); // Restore the sprite's opacity
        }
    }

}