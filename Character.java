package FightingGame;
// Defines the `FightingGame` package.

import javafx.animation.KeyFrame;
// Represents a single frame in an animation timeline.
import javafx.animation.PauseTransition;
// One-shot FX-thread timer; used for G1 (attack-speed reset) and G2 (stun-opacity restore).
import javafx.animation.Timeline;
// Used for creating animations that cycle through multiple `KeyFrame` objects.
import javafx.scene.Group;
// Used to group multiple visual elements (e.g., body parts of the character) into one sprite.
import javafx.scene.effect.DropShadow;
// Simulates a light source on the character, making them appear illuminated.
import javafx.scene.paint.Color;
// Allows for color specification used in sprites and effects.
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

    private Group characterSprite; // The character's visual representation, composed of shapes.

    private double health; // Current health of the character.
    private int maxHealth; // Maximum possible health of the character.

    // C2: base stats are set once at construction and never mutated by buffs.
    private int baseAttackPower; // Attack power before any active multipliers are applied.
    private double baseMovementSpeed = 180; // U4: base pixels per second; buffs multiply this.

    private double defensePower; // Defense attribute for reducing incoming damage.

    private boolean isAttacking = false; // True while wind-up is in progress.
    private boolean isInLag = false; // True during end-lag recovery after an attack.
    private boolean isShieldAvailable = true; // G6: initialized true so shield works from the start.
    private boolean isCharging = false; // Tracks if the character is charging a projectile.
    private boolean isShieldActive = false; // Indicates if the shield is currently active.
    private boolean projectileKeyPressed = false; // Tracks if the projectile key was recently pressed.

    private double shieldLevel = 1.0; // Current shield strength (0.0 to 1.0).

    private long shieldLastUsedTime; // Tracks the last time the shield was broken.
    private long lastSpecialUsedTime = 0; // Timestamp of the last special ability use.
    private long chargingStartTime = -1; // Tracks when projectile charging began (-1 = not charging).
    private long specialAbilityCooldown; // Cooldown duration for special abilities in milliseconds.
    private long stunnedUntil = 0; // G2: timestamp until which the character is stunned (0 = not stunned).

    // T2: attack timing driven by wall-clock timestamps; no Thread.sleep anywhere.
    private long attackWindUpEndMs = 0;       // Epoch-ms when wind-up completes.
    private long attackEndLagEndMs = 0;       // Epoch-ms when end-lag completes.
    private Character pendingAttackTarget = null; // Target stored while wind-up is in progress.

    // G1: per-instance multiplier replaces the shared mutable static fields.
    private double attackSpeedMultiplier = 1.0; // Multiplier applied to attack wind-up and end-lag.

    // C2: active buff state — timestamped so buffs expire cleanly without mutations.
    private double activeAttackMult = 1.0;     // Multiplier applied on top of baseAttackPower.
    private long activeAttackMultExpiry = 0;   // Epoch-ms; 0 = no active attack buff.
    private double activeSpeedMult = 1.0;      // Multiplier applied on top of baseMovementSpeed.
    private long activeSpeedMultExpiry = 0;    // Epoch-ms; 0 = no active speed buff.
    private double nextAttackBonus = 0;        // Flat damage bonus consumed by the next attack.
    private long nextAttackBonusExpiry = 0;    // Epoch-ms; bonus expires if unused after 5 s.

    // C4: heal-over-time state, applied per frame via update().
    private double healPerSecond = 0;    // HP gained per second; 0 = no active heal.
    private long healUntilMs = 0;        // Epoch-ms when the heal-over-time effect stops.

    // G1: base timing constants; never mutated.
    private static final long   BASE_ATTACK_DELAY_MS   = 500;   // Base wind-up time in milliseconds.
    private static final long   BASE_END_LAG_MS        = 1350;  // Base recovery lag in milliseconds.
    private static final long   SHIELD_COOLDOWN        = 5000;  // Cooldown after shield breaks (ms).
    private static final long   MAX_CHARGE_MS          = 2000;  // G5: maximum charge duration (ms).
    private static final double SHIELD_REGEN_RATE      = 0.0035; // Shield regen per frame.
    private static final double SHIELD_DEPLETION_RATE  = 0.0058; // Shield depletion per frame while active.

    // C1: named combat constants replace magic numbers.
    private static final double MISS_CHANCE     = 0.10;  // Probability of a complete miss.
    private static final double CRIT_CHANCE     = 0.30;  // Probability of a critical hit (top of range).
    private static final double CRIT_MULTIPLIER = 1.15;  // Damage multiplier on a critical hit.
    private static final int    MIN_DAMAGE      = 18;    // Minimum damage floor.

    // G3: maximum distance (px) for melee to connect.
    private static final int ATTACK_RANGE = 70;

    public Character(String name, int maxHealth, int attackPower, int defensePower,
                     String specialAbilityName, Color color, int x, int y)
    {
        // Assigns the character's name.
        this.name = name;

        // Sets the maximum and current health to the same initial value.
        this.maxHealth = maxHealth;
        this.health = maxHealth;

        // C2: store base stats; they are never overwritten by buff methods.
        this.baseAttackPower = attackPower;
        this.defensePower = defensePower;

        // Sets the name of the special ability for this character.
        this.specialAbilityName = specialAbilityName;

        // Sets the character's color (used for the sprite and effects).
        this.color = color;

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

    }

    // --- Getters and setters ---

    public Group getCharacterSprite() { return characterSprite; }

    public void setProjectileKeyPressed(boolean pressed) { this.projectileKeyPressed = pressed; }

    public void setOpponent(Character opponent) { this.opponent = opponent; }

    /** Returns base attack power (before active buffs). Use getEffectiveAttack() for combat. */
    public int getAttackPower() { return baseAttackPower; }

    public double getMaxHealth() { return this.maxHealth; }

    public Character getOpponent() { return opponent; }

    /** Returns current effective movement speed including any active speed buff. (C2) */
    public double getMovementSpeed()
    {
        return getEffectiveSpeed(); // C2: apply active buff multiplier.
    }

    public String getSpecialAbilityName() { return this.specialAbilityName; }

    public double getDefensePower() { return defensePower; }

    public double getShieldLevel() { return shieldLevel; }

    public double getHealth() { return health; }

    public String getName() { return name; }

    public double getHealthPercentage() { return (double) health / maxHealth; }

    public boolean isShieldActive() { return isShieldActive; }

    public boolean isShieldAvailable() { return isShieldAvailable; } // U2: read by GameSession.updateBars.

    public boolean isProjectileKeyPressed() { return projectileKeyPressed; }

    public Color getColor() { return color; }

    // --- C2: Buff system ---

    /**
     * Returns the effective attack power for this frame: base * multiplier + next-attack bonus. (C2)
     * Active multiplier expires automatically after its duration.
     */
    public int getEffectiveAttack()
    {
        long now = System.currentTimeMillis();
        double mult  = (now < activeAttackMultExpiry) ? activeAttackMult : 1.0;
        double bonus = (now < nextAttackBonusExpiry && nextAttackBonus > 0) ? nextAttackBonus : 0;
        return (int)(baseAttackPower * mult + bonus);
    }

    /**
     * Returns effective movement speed: base * active speed multiplier. (C2)
     * Active multiplier expires automatically after its duration.
     */
    public double getEffectiveSpeed()
    {
        long now = System.currentTimeMillis();
        return baseMovementSpeed * ((now < activeSpeedMultExpiry) ? activeSpeedMult : 1.0);
    }

    /**
     * Applies a timed attack power multiplier. Replaces any existing attack buff. (C2)
     * The base stat is never mutated; expiry drives the revert automatically.
     */
    public void applyAttackBuff(double multiplier, int durationMs)
    {
        activeAttackMult = multiplier;
        activeAttackMultExpiry = System.currentTimeMillis() + durationMs;
        System.out.println(name + " attack buff: x" + multiplier + " for " + durationMs + "ms");
    }

    /**
     * Applies a timed movement speed multiplier. Replaces any existing speed buff. (C2)
     */
    public void applySpeedBuff(double multiplier, int durationMs)
    {
        activeSpeedMult = multiplier;
        activeSpeedMultExpiry = System.currentTimeMillis() + durationMs;
        System.out.println(name + " speed buff: x" + multiplier + " for " + durationMs + "ms");
    }

    /**
     * Adds a flat bonus to the next attack. The bonus is consumed on the next swing
     * (hit, miss, or out of range) or expires after 5 seconds if unused. (C2)
     */
    public void applyNextAttackBonus(double bonus)
    {
        nextAttackBonus += bonus; // Stack bonuses from multiple sources before expiry.
        nextAttackBonusExpiry = System.currentTimeMillis() + 5000; // 5-second expiry window.
        System.out.println(name + " next attack bonus: +" + (int)bonus + " (expires in 5s)");
    }

    // --- C4: Heal-over-time ---

    /**
     * Starts a heal-over-time effect applied per frame in update(). (C4)
     * Stops automatically when healUntilMs is reached or the character is defeated.
     */
    public void startHealOverTime(double healPerSec, int durationMs)
    {
        this.healPerSecond = healPerSec;
        this.healUntilMs = System.currentTimeMillis() + durationMs;
        System.out.println(name + " starts healing " + String.format("%.1f", healPerSec) + " HP/s");
    }

    // --- G2: Stun ---

    /**
     * Returns true if the character is currently stunned. (G2)
     */
    public boolean isStunned()
    {
        return System.currentTimeMillis() < stunnedUntil;
    }

    /**
     * Applies a stun lasting durationMs milliseconds. (G2)
     * A new stun only extends stunnedUntil to the later end time; it never shortens.
     */
    public void applyStun(long durationMs)
    {
        long newEnd = System.currentTimeMillis() + durationMs;
        if (newEnd > stunnedUntil)
        {
            stunnedUntil = newEnd; // Extend; never shorten.
        }
        System.out.println(name + " is stunned for " + durationMs + "ms.");

        if (characterSprite != null)
        {
            characterSprite.setOpacity(0.5); // Dim sprite to show stunned state.
            long remaining = Math.max(0, stunnedUntil - System.currentTimeMillis());
            PauseTransition restoreOpacity = new PauseTransition(Duration.millis(remaining));
            restoreOpacity.setOnFinished(e ->
            {
                if (!isStunned() && characterSprite != null)
                {
                    characterSprite.setOpacity(1.0);
                }
            });
            restoreOpacity.play();
        }
    }

    // --- U4: Frame-rate-independent movement ---

    /**
     * Moves the sprite by a normalized direction vector scaled by effective speed * dt. (U4, C2)
     * Positions are clamped to boundaries instead of rejected.
     */
    public void moveBy(double ndx, double ndy, double dt)
    {
        if (isStunned()) return; // G2: blocked while stunned.
        double speed = getEffectiveSpeed(); // C2: apply active speed buff.
        double newX = characterSprite.getLayoutX() + ndx * speed * dt;
        double newY = characterSprite.getLayoutY() + ndy * speed * dt;
        newX = Math.max(GameSession.BOUNDARY_LEFT,   Math.min(GameSession.BOUNDARY_RIGHT,  newX));
        newY = Math.max(GameSession.BOUNDARY_TOP,    Math.min(GameSession.BOUNDARY_BOTTOM, newY));
        characterSprite.setLayoutX(newX);
        characterSprite.setLayoutY(newY);
    }

    // --- T2, G3: Per-frame attack state machine ---

    /**
     * Per-frame update: resolves attack wind-up / end-lag and applies heal-over-time. (T2, C4)
     * Called by GameSession every frame on the FX thread.
     */
    public void update(long nowMillis, double dt)
    {
        // Wind-up complete: deliver the pending attack.
        if (isAttacking && nowMillis >= attackWindUpEndMs)
        {
            if (pendingAttackTarget != null)
            {
                // G3: Only deal damage when within ATTACK_RANGE px of the opponent.
                double dx   = characterSprite.getLayoutX()
                    - pendingAttackTarget.getCharacterSprite().getLayoutX();
                double dy   = characterSprite.getLayoutY()
                    - pendingAttackTarget.getCharacterSprite().getLayoutY();
                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist <= ATTACK_RANGE)
                {
                    int damage = calculateDamage(pendingAttackTarget); // C1: corrected formula.
                    pendingAttackTarget.takeDamage(damage, null);
                    System.out.println(name + " attacked " + pendingAttackTarget.getName()
                        + " for " + damage + " damage!");
                }
                else
                {
                    System.out.println(name + "'s attack whiffed — opponent too far ("
                        + (int)dist + " px, range=" + ATTACK_RANGE + ").");
                }
            }

            // C2: Consume next-attack bonus regardless of hit, miss, or out-of-range.
            nextAttackBonus = 0;
            nextAttackBonusExpiry = 0;

            pendingAttackTarget = null;
            isAttacking = false;
            isInLag = true; // Enter recovery lag.
            attackEndLagEndMs = nowMillis + (long)(BASE_END_LAG_MS / attackSpeedMultiplier);
        }

        // End-lag complete: ready for next attack.
        if (isInLag && nowMillis >= attackEndLagEndMs)
        {
            isInLag = false;
            System.out.println(name + " is ready to attack again!");
        }

        // C4: Heal-over-time — applied per frame as a double, stops if defeated.
        if (healPerSecond > 0 && nowMillis < healUntilMs && health > 0)
        {
            setHealth(health + healPerSecond * dt);
        }
    }

    // --- G1: Attack speed boost ---

    /**
     * Boosts attack speed by setting the per-instance multiplier for the given duration. (G1)
     * Uses PauseTransition so the reset runs on the FX thread with no Thread.sleep.
     */
    public void boostAttackSpeed(double speedMultiplier, int duration)
    {
        attackSpeedMultiplier = speedMultiplier;
        System.out.println(name + " attack speed boosted by " + speedMultiplier + "x!");

        PauseTransition reset = new PauseTransition(Duration.millis(duration));
        reset.setOnFinished(e ->
        {
            attackSpeedMultiplier = 1.0;
            System.out.println(name + "'s attack speed boost has ended.");
        });
        reset.play();
    }

    // --- Projectile charging ---

    public void startCharging()
    {
        if (isStunned())
        {
            System.out.println(name + " is stunned and cannot charge a projectile!");
            return;
        }
        if (!isCharging)
        {
            chargingStartTime = System.currentTimeMillis();
            isCharging = true;
        }
    }

    /**
     * Launches a charged projectile toward the opponent. (G5)
     * Size and speed are computed from the actual charge time, clamped to MAX_CHARGE_MS.
     */
    public Projectile launchChargedProjectile(Character opponent)
    {
        if (!isCharging)
        {
            System.out.println(name + " is not charging and cannot launch a projectile.");
            return null;
        }
        if (opponent == null)
        {
            System.err.println(name + " cannot launch a projectile because the opponent is null.");
            return null;
        }
        if (chargingStartTime == -1)
        {
            System.err.println(name + " attempted to launch a projectile without a valid charging state.");
            return null;
        }

        long chargeDuration = System.currentTimeMillis() - chargingStartTime;
        chargingStartTime = -1;
        isCharging = false;

        long clamped = Math.min(chargeDuration, MAX_CHARGE_MS); // G5: clamp.
        double ratio = (double) clamped / MAX_CHARGE_MS;
        double size  = 10.0 + ratio * 20.0;  // 10–30 px.
        double speed = 400.0 - ratio * 200.0; // 400–200 px/s.

        Projectile projectile = new Projectile(this,
            getCharacterSprite().getLayoutX(),
            getCharacterSprite().getLayoutY(),
            opponent.getCharacterSprite().getLayoutX(),
            opponent.getCharacterSprite().getLayoutY(),
            size, speed);

        System.out.println(name + " launched a projectile (size=" + String.format("%.1f", size)
            + ", speed=" + String.format("%.0f", speed) + " px/s).");
        return projectile;
    }

    // --- Shield ---

    public void updateShieldState(long currentTime)
    {
        if (isShieldActive)
        {
            shieldLevel -= SHIELD_DEPLETION_RATE;
            if (shieldLevel <= 0)
            {
                breakShield(); // G6: unified break.
            }
        }
        else if (shieldLevel < 1.0)
        {
            shieldLevel = Math.min(1.0, shieldLevel + SHIELD_REGEN_RATE);
        }

        if (!isShieldAvailable && (currentTime - shieldLastUsedTime >= SHIELD_COOLDOWN))
        {
            isShieldAvailable = true; // Cooldown expired; shield can be raised again.
        }
        // U2: bar updates removed; GameSession.updateBars() drives the HUD bars directly.
    }

    /** Breaks the shield: sets level to 0, starts cooldown, removes visual. (G6) */
    public void breakShield()
    {
        shieldLevel = 0;
        isShieldAvailable = false;
        shieldLastUsedTime = System.currentTimeMillis();
        System.out.println(name + "'s shield is broken!");
        deactivateShield();
    }

    /**
     * Activates the shield if all preconditions are met. (G6, G7)
     * Early return if already active, on cooldown, depleted, or stunned.
     */
    public void activateShield()
    {
        if (isShieldActive || !isShieldAvailable || shieldLevel <= 0 || isStunned())
        {
            return; // Guard against invalid activation.
        }

        if (characterSprite != null)
        {
            if (shieldEffect == null) // G7: create DropShadow only once.
            {
                shieldEffect = new DropShadow();
                shieldEffect.setColor(this.name.equals("Aqua") ? Color.DARKBLUE : Color.BLUE);
                shieldEffect.setRadius(20);
            }
            characterSprite.setEffect(shieldEffect);

            if (shieldPulseAnimation == null) // G7: create Timeline only once.
            {
                shieldPulseAnimation = new Timeline(
                    new KeyFrame(Duration.millis(500), e -> shieldEffect.setRadius(20)),
                    new KeyFrame(Duration.millis(1000), e -> shieldEffect.setRadius(30))
                );
                shieldPulseAnimation.setCycleCount(Timeline.INDEFINITE);
            }
            shieldPulseAnimation.play();
        }
        isShieldActive = true;
    }

    public void deactivateShield()
    {
        if (characterSprite != null)
        {
            characterSprite.setEffect(null);
            if (shieldPulseAnimation != null) shieldPulseAnimation.stop();
        }
        isShieldActive = false;
    }

    public void releaseShieldKey()
    {
        if (isShieldActive)
        {
            deactivateShield();
            shieldLastUsedTime = System.currentTimeMillis();
        }
    }

    // --- Attack ---

    /**
     * Starts the attack wind-up. Damage is delivered in update() when the timer fires. (T2)
     * G2: blocked while stunned. G1: wind-up duration uses the per-instance speed multiplier.
     */
    public void performAttack(Character opponent)
    {
        if (isStunned())
        {
            System.out.println(name + " is stunned and cannot attack!");
            return;
        }
        if (isAttacking || isInLag) return;

        System.out.println(this.name + " is winding up for an attack...");
        isAttacking = true;
        pendingAttackTarget = opponent;
        attackWindUpEndMs = System.currentTimeMillis()
            + (long)(BASE_ATTACK_DELAY_MS / attackSpeedMultiplier);
        applyAttackWindUpEffect();
    }

    /**
     * Applies a red glow during wind-up and clears it via PauseTransition. (T2)
     * Called on the FX thread; no Thread.sleep needed.
     */
    private void applyAttackWindUpEffect()
    {
        if (characterSprite == null) return;
        DropShadow attackEffect = new DropShadow();
        attackEffect.setColor(Color.RED);
        attackEffect.setRadius(30);
        characterSprite.setEffect(attackEffect);

        long windUp = (long)(BASE_ATTACK_DELAY_MS / attackSpeedMultiplier);
        PauseTransition clear = new PauseTransition(Duration.millis(windUp));
        clear.setOnFinished(e -> { if (characterSprite != null) characterSprite.setEffect(null); });
        clear.play();
    }

    /**
     * Calculates the damage dealt during an attack. (C1)
     * Order: miss check → raw = max(MIN_DAMAGE, effective - defense) → crit multiplies raw.
     * Uses the opponent parameter directly; the owner field has been removed. (C1)
     */
    private int calculateDamage(Character target)
    {
        if (target == null)
        {
            System.err.println("Error: Target is null during damage calculation.");
            return 0;
        }

        double roll = Math.random();

        // C1: Miss check first.
        if (roll < MISS_CHANCE)
        {
            System.out.println(name + " missed the attack!");
            return 0;
        }

        // C1: Raw damage formula.
        int effective = getEffectiveAttack(); // C2: uses base + active buffs.
        int raw = Math.max(MIN_DAMAGE, effective - (int) target.getDefensePower());

        // C1: Crit check last.
        if (roll > (1.0 - CRIT_CHANCE))
        {
            System.out.println(name + " landed a critical hit!");
            return (int)(raw * CRIT_MULTIPLIER);
        }

        System.out.println(name + " dealt " + raw + " damage to " + target.getName());
        return raw;
    }

    // --- Damage reception ---

    /**
     * Applies damage, considering the active shield. (T2)
     * Always called from the FX thread after Phase 3; no Platform.runLater needed.
     */
    public void takeDamage(double damage, Projectile projectile)
    {
        if (isShieldActive)
        {
            shieldLevel -= damage / 100.0; // Shield absorbs the hit. (G4: projectiles also absorbed)
            if (shieldLevel <= 0)
            {
                shieldLevel = 0;
                isShieldAvailable = false; // G6: start cooldown.
                shieldLastUsedTime = System.currentTimeMillis();
                System.out.println(name + "'s shield is broken!");
                deactivateShield();
            }
            else
            {
                System.out.println(name + "'s shield absorbed " + damage
                    + " damage. Shield: " + (int)(shieldLevel * 100) + "%");
            }
            return; // Shield absorbed the hit; no health damage.
        }

        setHealth(health - damage); // C4: route through setHealth for defeat-no-revival check.
    }

    // --- Special ability ---

    /**
     * Executes the character's special ability if off cooldown. Blocked while stunned. (G2)
     */
    public void performSpecialAbility(Character opponent)
    {
        if (isStunned())
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
            System.out.println(name + "'s special ability is on cooldown! "
                + timeRemaining + " seconds remaining.");
        }
    }

    public boolean canUseSpecialAbility()
    {
        return System.currentTimeMillis() - lastSpecialUsedTime >= specialAbilityCooldown;
    }

    // --- Health management ---

    /**
     * Sets health, clamped to [0, maxHealth]. (C4)
     * Never revives a defeated character: once health reaches 0, it stays at 0.
     * Accepts a double so heal-over-time fractions accumulate correctly.
     */
    public void setHealth(double newHealth)
    {
        if (health <= 0) return; // C4: once defeated, no revival.
        if (newHealth > maxHealth)
        {
            this.health = maxHealth;
        }
        else if (newHealth <= 0)
        {
            this.health = 0;
            System.out.println(name + " has been defeated!");
        }
        else
        {
            this.health = newHealth;
        }
        System.out.println(name + "'s health is now: " + (int)this.health);
        // U2: healthBar removed; GameSession.updateBars() drives the HUD health bar directly.
    }

    public void setSpecialAbilityCooldown(int cooldown)
    {
        this.specialAbilityCooldown = cooldown;
        this.lastSpecialUsedTime = System.currentTimeMillis(); // Start cooldown from now.
    }


}
