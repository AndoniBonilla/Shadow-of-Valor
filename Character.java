package FightingGame;
// Defines the `FightingGame` package. Groups related game files, such as `Character` and `GameSession`.

import javafx.animation.KeyFrame;
// Represents a single frame in an animation timeline.
import javafx.animation.PauseTransition;
// One-shot FX-thread timer; used for G1 (attack-speed reset) and G2 (stun-opacity restore).
import javafx.animation.Timeline;
// Used for creating animations that cycle through multiple `KeyFrame` objects.
import javafx.scene.Group;
// Used to group multiple visual elements (e.g., body parts of the character) into one sprite.
import javafx.scene.control.ProgressBar;
// Visual representation of health or shield levels above the character.
import javafx.scene.effect.DropShadow;
// Simulates a light source on the character, making them appear illuminated.
import javafx.scene.layout.Pane;
// Container used in addBarsToPane (U3) to attach bars without referencing GameSession directly.
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

    // G7: DropShadow and Timeline created once and reused; no stacking on key-repeat.
    private DropShadow shieldEffect; // Glow effect applied while shield is active.
    private Timeline shieldPulseAnimation; // Animates the shield glow radius when active.

    private Character opponent; // Reference to the character's current opponent.
    private Character owner; // Tracks the entity controlling the character or launching a projectile.

    private ProgressBar shieldBar; // Visual representation of the shield's current strength.
    private Group characterSprite; // The character's visual representation, composed of shapes.
    private ProgressBar healthBar; // Displays the character's current health level.

    private double health; // Current health of the character.
    private int maxHealth; // Maximum possible health of the character.
    private int attackPower; // Damage dealt during an attack.

    private boolean isAttacking = false; // Indicates if the character is currently in wind-up.
    private boolean isInLag = false; // Indicates if the character is in attack recovery lag.
    private boolean isShieldAvailable = true; // G6: initialized true so shield works from the start.
    private boolean isCharging = false; // Tracks if the character is charging a projectile.
    private boolean isShieldActive = false; // Indicates if the shield is currently active.
    private boolean projectileKeyPressed = false; // Tracks if the projectile key was recently pressed.

    private double shieldLevel = 1.0; // Current shield strength (0.0 to 1.0).
    private double defensePower; // Defense attribute for reducing incoming damage.
    private double movementSpeed = 180; // U4: pixels per second (replaces the old 3.0 per-frame value).

    private long shieldLastUsedTime; // Tracks the last time the shield was broken.
    private long lastSpecialUsedTime = 0; // Timestamp of the last special ability use.
    private long chargingStartTime = -1; // Tracks when projectile charging began (-1 = not charging).
    private long specialAbilityCooldown; // Cooldown duration for special abilities in milliseconds.
    private long stunnedUntil = 0; // G2: timestamp until which the character is stunned (0 = not stunned).

    // T2: attack timing driven by wall-clock timestamps; no Thread.sleep anywhere.
    private long attackWindUpEndMs = 0;        // Epoch-ms when wind-up completes.
    private long attackEndLagEndMs = 0;        // Epoch-ms when end-lag completes.
    private Character pendingAttackTarget = null; // Target stored while wind-up is in progress.

    // G1: per-instance multiplier replaces the shared mutable static fields.
    private double attackSpeedMultiplier = 1.0; // Multiplier applied to attack wind-up and end-lag.

    // G1: base timing constants; never mutated.
    private static final long   BASE_ATTACK_DELAY_MS   = 500;    // Base wind-up time in milliseconds.
    private static final long   BASE_END_LAG_MS        = 1350;   // Base recovery lag in milliseconds.
    private static final long   SHIELD_COOLDOWN        = 5000;   // Cooldown after shield breaks (ms).
    private static final long   MAX_CHARGE_MS          = 2000;   // G5: maximum charge duration (ms).
    private static final double SHIELD_REGEN_RATE      = 0.0035; // Shield regen per frame.
    private static final double SHIELD_DEPLETION_RATE  = 0.0058; // Shield depletion per frame while active.

    public Character(String name, int maxHealth, int attackPower, int defensePower,
                     String specialAbilityName, Color color, int x, int y)
    {
        // Assigns the character's name.
        this.name = name;

        // Sets the maximum and current health to the same initial value.
        this.maxHealth = maxHealth;
        this.health = maxHealth;

        // Assigns the attack power and defense attributes.
        this.attackPower = attackPower;
        this.defensePower = defensePower;

        // Sets the name of the special ability for this character.
        this.specialAbilityName = specialAbilityName;

        // Sets the character's color (used for the sprite and effects).
        this.color = color;

        // Creates a shield bar above the character.
        this.shieldBar = new ProgressBar(1.0); // Initializes the shield bar at full strength.
        this.shieldBar.setPrefWidth(100); // Sets the shield bar's visual width.
        this.shieldBar.setStyle("-fx-accent: blue;"); // Colors the shield bar blue.
        this.shieldBar.setLayoutX(x); // Positions the shield bar horizontally.
        this.shieldBar.setLayoutY(y - 30); // Positions the shield bar above the character.
        // U3: bars are NOT added to any root pane here; GameSession calls addBarsToPane() instead.

        // Creates the visual representation (sprite) of the character.
        Circle head     = new Circle(15, color); // Character head circle.
        Line body       = new Line(0, -20, 0, 20); // Vertical body line.
        Line leftArm    = new Line(-20, 0, 0, 0); // Left arm line.
        Line rightArm   = new Line(0, 0, 20, 0); // Right arm line.
        Line leftLeg    = new Line(-10, 20, 0, 30); // Left leg diagonal line.
        Line rightLeg   = new Line(10, 20, 0, 30); // Right leg diagonal line.

        body.setStroke(Color.BLACK);
        leftArm.setStroke(Color.BLACK);
        rightArm.setStroke(Color.BLACK);
        leftLeg.setStroke(Color.BLACK);
        rightLeg.setStroke(Color.BLACK);

        this.characterSprite = new Group(head, body, leftArm, rightArm, leftLeg, rightLeg);
        this.characterSprite.setLayoutX(x);
        this.characterSprite.setLayoutY(y);

        this.healthBar = new ProgressBar(1.0); // Initializes the health bar to full.
        this.healthBar.setPrefWidth(100);
        this.healthBar.setStyle("-fx-accent: green;");
        this.healthBar.setLayoutX(x);
        this.healthBar.setLayoutY(y - 20); // Places the health bar slightly above the character.
        // U3: bars are NOT added to any root pane here; GameSession calls addBarsToPane() instead.
    }

    /**
     * Adds this character's health and shield bars to the given pane. (U3)
     * GameSession calls this after repositioning the sprite so bars track the fighter's in-game position.
     *
     * @param root The pane that will display the bars.
     */
    public void addBarsToPane(Pane root)
    {
        double x = characterSprite.getLayoutX(); // Use the in-game position, not the CharacterRoster position.
        double y = characterSprite.getLayoutY();
        shieldBar.setLayoutX(x); // Reposition shield bar to match sprite.
        shieldBar.setLayoutY(y - 30);
        healthBar.setLayoutX(x); // Reposition health bar to match sprite.
        healthBar.setLayoutY(y - 20);
        root.getChildren().add(shieldBar); // Attach to the active game pane.
        root.getChildren().add(healthBar);
    }

    public Group getCharacterSprite()
    {
        // Returns the graphical representation (sprite) of the character.
        return characterSprite;
    }

    public void setProjectileKeyPressed(boolean pressed)
    {
        // Sets whether the projectile key is currently being pressed.
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
        return attackPower;
    }

    public double getMaxHealth()
    {
        // Returns the maximum health of the character.
        return this.maxHealth;
    }

    public Character getOpponent()
    {
        // Returns the opponent assigned to this character.
        return opponent;
    }

    public double getMovementSpeed()
    {
        // Returns the character's movement speed (pixels per second after U4).
        return movementSpeed;
    }

    public void setMovementSpeed(double movementSpeed)
    {
        // Sets the character's movement speed to a new value.
        this.movementSpeed = movementSpeed;
    }

    public void setAttackPower(int attackPower)
    {
        // Updates the character's attack power to a new value.
        this.attackPower = attackPower;
    }

    public String getSpecialAbilityName()
    {
        // Returns the name of the character's special ability.
        return this.specialAbilityName;
    }

    public double getDefensePower()
    {
        // Returns the character's defense power.
        return defensePower;
    }

    public double getShieldLevel()
    {
        // Returns the current shield level (0.0 to 1.0).
        return shieldLevel;
    }

    public double getHealth()
    {
        // Returns the character's current health value.
        return health;
    }

    public String getName()
    {
        // Returns the name of the character.
        return name;
    }

    public double getHealthPercentage()
    {
        // Returns the character's current health as a percentage of the maximum health.
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

    /**
     * Returns true if the character is currently stunned. (G2)
     * Uses a timestamp so stun duration is accurate regardless of frame rate.
     */
    public boolean isStunned()
    {
        return System.currentTimeMillis() < stunnedUntil;
    }

    /**
     * Applies a stun lasting durationMs milliseconds. (G2)
     * A new stun extends stunnedUntil to the later end time; it never shortens.
     * Uses PauseTransition to restore opacity on the FX thread when the stun expires.
     */
    public void applyStun(long durationMs)
    {
        long newEnd = System.currentTimeMillis() + durationMs;
        if (newEnd > stunnedUntil) // Only extend, never shorten an existing stun.
        {
            stunnedUntil = newEnd;
        }
        System.out.println(name + " is stunned for " + durationMs + "ms.");

        if (characterSprite != null)
        {
            characterSprite.setOpacity(0.5); // Dim sprite to indicate stunned state.

            // Restore opacity on FX thread once the stun actually expires.
            long remaining = Math.max(0, stunnedUntil - System.currentTimeMillis());
            PauseTransition restoreOpacity = new PauseTransition(Duration.millis(remaining));
            restoreOpacity.setOnFinished(e ->
            {
                if (!isStunned() && characterSprite != null)
                {
                    characterSprite.setOpacity(1.0); // Only restore after stun has ended.
                }
            });
            restoreOpacity.play();
        }
    }

    /**
     * Moves the sprite by a normalized direction vector scaled by speed * dt. (U4)
     * Positions are clamped to GameSession boundaries instead of rejected, so diagonal
     * movement against a wall slides smoothly along it.
     *
     * @param ndx Normalized X component of the movement direction.
     * @param ndy Normalized Y component of the movement direction.
     * @param dt  Seconds elapsed since the last frame.
     */
    public void moveBy(double ndx, double ndy, double dt)
    {
        if (isStunned()) return; // G2: movement is blocked while stunned.
        double newX = characterSprite.getLayoutX() + ndx * movementSpeed * dt;
        double newY = characterSprite.getLayoutY() + ndy * movementSpeed * dt;
        // Clamp to boundaries (U4: clamp instead of reject, so diagonal never stops early).
        newX = Math.max(GameSession.BOUNDARY_LEFT,   Math.min(GameSession.BOUNDARY_RIGHT,  newX));
        newY = Math.max(GameSession.BOUNDARY_TOP,    Math.min(GameSession.BOUNDARY_BOTTOM, newY));
        characterSprite.setLayoutX(newX);
        characterSprite.setLayoutY(newY);
    }

    /**
     * Per-frame update: resolves attack wind-up and end-lag via wall-clock timestamps. (T2)
     * Called by GameSession every frame on the FX thread; no Thread.sleep is used.
     *
     * @param nowMillis System.currentTimeMillis() sampled at the start of the frame.
     * @param dt        Seconds elapsed since the last frame (capped to 0.05 by the caller).
     */
    public void update(long nowMillis, double dt)
    {
        // Wind-up complete: deliver the pending attack to the stored target.
        if (isAttacking && nowMillis >= attackWindUpEndMs)
        {
            if (pendingAttackTarget != null)
            {
                int damage = calculateDamage(); // Compute damage on the FX thread.
                pendingAttackTarget.takeDamage(damage, null); // Apply to the opponent.
                System.out.println(name + " attacked " + pendingAttackTarget.getName()
                    + " for " + damage + " damage!");
            }
            pendingAttackTarget = null; // Clear pending target.
            isAttacking = false;
            isInLag = true; // Enter recovery lag.
            attackEndLagEndMs = nowMillis + (long)(BASE_END_LAG_MS / attackSpeedMultiplier);
        }

        // End-lag complete: ready for the next attack.
        if (isInLag && nowMillis >= attackEndLagEndMs)
        {
            isInLag = false;
            System.out.println(name + " is ready to attack again!");
        }
    }

    /**
     * Boosts attack speed by setting the per-instance multiplier for the given duration. (G1)
     * Uses PauseTransition so the reset runs on the FX thread with no Thread.sleep.
     * Only this character's timing is affected; opponents are unaffected.
     *
     * @param speedMultiplier Factor by which attack speed is increased.
     * @param duration        Duration of the boost in milliseconds.
     */
    public void boostAttackSpeed(double speedMultiplier, int duration)
    {
        attackSpeedMultiplier = speedMultiplier; // Apply the speed boost to this character.
        System.out.println(name + " attack speed boosted by " + speedMultiplier + "x!");

        // Reset multiplier to default after the boost expires (FX-thread safe).
        PauseTransition reset = new PauseTransition(Duration.millis(duration));
        reset.setOnFinished(e ->
        {
            attackSpeedMultiplier = 1.0; // Restore default attack timing.
            System.out.println(name + "'s attack speed boost has ended.");
        });
        reset.play();
    }

    // Begins the charging process for a projectile.
    public void startCharging()
    {
        if (isStunned()) // G2: blocked while stunned.
        {
            System.out.println(name + " is stunned and cannot charge a projectile!");
            return;
        }
        if (!isCharging)
        {
            chargingStartTime = System.currentTimeMillis(); // Record when charging began.
            isCharging = true;
        }
    }

    /**
     * Launches a charged projectile toward the opponent. (G5)
     * Size and speed are computed from the actual charge time, clamped to MAX_CHARGE_MS.
     */
    public Projectile launchChargedProjectile(Character opponent)
    {
        // Ensure the character is charging before launching.
        if (!isCharging)
        {
            System.out.println(name + " is not charging and cannot launch a projectile.");
            return null;
        }

        // Ensure the opponent is valid.
        if (opponent == null)
        {
            System.err.println(name + " cannot launch a projectile because the opponent is null.");
            return null;
        }

        // Ensure charging has a valid start time.
        if (chargingStartTime == -1)
        {
            System.err.println(name + " attempted to launch a projectile without a valid charging state.");
            return null;
        }

        long chargeDuration = System.currentTimeMillis() - chargingStartTime;
        chargingStartTime = -1; // Reset charging state.
        isCharging = false;

        // Clamp charge duration to the maximum. (G5)
        long clamped = Math.min(chargeDuration, MAX_CHARGE_MS);
        double ratio = (double) clamped / MAX_CHARGE_MS;

        // Size: 10 px (no charge) to 30 px (full charge). (G5)
        double size = 10.0 + ratio * 20.0;

        // Speed: 400 px/s (no charge) to 200 px/s (full charge). (G5)
        double speed = 400.0 - ratio * 200.0;

        Projectile projectile = new Projectile(this,
            getCharacterSprite().getLayoutX(), // Start X-coordinate.
            getCharacterSprite().getLayoutY(), // Start Y-coordinate.
            opponent.getCharacterSprite().getLayoutX(), // Target X-coordinate.
            opponent.getCharacterSprite().getLayoutY(), // Target Y-coordinate.
            size, speed);

        System.out.println(name + " launched a projectile (size=" + String.format("%.1f", size)
            + ", speed=" + String.format("%.0f", speed) + " px/s).");
        return projectile;
    }

    // Updates the shield state, including regeneration, depletion, and cooldown.
    public void updateShieldState(long currentTime)
    {
        // Ensure the shield bar is initialized before performing operations.
        if (shieldBar == null)
        {
            System.err.println("Error: shieldBar is not initialized for " + name);
            return;
        }

        // Handle shield depletion if it is active.
        if (isShieldActive)
        {
            shieldLevel -= SHIELD_DEPLETION_RATE; // Deplete shield at constant rate.
            if (shieldLevel <= 0)
            {
                breakShield(); // G6: unified break method handles cooldown and visual.
            }
        }
        // Regenerate the shield if it is not active and not full.
        else if (shieldLevel < 1.0)
        {
            shieldLevel = Math.min(1.0, shieldLevel + SHIELD_REGEN_RATE); // Cap at 100%.
        }

        // Check if the shield cooldown has expired and re-enable the shield.
        if (!isShieldAvailable && (currentTime - shieldLastUsedTime >= SHIELD_COOLDOWN))
        {
            isShieldAvailable = true; // Cooldown expired; shield can be activated again.
        }

        // Update the shield bar's progress and color based on availability.
        shieldBar.setProgress(shieldLevel);
        shieldBar.setStyle(isShieldAvailable ? "-fx-accent: green;" : "-fx-accent: red;"); // Red on cooldown.
    }

    /**
     * Breaks the shield: sets level to 0, starts the 5-second cooldown,
     * and removes the visual glow effect. (G6)
     * Call only from the FX thread (updateShieldState, releaseShieldKey, takeDamage).
     */
    public void breakShield()
    {
        shieldLevel = 0; // Deplete shield completely.
        isShieldAvailable = false; // Block reactivation until cooldown expires.
        shieldLastUsedTime = System.currentTimeMillis(); // Start the cooldown timer.
        System.out.println(name + "'s shield is broken!");
        deactivateShield(); // Remove visual effect (must run on FX thread).
    }

    /**
     * Activates the shield if all preconditions are met. (G6, G7)
     * Returns early if the shield is already active, on cooldown, depleted, or the
     * character is stunned. The DropShadow and Timeline are created once and reused.
     */
    public void activateShield()
    {
        if (isShieldActive || !isShieldAvailable || shieldLevel <= 0 || isStunned())
        {
            return; // G6: guard against invalid activation (also fixes G7 Timeline stacking).
        }

        if (characterSprite != null)
        {
            if (shieldEffect == null) // G7: create DropShadow only once.
            {
                shieldEffect = new DropShadow();
                shieldEffect.setColor(this.name.equals("Aqua") ? Color.DARKBLUE : Color.BLUE);
                shieldEffect.setRadius(20);
            }
            characterSprite.setEffect(shieldEffect); // Apply glow.

            if (shieldPulseAnimation == null) // G7: create Timeline only once.
            {
                shieldPulseAnimation = new Timeline(
                    new KeyFrame(Duration.millis(500), e -> shieldEffect.setRadius(20)), // Pulse down.
                    new KeyFrame(Duration.millis(1000), e -> shieldEffect.setRadius(30))  // Pulse up.
                );
                shieldPulseAnimation.setCycleCount(Timeline.INDEFINITE);
            }
            shieldPulseAnimation.play(); // Restart the existing animation.
        }
        isShieldActive = true;
    }

    // Deactivates the shield, removing the glowing effect.
    public void deactivateShield()
    {
        if (characterSprite != null)
        {
            characterSprite.setEffect(null); // Remove glow.
            if (shieldPulseAnimation != null)
            {
                shieldPulseAnimation.stop(); // Stop animation; keep reference for reuse. (G7)
            }
        }
        isShieldActive = false;
    }

    /**
     * Deactivates the shield when the shield key is released.
     */
    public void releaseShieldKey()
    {
        if (isShieldActive)
        {
            deactivateShield(); // Deactivate the shield and remove visual effects.
            shieldLastUsedTime = System.currentTimeMillis(); // Record for cooldown tracking.
        }
    }

    /**
     * Starts the attack wind-up. The actual damage is delivered in update() when
     * attackWindUpEndMs is reached. (T2: no Thread.sleep; timing via timestamps.)
     *
     * @param opponent The opponent character being attacked.
     */
    public void performAttack(Character opponent)
    {
        if (isStunned()) // G2: attack is blocked while stunned.
        {
            System.out.println(name + " is stunned and cannot attack!");
            return;
        }

        if (isAttacking || isInLag) // Previous attack still in wind-up or end-lag.
        {
            return;
        }

        if (this.owner == null) // Ensure owner reference is set (used by calculateDamage).
        {
            this.owner = this;
        }

        System.out.println(this.name + " is winding up for an attack...");
        isAttacking = true;
        pendingAttackTarget = opponent; // Store target; damage applied when wind-up ends.
        attackWindUpEndMs = System.currentTimeMillis()
            + (long)(BASE_ATTACK_DELAY_MS / attackSpeedMultiplier); // G1: per-instance timing.
        applyAttackWindUpEffect(); // Visual glow during wind-up.
    }

    /**
     * Applies a red glow during wind-up and clears it via PauseTransition. (T2)
     * Called from performAttack which runs on the FX thread; no Thread.sleep used.
     */
    private void applyAttackWindUpEffect()
    {
        if (characterSprite == null) return;

        DropShadow attackEffect = new DropShadow();
        attackEffect.setColor(Color.RED);
        attackEffect.setRadius(30);
        characterSprite.setEffect(attackEffect); // On FX thread; direct call is safe.

        // Clear the effect after the wind-up duration using a FX-thread-safe timer.
        long windUp = (long)(BASE_ATTACK_DELAY_MS / attackSpeedMultiplier); // G1: scaled.
        PauseTransition clear = new PauseTransition(Duration.millis(windUp));
        clear.setOnFinished(e ->
        {
            if (characterSprite != null) characterSprite.setEffect(null);
        });
        clear.play();
    }

    /**
     * Calculates the damage dealt during an attack.
     *
     * @return The amount of damage to be dealt.
     */
    private int calculateDamage()
    {
        if (this.owner == null)
        {
            System.err.println("Error: Owner is null during damage calculation.");
            return 0;
        }

        Character target = this.owner.getOpponent(); // Opponent of the attacker.
        if (target == null)
        {
            System.err.println("Error: Opponent is null during damage calculation.");
            return 0;
        }

        int baseDamage = this.attackPower;
        double randomFactor = Math.random();

        if (randomFactor > 0.7) // 30% chance for a critical hit.
        {
            System.out.println(owner.getName() + " landed a critical hit!");
            return (int)(baseDamage * 1.15); // Critical hit deals 115% damage.
        }

        if (randomFactor < 0.1) // 10% chance for a miss.
        {
            System.out.println(owner.getName() + " missed the attack!");
            return 0;
        }

        // Damage reduced by the target's defense; minimum of 18.
        double defenseReduction = target.getDefensePower();
        int finalDamage = Math.max(18, baseDamage - (int) defenseReduction);
        System.out.println(owner.getName() + " dealt " + finalDamage + " damage to " + target.getName());
        return finalDamage;
    }

    /**
     * Applies damage, considering the active shield. (T2)
     * Now always called from the FX thread (game loop or update()), so no Platform.runLater needed.
     *
     * @param damage     The amount of damage to be applied.
     * @param projectile The projectile that caused the damage (null for melee).
     */
    public void takeDamage(double damage, Projectile projectile)
    {
        // Check if the shield is active.
        if (isShieldActive)
        {
            double shieldDamage = damage;
            shieldLevel -= shieldDamage / 100.0; // Reduce shield by absorbed damage.

            if (shieldLevel <= 0)
            {
                // T2: now on FX thread; direct call is safe (no Platform.runLater needed).
                shieldLevel = 0;
                isShieldAvailable = false; // G6: start cooldown.
                shieldLastUsedTime = System.currentTimeMillis();
                System.out.println(name + "'s shield is broken!");
                deactivateShield(); // Remove glow; runs on FX thread.
            }
            else
            {
                System.out.println(name + "'s shield absorbed " + shieldDamage
                    + " damage. Remaining shield: " + (shieldLevel * 100) + "%");
            }
            return; // Shield absorbed the hit; no health damage.
        }

        // Apply damage directly to health if the shield is inactive.
        health = Math.max(0, health - damage);
        System.out.println(name + " took " + damage + " damage. Remaining health: " + health);

        if (health <= 0)
        {
            System.out.println(name + " has been defeated!");
        }
    }

    /**
     * Executes the character's special ability if off cooldown. Blocked while stunned. (G2)
     *
     * @param opponent The opponent who may be affected by the special ability.
     */
    public void performSpecialAbility(Character opponent)
    {
        if (isStunned()) // G2: special ability is blocked while stunned.
        {
            System.out.println(name + " is stunned and cannot use a special ability!");
            return;
        }

        if (canUseSpecialAbility())
        {
            System.out.println(name + " is using their special ability: " + specialAbilityName);
            AbilityManager.executeAbility(this, opponent);
            lastSpecialUsedTime = System.currentTimeMillis();
        }
        else
        {
            long timeRemaining = (specialAbilityCooldown
                - (System.currentTimeMillis() - lastSpecialUsedTime)) / 1000;
            System.out.println(name + "'s special ability is on cooldown! Cooldown time remaining: "
                + timeRemaining + " seconds.");
        }
    }

    /**
     * Checks if the character's special ability can be used.
     *
     * @return True if the special ability can be used, false otherwise.
     */
    public boolean canUseSpecialAbility()
    {
        long currentTime = System.currentTimeMillis();
        return currentTime - lastSpecialUsedTime >= specialAbilityCooldown;
    }

    /**
     * Updates the character's health (clamped to [0, maxHealth]). (T2)
     * Now always called from the FX thread (Timeline or game loop); no Platform.runLater needed.
     *
     * @param health The new health value.
     */
    public void setHealth(int health)
    {
        if (health < 0)
        {
            this.health = 0;
        }
        else if (health > maxHealth)
        {
            this.health = maxHealth;
        }
        else
        {
            this.health = health;
        }
        System.out.println(name + "'s health is now: " + this.health);

        if (healthBar != null)
        {
            double progress = (double) this.health / maxHealth;
            healthBar.setProgress(progress); // T2: on FX thread; direct call is safe.
        }
    }

    /**
     * Sets the cooldown for the character's special ability.
     *
     * @param cooldown The cooldown duration in milliseconds.
     */
    public void setSpecialAbilityCooldown(int cooldown)
    {
        this.specialAbilityCooldown = cooldown;
        this.lastSpecialUsedTime = System.currentTimeMillis(); // Start cooldown from now.
    }


}
