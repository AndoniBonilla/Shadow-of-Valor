package FightingGame;

import java.util.List;    
//Provides the List interface, used for managing collections of objects like players in the game.c


public class RoundManager 
{
    /**
     * Constructor for RoundManager.
     *
     * @param totalRounds Total number of rounds in the game.
     * @param players     List of two players participating in the game.
     * @throws IllegalArgumentException if the players list is null or doesn't contain exactly two players.
     */
	public RoundManager(int totalRounds, List<Character> players) 
	{
	    // Check if the players list is null or does not contain exactly two players.
	    if (players == null || players.size() != 2) 
	    {
	        // Throw an IllegalArgumentException if the condition is true,
	        // indicating that the RoundManager requires exactly two players.
	        throw new IllegalArgumentException("RoundManager requires exactly two players.");
	    }
	} 
    
}
