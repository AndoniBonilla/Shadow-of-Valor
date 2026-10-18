package FightingGame;

import java.util.ArrayList;
// Provides a dynamic array implementation for managing collections of projectiles.
import java.util.Iterator;
// Allows traversal of collections while enabling safe removal of elements during iteration.
import java.util.List;
// Defines the List interface for ordered collections.

import javafx.scene.layout.Pane;
// Container whose children are manipulated when projectiles are added or removed.

/**
 * Manages the lifecycle of projectiles: adding, removing, and collision detection. (U3)
 * Converted from a static utility class to an instance class owned by GameSession.
 * No static state remains, so multiple matches never share projectile lists.
 * Synchronized blocks removed because all methods are called from the FX thread. (T2)
 */
public class ProjectileManager
{

    private static final int MAX_PROJECTILES_PER_PLAYER = 6;
    // Maximum number of active projectiles allowed per owner.

    private final List<Projectile> activeProjectiles = new ArrayList<>();
    // Instance list; not static so each GameSession has its own. (U3)

    private final Pane root;
    // Scene-graph root; projectile sprites are added to and removed from this pane.

    /**
     * Creates a manager bound to the given pane. (U3)
     *
     * @param root The pane where projectile sprites live.
     */
    public ProjectileManager(Pane root)
    {
        this.root = root;
    }

    /**
     * Adds a projectile to the scene and tracks it.
     * Called from the FX thread (game loop); no synchronized or Platform.runLater needed. (T2)
     *
     * @param projectile The projectile to be added.
     */
    public void addProjectile(Projectile projectile)
    {
        Character owner = projectile.getOwner();

        if (owner == null)
        {
            System.err.println("Projectile owner is null. Cannot add projectile.");
            return;
        }

        // Count projectiles currently owned by this player.
        long ownerProjectileCount = activeProjectiles.stream()
            .filter(p -> p.getOwner() == owner)
            .count();

        // Enforce max projectiles per player.
        if (ownerProjectileCount >= MAX_PROJECTILES_PER_PLAYER)
        {
            System.out.println(owner.getName() + " cannot launch more than "
                + MAX_PROJECTILES_PER_PLAYER + " projectiles.");
            return;
        }

        activeProjectiles.add(projectile); // Track in the instance list.
        root.getChildren().add(projectile.getSprite()); // Add sprite to scene; on FX thread.
    }

    /**
     * Handles a collision between two projectiles.
     *
     * @param p1 The first projectile.
     * @param p2 The second projectile.
     */
    public void handleProjectileCollision(Projectile p1, Projectile p2)
    {
        if (p1.collidesWith(p2))
        {
            System.out.println("Collision detected between two projectiles.");

            // The larger projectile destroys the smaller one.
            if (p1.getSize() > p2.getSize())
            {
                System.out.println("Projectile " + p1 + " destroyed projectile " + p2 + ".");
                removeProjectile(p2); // Smaller projectile is destroyed.
            }
            else if (p1.getSize() < p2.getSize())
            {
                System.out.println("Projectile " + p2 + " destroyed projectile " + p1 + ".");
                removeProjectile(p1); // Smaller projectile is destroyed.
            }
            else
            {
                System.out.println("Both projectiles are the same size and cancel each other out.");
                removeProjectile(p1); // Equal size: both are removed.
                removeProjectile(p2);
            }
        }
    }

    /**
     * Handles a collision between a projectile and a character.
     * The iterator parameter allows safe removal during iteration. (G4: wired in Phase 4)
     *
     * @param projectile The projectile involved in the collision.
     * @param target The character that was hit.
     * @param iterator The iterator for active projectiles.
     */
    public void handleCollision(Projectile projectile, Character target, Iterator<Projectile> iterator)
    {
        if (projectile.getBounds() != null &&
            projectile.getBounds().intersects(target.getCharacterSprite().getBoundsInParent()))
        {
            target.takeDamage((int) projectile.getDamage(), projectile);
            System.out.println(target.getName() + " took damage. Remaining health: " + target.getHealth());

            // Remove the projectile sprite from the pane (on FX thread; no runLater needed). (T2)
            root.getChildren().remove(projectile.getSprite());
            iterator.remove(); // Safely remove from the list during iteration.
        }
    }

    /**
     * Removes a projectile from the active list and from the scene.
     * Called from the FX thread; no Platform.runLater needed. (T2)
     *
     * @param projectile The projectile to be removed.
     */
    public void removeProjectile(Projectile projectile)
    {
        activeProjectiles.remove(projectile); // Remove from tracking list.
        root.getChildren().remove(projectile.getSprite()); // Remove sprite from scene.
    }

    /**
     * Returns the live list of active projectiles for iteration in the game loop.
     * Callers that iterate while removing must use an Iterator to avoid CME.
     *
     * @return The list of active projectiles.
     */
    public List<Projectile> getActiveProjectiles()
    {
        return activeProjectiles;
    }

}
