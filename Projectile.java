package FightingGame;
 

import javafx.scene.Node;    
//A container that organizes and displays multiple `Node` elements in a 2D layout.

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
    
    /**
     * Constructs a new Projectile object and initializes its properties.
     * The projectile is assigned an owner, speed, size, and a calculated trajectory.
     * It is also given a graphical representation (a circle) and a movement direction.
     *
     * @param owner          The character who launched the projectile.
     * @param startX         The starting X-coordinate of the projectile.
     * @param startY         The starting Y-coordinate of the projectile.
     * @param targetX        The X-coordinate the projectile is moving toward.
     * @param targetY        The Y-coordinate the projectile is moving toward.
     * @param size           The radius (size) of the projectile.
     * @param chargeDuration The amount of time the projectile was charged, affecting its speed.
     */
    public Projectile(Character owner, double startX, double startY, double targetX, double targetY, double size, double chargeDuration) 
    {
        // Ensure that the projectile has a valid owner (prevents null references)
        if (owner == null) 
        {
            throw new IllegalArgumentException("Error: Owner cannot be null when creating a projectile.");
        }

        // Assign owner reference
        this.owner = owner;

        // Assign projectile size
        this.size = size;

        // Calculate and assign the projectile's speed based on charge duration
        this.speed = calculateSpeed(chargeDuration);



        // Create a circular visual representation (sprite) for the projectile
        this.sprite = new Circle(size);  
        this.sprite.setFill(owner.getColor()); // Set the projectile's color to match the owner
        this.sprite.setLayoutX(startX); // Set initial X position
        this.sprite.setLayoutY(startY); // Set initial Y position

        // Calculate the distance to the target position for direction normalization
        double distance = Math.sqrt(Math.pow(targetX - startX, 2) + Math.pow(targetY - startY, 2));

        // Determine the direction vector by normalizing the movement of the projectile
        this.directionX = (targetX - startX) / distance;
        this.directionY = (targetY - startY) / distance;
    }


    /**
     * Calculates the speed of the projectile based on the charge duration.
     *
     * @param chargeDuration The duration the projectile is charged.
     * @return The calculated speed.
     */
    public double calculateSpeed(double chargeDuration) 
    {
        double baseSpeed = 85.0; // base speed
        double calculatedSpeed = baseSpeed + (chargeDuration / 300.0); // Calculation of the speed of the projectile based on charge duration
        return Math.min(calculatedSpeed, 200.0); // Cap the speed at 200.0
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
     * Retrieves the sprite (visual representation) of the projectile.
     *
     * @return The sprite as a Node object.
     */
    public Node getSprite() 
    {
        return this.sprite; // Return the sprite associated with this projectile
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

}
