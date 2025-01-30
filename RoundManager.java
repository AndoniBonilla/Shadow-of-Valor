package FightingGame;

// Import necessary JavaFX and utility classes
//Import necessary classes for managing lists and UI elements
import java.util.List; 
//Provides the List interface, used for managing collections of objects like players in the game.

import javafx.scene.control.Alert; 
//Provides the Alert class for displaying pop-up dialog boxes, 
//such as notifications for the end of a round or declaring the final winner.


/**
 * Manages the rounds in the game, including timing, player states, and determining winners.
 */
public class RoundManager {
    private int totalRounds; // Total number of rounds in the game
    private int currentRound; // The current round number
    private int player1Wins; // Tracks Player 1's round wins
    private int player2Wins; // Tracks Player 2's round wins
    private List<Character> players; // List of players participating in the game (expected size: 2)
    private int timeRemaining; // Remaining time for the current round

    /**
     * Constructor for RoundManager.
     *
     * @param totalRounds Total number of rounds in the game.
     * @param players     List of two players participating in the game.
     * @throws IllegalArgumentException if the players list is null or doesn't contain exactly two players.
     */
    public RoundManager(int totalRounds, List<Character> players) {
        if (players == null || players.size() != 2) {
            throw new IllegalArgumentException("RoundManager requires exactly two players.");
        }
        this.totalRounds = totalRounds; // Initialize the total number of rounds
        this.currentRound = 0; // Start at round 0
        this.player1Wins = 0; // Initialize Player 1's wins to 0
        this.player2Wins = 0; // Initialize Player 2's wins to 0
        this.players = players; // Assign the players list
        this.timeRemaining = 200; // Initialize the round timer to 200 seconds
    }

    /**
     * Resets the state for a new round, including restoring health and clearing projectiles.
     *
     * @param player1 The first player.
     * @param player2 The second player.
     */
    private void resetRound(Character player1, Character player2) {
        player1.restoreHealthToFull(); // Fully restore Player 1's health
        player2.restoreHealthToFull(); // Fully restore Player 2's health
        ProjectileManager.clearAllProjectiles(GameApp.root); // Clear all active projectiles from the arena
    }

    /**
     * Starts a new round by resetting the game state and refreshing the UI.
     *
     * @param player1 The first player.
     * @param player2 The second player.
     */
    public void startNewRound(Character player1, Character player2) {
        System.out.println("Starting Round " + (currentRound + 1) + "..."); // Log the start of the new round
        resetRound(player1, player2); // Reset the round state
        timeRemaining = 200; // Reset the round timer
        GameApp.updateBars(player1, player2); // Update the UI elements like health and shield bars
    }

    /**
     * Ends the current round and updates the game state.
     *
     * @param winner The player who won the round.
     */
    public void endRound(Character winner) {
        if (winner == players.get(0)) {
            player1Wins++; // Increment Player 1's win count if they are the winner
        } else if (winner == players.get(1)) {
            player2Wins++; // Increment Player 2's win count if they are the winner
        }

        currentRound++; // Move to the next round
        System.out.println("Current Round: " + currentRound + ", Total Rounds: " + totalRounds);

        if (isGameOver()) {
            displayFinalWinner(); // If the game is over, display the final winner
        } else {
            startNewRound(players.get(0), players.get(1)); // Otherwise, start a new round
        }
    }

    /**
     * Determines the winner of the round when time runs out.
     *
     * @return The player with the most health, or null if it's a tie.
     */
    public Character determineTimeoutWinner() {
        Character player1 = players.get(0); // Retrieve Player 1
        Character player2 = players.get(1); // Retrieve Player 2

        if (player1.getHealth() > player2.getHealth()) {
            return player1; // Player 1 wins if they have more health
        } else if (player2.getHealth() > player1.getHealth()) {
            return player2; // Player 2 wins if they have more health
        }
        return null; // Return null if it's a tie
    }

    /**
     * Displays the final winner when the game is over.
     */
    private void displayFinalWinner() {
        String finalMessage;
        if (player1Wins > player2Wins) {
            finalMessage = players.get(0).getName() + " is the overall winner!"; // Player 1 wins overall
        } else if (player2Wins > player1Wins) {
            finalMessage = players.get(1).getName() + " is the overall winner!"; // Player 2 wins overall
        } else {
            finalMessage = "It's a draw overall!"; // Game ends in a draw
        }

        // Display the final result in an alert box
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over"); // Set title of the alert
        alert.setHeaderText("Final Results"); // Set header text of the alert
        alert.setContentText(finalMessage); // Set the content of the alert
        alert.showAndWait(); // Display the alert and wait for user action

        System.exit(0); // Exit the application
    }

    /**
     * Checks if the game is over based on the number of wins.
     *
     * @return True if one player has won more than half the total rounds, false otherwise.
     */
    private boolean isGameOver() {
        return player1Wins > totalRounds / 2 || player2Wins > totalRounds / 2; // Determine if a player has majority wins
    }

    /**
     * Gets the remaining time for the current round.
     *
     * @return The time remaining in seconds.
     */
    public int getTimeRemaining() {
        return timeRemaining; // Return the remaining time
    }

    /**
     * Decrements the remaining time by 1 second, if time is greater than 0.
     */
    public void decrementTimeRemaining() {
        if (timeRemaining > 0) {
            timeRemaining--; // Reduce the time remaining by 1 second
        }
    }
}
