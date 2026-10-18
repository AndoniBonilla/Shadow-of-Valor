package FightingGame;

// Import statements for required utilities and libraries
import javafx.animation.PauseTransition;
// One-shot FX-thread timer; used for goInvisible restore. (T2)
import javafx.util.Duration;
// Represents time intervals used by PauseTransition.

// Main class responsible for managing special abilities in the game

public class AbilityManager

{

    /**
     * Executes the special ability of the given user and applies its effects
     * to the opponent or the user itself based on the ability.
     *
     * @param user The character using the ability
     * @param opponent The opponent character affected by the ability
     */

    public static void executeAbility(Character user, Character opponent)
    {
        switch (user.getSpecialAbilityName().toLowerCase())  // Retrieve the name of the user's special ability and act accordingly

        {
            case "flame rush":
                user.setSpecialAbilityCooldown(10000); // Set cooldown to 10 seconds
                boostAttack(user, 2.0, 6000);  // Double attack power for 6 seconds
                break;

            case "speed surge":
                user.setSpecialAbilityCooldown(12000); // Cooldown of 12 seconds
                speedSurge(user, 1.2, 4000); // Increase speed and attack rate for 4 seconds
                break;

            case "electric burst":
                user.setSpecialAbilityCooldown(9000); // Set cooldown to 9 seconds
                amplifyMovementAndAttack(user, 1.1, 5000); // Boost attack and movement by 1.1x for 5 seconds
                stunOpponent(opponent, 2000); // Stun opponent for 2 seconds
                break;

            case "blazing punch":
                user.setSpecialAbilityCooldown(9000); // Set cooldown to 9 seconds
                boostNextAttack(user, 18); // Boost the next attack's damage by +18
                break;

            case "shadow step":
                user.setSpecialAbilityCooldown(10000); // Set cooldown to 10 seconds
                boostNextAttack(user, 15); // Boost the next attack's damage by +15
                teleport(user, opponent); // C3: teleport ~50 px behind the opponent.
                break;

            case "nature's embrace":
                user.setSpecialAbilityCooldown(15000); // Set cooldown to 15 seconds
                gradualHeal(user, 0.04, 10000); // Heal 4% of max health over 10 seconds
                break;

            case "shadow strike":
                user.setSpecialAbilityCooldown(13000); // Set cooldown to 13 seconds
                boostNextAttack(user, 20); // Boost the next attack's damage by +20
                goInvisible(user, 5000); // Make the user invisible for 5 seconds
                break;

            case "earthquake":
                user.setSpecialAbilityCooldown(16000); // Set cooldown to 16 seconds
                amplifyMovementAndAttack(user, 1.15, 5000); // Slight boost to attack/movement for 5 seconds
                stunOpponent(opponent, 7000); // Stun the opponent for 7 seconds
                break;

            default:
                System.out.println("Unknown ability: " + user.getSpecialAbilityName());
        }
    }

    /**
     * Boosts the user's next attack by adding a flat damage bonus. (C2)
     * The bonus is consumed on the next swing or expires after 5 seconds.
     *
     * @param user  The character receiving the next-attack bonus.
     * @param bonus The flat damage amount to add.
     */
    private static void boostNextAttack(Character user, int bonus)
    {
        user.applyNextAttackBonus(bonus); // C2: consumed by next attack; never mutates base stat.
        System.out.println(user.getName() + " has boosted the next attack by +" + bonus + "!");
    }

    // Amplifies the user's movement speed and attack power for a specified duration. (C2)
    private static void amplifyMovementAndAttack(Character user, double multiplier, int duration)
    {
        user.applyAttackBuff(multiplier, duration); // C2: multiplies effective attack via timestamped field.
        user.applySpeedBuff(multiplier, duration);  // C2: multiplies effective speed via timestamped field.
        System.out.println(user.getName() + " has amplified movement and attack by x" + multiplier + "!");
    }

    // Boosts the user's attack power by a specified multiplier for a set duration. (C2)
    private static void boostAttack(Character user, double multiplier, int duration)
    {
        user.applyAttackBuff(multiplier, duration); // C2: base attack is never mutated.
        System.out.println(user.getName() + " boosted attack by x" + multiplier + "!");
    }

    // Increases the user's movement speed and attack rate for a specified duration. (C2)
    private static void speedSurge(Character user, double multiplier, int duration)
    {
        user.applySpeedBuff(multiplier, duration); // C2: base speed is never mutated.
        user.boostAttackSpeed(multiplier, duration); // G1: per-instance attack speed multiplier.
        System.out.println(user.getName() + " is in Speed Surge mode!");
    }

    // Temporarily stuns the opponent, preventing them from acting. (G2)
    // Uses applyStun so overlapping stuns extend rather than shorten each other.
    private static void stunOpponent(Character opponent, int duration)
    {
        opponent.applyStun(duration); // Timestamp-based stun; never shortens existing stun.
        System.out.println(opponent.getName() + " is stunned for " + duration + "ms!");
    }

    /**
     * Teleports the user ~50 px behind the opponent. (C3)
     * "Behind" = the side of the opponent away from the user.
     * The landing point is clamped to the arena's movement boundaries.
     * Called from the FX game-loop thread; no Platform.runLater needed. (C3)
     *
     * @param user     The character that teleports.
     * @param opponent The target whose position defines "behind."
     */
    private static void teleport(Character user, Character opponent)
    {
        double oppX = opponent.getCharacterSprite().getLayoutX();
        double oppY = opponent.getCharacterSprite().getLayoutY();
        double userX = user.getCharacterSprite().getLayoutX();

        // Pick the side of the opponent that is further from the user.
        double newX = (userX < oppX) ? oppX + 50 : oppX - 50;
        double newY = oppY; // Same row as the opponent.

        // Clamp to movement boundaries so the fighter never leaves the arena.
        newX = Math.max(GameSession.BOUNDARY_LEFT,   Math.min(GameSession.BOUNDARY_RIGHT,  newX));
        newY = Math.max(GameSession.BOUNDARY_TOP,    Math.min(GameSession.BOUNDARY_BOTTOM, newY));

        user.getCharacterSprite().setLayoutX(newX); // Move sprite directly; on FX thread. (C3)
        user.getCharacterSprite().setLayoutY(newY);
        System.out.println(user.getName() + " teleported behind " + opponent.getName()
            + " to (" + (int)newX + ", " + (int)newY + ").");
    }

    /**
     * Starts a heal-over-time effect via Character.startHealOverTime. (C4)
     * Replaces the Timeline-based approach; per-frame healing happens in update().
     *
     * @param user         The character to heal.
     * @param healPercent  Fraction of max health healed over the full duration.
     * @param durationMs   Duration of the heal effect in milliseconds.
     */
    private static void gradualHeal(Character user, double healPercent, int durationMs)
    {
        double totalHeal = user.getMaxHealth() * healPercent; // e.g. 4% of max health.
        double healPerSec = totalHeal / (durationMs / 1000.0); // Spread evenly over the duration.
        user.startHealOverTime(healPerSec, durationMs); // C4: applied per frame in update().
        System.out.println(user.getName() + " starts healing "
            + String.format("%.1f", healPerSec) + " HP/s for " + (durationMs / 1000) + "s.");
    }

    // Makes the user invisible for a specified duration. (T2)
    // PauseTransition runs on the FX thread; no Thread.sleep or Platform.runLater needed.
    private static void goInvisible(Character user, int duration)
    {
        // Set the user's character sprite opacity to 0 (invisible).
        user.getCharacterSprite().setOpacity(0);
        System.out.println(user.getName() + " is now invisible!");

        // Restore opacity after the duration via a FX-thread-safe timer. (T2)
        PauseTransition restore = new PauseTransition(Duration.millis(duration));
        restore.setOnFinished(e ->
        {
            user.getCharacterSprite().setOpacity(1.0); // Restore visibility.
        });
        restore.play();
    }
}
