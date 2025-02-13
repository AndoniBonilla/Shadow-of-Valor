package FightingGame;
 
import javafx.geometry.Bounds;    
//Represents the bounding box of a node in the JavaFX scene graph.  
//Useful for detecting collisions and determining positioning.
import javafx.scene.Node;
//A base class for all visual elements in the JavaFX scene graph.
//Represents UI components like sprites, projectiles, or character elements.
import javafx.scene.layout.Pane;
//A container that organizes and displays multiple `Node` elements in a 2D layout.
//Commonly used as the root container for the game scene.
import javafx.scene.shape.Circle;
//Represents a circular shape, often used for rendering projectiles, characters' heads, or other circular objects in the game.


public class Projectile 
{
    private Circle sprite; // Visual representation of the projectile as a circle.
    private double speed; // Speed at which the projectile moves.
    private double directionX; // Normalized X-direction of movement.
    private double directionY; // Normalized Y-direction of movement.
    private Character owner; // Reference to the character that launched the projectile.
    double size; // Size (radius) of the projectile.
    private double damage; // Stores the calculated damage of the projectile.
    private double targetX; // Target X-coordinate for the projectile.
    private double targetY; // Target Y-coordinate for the projectile.
    private double startX; // Starting X-coordinate of the projectile.
    private double startY; // Starting Y-coordinate of the projectile.

    /**
     * Constructs a new Projectile.
     *
     * @param owner          The character launching the projectile.
     * @param startX         The starting X-coordinate of the projectile.
     * @param startY         The starting Y-coordinate of the projectile.
     * @param targetX        The target X-coordinate for the projectile.
     * @param targetY        The target Y-coordinate for the projectile.
     * @param size           The size (radius) of the projectile.
     * @param chargeDuration The duration of the button press, used to calculate size and speed.
     */
    public Projectile(Character owner, double startX, double startY, double targetX, double targetY, double size, double chargeDuration) 
    {
        if (owner == null) 
        {
            throw new IllegalArgumentException("Error: Owner cannot be null when creating a projectile.");
        }
        this.owner = owner;
        this.startX = startX;
        this.startY = startY;
        this.targetX = targetX;
        this.targetY = targetY;

        this.size = size;
        this.speed = calculateSpeed(chargeDuration);
        this.damage = calculateDamage();

        // Initialize the sprite as a circle with a given size and color.
        this.sprite = new Circle(size);
        this.sprite.setFill(owner.getColor());
        this.sprite.setLayoutX(startX);
        this.sprite.setLayoutY(startY);

        // Calculate normalized direction vector for movement.
        double distance = Math.sqrt(Math.pow(targetX - startX, 2) + Math.pow(targetY - startY, 2));
        this.directionX = (targetX - startX) / distance;
        this.directionY = (targetY - startY) / distance;
      
    }

    /**
     * Moves the projectile based on its speed and direction.
     */
    public void move()
    {
        double deltaX = targetX - startX;
        double deltaY = targetY - startY;
        double magnitude = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        double moveX = (deltaX / magnitude) * speed;
        double moveY = (deltaY / magnitude) * speed;

        sprite.setLayoutX(sprite.getLayoutX() + moveX);
        sprite.setLayoutY(sprite.getLayoutY() + moveY);
    }

    /**
     * Checks if the projectile is still within the game boundaries.
     *
     * @return True if the projectile is within bounds, otherwise false.
     */
    public boolean isInBounds() 
    {
        double x = this.sprite.getLayoutX();
        double y = this.sprite.getLayoutY();
        return x >= GameApp.BOUNDARY_LEFT && x <= GameApp.BOUNDARY_RIGHT
                && y >= GameApp.BOUNDARY_TOP && y <= GameApp.BOUNDARY_BOTTOM;
    }

    /**
     * Handles the logic when the projectile hits a character.
     *
     * @param target The character hit by the projectile.
     */
    public void onHit(Character target) 
    {
        // Apply damage and effects to the target.
        double damage = this.calculateDamage();
        System.out.println("Projectile dealt " + damage + " damage to " + target.getName());
        target.takeDamage(damage, this);

        // Mark the projectile as hit and remove it from the game.
        this.hit();
        Pane root = (Pane) this.sprite.getParent();
        if (root != null) {
            root.getChildren().remove(this.sprite);
        }
        ProjectileManager.removeFromActiveProjectiles(this);
    }

    /**
     * Calculates the speed of the projectile based on the charge duration.
     *
     * @param chargeDuration The duration the projectile is charged.
     * @return The calculated speed.
     */
    public double calculateSpeed(double chargeDuration) 
    {
        double baseSpeed = 85.0;
        double calculatedSpeed = baseSpeed + (chargeDuration / 300.0);
        return Math.min(calculatedSpeed, 200.0); // Cap the speed at 200.0
    }

    /**
     * Calculates the damage of the projectile based on its size and speed.
     *
     * @return The calculated damage value.
     */
    public double calculateDamage()
    {
        double baseDamage = 5.0; // Minimum damage.
        double sizeMultiplier = size / 10.0; // Scale with projectile size.
        double speedMultiplier = speed / 10.0; // Scale with projectile speed.
        double ownerAttackPower = owner.getAttackPower(); // Owner's attack power.

        // Include target's defense reduction if available.
        Character target = owner.getOpponent();
        double targetDefense = target != null ? target.getDefensePower() * target.getDefenseMultiplier() : 0;

        // Calculate the final damage value.
        return baseDamage + (ownerAttackPower * sizeMultiplier * speedMultiplier) - (targetDefense * 0.5);
    }

    /**
     * Updates the position of the projectile based on the elapsed time.
     *
     * @param deltaTime Time elapsed since the last update.
     */
    public void updatePosition(double deltaTime) 
    {
        double distanceX = directionX * speed * deltaTime;
        double distanceY = directionY * speed * deltaTime;

        sprite.setLayoutX(sprite.getLayoutX() + distanceX);
        sprite.setLayoutY(sprite.getLayoutY() + distanceY);
    }

    /**
     * Checks for collisions between this projectile and another projectile.
     *
     * @param other The other projectile to check collision against.
     * @return True if a collision is detected, otherwise false.
     */
    public boolean collidesWith(Projectile other)
    {
        if (this.getSprite() == null || other.getSprite() == null)
        {
            System.out.println("Collision check failed: one or both sprites are null.");
            return false;
        }

        double dx = this.getSprite().getLayoutX() - other.getSprite().getLayoutX();
        double dy = this.getSprite().getLayoutY() - other.getSprite().getLayoutY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        double collisionDistance = (this.getSize() + other.getSize()) / 2;

        System.out.printf("Collision Check -> Distance: %.2f, Collision Distance: %.2f%n", distance, collisionDistance);

        return distance < collisionDistance;
    }

    /**
     * Marks the projectile as hit, indicating it should no longer interact with other objects.
     * This is used during collision handling.
     */
    public void hit() 
    {
        System.out.println("Projectile hit!"); // Log that the projectile has been hit
    }

    /**
     * Retrieves the size (radius) of the projectile.
     *
     * @return The size of the projectile.
     */
    public double getSize() 
    {
        return this.size; // Return the projectile's size attribute
    }

    /**
     * Retrieves the sprite (visual representation) of the projectile.
     *
     * @return The sprite as a Node object.
     */
    public Node getSprite() 
    {
        return this.sprite; // Return the sprite associated with this projectile
    }  

    /**
     * Retrieves the calculated damage value of the projectile.
     *
     * @return The damage dealt by the projectile.
     */
    public double getDamage() 
    {
        return this.damage; // Return the damage value of the projectile
    }

    /**
     * Retrieves the owner (character that launched the projectile) of the projectile.
     *
     * @return The owner character of the projectile.
     */
    
    public Character getOwner() 
    {
        return owner; // Return the reference to the character that launched the projectile
    }

    /**
     * Retrieves the bounds of the projectile's sprite in the game scene.
     * This is used for collision detection.
     *
     * @return The bounds of the projectile's sprite or null if the sprite is not initialized.
     */
    public Bounds getBounds() 
    {
        if (sprite != null) 
        { // Check if the sprite exists
            return sprite.getBoundsInParent(); // Return the bounds of the sprite
        }
        return null; // Return null if the sprite is not initialized
    }

    
}
