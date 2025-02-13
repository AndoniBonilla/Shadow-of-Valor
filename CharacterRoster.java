package FightingGame;

// Import necessary Java and JavaFX packages
import java.util.ArrayList; // For creating resizable arrays to store character data
import java.util.List; // For managing collections of characters
import javafx.scene.paint.Color; // For setting the colors of characters

/**
 * Class to manage and provide the list of playable characters.
 */
public class CharacterRoster 
{

    /**
     * Returns a list of playable characters with their attributes.
     *
     * @return a List of Character objects representing the roster of playable characters.
     */
	public static List<Character> getCharacters() 
	{
	    // Initialize an empty list to store character objects.
	    List<Character> characters = new ArrayList<>();

	    // Blaze: A character with the ability to apply a burning effect to opponents.
	    Character blaze = new Character(
	        "Blaze", // Name of the character
	        800, // Maximum health
	        68, // Attack power
	        68, // Defense power
	        "Flame Rush", // Name of the special ability
	        Color.DARKRED, // Color representing the character
	        100, // Starting position for the character on the X-coordinate
	        200
	    );

	    // Aqua: A defensive character with a speed boost ability.
	    Character aqua = new Character(
	        "Aqua", // Name of the character
	        1093, // Maximum health
	        48, // Attack power
	        74, // Defense power
	        "Speed Surge", // Name of the special ability
	        Color.DARKBLUE, // Color representing the character
	        120, // Starting position for the character on the X-coordinate
	        200
	    );

	    // Volt: A fast character with a chance to stun opponents.
	    Character volt = new Character(
	        "Volt", // Name of the character
	        624, // Maximum health
	        93, // Attack power
	        35, // Defense power
	        "Electric Burst", // Name of the special ability
	        Color.GOLD, // Color representing the character
	        140, // Starting position for the character on the X-coordinate
	        200
	    );

	    // Amber: A character with fast attacks and amplifier ability.
	    Character amber = new Character(
	        "Amber", // Name of the character
	        900, // Maximum health
	        60, // Attack power
	        60, // Defense power
	        "Blazing Punch", // Name of the special ability
	        Color.ORANGERED, // Color representing the character
	        160, // Starting position for the character on the X-coordinate
	        200
	    );

	    // Shade: A teleporting character with damage bonuses from teleports.
	    Character shade = new Character(
	        "Shade", // Name of the character
	        687, // Maximum health
	        87, // Attack power
	        40, // Defense power
	        "Shadow Step", // Name of the special ability
	        Color.MEDIUMPURPLE, // Color representing the character
	        180, // Starting position for the character on the X-coordinate
	        200
	    );

	    // Terra: A nature-based character with healing mechanics.
	    Character terra = new Character(
	        "Terra", // Name of the character
	        1054, // Maximum health
	        40, // Attack power
	        68, // Defense power
	        "Nature's Embrace", // Name of the special ability
	        Color.DARKGREEN, // Color representing the character
	        200, // Starting position for the character on the X-coordinate
	        200
	    );

	    // Night: A character focused on attacking with his special ability.
	    Character night = new Character(
	        "Night", // Name of the character
	        750, // Maximum health
	        80, // Attack power
	        50, // Defense power
	        "Shadow Strike", // Name of the special ability
	        Color.DARKSLATEGRAY, // Color representing the character
	        220, // Starting position for the character on the X-coordinate
	        200
	    );

	    // Boulder: A defensive tank character with a temporary defense boost ability.
	    Character boulder = new Character(
	        "Boulder", // Name of the character
	        1125, // Maximum health
	        35, // Attack power
	        80, // Defense power
	        "Earthquake", // Name of the special ability
	        Color.SADDLEBROWN, // Color representing the character
	        240, // Starting position for the character on the X-coordinate
	        200
	    );

	    // Set up character opponents for gameplay.
	    blaze.setOpponent(aqua);
	    aqua.setOpponent(blaze);

	    volt.setOpponent(amber);
	    amber.setOpponent(volt);

	    shade.setOpponent(terra);
	    terra.setOpponent(shade);

	    night.setOpponent(boulder);
	    boulder.setOpponent(night);

	    // Add all characters to the roster list.
	    characters.add(blaze);
	    characters.add(aqua);
	    characters.add(volt);
	    characters.add(amber);
	    characters.add(shade);
	    characters.add(terra);
	    characters.add(night);
	    characters.add(boulder);

	    // Return the completed list of characters.
	    return characters;
	}

}
