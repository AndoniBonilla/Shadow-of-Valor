package FightingGame;

import java.util.ArrayList;       
//Allows traversal of collections while enabling safe removal of elements during iteration.
import java.util.List;

import javafx.application.Platform;

/**
 * Manages the lifecycle of projectiles in the game, including movement, collisions, and visual updates.
 */
public class ProjectileManager 
{

    private static final int MAX_PROJECTILES_PER_PLAYER = 6; 
    // Maximum number of active projectiles allowed.

    private static final List<Projectile> activeProjectiles = new ArrayList<>(); 
    // Tracks all active projectiles in the game.

    /**
     * Adds a projectile to the scene and tracks it.
     * @param projectile The projectile to be added.
     */
    /**
     * Adds a new projectile to the game while ensuring that the player does not exceed 
     * the maximum allowed number of active projectiles.
     * This method also updates the game UI to display the projectile.
     *
     * @param projectile The projectile to be added to the game.
     */
    public static void addProjectile(Projectile projectile) 
    {
        // Retrieve the character that launched the projectile
        Character owner = projectile.getOwner();

        // Ensure the projectile has a valid owner before adding it to the game
        if (owner == null) 
        {
            System.err.println("Projectile owner is null. Cannot add projectile.");
            return; // Exit early to prevent errors
        }

        // Count the number of active projectiles currently owned by this character
        long ownerProjectileCount = activeProjectiles.stream()
            .filter(p -> p.getOwner() == owner)
            .count();

        // Enforce the maximum number of projectiles per player to prevent spam
        if (ownerProjectileCount >= MAX_PROJECTILES_PER_PLAYER) 
        {
            System.out.println(owner.getName() + " cannot launch more than " 
                + MAX_PROJECTILES_PER_PLAYER + " projectiles."); // Debug statement
            return; // Exit early if limit is reached
        }

        // Synchronize access to the projectile list to prevent concurrency issues
        synchronized (activeProjectiles) 
        {
            activeProjectiles.add(projectile); // Add the projectile to the active list
        }

        // Add the projectile's visual representation to the game UI safely
        Platform.runLater(() -> GameApp.root.getChildren().add(projectile.getSprite()));
    }

    /**
     * Removes a projectile from the game, both from the active list and the UI.
     * This ensures that projectiles do not linger after being destroyed or leaving the screen.
     *
     * @param projectile The projectile to be removed from the game.
     */
    static void removeProjectile(Projectile projectile) 
    {
        // Remove the projectile from the active list
        activeProjectiles.remove(projectile); 

        // Ensure the projectile is removed from the game UI safely using the JavaFX thread
        Platform.runLater(() ->
        {
            GameApp.root.getChildren().remove(projectile.getSprite());
        });
    }

    /**
     * Retrieves the current list of active projectiles.
     * 
     * @return A list containing all active projectiles in the game.
     */
    public static List<Projectile> getActiveProjectiles()
    {
         

        return activeProjectiles; // Return the list of active projectiles.
        
    }
    
} 
