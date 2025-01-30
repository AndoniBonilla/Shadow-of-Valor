package FightingGame;

// Import necessary Java and JavaFX packages
import java.util.ArrayList; // For creating resizable arrays to store character data
import java.util.List; // For managing collections of characters
import javafx.scene.paint.Color; // For setting the colors of characters

/**
 * Class to manage and provide the list of playable characters.
 */
public class CharacterRoster {

    /**
     * Returns a list of playable characters with their attributes.
     *
     * @return a List of Character objects representing the roster of playable characters.
     */
    public static List<Character> getCharacters() {
        // Initialize an empty list to store character objects.
        List<Character> characters = new ArrayList<>();

        // Create and initialize each character with their specific attributes.

        // Blaze: A character with the ability to apply a burning effect to opponents.
        Character blaze = new Character(
            "Blaze", // Name of the character
            440, // Maximum health
            9, // Attack power
            6, // Defense power
            "Flame Rush", // Name of the special ability
            Color.DARKRED, // Color representing the character
            100, // Initial X-coordinate
            200, // Initial Y-coordinate
            "" // Passive ability description (none)
        );
        // Special ability description: Blaze's attacks make him do more damage. 

        // Aqua: A defensive character with a speed boost ability.
        Character aqua = new Character(
            "Aqua", // Name of the character
            520,  // Maximum health
            8, // Attack power
            5, // Defense power 
            "Speed Surge", // Name of the special ability
            Color.DARKBLUE, // Color representing the character
            120, // Inital X-coordinate
            200, // Inital Y-coordinate
            "" //  Passive ability description(none)
        );
        

        // Volt: A fast character with a chance to stun opponents.
        Character volt = new Character(
            "Volt", // Name of the character
            490, // Maxium Health
            7, // Attack power
            5, // Defense power 
            "Electric Burst", // Name of the special ability
            Color.GOLD, // Color representing the character
            140, // Inital X-coordinate
            200, // Inital Y-coordinate
            "" //  Passive ability description(none)
        );
        // Special ability description: Volt moves faster than other characters and temporarily opponents

        // Amber: A character with fast attacks and ampfilier ability
        Character amber = new Character(
            "Amber", // Name of the character
            510, // Maxium Health
            8,  // Attack power
            5, // Defense power 
            "Blazing Punch", // Name of the special ability
            Color.ORANGERED, // Color representing the character
            160, // Inital X-coordinate
            200, // Inital Y-coordinate
            ""  //  Passive ability description(none)
        );
        // Special ability description: Amber's attacks are boosted for 5 seconds

        // Shade: A teleporting character with damage bonuses from teleports.
        Character shade = new Character(
            "Shade", // Name of the character
            495, // Maxium Health
            7, // Attack power
            5, // Defense power 
            "Shadow Step", // Name of the special ability
            Color.MEDIUMPURPLE, // Color representing the character
            180, // Inital X-coordinate
            200, // Inital Y-coordinate
            "" //  Passive ability description(none)
        );
        // Special ability description: Shade teleports with his special ability

        // Terra: A nature based character with healing mechanics.
        Character terra = new Character(
            "Terra", // Name of the character
            535,  // Maxium Health
            8, // Attack power
            6, // Defense power 
            "Nature's Embrace", // Name of the special ability
            Color.DARKGREEN, // Color representing the character
            200, // Inital X-coordinate
            200, // Inital Y-coordinate
            "" //  Passive ability description(none)
        );
        // Special ability description: Terra's special ability allows her to heal a piece of her health. 

        // Night: A character that is based on attacking with his special ability. 
        Character night = new Character(
            "Night", // Name of the character
            500, // Maxium Health
            8, // Attack power
            5, // Defense power 
            "Shadow Strike", // Name of the special ability
            Color.DARKSLATEGRAY, // Color representing the character
            220, // Inital X-coordinate
            200, // Inital Y-coordinate
            "" //  Passive ability description(none)
        );
        // Special ability description: Night makes himself stronger for 3 seconds. 

        // Boulder: A defensive tank character with a temporary defense boost ability.
        Character boulder = new Character(
            "Boulder",  // Name of the character
            530,// Maxium Health
            8, // Attack power
            5, // Defense power 
            "Earthquake",  // Name of the special ability
            Color.SADDLEBROWN,  // Color representing the character
            240,  // Inital X-coordinate
            200,  // Inital Y-coordinate
            "" //  Passive ability description(none)
        );
        // Special ability description: Boulder throws an earthquake that stuns enemies and he strengths himself by increasing his attack power and speed.  

        // Set up character opponents for gameplay.
        blaze.setOpponent(aqua); // Blaze's opponent is Aqua.
        aqua.setOpponent(blaze); // Aqua's opponent is Blaze.

        volt.setOpponent(amber); // Volt's opponent is Amber.
        amber.setOpponent(volt); // Amber's opponent is Volt.

        shade.setOpponent(terra); // Shade's opponent is Terra.
        terra.setOpponent(shade); // Terra's opponent is Shade.

        night.setOpponent(boulder); // Night's opponent is Boulder.
        boulder.setOpponent(night); // Boulder's opponent is Night.

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
