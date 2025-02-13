package FightingGame;

import java.util.ArrayList;  
//Provides a dynamic array implementation for managing collections of objects like projectiles.
import java.util.Iterator; 
//Allows traversal of collections while enabling safe removal of elements during iteration.
import java.util.List;

import javafx.application.Platform;        
//Represents a visual element in the JavaFX scene graph, such as characters or projectiles.
import javafx.scene.layout.Pane; 
//A container for organizing and displaying visual elements (nodes) in the JavaFX scene. 
//Defines the List interface for creating ordered collections, commonly used for managing game elements.

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
    public static void addProjectile(Projectile projectile) 
    {
        Character owner = projectile.getOwner();

        if (owner == null) 
        {
            System.err.println("Projectile owner is null. Cannot add projectile.");
            return;
        }

        // Count projectiles for this owner
        long ownerProjectileCount = activeProjectiles.stream()
            .filter(p -> p.getOwner() == owner)
            .count();

        // Enforce max projectiles per player
        if (ownerProjectileCount >= MAX_PROJECTILES_PER_PLAYER) 
        {
            System.out.println(owner.getName() + " cannot launch more than " + MAX_PROJECTILES_PER_PLAYER + " projectiles.");
            return;
        }

        // Add the projectile to the active list
        synchronized (activeProjectiles) 
        {
            activeProjectiles.add(projectile);
        }

        // Add the projectile sprite to the UI
        Platform.runLater(() -> GameApp.root.getChildren().add(projectile.getSprite()));

    }

    /**
     * Handles a collision between two projectiles.
     * @param p1 The first projectile.
     * @param p2 The second projectile.
     */
    public static void handleProjectileCollision(Projectile p1, Projectile p2)
    {
        if (p1.collidesWith(p2)) 
        {
            System.out.println("Collision detected between two projectiles.");

            // Compare projectilfe sizes
            if (p1.getSize() > p2.getSize()) 
            {
                System.out.println("Projectile " + p1 + " destroyed projectile " + p2 + ".");
                removeProjectile(p2); // Smaller projectile is destroyed
            } 
            else if (p1.getSize() < p2.getSize()) 
            {
                System.out.println("Projectile " + p2 + " destroyed projectile " + p1 + ".");
                removeProjectile(p1); // Smaller projectile is destroyed
            } 
            else
            {
                System.out.println("Both projectiles are the same size and cancel each other out.");
                removeProjectile(p1);
                removeProjectile(p2); // Both projectiles are removed
            }
        }
    }


    /**
     * Handles a collision between a projectile and a character.
     * @param projectile The projectile involved in the collision.
     * @param target The character that was hit.
     * @param iterator The iterator for active projectiles.
     */
    public static void handleCollision(Projectile projectile, Character target, Iterator<Projectile> iterator) 
    {
        if (projectile.getBounds() != null &&
            projectile.getBounds().intersects(target.getCharacterSprite().getBoundsInParent()))
        {
            
        	target.takeDamage((int) projectile.getDamage(), projectile);
            System.out.println(target.getName() + " took damage. Remaining health: " + target.getHealth());

            // Remove the projectile from the root pane and active list.
            Pane root = (Pane) projectile.getSprite().getParent();
            if (root != null) 
            {
                root.getChildren().remove(projectile.getSprite());
            }
            iterator.remove(); // Safely remove the projectile using the iterator.
        }
    }

    /**
     * Removes a projectile from the active projectiles list.
     * 
     * @param projectile The projectile to be removed.
     */
    public static void removeFromActiveProjectiles(Projectile projectile) 
    {
        activeProjectiles.remove(projectile); 
        // Remove the projectile from the active list.
    }
    static void removeProjectile(Projectile projectile) 
    {
        activeProjectiles.remove(projectile); // Remove from active list
        Platform.runLater(() ->
        {
            GameApp.root.getChildren().remove(projectile.getSprite()); // Remove from UI
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