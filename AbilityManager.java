package FightingGame;

// Import statements for required utilities and libraries 
import javafx.application.Platform; // Used for updating JavaFX UI components 

//Main class responsible for managing special abilities in the game

public class AbilityManager 

{

	

    /**
     * Exe cutes the special ability of the given user and applies its effects
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
     * Temporarily boosts the user's attack for their next attack.
     *
     * @param user The character receiving the attack boost
     * @param bonus The amount of attack power to add
     */
    private static void boostNextAttack(Character user, int bonus) 
    {
        int originalAttack = user.getAttackPower(); // Store the user's original attack power
        user.setAttackPower(originalAttack + bonus); // Increase attack power temporarily
        System.out.println(user.getName() + " has boosted the next attack!");

        // Revert attack power to its original value after 5 seconds
        new Thread(() -> 
        {
            try 
            {
                Thread.sleep(5000); // Wait for 5 seconds
            } 
            catch (InterruptedException e) 
            {
                e.printStackTrace(); // Handle thread interruption
            }
            user.setAttackPower(originalAttack); // Restore original attack power
            System.out.println(user.getName() + "'s attack boost has ended.");
        }).start();
    }

    // Additional methods like `amplifyMovementAndAttack`, `speedSurge`, `stunOpponent`, 
    // `teleport`, `gradualHeal`, and `goInvisible` follow similar logic.
    // Each has its own specific parameters and effects for unique abilities.

 //  Amplifies the user's movement speed and attack power for a specified duration
    private static void amplifyMovementAndAttack(Character user, double multiplier, int duration)
    {
        // Save the original speed and attack power to restore later
        double originalSpeed = user.getMovementSpeed();
        int originalAttack = user.getAttackPower();

        // Apply the multiplier to the user's speed and attack power
        user.setMovementSpeed(originalSpeed * multiplier);
        user.setAttackPower((int) (originalAttack * multiplier));
        System.out.println(user.getName() + " has amplified movement and attack!");

        // Create a new thread to handle the timed effect
        new Thread(() -> 
        {
            try 
            {
                Thread.sleep(duration); // Wait for the duration of the effect
            }
            catch (InterruptedException e)
            {
                e.printStackTrace(); // Print the stack trace if the thread is interrupted
            }
            // Restore the user's original speed and attack power
            user.setMovementSpeed(originalSpeed);
            user.setAttackPower(originalAttack);
            System.out.println(user.getName() + "'s amplification has ended.");
        }).start();
    }

    // Boosts the user's attack power by a specified multiplier for a set duration
    private static void boostAttack(Character user, int multiplier, int duration) 
    {
        // Save the user's original attack power
        int originalAttack = user.getAttackPower();

        // Multiply the user's attack power by the specified multiplier
        user.setAttackPower(originalAttack * multiplier);
        System.out.println(user.getName() + " boosted attack!");

        // Create a new thread to restore the original attack power after the effect ends
        new Thread(() -> 
        {
            try 
            {
                Thread.sleep(duration); // Wait for the duration of the boost
            } 
            catch (InterruptedException e) 
            {
                e.printStackTrace(); // Print the stack trace if the thread is interrupted
            }
            // Restore the original attack power
            user.setAttackPower(originalAttack);
            System.out.println(user.getName() + "'s attack boost has ended.");
        }).start();
    }

    // Increases the user's movement speed and attack rate for a specified duration
    private static void speedSurge(Character user, double multiplier, int duration) 
    {
        // Save the original movement speed
        double originalSpeed = user.getMovementSpeed();

        // Increase the movement speed by the multiplier
        user.setMovementSpeed(originalSpeed * multiplier);
        System.out.println(user.getName() + " is in Speed Surge mode!");

        // Delegate attack-related changes to the user's method
        user.boostAttackSpeed(multiplier, duration);

        // Create a new thread to restore the original movement speed after the duration ends
        new Thread(() -> 
        {
            try 
            {
                Thread.sleep(duration); // Wait for the duration of the effect
            } 
            catch (InterruptedException e) 
            {
                e.printStackTrace(); // Print the stack trace if the thread is interrupted
            }
            // Restore the original movement speed
            user.setMovementSpeed(originalSpeed);
            System.out.println(user.getName() + "'s Speed Surge has ended!");
        }).start();
    }

    // Temporarily stuns the opponent, preventing them from acting
    private static void stunOpponent(Character opponent, int duration) 
    {
        // Set the opponent's stunned state to true
        opponent.setStunned(true);
        System.out.println(opponent.getName() + " is stunned!");

        // Create a new thread to remove the stun after the duration ends
        new Thread(() -> 
        {
            try 
            {
                Thread.sleep(duration); // Wait for the duration of the stun
            } 
            catch (InterruptedException e) {
                e.printStackTrace(); // Print the stack trace if the thread is interrupted
            }
            // Use Platform.runLater to update JavaFX components on the main thread
            Platform.runLater(() -> {
                opponent.setStunned(false); // Remove the stunned state
                System.out.println(opponent.getName() + " is no longer stunned.");
            });
        }).start();
    }

    // Teleports the user to a random position within the arena
    private static void teleport(Character user) 
    {
        // Calculate a random position within the arena's bounds
        double newX = Math.random() * (GameApp.ARENA_WIDTH - 50) + 25;
        double newY = Math.random() * (GameApp.ARENA_HEIGHT - 50) + 25;

        // Use Platform.runLater to update JavaFX components on the main thread
        Platform.runLater(() -> 
        {
            user.getCharacterSprite().setLayoutX(newX); // Set the new X position
            user.getCharacterSprite().setLayoutY(newY); // Set the new Y position
            System.out.println(user.getName() + " teleported to (" + newX + ", " + newY + ").");
        });
    }

    // Gradually heals the user over a specified duration
    private static void gradualHeal(Character user, double healPercent, int duration) 
    {
        // Calculate the total amount of health to heal and divide it into steps
        int totalHeal = (int) (user.getMaxHealth() * healPercent);
        int healSteps = duration / 1000; // Heal over 1-second intervals
        int healPerStep = totalHeal / healSteps;

        // Create a new thread to handle the gradual healing process
        new Thread(() -> 
        {
            for (int i = 0; i < healSteps; i++)
            {
                try 
                {
                    Thread.sleep(1000); // Wait 1 second between each heal step
                } 
                catch (InterruptedException e) 
                {
                    e.printStackTrace(); // Print the stack trace if the thread is interrupted
                }
                // Increase the user's health by the calculated amount for each step
                user.setHealth((int) (user.getHealth() + healPerStep));
                System.out.println(user.getName() + " healed " + healPerStep + " health.");
            }
        }).start();
    }

    // Makes the user invisible for a specified duration
    private static void goInvisible(Character user, int duration) 
    {
        // Set the user's character sprite opacity to 0 (invisible)
        user.getCharacterSprite().setOpacity(0);
        System.out.println(user.getName() + " is now invisible!");

        // Create a new thread to make the user visible again after the duration ends
        new Thread(() -> 
        {
            try 
            {
                Thread.sleep(duration); // Wait for the duration of invisibility
            } 
            catch (InterruptedException e) 
            {
                e.printStackTrace(); // Print the stack trace if the thread is interrupted
            }
            // Restore the sprite's opacity to make the user visible again
            user.getCharacterSprite().setOpacity(1.0);
        }).start();
    }
}
