package FightingGame;

// Import necessary Java Swing and AWT classes for graphical rendering
import javax.swing.*; // JPanel for the custom arena panel 
import java.awt.*; // Graphics for rendering sprites and shapes
import java.awt.image.BufferedImage; // BufferedImage for handling sprite images

/**
 * FightArenaPanel is a custom JPanel for rendering the fight arena and managing player sprites.
 */
public class FightArenaPanel extends JPanel 
{
    private static final long serialVersionUID = 1L; // Resolves serialization warning for JPanel

    // Sprites for Player 1 and Player 2
    private BufferedImage player1Sprite, player2Sprite;

    // Coordinates for Player 1 and Player 2
    private int player1X, player1Y, player2X, player2Y;

    // Dimensions of the fight arena
    private static final int ARENA_WIDTH = 600; // Width of the arena
    private static final int ARENA_HEIGHT = 400; // Height of the arena

    /**
     * Constructor for FightArenaPanel.
     * Initializes the sprites, starting positions, and background color of the arena.
     *
     * @param player1Sprite BufferedImage representing Player 1's sprite
     * @param player2Sprite BufferedImage representing Player 2's sprite
     */
    public FightArenaPanel(BufferedImage player1Sprite, BufferedImage player2Sprite) {
        this.player1Sprite = player1Sprite; // Assign sprite for Player 1
        this.player2Sprite = player2Sprite; // Assign sprite for Player 2

        // Initial positions of Player 1 and Player 2
        this.player1X = 50; // Starting X coordinate for Player 1
        this.player1Y = 300; // Starting Y coordinate for Player 1
        this.player2X = 500; // Starting X coordinate for Player 2
        this.player2Y = 300; // Starting Y coordinate for Player 2

        // Set the background color of the arena
        setBackground(Color.BLACK); // Black for a contrasting fight environment
    }

    /**
     * Overridden paintComponent method for custom rendering.
     * Draws the health bars, arena boundaries, and player sprites.
     *
     * @param g Graphics object for drawing shapes and images
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Clear the panel before drawing

        // Draw health bars for Player 1 and Player 2
        g.setColor(Color.GREEN); // Green indicates remaining health
        g.fillRect(50, 30, 200, 20); // Health bar for Player 1
        g.fillRect(ARENA_WIDTH - 250, 30, 200, 20); // Health bar for Player 2

        // Draw health bar outlines
        g.setColor(Color.WHITE); // White outlines for visibility
        g.drawRect(50, 30, 200, 20); // Outline for Player 1's health bar
        g.drawRect(ARENA_WIDTH - 250, 30, 200, 20); // Outline for Player 2's health bar

        // Draw Player 1 and Player 2 sprites at their current positions
        g.drawImage(player1Sprite, player1X, player1Y, null); // Player 1 sprite
        g.drawImage(player2Sprite, player2X, player2Y, null); // Player 2 sprite
    }

    /**
     * Updates the positions of Player 1 and Player 2 based on input.
     * Ensures players stay within the arena boundaries.
     *
     * @param dx1 Horizontal movement for Player 1
     * @param dy1 Vertical movement for Player 1
     * @param dx2 Horizontal movement for Player 2
     * @param dy2 Vertical movement for Player 2
     */
    public void updatePositions(int dx1, int dy1, int dx2, int dy2) {
        // Update Player 1's position, clamping it within the arena boundaries
        player1X = Math.min(Math.max(player1X + dx1, 0), ARENA_WIDTH - 30); // Horizontal bounds for Player 1
        player1Y = Math.min(Math.max(player1Y + dy1, 0), ARENA_HEIGHT - 30); // Vertical bounds for Player 1

        // Update Player 2's position, clamping it within the arena boundaries
        player2X = Math.min(Math.max(player2X + dx2, 0), ARENA_WIDTH - 30); // Horizontal bounds for Player 2
        player2Y = Math.min(Math.max(player2Y + dy2, 0), ARENA_HEIGHT - 30); // Vertical bounds for Player 2

        // Repaint the panel to reflect the updated positions
        repaint(); // Triggers a call to paintComponent
    }
}
