package FightingGame;

// Import necessary Java and JavaFX packages
import java.util.ArrayList; // For creating resizable arrays to store character specs
import java.util.List; // For managing collections of character specs
import javafx.scene.paint.Color; // For setting the colors of characters

/**
 * Class to manage and provide the list of playable characters.
 */
public class CharacterRoster
{

    /**
     * Immutable spec for one fighter: just data, no JavaFX nodes. (U2)
     * Call createCharacter() when a match actually starts to get a live instance.
     * Separating spec from instance makes mirror matches possible because two fresh
     * Character objects (each with their own sprite) are created from the same spec.
     */
    public record CharacterSpec(
        String name,
        int maxHealth,
        int attackPower,
        int defensePower,
        String specialAbilityName,
        Color color
    )
    {
        /** Builds a fresh Character instance from this spec. (U2) */
        public Character createCharacter()
        {
            // x/y are placeholder values; GameSession repositions sprites before play starts.
            return new Character(name, maxHealth, attackPower, defensePower,
                                 specialAbilityName, color, 0, 0);
        }
    }

    /**
     * Returns the immutable specs for all 8 fighters. (U2)
     * No Character instances — and therefore no JavaFX nodes — are created here.
     * Opponent pairings are intentionally absent; GameSession calls setOpponent() instead.
     *
     * @return a List of CharacterSpec records representing the roster.
     */
    public static List<CharacterSpec> getCharacters()
    {
        List<CharacterSpec> specs = new ArrayList<>();

        // Blaze: aggressive fighter with a temporary attack boost.
        specs.add(new CharacterSpec("Blaze", 800, 68, 68, "Flame Rush", Color.DARKRED));

        // Aqua: defensive fighter with a speed surge ability.
        specs.add(new CharacterSpec("Aqua", 1093, 48, 74, "Speed Surge", Color.DARKBLUE));

        // Volt: fast attacker that can stun opponents.
        specs.add(new CharacterSpec("Volt", 624, 93, 35, "Electric Burst", Color.GOLD));

        // Amber: balanced fighter with a single powerful next-attack boost.
        specs.add(new CharacterSpec("Amber", 900, 60, 60, "Blazing Punch", Color.ORANGERED));

        // Shade: teleporter with a next-attack damage bonus.
        specs.add(new CharacterSpec("Shade", 687, 87, 40, "Shadow Step", Color.MEDIUMPURPLE));

        // Terra: tanky healer with the lowest attack power.
        specs.add(new CharacterSpec("Terra", 1054, 40, 68, "Nature's Embrace", Color.DARKGREEN));

        // Night: stealthy fighter with a invisibility + attack combo.
        specs.add(new CharacterSpec("Night", 750, 80, 50, "Shadow Strike", Color.DARKSLATEGRAY));

        // Boulder: defensive tank with a long stun.
        specs.add(new CharacterSpec("Boulder", 1125, 35, 80, "Earthquake", Color.SADDLEBROWN));

        return specs;
    }

}
