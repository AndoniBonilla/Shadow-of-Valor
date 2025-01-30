package FightingGame;

import javafx.application.Platform;      
//Used to run UI updates on the JavaFX Application Thread, ensuring thread-safe modifications to UI elements.

import javafx.geometry.Bounds; 
//Represents the boundaries of a node, used for collision detection and positioning.

import java.util.Iterator; 
//Allows traversal of collections while enabling safe removal of elements during iteration.

import javafx.scene.Node; 
//Represents a visual element in the JavaFX scene graph, such as characters or projectiles.

import javafx.scene.layout.Pane; 
//A container for organizing and displaying visual elements (nodes) in the JavaFX scene.

import java.util.ArrayList; 
//Provides a dynamic array implementation for managing collections of objects like projectiles.

import java.util.List; 
//Defines the List interface for creating ordered collections, commonly used for managing game elements.

/**
 * Manages the lifecycle of projectiles in the game, including movement, collisions, and visual updates.
 */
public class ProjectileManager {

    private static final int MAX_PROJECTILES_PER_PLAYER = 6; 
    // Maximum number of active projectiles allowed.

    private static final List<Projectile> activeProjectiles = new ArrayList<>(); 
    // Tracks all active projectiles in the game.

    /**
     * Adds a projectile to the scene and tracks it.
     * @param projectile The projectile to be added.
     */
    public static void addProjectile(Projectile projectile) {
        Character owner = projectile.getOwner();

        if (owner == null) {
            System.err.println("Projectile owner is null. Cannot add projectile.");
            return;
        }

        // Count projectiles for this owner
        long ownerProjectileCount = activeProjectiles.stream()
            .filter(p -> p.getOwner() == owner)
            .count();

        // Enforce max projectiles per player
        if (ownerProjectileCount >= MAX_PROJECTILES_PER_PLAYER) {
            System.out.println(owner.getName() + " cannot launch more than " + MAX_PROJECTILES_PER_PLAYER + " projectiles.");
            return;
        }

        // Add the projectile to the active list
        synchronized (activeProjectiles) {
            activeProjectiles.add(projectile);
        }

        // Add the projectile sprite to the UI
        Platform.runLater(() -> GameApp.root.getChildren().add(projectile.getSprite()));

        System.out.println("Projectile added: " + projectile);
    }



    public static boolean isDuplicate(Projectile projectile) {
        synchronized (activeProjectiles) {
            for (Projectile active : activeProjectiles) {
                if (active.equals(projectile)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Updates all active projectiles, checking movement and collisions.
     * @param root The root pane containing all projectiles.
     * @param players The list of characters in the game.
     */
    public static void updateProjectiles(Pane root, List<Character> players) {
        List<Projectile> toRemove = new ArrayList<>(); // Store projectiles for removal.

        synchronized (activeProjectiles) {
            for (Projectile p1 : activeProjectiles) {
                // Move the projectile
                p1.move();

                // Check for collisions with players
                for (Character player : players) {
                    if (p1.getOwner() != player && checkCollision(player.getCharacterSprite(), p1.getSprite())) {
                        // Apply damage to the player
                        double damage = p1.calculateDamage();
                        player.takeDamage(damage, p1);

                        // Optionally print collision and damage details
                        System.out.println(player.getName() + " was hit by a projectile! Took " + damage + " damage.");

                        // Apply effects if needed
                        applyProjectileEffects(p1, player);

                        // Mark projectile for removal
                        toRemove.add(p1);
                        break; // Exit loop to avoid duplicate collisions for this projectile
                    }
                }

                // Remove out-of-bounds projectiles
                if (!p1.isInBounds()) {
                    System.out.println("Projectile out of bounds and removed: " + p1);
                    toRemove.add(p1);
                }
            }

            // Remove all marked projectiles from active list and UI
            activeProjectiles.removeAll(toRemove);
            Platform.runLater(() -> toRemove.forEach(proj -> root.getChildren().remove(proj.getSprite())));
        }
    }

    



    private static boolean checkCollision(Node characterSprite, Node projectileSprite) {
        Bounds characterBounds = characterSprite.getBoundsInParent();
        Bounds projectileBounds = projectileSprite.getBoundsInParent();
        boolean collision = characterBounds.intersects(projectileBounds);

        System.out.println("Collision Detection:");
        System.out.println("Character Bounds: " + characterBounds);
        System.out.println("Projectile Bounds: " + projectileBounds);
        System.out.println("Collision Result: " + collision);

        return collision;
    }




	// Modularized method for projectile effects.
    public static void applyProjectileEffects(Projectile projectile, Character target) {
        if (projectile.isBurning()) {
            target.applyBurningEffect(); // Assume this handles a burning status effect.
            System.out.println(target.getName() + " is burning!");
        }
        if (projectile.isLifesteal()) {
            double healAmount = projectile.calculateDamage() / 2.0;
            projectile.getOwner().heal(healAmount);
            System.out.println(projectile.getOwner().getName() + " healed for " + healAmount + " due to lifesteal!");
        }
    }



    /**
     * Handles a collision between two projectiles.
     * @param p1 The first projectile.
     * @param p2 The second projectile.
     */
    public static void handleProjectileCollision(Projectile p1, Projectile p2) {
        if (p1.collidesWith(p2)) {
            System.out.println("Collision detected between two projectiles.");

            // Compare projectilfe sizes
            if (p1.getSize() > p2.getSize()) {
                System.out.println("Projectile " + p1 + " destroyed projectile " + p2 + ".");
                removeProjectile(p2); // Smaller projectile is destroyed
            } else if (p1.getSize() < p2.getSize()) {
                System.out.println("Projectile " + p2 + " destroyed projectile " + p1 + ".");
                removeProjectile(p1); // Smaller projectile is destroyed
            } else {
                System.out.println("Both projectiles are the same size and cancel each other out.");
                removeProjectile(p1);
                removeProjectile(p2); // Both projectiles are removed
            }
        }
    }

    // Helper method to remove a projectile
    static void removeProjectile(Projectile projectile) {
        activeProjectiles.remove(projectile); // Remove from active list
        Platform.runLater(() -> {
            GameApp.root.getChildren().remove(projectile.getSprite()); // Remove from UI
        });
        System.out.println("Projectile removed: " + projectile);
    }


    /**
     * Handles a collision between a projectile and a character.
     * @param projectile The projectile involved in the collision.
     * @param target The character that was hit.
     * @param iterator The iterator for active projectiles.
     */
    public static void handleCollision(Projectile projectile, Character target, Iterator<Projectile> iterator) {
        if (projectile.getBounds() != null &&
            projectile.getBounds().intersects(target.getCharacterSprite().getBoundsInParent())) {
            
        	target.takeDamage((int) projectile.getDamage(), projectile);
            System.out.println(target.getName() + " took damage. Remaining health: " + target.getHealth());

            // Remove the projectile from the root pane and active list.
            Pane root = (Pane) projectile.getSprite().getParent();
            if (root != null) {
                root.getChildren().remove(projectile.getSprite());
            }
            iterator.remove(); // Safely remove the projectile using the iterator.
        }
    }

    /**
     * Determines whether a player is allowed to launch another projectile.
     * 
     * @param player The player attempting to launch a projectile.
     * @return True if the player can launch more projectiles, otherwise false.
     */
    public static boolean canLaunchProjectile(Character player) {
        synchronized (activeProjectiles) {
            // Count the projectiles currently owned by the player.
            long activeProjectilesCount = activeProjectiles.stream()
                .filter(p -> p.getOwner() == player) // Filter projectiles owned by the given player.
                .count();
            // Check if the count is less than the maximum allowed.
            return activeProjectilesCount < MAX_PROJECTILES_PER_PLAYER; 
        }
    }

    /**
     * Removes all active projectiles from the game scene and the active list.
     * 
     * @param root The root pane containing the projectiles.
     */
    public static void clearAllProjectiles(Pane root) {
        // Iterate over all active projectiles.
        for (Projectile projectile : activeProjectiles) {
            root.getChildren().remove(projectile.getSprite()); 
            // Remove the projectile's visual representation from the scene.
        }
        activeProjectiles.clear(); 
        // Clear the active projectile list.
        System.out.println("All projectiles cleared."); // Log the operation.
    }

    /**
     * Adds a projectile to the active projectiles list if it does not already exist.
     * 
     * @param projectile The projectile to be added.
     */
    public static void addToActiveProjectiles(Projectile projectile) {
        synchronized (activeProjectiles) {
            // Check if the projectile is already in the list.
            if (!activeProjectiles.contains(projectile)) {
                activeProjectiles.add(projectile); // Add the projectile to the active list.
                System.out.println("Projectile added: " + projectile); // Log the addition.
            } else {
                System.err.println("Duplicate projectile detected: " + projectile); 
                // Log an error if the projectile already exists in the list.
            }
        }
    }

    /**
     * Removes a projectile from the active projectiles list.
     * 
     * @param projectile The projectile to be removed.
     */
    public static void removeFromActiveProjectiles(Projectile projectile) {
        activeProjectiles.remove(projectile); 
        // Remove the projectile from the active list.
    }

    /**
     * Retrieves the current list of active projectiles.
     * 
     * @return A list containing all active projectiles in the game.
     */
    public static List<Projectile> getActiveProjectiles() {
        return activeProjectiles; // Return the list of active projectiles.
    }
} 