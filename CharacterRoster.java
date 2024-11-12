package FightingGame;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;

public class CharacterRoster 
{
    public static List<Character> getCharacters() 
    {
        List<Character> characters = new ArrayList<>();

        characters.add(new Character("Blaze", 100, 25, 10, "Flame Rush", Color.RED));
        characters.add(new Character("Aqua", 120, 20, 15, "Water Shield", Color.BLUE));
        characters.add(new Character("Volt", 90, 30, 8, "Electric Burst", Color.YELLOW));
        characters.add(new Character("Amber", 110, 22, 12, "Blazing Punch", Color.ORANGE));
        characters.add(new Character("Shade", 95, 20, 10, "Shadow Step", Color.PURPLE));
        characters.add(new Character("Terra", 130, 18, 20, "Nature’s Embrace", Color.GREEN));
        characters.add(new Character("Night", 100, 28, 12, "Shadow Strike", Color.BLACK));
        characters.add(new Character("Boulder", 150, 15, 25, "Stone Wall", Color.BROWN));

        return characters;
    }
}
