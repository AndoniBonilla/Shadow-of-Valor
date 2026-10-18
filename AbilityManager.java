package FightingGame;

// Import statements for required utilities and libraries
import javafx.animation.KeyFrame;
// Single frame in an animation timeline; used for gradualHeal's per-step heal events.
import javafx.animation.PauseTransition;
// One-shot FX-thread timer; replaces Thread.sleep for all timed ability reverts. (T2)
import javafx.animation.Timeline;
// Multi-keyframe animation; used for gradualHeal to schedule heal ticks. (T2)
import javafx.application.Platform;
// Used for updating JavaFX UI components on the FX thread (teleport only).
import javafx.util.Duration;
// Represents time intervals used by PauseTransition and Timeline.

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
                user.setSpecialAbilityCooldown(10000); // Set cooldown to 10 second
                boostAttack(user, 2, 6000);  // Double attack power for 6 seconds
                break;

            case "speed surge":
                user.setSpecialAbilityCooldown(12000); // Cooldown of 9 seconds
                speedSurge(user, 1.2, 4000); // Increase speed and attack rate for 4 seconds
                break;

            case "electric burst":
                user.setSpecialAbilityCooldown(9000); // Set cooldown to 9 seconds
                amplifyMovementAndAttack(user, 1.1, 5000); // Boost attack and movement by 2x for 5 seconds
                stunOpponent(opponent, 2000); // Stun opponent for 2 seconds
                break;

            case "blazing punch":
                user.setSpecialAbilityCooldown(9000); // Set cooldown to 9 seconds
                boostNextAttack(user, 18); // Boost the next attack's damage by +12
                break;

            case "shadow step":
                user.setSpecialAbilityCooldown(10000); // Set cooldown to 10 seconds
                boostNextAttack(user, 15); // Boost the next attack's damage by +15
                teleport(user); // Teleport the user to a random position
                break;

            case "nature's embrace":
                user.setSpecialAbilityCooldown(15000); // Set cooldown to 15 seconds
                gradualHeal(user, 0.04, 10000); // Heal 2% of max health over 10 seconds
                break;

            case "shadow strike":
                user.setSpecialAbilityCooldown(13000); // Set cooldown to 13000 seconds
                boostNextAttack(user, 20); // Boost the next attack's damage by +20
                goInvisible(user, 5000); // Make the user invisible for 5 seconds
                break;
            case "earthquake":
                user.setSpecialAbilityCooldown(16000); // Set cooldown to 10 seconds
                amplifyMovementAndAttack(user, 1.15, 5000); // Slight boost to attack/movement for 5 seconds
                stunOpponent(opponent, 7000); // Stun the opponent for 7 seconds
                break;



            default:
                System.out.println("Unknown ability: " + user.getSpecialAbilityName());
        }
    }

    /**
     * Temporarily boosts the user's attack for their next attack. (T2)
     * PauseTransition replaces Thread.sleep; runs on the FX thread.
     *
     * @param user The character receiving the attack boost
     * @param bonus The amount of attack power to add
     */
    private static void boostNextAttack(Character user, int bonus)
    {
        int originalAttack = user.getAttackPower(); // Store the user's original attack power
        user.setAttackPower(originalAttack + bonus); // Increase attack power temporarily
        System.out.println(user.getName() + " has boosted the next attack!");

        // Revert attack power to its original value after 5 seconds (FX-thread-safe). (T2)
        PauseTransition revert = new PauseTransition(Duration.seconds(5));
        revert.setOnFinished(e ->
        {
            user.setAttackPower(originalAttack); // Restore original attack power.
            System.out.println(user.getName() + "'s attack boost has ended.");
        });
        revert.play();
    }

    // Amplifies the user's movement speed and attack power for a specified duration. (T2)
    private static void amplifyMovementAndAttack(Character user, double multiplier, int duration)
    {
        // Save the original speed and attack power to restore later
        double originalSpeed = user.getMovementSpeed();
        int originalAttack = user.getAttackPower();

        // Apply the multiplier to the user's speed and attack power
        user.setMovementSpeed(originalSpeed * multiplier);
        user.setAttackPower((int) (originalAttack * multiplier));
        System.out.println(user.getName() + " has amplified movement and attack!");

        // Restore original values after the duration using a FX-thread-safe timer. (T2)
        PauseTransition revert = new PauseTransition(Duration.millis(duration));
        revert.setOnFinished(e ->
        {
            user.setMovementSpeed(originalSpeed); // Restore original speed.
            user.setAttackPower(originalAttack); // Restore original attack power.
            System.out.println(user.getName() + "'s amplification has ended.");
        });
        revert.play();
    }

    // Boosts the user's attack power by a specified multiplier for a set duration. (T2)
    private static void boostAttack(Character user, int multiplier, int duration)
    {
        // Save the user's original attack power
        int originalAttack = user.getAttackPower();

        // Multiply the user's attack power by the specified multiplier
        user.setAttackPower(originalAttack * multiplier);
        System.out.println(user.getName() + " boosted attack!");

        // Restore the original attack power after the effect ends. (T2)
        PauseTransition revert = new PauseTransition(Duration.millis(duration));
        revert.setOnFinished(e ->
        {
            user.setAttackPower(originalAttack); // Restore original attack power.
            System.out.println(user.getName() + "'s attack boost has ended.");
        });
        revert.play();
    }

    // Increases the user's movement speed and attack rate for a specified duration. (T2)
    private static void speedSurge(Character user, double multiplier, int duration)
    {
        // Save the original movement speed
        double originalSpeed = user.getMovementSpeed();

        // Increase the movement speed by the multiplier
        user.setMovementSpeed(originalSpeed * multiplier);
        System.out.println(user.getName() + " is in Speed Surge mode!");

        // Delegate attack-speed change to the character's own PauseTransition method. (G1)
        user.boostAttackSpeed(multiplier, duration);

        // Restore the original movement speed after the duration. (T2)
        PauseTransition revert = new PauseTransition(Duration.millis(duration));
        revert.setOnFinished(e ->
        {
            user.setMovementSpeed(originalSpeed); // Restore original movement speed.
            System.out.println(user.getName() + "'s Speed Surge has ended!");
        });
        revert.play();
    }

    // Temporarily stuns the opponent, preventing them from acting. (G2)
    // Uses applyStun so overlapping stuns extend rather than shorten each other.
    private static void stunOpponent(Character opponent, int duration)
    {
        opponent.applyStun(duration); // Timestamp-based stun; never shortens existing stun.
        System.out.println(opponent.getName() + " is stunned for " + duration + "ms!");
    }

    // Teleports the user to a random position within the arena
    private static void teleport(Character user)
    {
        // Calculate a random position within the arena's bounds
        double newX = Math.random() * (GameSession.ARENA_WIDTH - 50) + 25;
        double newY = Math.random() * (GameSession.ARENA_HEIGHT - 50) + 25;

        // Use Platform.runLater to update JavaFX components on the main thread
        Platform.runLater(() ->
        {
            user.getCharacterSprite().setLayoutX(newX); // Set the new X position
            user.getCharacterSprite().setLayoutY(newY); // Set the new Y position
            System.out.println(user.getName() + " teleported to (" + newX + ", " + newY + ").");
        });
    }

    // Gradually heals the user over a specified duration. (T2)
    // Uses a Timeline to schedule one heal tick per second on the FX thread.
    private static void gradualHeal(Character user, double healPercent, int duration)
    {
        // Calculate the total amount of health to heal and divide it into steps
        int totalHeal = (int) (user.getMaxHealth() * healPercent);
        int healSteps = duration / 1000; // One heal tick per second.
        int healPerStep = Math.max(1, totalHeal / healSteps); // At least 1 HP per tick.

        // Schedule each heal tick as a KeyFrame on the FX thread. (T2)
        Timeline healTimeline = new Timeline();
        for (int i = 1; i <= healSteps; i++)
        {
            final int tick = i; // Captured for the lambda.
            healTimeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(tick), e ->
                {
                    user.setHealth((int)(user.getHealth() + healPerStep)); // Apply one step of healing.
                    System.out.println(user.getName() + " healed " + healPerStep + " health.");
                })
            );
        }
        healTimeline.play();
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
