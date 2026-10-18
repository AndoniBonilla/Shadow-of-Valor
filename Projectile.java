package FightingGame;

import javafx.geometry.Bounds;
// Bounding box of a scene-graph node; used for collision detection.

import javafx.scene.Node;
// Base type returned by getSprite() so callers don't depend on Circle directly.

import javafx.scene.paint.Color;
// Used to color the circle with the owner's color.

import javafx.scene.shape.Circle;
// The projectile's visual representation: a filled circle of radius size/2.

/**
 * A single projectile launched by a character, moving in a straight line
 * toward the target position at a fixed speed (pixels per second).
 */
public class Projectile
{
    private final Circle sprite;      // Visual circle in the scene graph.
    private final double  speed;      // Movement speed in pixels per second.
    private final double  size;       // Diameter used for collision radius and getDamage().
    private final double  directionX; // Normalized X component of the movement vector.
    private final double  directionY; // Normalized Y component of the movement vector.
    private final Character owner;    // Character that launched this projectile.

    /**
     * Constructs a projectile that travels from (startX, startY) toward (targetX, targetY).
     *
     * @param owner    Character launching the projectile; must not be null.
     * @param startX   Starting layout X-coordinate.
     * @param startY   Starting layout Y-coordinate.
     * @param targetX  Target layout X-coordinate used to compute direction.
     * @param targetY  Target layout Y-coordinate used to compute direction.
     * @param size     Diameter of the circle; drives getDamage() and collision radius.
     * @param speed    Movement speed in pixels per second.
     */
    public Projectile(Character owner, double startX, double startY,
                      double targetX, double targetY, double size, double speed)
    {
        if (owner == null)
        {
            throw new IllegalArgumentException("Projectile owner cannot be null.");
        }
        this.owner = owner;
        this.size  = size;
        this.speed = speed;

        // Build a normalized direction vector; default to rightward if target equals start.
        double dx     = targetX - startX;
        double dy     = targetY - startY;
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length == 0)
        {
            this.directionX = 1.0; // Fallback direction when target == start.
            this.directionY = 0.0;
        }
        else
        {
            this.directionX = dx / length;
            this.directionY = dy / length;
        }

        // Use the owner's color; fall back to gray when color is not yet assigned
        // (e.g., during Character field initialization before the constructor runs).
        Color fill = (owner.getColor() != null) ? owner.getColor() : Color.GRAY;

        this.sprite = new Circle(size / 2, fill); // Radius = size / 2 per spec.
        this.sprite.setLayoutX(startX); // Layout coords so isOutOfBounds() reads correctly.
        this.sprite.setLayoutY(startY);
    }

    /**
     * Returns the character that owns (launched) this projectile.
     */
    public Character getOwner()
    {
        return owner;
    }

    /**
     * Returns the projectile's scene-graph node (a Circle).
     */
    public Node getSprite()
    {
        return sprite;
    }

    /**
     * Advances the projectile along its direction vector.
     *
     * @param deltaTime Seconds elapsed since the last frame.
     */
    public void updatePosition(double deltaTime)
    {
        sprite.setLayoutX(sprite.getLayoutX() + directionX * speed * deltaTime);
        sprite.setLayoutY(sprite.getLayoutY() + directionY * speed * deltaTime);
    }

    /**
     * Returns the bounding box of the sprite in parent-coordinate space.
     * Used by ProjectileManager.handleCollision for intersection checks.
     */
    public Bounds getBounds()
    {
        return sprite.getBoundsInParent();
    }

    /**
     * Circle–circle distance check between this projectile and another.
     *
     * @param other The other projectile.
     * @return True if the two circles overlap.
     */
    public boolean collidesWith(Projectile other)
    {
        double dx       = sprite.getLayoutX() - other.sprite.getLayoutX();
        double dy       = sprite.getLayoutY() - other.sprite.getLayoutY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        double combined = (this.size / 2) + (other.size / 2); // Sum of radii.
        return distance < combined;
    }

    /**
     * Returns the projectile's size (used for collision radius calculations).
     */
    public double getSize()
    {
        return size;
    }

    /**
     * Returns damage proportional to size. Wired into the health system by G4.
     */
    public double getDamage()
    {
        return size * 0.5; // Simple linear scale from projectile size.
    }
}
